# SpringCloud 学习笔记



#### 五大常用组件

- 服务发现 - **Eureka**
- 客服端负载均衡 — **Ribbon**
- 断路器 — **Hystrix**
- 服务网关 — **Zuul**
- 分布式配置 — **Spring Cloud Config**

---





### Eureka

- Eureka是其中的一个**组件**，作用是**服务注册和发现**，**Eureka服务端**用作服务注册和发现，支持集群部署。客户端用于处理服务逻辑，提供服务和注册和服务端维护心跳。</br>
- 项目中eureka-server 是作为服务端，提供注册中心,配置文件如下

```
server:
  port: 9000
eureka:
  instance:
      hostname: localhost
      non-secure-port: ${server.port}   # 不指定当前属性会默认为8080 指端口无效
  #server:
      #enable-self-preservation: false   # 关闭自我保护（开发环境操作 ）
  client:
      registerWithEureka: false  # 自己是注册中心 不注册 如果做高可用 registerWithEureka+fetchRegistry需要注释。让server互相注册
      fetchRegistry: false # 自己是注册中心 不获取其他服务
      serviceUrl:
          defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

注意：在启动类中需要加入注解 **@EnableEurekaServer**  标识当前是一个Eureka服务。启动后访问[http://localhost:9000/](https://note.youdao.com/)会有个简单的web页面维护信息。</br>

- eureka-order 作为客户端向server注册，我们来看看client的配置文件

```
eureka:
  client:
    service-url:
      defaultZone: http://localhost:9000/eureka/  # 注册到注册中心
server:
  port: 9001
spring:
  application:
    name: order-server
```
这次我们并没有指定registerWithEureka，fetchRegistry两个属性，但是服务依旧能注册上去,我们看看源码

```
 public EurekaClientConfigBean() {
        this.serviceUrl.put("defaultZone", "http://localhost:8761/eureka/");
        this.gZipContent = true;
        this.useDnsForFetchingServiceUrls = false;
        this.registerWithEureka = true;
        this.preferSameZoneEureka = true;
        this.availabilityZones = new HashMap();
        this.filterOnlyUpInstances = true;
        this.fetchRegistry = true;
        this.dollarReplacement = "_-";
        this.escapeCharReplacement = "__";
        this.allowRedirects = false;
        this.onDemandUpdateStatusChange = true;
        this.clientDataAccept = EurekaAccept.full.name();
        this.shouldUnregisterOnShutdown = true;
        this.shouldEnforceRegistrationAtInit = false;
        this.order = 0;
    }
    
    public void setRegisterWithEureka(boolean registerWithEureka) {
        this.registerWithEureka = registerWithEureka;
    }
    
     public void setFetchRegistry(boolean fetchRegistry) {
        this.fetchRegistry = fetchRegistry;
    }
```
在初始化Bean的时候,Eureka已经给我们默认值了,所以不设置值也可以注册。当然我们自己指定就会调用set方法，给ClientConfigBean设置新的属性值。最后也当然需要在启动类中指定当前服务是client ,使用的注解是 **@EnableEurekaClient** 

在使用Spring Cloud + Eureka服务发现的时候提到了两种注解，一种为 **@EnableDiscoveryClient**,一种为 **@EnableEurekaClient**

如果选用注册中心是Eureka推荐使用 **@EnableEurekaClient**,如果是其他注册中心（zk）那么使用 **@EnableDiscoveryClient**

```
    @Autowired
    private DiscoveryClient discoveryClient;


    @RequestMapping("/")
    public String hello(){
        List<ServiceInstance> instance=discoveryClient.getInstances("order-server");
        for (ServiceInstance serviceInstance : instance) {
            System.out.println(serviceInstance.getHost());
        }
        return "hello ！";
    }
```
### Eureka高可用
- 生产上启动至少两台server端，让他们相互注册，建议三台,两台互相注册。  
我们来测试一下，本地启动两台sever，idea配置两个不同端口相互注册。
vm options:-Dserver.port=8761
vm options:-Dserver.port=8762
启动8761端口服务向8762注册，8762向8761注册。
```javascript
#server1
client:
      #registerWithEureka: false  # 自己是注册中心 不注册
      #fetchRegistry: false # 自己是注册中心 不在查找
      serviceUrl:
          defaultZone: http://localhost:8762/eureka/
		  
