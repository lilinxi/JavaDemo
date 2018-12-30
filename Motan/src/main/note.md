在多个服务器进程之间的通信，目前使用的技术一般是 RPC（Remote Procedure Call Protocol，远程过程调用协议）。

使用 RPC 可以访问远程主机的进程服务，不需要清楚底层网络通信机制，只需要关注服务本身即可。RPC 是目前分布式开发技术中一种常用的技术，其在分布式开发中能更简单地调用远程服务，就像本地开发一样。

Motan 是新浪微博开源的 RPC 轻量级框架，其底层网络通信使用了 Netty 网络框架，序列化协议支持 Hessian 和 Java 的序列化，网络通信协议可以支持 Motan、Http、TCP 等。

# Motan Github 链接：https://github.com/weibocom/motan

# 创建 webapp 项目参见链接：https://blog.csdn.net/qq_39384184/article/details/85406037

# 添加 pom 依赖

```
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.youyoo</groupId>
    <artifactId>Motan</artifactId>
    <packaging>war</packaging>
    <version>1.0</version>
    <name>Motan Maven Webapp</name>
    <url>http://maven.apache.org</url>
    <repositories>
        <repository>
            <id>spy</id>
            <name>36</name>
            <layout>default</layout>
            <url>http://repo1.maven.org/maven2</url>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
        </repository>
    </repositories>
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>3.8.1</version>
            <scope>test</scope>
        </dependency>

        <!--Motan-->
        <dependency>
            <groupId>com.weibo</groupId>
            <artifactId>motan-core</artifactId>
            <version>0.0.1</version>
        </dependency>
        <dependency>
            <groupId>com.weibo</groupId>
            <artifactId>motan-transport-netty</artifactId>
            <version>0.0.1</version>
        </dependency>
        <!-- 集群相关 -->
        <!-- consul -->
        <dependency>
            <groupId>com.weibo</groupId>
            <artifactId>motan-registry-consul</artifactId>
            <version>0.0.1</version>
        </dependency>
        <!-- zookeeper -->
        <dependency>
            <groupId>com.weibo</groupId>
            <artifactId>motan-registry-zookeeper</artifactId>
            <version>0.0.1</version>
        </dependency>
        <!-- dependencies blow were only needed for spring-based features -->
        <dependency>
            <groupId>com.weibo</groupId>
            <artifactId>motan-springsupport</artifactId>
            <version>0.0.1</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>4.0.5.RELEASE</version>
        </dependency>
    </dependencies>
    <build>
        <finalName>Motan</finalName>
    </build>
</project>

```

# 为服务方的调用方创建接口

- FooService

```
package server;

import java.util.List;
import java.util.Map;


public interface FooService {
	String hello(String name);

	int helloInt(int number1);

	double helloDouble(double number2);

	List<String> helloList(List<String> list);

	Map<String, List<String>> helloMap(Map<String, List<String>> map);

	DemoBean helloJavabean(DemoBean bean);
}

```

- DemoBean

```
package server;

import java.io.Serializable;

public class DemoBean implements Serializable {
	private static final long serialVersionUID = -8088381880917660322L;
	private long id;
	private String name;
	private double score;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

}

```

# 服务方实现这个接口的逻辑

- FooServiceImpl

```
package server;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FooServiceImpl implements FooService {

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"classpath:motan_server.xml");
		System.out.println("server start...");
	}

	public String hello(String name) {
		System.out.println("invoked rpc service " + name);
		return "hello " + name;
	}

	public int helloInt(int number1) {
		System.out.println("invoked rpc service " + number1);
		return number1;
	}

	public double helloDouble(double number2) {
		System.out.println("invoked rpc service " + number2);
		return number2;
	}

	public List<String> helloList(List<String> list) {
		System.out.print("invoked rpc service ");
		for (String string : list) {
			System.out.print(string + ",");
		}
		System.out.println();
		return list;
	}

	public Map<String, List<String>> helloMap(Map<String, List<String>> map) {
		System.out.print("invoked rpc service ");
		for (String key : map.keySet()) {
			System.out.print(key + ":[");
			for (String list : map.get(key)) {
				System.out.print(list + ",");
			}
			System.out.print("],");
		}
		System.out.println();
		return map;
	}

	public DemoBean helloJavabean(DemoBean bean) {
		System.out.print("invoked rpc service " + bean);
		System.out.print("," + bean.getId());
		System.out.print("," + bean.getName());
		System.out.print("," + bean.getScore());
		System.out.println();
		return bean;
	}

}

```

# 配置服务方暴露接口

