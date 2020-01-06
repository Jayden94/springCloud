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
      registerWithEureka: false  # 自己是注册中心 不注册
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

### 待续...

        