#server2
client:
      #registerWithEureka: false  # 自己是注册中心 不注册
      #fetchRegistry: false # 自己是注册中心 不在查找
      serviceUrl:
          defaultZone: http://localhost:8761/eureka/		  
```

client就需要配置两个注册server地址，一个挂掉，自动注册到另一台保持服务高可用。
```javascript
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/,http://localhost:8762/eureka/  # 注册到注册中心
server:
  port: 9001
spring:
  application:
    name: order-client
```
### Ribbon 服务通信
- Ribbon是一个基于HTTP和TCP客户端负载均衡工具。通过Cloud封装，我们可以轻松将面向服务的REST模板自动转换成
客户端负载均衡服务端调用。       
- restTemplate调用  
    1.通过restTemplate调用 有两种方式,如下面订单服务需要调用产品服务
    ```
       @Component
       public class RestTemplateConfig {
            
            //注入restTemplate
           @Bean
           @LoadBalanced
           public RestTemplate restTemplate(){
               return new RestTemplate();
           }
       
       }     
    ```
    ```
        @Autowired
        private RestTemplate restTemplate;
    
        @RequestMapping("/getProductMsg")
        public String hello(){
            //1 。第一种通过restTemplate 调用（直接使用RestTemplate 缺点 url直接写死，多台服务不好调用）
            /*RestTemplate restTemplate= new RestTemplate();
            String msg = restTemplate.getForObject("http://localhost:9002/msg", String.class);*/
    
            //第二种方式（利用loadBalanced,可在restTemplate里使用服务名字(Spring.application.name))
            String msg =restTemplate.getForObject("http://product-client/msg",String.class);
            return msg;
        }
           
    ```
- Feign调用 
   Feign是一个申明式REST客户端(伪RPC)，采用了基于接口的注解，对远程服务的调用。内部也是使用了Ribbon做负载均衡。  
   1.导入相关依赖，详细参考pom文件。  
   2.启动类加入注解 @EnableFeignClients  
   3.新建接口调用，不用写实现，区别于之前接口调用。    
   ```
           @FeignClient(name = "product-client")
           public interface ProductClient {
           
               @GetMapping("/msg")
               String productMsg();
           
           }
   ```   
   一般使用会将应用拆分为多模块  
   product-server  所有业务逻辑  
   product-client  对外暴露的Feign接口
   product-common  公用对象，可以被内部和外部使用。  
   product项目做了分层，详细看代码。order为了方便没有处理。  
   需要注意的地方
    - 需要调用对方feign接口，需要在当前服务最外层引入依赖，server模块也要引入依赖  
    - 调用方需要加入扫描包@EnableFeignClients(basePackages = "对方暴露的client包路径") 
    
### Spring Cloud Config 远程配置中心  
- config分为server端和client端，能够远程在git上拉取配置，动态刷新配置文件。线上配置账号密码隔离性。
#### server端
- 导入相关pom文件依赖
- 启动类加入@EnableConfigServer ，@EnableEurekaClient 注解。
- 配置文件配置
```
spring:
  application:
    name: config
  cloud:
    config:
      server:
        git:
          uri:  # git地址uri
          username: git账号
          password: 密码
          basedir: 拉取本地的配置路径
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

```
启动后url 访问
http://localhost:8080/order-dev.yml 即可看到配置文件  
 读取格式   
   - /{name}-{profiles}.yml   --默认master  
   - /{lable}/{name}-{profiles}.yml  
      - name  文件名  
      - profiles 环境  
      -  lable   
#### client端
- 首先导入config-client pom 文件依赖
- 修改yml文件为bootstrap.yml ，确保优先启动。
- 设置yml文件
```
spring:
  application:
    name: order
  cloud:
    config:
      discovery:
        enabled: true
        service-id: CONFIG #从config读取配置
      profile: dev # 读取配置的profiles