- motan_server.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:motan="http://api.weibo.com/schema/motan"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
   http://api.weibo.com/schema/motan http://api.weibo.com/schema/motan.xsd">
    <!-- service implemention bean -->
    <bean id="serviceImpl" class="server.FooServiceImpl"/>
    <!-- exporting service by motan -->
    <motan:service interface="server.FooService" ref="serviceImpl"
                   export="8002"/>

    <!--集群配置：registry 定义-->
    <!--consul-->
    <!--<motan:registry regProtocol="consul" name="my_consul"-->
    <!--address="127.0.0.1:8003"></motan:registry>-->
    <!--consul server-->
    <!--<motan:service interface="server.FooService" ref="serviceImpl"-->
    <!--registry="my_consul" export="8002"/>-->
    <!--zookeeper 单节点-->
    <!--<motan:registry regProtocol="zookeeper" name="my_zookeeper"-->
    <!--address="127.0.0.1:2181"></motan:registry>-->
    <!--zookeeper 多节点集群-->
    <!--<motan:registry regProtocol="zookeeper" name="my_zookeeper"-->
    <!--address="127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183"></motan:registry>-->
    <!--zookeeper server-->
    <!--<motan:service interface="server.FooService" ref="serviceImpl"-->
    <!--registry="my_zookeeper" export="8002"/>-->
</beans>
```

# 配置调用方调用接口

- client_server.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:motan="http://api.weibo.com/schema/motan"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd
   http://api.weibo.com/schema/motan http://api.weibo.com/schema/motan.xsd">
    <!-- reference to the remote service -->
    <motan:referer id="remoteService" interface="server.FooService"
                   directUrl="localhost:8002"/>

    <!--集群配置：registry 定义-->
    <!--consul-->
    <!--<motan:registry regProtocol="consul" name="my_consul"-->
    <!--address="127.0.0.1:8500"></motan:registry>-->
    <!--consul client-->
    <!--<motan:referer id="remoteService"-->
    <!--interface="server.FooService"-->
    <!--registry="my_consul"></motan:referer>-->
    <!--zookeeper 单节点-->
    <!--<motan:registry regProtocol="zookeeper" name="my_zookeeper"-->
    <!--address="127.0.0.1:2181"></motan:registry>-->
    <!--zookeeper 多节点集群-->
    <!--<motan:registry regProtocol="zookeeper" name="my_zookeeper"-->
    <!--address="127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183"></motan:registry>-->
    <!--zookeeper client-->
    <!--<motan:referer id="remoteService"-->
    <!--interface="server.FooService"-->
    <!--registry="my_zookeeper"></motan:referer>-->
</beans>
```

# 调用方调用

- Client

```
package client;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import server.DemoBean;
import server.FooService;

public class Client {
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"classpath:motan_client.xml");
		// 获取到service
		FooService service = (FooService) ctx.getBean("remoteService");
		// rpc调用
		/** String **/
		String ret1 = service.hello("motan");
		System.out.println(ret1);
		/** int **/
		int ret2 = service.helloInt(110);
		System.out.println(ret2);
		/** double **/
		double ret3 = service.helloDouble(11.2);
		System.out.println(ret3);
		/** list **/
		List<String> list = new ArrayList<String>();
		list.add("hello");
		list.add("motan");
		List<String> ret4 = service.helloList(list);
		for (String string : ret4) {
			System.out.print(string + ",");
		}
		System.out.println();
		/** map **/
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		map.put("key1", Arrays.asList(new String[] { "val1","val2" }));
		map.put("key2", Arrays.asList(new String[] { "val1","val2","val3" }));
		map.put("key3", Arrays.asList(new String[] { "val1","val2","val3","val4" }));
		Map<String, List<String>> ret5 = service.helloMap(map);
		for (String key : ret5.keySet()) {
			System.out.print(key + ":[");
			for (String tmp : map.get(key)) {
				System.out.print(tmp + ",");
			}
			System.out.print("],");
		}
		System.out.println();
		/** javabean **/
		DemoBean bean = new DemoBean();
		bean.setId(1001l);
		bean.setName("motan bean");
		bean.setScore(99.998);
		DemoBean ret6 = service.helloJavabean(bean);
		System.out.print(ret6.getId());
		System.out.print("," + ret6.getName());
		System.out.print("," + ret6.getScore());
		System.out.println();
	}

}

```

**Motan 的服务方创建好服务接口之后，将暴露接口、端口等在配置文件中写好，启动配置文件即可开启，调用方需要使用与服务方相同的接口（包括包名相同），并正确配置配置文件，即可在代码中调用服务方暴露的接口服务。**

# 集群配置

实现集群调用只需要在上述操作的基础上做一点改变即可，Motan 的集群与阿里的 Dubbo 的原理类似，通过注册方、服务方、调用方三方实现。

- RPC Sever 向 RPC Registry 注册服务，并每隔一定时间向 RPC Registry 发送心跳汇报状态。
- RPC Client 需要向 RPC Registry 订阅 RPC 服务，RPC Client 根据 RPC Registry 返回的服务列表，对具体的 RPC Server 进行 RPC 调用。
- 当 RPC Server 发生变更时，RPC Registry 会同步变更，RPC Client 感知后会对本地的服务列表作相应调整。

**Motan 支持 consul 和 Zookeeper 两种外部服务器发现组件，实现方式见上述代码的注释**：需要添加 pom 依赖和在 server 和 client 的配置文件中增加 registry 的定义。

consul 需要显示调用心跳开关注册到 consul（zookeeper 不需要）。

```
MotanSwitcherUtil.setSwitcher(ConsulConstants.NAMING_PROCESS_HEARTBEAT_SWITCHER,true);

```