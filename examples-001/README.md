# 如何平滑的更换service层的实现层？

lorne 2023-10-07  

## Service层使用过程介绍

Service层是再很多架构中都会经常被使用的一层，它的作用是将业务逻辑封装起来，包装到一个service类中。
在日常的项目开发过程中，通常会将service分为接口定义层和实现层。

例如：

接口层的定义

```java

public interface DemoService {

    String hello();
}

```
实现层的定义
```java


@Service
public class DemoServiceImpl implements DemoService {

    @Override
    public String hello() {
        return "Hello World!";
    }
}

```

在项目的使用过程中，通常会直接引用接口对象，然后调用接口中的方法，例如：

```java
@RestController
@RequestMapping("/demo")
@AllArgsConstructor
public class DemoController {

    private final DemoService demoService;

    @GetMapping("/hello")
    public String hello() {
        return demoService.hello();
    }

}

```

## Service接口定义的左右何在？

在上述的例子过程中，我们定义了接口和实现，然后在项目中使用接口而不是实现来访问service，感觉像是有非常好的解耦效果，但是实际呢？

下面以场景举例，说明  
1. 假如我们需要重写service的逻辑，目前的做法是怎样的？  
通常大家的处理方式可能是直接修改DemoServiceImpl的代码。例如：  
```java

@Service
public class DemoServiceImpl implements DemoService {

    @Override
    public String hello() {
        return "new Hello World!";
    }
}

```
如果我们这样去做的话，其实与其定义接口的意义就没有了，因为我们直接修改了实现层的代码，那么接口的定义就没有任何意义了。

总结来说：  
这个场景表达的含义是业务需求做了调整，对于我们实现来说不得不去修改逻辑来应对调整，但是当我们这样去做了以后，你将感觉不到service层定义接口的任何价值，只会感觉到service多写一次接口反而更加麻烦。

2. 假如我们开发一个支付功能，需要支持微信和支付宝两种不同方式的支付，那么我们的service层应该如何设计？

如下为了简化，我们直接定义一个PayService的代码。例如：
    
```java
public interface PayService {

    String pay();
    
}
```

对应我们需要写两种不同的实现，分别是WechatPayServiceImpl和AlipayPayServiceImpl。例如：

```java

@Service
public class AlipayPayServiceImpl implements PayService {

    @Override
    public String pay() {
        return "alipay pay";
    }
}

```


```java

@Service
public class WechatPayServiceImpl implements PayService {

    @Override
    public String pay() {
        return "wechat pay";
    }

}

```

当我们定义了多个Service的实现的时候，我们在使用Service的时候还需要做区分，如果直接注入PayService的话，将会报错
因为Spring无法判断我们需要注入哪个实现，例如：

错误示范如下：

```java
@RestController
@RequestMapping("/pay")
@AllArgsConstructor
public class PayController {
    
    private final PayService payService;
    
    @GetMapping("/pay")
    public String pay() {
        return payService.pay();
    }
    
}

```

错误日志如下：
```

***************************
APPLICATION FAILED TO START
***************************

Description:

Parameter 0 of constructor in com.lease.examples.le001.server.controller.PayController required a single bean, but 2 were found:
	- alipayPayServiceImpl: defined in file [C:\Users\linqu\developer\github\le-examples\examples-001\server\target\classes\com\lease\examples\le001\server\service\impl\AlipayPayServiceImpl.class]
	- wechatPayServiceImpl: defined in file [C:\Users\linqu\developer\github\le-examples\examples-001\server\target\classes\com\lease\examples\le001\server\service\impl\WechatPayServiceImpl.class]


Action:

Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans, or using @Qualifier to identify the bean that should be consumed
```
让我们处理一下错误，做一个正确的示范：  

接口层的调整：  

```java
public interface PayService {

    String pay();

    boolean support(String payType);
}

```

实现层的定义： 
```java

@Service
public class AlipayPayServiceImpl implements PayService {

    @Override
    public String pay() {
        return "alipay pay";
    }

    @Override
    public boolean support(String payType) {
        return payType.equalsIgnoreCase("alipay");
    }
}

@Service
public class WechatPayServiceImpl implements PayService {

    @Override
    public String pay() {
        return "wechat pay";
    }

    @Override
    public boolean support(String payType) {
        return payType.equalsIgnoreCase("wechat");
    }

}
```

访问层的Factory定义：
```java

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class PayServiceFactory {

    private final List<PayService> payServiceList;

    public PayService getPayService(String payType) {
        for (PayService payService : payServiceList) {
            if (payService.support(payType)) {
                return payService;
            }
        }
        throw new RuntimeException("not support pay type");
    }
}
```

使用层的定义：
```java

import com.lease.examples.le001.server.service.PayServiceFactory;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay")
@AllArgsConstructor
public class PayController {

    private final PayServiceFactory payServiceFactory;

    @GetMapping("/pay")
    public String pay(@RequestParam(value = "payType", defaultValue = "wechat") String payType) {
        return payServiceFactory.getPayService(payType).pay();
    }
    

}

```

总结说明：
当我们需要切换不同的Service的实现的时候，还需要做额外的处理，不然在直接注入Service的时候还将会出现错误。   
虽然在需要切换的场景下，service层是比较方便，可以通过增加新的实现的方式实现对新的实现的切换，但是在使用的时候还需要引入Factory来做额外的处理，实际上这样的处理的方法是得力于策略模式的引入，而非service层的优势。

![](images/img.png)
[策略模式介绍](https://www.runoob.com/design-pattern/strategy-pattern.html)

通过策略模式的引入，我们可以非常简单的实现通过增加一个新的实现来实现对新的实现的切换. 例如我们增加一个余额支付
```java

@Service
public class BalancePayServiceImpl implements PayService {

    @Override
    public String pay() {
        return "balance pay";
    }

    @Override
    public boolean support(String payType) {
        return payType.equalsIgnoreCase("balance");
    }

}

```