## 需要将注册配置配置在项目中，防止找不到注册中心，直接去找config失败
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/,http://localhost:8762/eureka/
```
##### config 高可用
- config server部署多台机器，不同端口启动。
##### 特别注意
- git 配置多个文件时,config读取时
例子：       
      - order-dev.yml	  
      - order-test.yml  
      - order.yml  
如果读取dev,远程配置会把order配置合并到dev，或者test配置中，基于这点，order也可以放通用配置。

### cloud bus 
- sprignCloud bus 会自动刷新配置,config-server使用了springCloud bus 会对外提供一个http接口 /bus-refresh ,修改配置后会发送一个消息到消息队列,然后服务读取消息。从而修改配置。
#### server端
- 首先我们在server端添加依赖
```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bus-amqp</artifactId>
        </dependency>
```
- 配置文件配置 ，暴露bus refresh 接口,配置文件需要配置MQ信息。本地会默认localhost。
```
## 暴露bus refresh 接口 * 是所有 除了本地环境,其他环境要配置mq配置
management:
  endpoints:
    web:
      exposure:
        include: "*"
```
#### client端
- 添加依赖
```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-bus-amqp</artifactId>
        </dependency>
```
- 如果其他环境,配置文件需要配置MQ信息。本地会默认localhost。
- 代码中获取配置文件配置类
```
@Component
@ConfigurationProperties(prefix = "user")
@RefreshScope
public class UserConfig {
    String name;
    String age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}


### 配置文件中配置
user :
  name : jack
  age : 19
```
- 需要自动刷新位置必须加入 @RefreshScope 注解。
- 在远端git更新配置后调用 http://localhost:8080/actuator/bus-refresh
    - POST方式
    - 如果405 查看server端暴露是否有问题。
- 调用成功后,刷新配置成功。
####  Webhooks 更改配置后自自动刷新 
设置Webhooks后，不需要在请求/actuator/bus-refresh 能自动刷新配置.  设置时候不在用bus-refresh, config组件提供了个专门用webhook的路由(monitor).
- http://xxxxxxx/monitor
- Content type =json

### Spring Cloud Stream 
Spring Cloud stream 进一步封装了MQ的组件,目前支持rabbitMQ和kafka,通过input,和outputs 和binder交互,开发人员不用再关心routingkey和Exchanges ,简化开发。
- pom文件
``` 
  <!-- cloud stream -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
        </dependency>
```        

- 定义接口 
``` 
public interface StreamClient {

    String INPUT= "myMsg";

    @Input(StreamClient.INPUT)
    SubscribableChannel input();

    @Output(StreamClient.INPUT)
    MessageChannel output();
}

```   
- 消费端
``` 
@Component
@EnableBinding(StreamClient.class)
@Slf4j
public class SteamReceiver {

    @StreamListener(StreamClient.INPUT) //监听队列
    public void process(Object message){
        System.out.println("SteamReceiver =====:[{"+message+"}]");
    }

}
``` 
- 发送
``` 
    @Autowired
    private StreamClient streamClientl;

    @GetMapping("/sendMessage")
    public  void process(){
        streamClientl.output().send(MessageBuilder.withPayload("now"+ new Date()).build());
    }
``` 
- 消息分组使用
```
spring:
  application:
    name: order
  cloud:
    config:
      discovery:
        enabled: true
        service-id: CONFIG #从config读取配置
      profile: dev # 读取配置的profiles
    ####  消息分组使用  避免服务集群后重复消费消息   
    stream:
      binders:
          myMsg:
            group: order
            ### 序列化对象 
            content-type: application/json 
```            

### Zuul
在微服务架构中,通常会有多个服务提供者。设想一个电商系统，可能会有商品、订单、支付、用户等多个类型的服务,客户端不可能和每个服务端调用，这个时候就需要服务网关,来做统一请求的入口,聚合API。  
Zuul提供了动态路由、监控、弹性负载和安全功能。Zuul底层利用各种filter实现如下功能：  

- 认证和安全 识别每个需要认证的资源，拒绝不符合要求的请求。
- 性能监测 在服务边界追踪并统计数据，提供精确的生产视图。
- 动态路由 根据需要将请求动态路由到后端集群。
- 压力测试 逐渐增加对集群的流量以了解其性能。
- 负载卸载 预先为每种类型的请求分配容量，当请求超过容量时自动丢弃。
- 静态资源处理 直接在边界返回某些响应。

#####  基础使用
- 导入相关pom文件
- 启动类开启@EnableZuulProxy
- 相关基本配置
```
spring:
  application:
    name: api-geteway
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
zuul:
# 设置过滤路由 ,当前url 正则拒绝访问 多个后面继续添加 
#  ignored-patterns:
#    - /**/product/**
  # 第一种写法 myProduct是自定义前缀 http://localhost:9000/product/product/list  http://localhost:9000/myProduct/product/list
#  routes:
#    myProduct:  
#      path: /myProduct/**
#      serviceId: product
  # 第二种写法
  routes:
    product: /product/**
  # 不设置这个 cookies 写入不了,有写入cookies 必须加入配置
  sensitive-headers:     
####  对外暴露所有的端口 
management:
  endpoints:
    web:
      exposure:
        include: "*"
```  
/actuator可以查看当前暴露的url,具体可以访问参考。/actuator/routes/ 可以查看可以转发的服务
- 动态路由
将配置放到远端git上维护。代码中加入 可以自定义config或者启动类中加入
``` 
    //动态路由
    @ConfigurationProperties("zuul")
    @RefreshScope
    public ZuulProperties zuulProperties(){
        return  new ZuulProperties();
    }
``` 


##### zuul高可用
- 多个zuul节点注册到Euraka Server

- Nginx和Zuul 混搭，对于外部请求，我们可以用nginx暴露一个url给外部节点，然后负责均衡到多个zuul节点上 。


#### zuul 四种过滤器
- pre：在 Zuul 按照规则路由到下级服务之前执行。如果需要对请求进行预处理，比如鉴权、限流等，都应考虑在此类 Filter 实现。
- route：这类 Filter 是 Zuul 路由动作的执行者，是 Apache Http Client 或 Netflix Ribbon 构建和发送原始 HTTP 请求的地方，目前已支持 Okhttp。
- post：这类 Filter 是在源服务返回结果或者异常信息发生后执行的，如果需要对返回信息做一些处理，则在此类 Filter 进行处理。
- error：在整个生命周期内如果发生异常，则会进入 error Filter，可做全局异常处理。

##### pre 过滤器  
请求目标结果之前，对请求做处理。
例:实现一个不带token参数,跳转401 无权限。
```
/**
 * Created by kui.jin
 */
@Component
public class TokenFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return PRE_TYPE;
    }
    
    //执行顺序 找到对应枚举类 数据越小权最先执行
    @Override
    public int filterOrder() {
        return PRE_DECORATION_FILTER_ORDER-1; //放在pre 
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String token = request.getParameter("token");
        if(StringUtils.isBlank(token)){
            //设置返回
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
        }
        return null;
    }
```  
#### post 过滤器
请求目标之后对请求做处理
```  
/**
 * Created by kui.jin ON 2020/4/21
 * 实现一个请求到目标后对结果加工
 */
public class PostFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        //post 是权重最大-1
        return SEND_RESPONSE_FILTER_ORDER-1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletResponse response = requestContext.getResponse();
        response.setHeader("X",UUID.randomUUID().toString());
        return null;
    }
}
```  
#### pre 实现令牌通限流
令牌通是限流的一种算法,按匀速往桶中放入请求,如果没拿到,直接放回。拿到继续业务逻辑。下面实现以下,用到了RateLimiter(谷歌令牌桶实现)
``` 
**
 * Created by kui.jin ON 2020/4/21
 * 基于pre 过滤器实现 令牌桶限流
 */
public class RateLimitFilter extends ZuulFilter {
    //基于guava组件
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(100); //参数每秒放多少个令牌

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return SERVLET_DETECTION_FILTER_ORDER-1; //优先最高
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }
    
    //也可以针对某个Url做令牌桶，从request中拿到url判断
    @Override
    public Object run() throws ZuulException {
        //如果没有拿到令牌
        if(RATE_LIMITER.tryAcquire()){
            throw  new RateLimitException();
        }
        return null;
    }
``` 
#### zuul 权限校验
例子：使用前置pre  
/order/create 只能买家访问
/order/finish 只能卖家访问
/product/list 都可以访问
``` 
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
       /* /order/create 只能买家访问
          /order/finish 只能卖家访问
          /product/list 都可以访问*/
       //注意 判断拦截通过zuul url 需要加入serverID前缀
       if("order/order/create".equals(request.getRequestURI())){
           //判断逻辑 如果不符合条件 返回401
       }
        //注意 判断拦截通过zuul url 需要加入前缀
        if("order/order/finish".equals(request.getRequestURI())){
            //判断逻辑 如果不符合条件 返回401
        }
        //都可以访问不需要做逻辑处理
        return null;
    }
``` 

####  爬坑
本地加入配置调试的时候,例如:加入暴露所有断点配置。
```
management:
  endpoints:
    web:
      exposure:
        include: "*"
```          
本地调用 http://localhost:9000/actuator/routes 查看转发的服务直接报404,当时就很无语,配置什么都没有问题,去查看server端,自我保护也关闭了,后来看编译的包,发现执行的是老代码。install一下就好了。  
总结：配置文件最好还是放在git上,去做动态刷新。避免缓存留坑。
### Hystrix
在生产中,服务之间调用链路通常比较长,例 A->B->C,如C服务不可用,B会一直重试,最后会导致B也不可用,A->B,A也开始重试,这样会产生雪崩效应,在生产上我们要避免雪崩效应。Hystrix提供服务降级,服务熔断,依赖隔离和监控。
- 服务降级  
 优先核心服务，非核心服务不可用或者弱可用. 区别业务场景,例如双十一，提示网络开小差。可优先支付服务,订单查询服务等次级等等。  
    - 导入pom文件
    - 开启注解 @EnableCircuitBreaker ，如果包含多个注解可以用@SpringCloudApplication
    - 服务降级使用
    
    ```
    @RestController
    //设置所有方法如果异常走 默认降级
    @DefaultProperties(defaultFallback = "defaultFallback" )
    public class HystrixController {
    
    //降级注解 异常和服务调用失败走降级 指定降级方法,直接注解不写参数走默认
    @HystrixCommand(fallbackMethod = "fallback")
    @GetMapping("/getProductList")
    public String getProductList(){
        //1 服务不可用会走降级
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://localhost:9002/product/list",String.class);
        //2 本地方法异常也可以走降级
        //throw  new RuntimeException();
        return result;

    }
    
    //降级逻辑
    public String fallback(){
        return "稍后再试！";
    }

    ```    
    - 服务设置超时
    ```
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value ="3000" )
    })
    ```   
- 服务熔断
    - 例子 当一个服务错误率达到一定百分比,后面请求无意义,触发熔断,直接走服务降级逻辑，过段时间，会关闭断路器open状态,进入半闭合,请求服务,如果请求成功继续走业务逻辑。关闭熔断。
    
   ```
    //requestVolumeThreshold   熔断请求数量
    //sleepWindowInMilliseconds
    // 时间窗口，当断路器打开，这段时间内，降级逻辑会成为主逻辑。过了时间，会在请求，如果主逻辑恢复走主逻辑
    //errorThresholdPercentage   错误百分比条件
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value ="true" ),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value ="10" ), //熔断请求数量
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value ="10000" ),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value ="60" ) //错误百分比条件
    })
    @GetMapping("/getProductList")
    public String getProductList(@RequestParam("number")Integer number){
        if(number % 2 == 0){
            return "success !" ;
        }
        //1 服务不可用会走降级
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://localhost:9002/product/list",String.class);
        return result;

    }
    ```  
 - feign也支持hystrix 
    - 在配置文件中配置
    ```
        feign:
            hystrix:
                enabled: true
    })
    ```   
    - 对外暴露的client 注解新增fallback  
    ```
    @FeignClient(name = "product",fallback = ProductClientFallback.class)
    public interface ProductClient {
    
    //查询商品
    @PostMapping("/product/listForOrder")
    List<ProductInfoOutput> listForOrder(@RequestBody  List<ProductInfoInput> productIdList);

    //降级类
    @Component
    static  class  ProductClientFallback implements ProductClient{
        @Override
        public List<ProductInfoOutput> listForOrder(List<ProductInfoInput> productIdList) {
            return null;
        }
    }
    ```
- 监控(Hystrix Dashboard)


### 待学习 
- nacos 代替 eureka
- sentinel 



       