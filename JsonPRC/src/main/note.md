在多个服务器进程之间的通信，目前使用的技术一般是 RPC（Remote Procedure Call Protocol，远程过程调用协议）。

使用 RPC 可以访问远程主机的进程服务，不需要清楚底层网络通信机制，只需要关注服务本身即可。RPC 是目前分布式开发技术中一种常用的技术，其在分布式开发中能更简单地调用远程服务，就像本地开发一样。

# Json-rpc：jsonrpc4j 的 jar 包及其依赖的 jar 包的 maven 项目的 pom文件

```
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId></groupId>
    <artifactId>JsonPRC</artifactId>
    <packaging>war</packaging>
    <version>1.0</version>
    <name>JsonPRC Maven Webapp</name>
    <url>http://maven.apache.org</url>
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>3.8.1</version>
            <scope>test</scope>
        </dependency>

        <!-- jsonrpc4j -->
        <dependency>
            <groupId>com.github.briandilley.jsonrpc4j</groupId>
            <artifactId>jsonrpc4j</artifactId>
            <version>1.0</version>
        </dependency>
        <!-- jsonrpc4j依赖的包 -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.10</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>2.0.2</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.0.2</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.0.2</version>
        </dependency>
        <dependency>
            <groupId>javax.portlet</groupId>
            <artifactId>portlet-api</artifactId>
            <version>2.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>3.1.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>3.1.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>3.1.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>3.1.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>commons-codec</groupId>
            <artifactId>commons-codec</artifactId>
            <version>1.4</version>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpcore-nio</artifactId>
            <version>4.2.1</version>
        </dependency>
        <dependency>
            <groupId>org.jmock</groupId>
            <artifactId>jmock-junit4</artifactId>
            <version>2.5.1</version>
        </dependency>
        <dependency>
            <groupId>org.jmock</groupId>
            <artifactId>jmock</artifactId>
            <version>2.5.1</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.jetty</groupId>
            <artifactId>jetty-server</artifactId>
            <version>9.0.0.RC0</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.jetty</groupId>
            <artifactId>jetty-servlet</artifactId>
            <version>9.0.0.RC0</version>
        </dependency>
        <dependency>
            <groupId>org.ow2.chameleon.fuchsia.base.json-rpc</groupId>
            <artifactId>
                org.ow2.chameleon.fuchsia.base.json-rpc.json-rpc-bundle
            </artifactId>
            <version>0.0.2</version>
        </dependency>
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-all</artifactId>
            <version>5.0.0.Alpha1</version>
        </dependency>
    </dependencies>
    <build>
        <finalName>JsonPRC</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>6</source>
                    <target>6</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

```

# org.apache.maven-archtypes:maven-archtype-webapp 项目

## Idea 设置 Maven 选项直接创建

1.png

Maven：settings.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<settings> 
<localRepository>/home/yizhen/.m2/repository</localRepository><!--需要改成自己的maven的本地仓库地址-->
    <mirrors>
        <mirror>
            <id>alimaven</id>
            <name>aliyun maven</name>
            <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>
  <profiles>
    <profile>
       <id>nexus</id> 
        <repositories>
            <repository>
                <id>nexus</id>
                <name>local private nexus</name>
                <url>http://maven.oschina.net/content/groups/public/</url>
                <releases>
                    <enabled>true</enabled>
                </releases>
                <snapshots>
                    <enabled>false</enabled>
                </snapshots>
            </repository>
        </repositories>
        
        <pluginRepositories>
            <pluginRepository>
            <id>nexus</id>
            <name>local private nexus</name>
            <url>http://maven.oschina.net/content/groups/public/</url>
            <releases>
                <enabled>true</enabled>
            </releases>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            </pluginRepository>
        </pluginRepositories>
    </profile></profiles>
</settings>
 


```

创建 Maven 项目时选择框架：

```
Group Id: org.apache.maven-archtypes
Artifact Id: maven-archtype-webapp
Version: 1.0

```

## Idea 创建失败时本地创建项目

设置 Maven 环境变量（Mac+Idea）

```
cd ~

open -e .bash_profile

MAVEN_HOME="/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3"
PATH=$MAVEN_HOME/bin:$PATH:.
export MAVEN_HOME
export PATH

source .bash_profile

mvn 提示权限不足

sudo chmod -R 777 /Applications/IntelliJ\ IDEA.app/Contents/plugins/maven/lib/maven3

mvn -v

```

本地创建项目

```
mvn archetype:generate

10: internal -> org.apache.maven.archetypes:maven-archetype-webapp (An archetype which contains a sample Maven Webapp project.)
Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains): 7: 10

选择 webapp

Define value for property 'groupId': mygroupId
Define value for property 'artifactId': myartifactId
Define value for property 'version' 1.0-SNAPSHOT: : 1.0
Define value for property 'package' mygroupId: : war

如果是一个大的 Maven 项目的一个 Module：

 <modules>
        <module>myartifactId</module>
 </modules>
```

# 示例

## 被调用的服务

- DemoBean

```
import java.io.Serializable;

public class DemoBean implements Serializable {
    private static final long serialVersionUID = 1204541279122561610L;
    private int code;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

```

- DemoService

```
public interface DemoService {
    DemoBean getDemo(String code, String msg);

    Integer getInt(Integer code);

    String getString(String msg);

    void doSomething();
}

```

- DemoServiceImply

```
public class DemoServiceImply {
    public DemoBean getDemo(Integer code, String msg) {
        DemoBean bean = new DemoBean();
        bean.setCode(code);
        bean.setMsg(msg);
        return bean;
    }

    public Integer getInt(Integer code) {
        return code;
    }

    public String getString(String msg) {
        return msg;
    }

    public void doSomething() {
        System.out.println("do something");
    }

}

```

## Rpc 服务器实现：基于 Tomcat7，使用 @WebServlet 来部署 Servlet

- RpcServer

```
import com.googlecode.jsonrpc4j.JsonRpcServer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/RpcServer")
public class RpcServer extends HttpServlet {
    private static final long serialVersionUID = 8451788270868979589L;
    private JsonRpcServer rpcServer;

    public RpcServer() {
        super();
        rpcServer = new JsonRpcServer(new DemoServiceImply(), DemoService.class);
    }

    @Override
    protected void service(HttpServletRequest request,
                           HttpServletResponse response) throws ServletException, IOException {
        rpcServer.handle(request, response);
    }
}

```

## 客户端远程调用服务示例

- JsonRpcTest

```
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class JsonRpcTest {
    static JsonRpcHttpClient client;

    public JsonRpcTest() {
    }

    public static void main(String[] args) throws Throwable {
        // 实例化请求地址，注意服务端web.xml中地址的配置
        try {
//            client = new JsonRpcHttpClient(new URL("http://127.0.0.1:8080/RpcServer"));
            client = new JsonRpcHttpClient(new URL("http://127.0.0.1:8400/RpcServer"));
            // 请求头中添加的信息
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Rpc-Type", "shop");
            // 添加到请求头中去
            client.setHeaders(headers);
            JsonRpcTest test = new JsonRpcTest();
            test.doSomething();
            DemoBean demo = test.getDemo(1, "哈");
            int code = test.getInt(2);
            String msg = test.getString("哈哈哈");
            // print
            System.out.println("===========================javabean");
            System.out.println(demo.getCode());
            System.out.println(demo.getMsg());
            System.out.println("===========================Integer");
            System.out.println(code);
            System.out.println("===========================String");
            System.out.println(msg);
            System.out.println("===========================end");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doSomething() throws Throwable {
        client.invoke("doSomething", null);
    }

    public DemoBean getDemo(int code, String msg) throws Throwable {
        String[] params = new String[]{String.valueOf(code), msg};
        DemoBean demo;
        demo = client.invoke("getDemo", params, DemoBean.class);
        return demo;
    }

    public int getInt(int code) throws Throwable {
        Integer[] codes = new Integer[]{code};
        return client.invoke("getInt", codes, Integer.class);
    }

    public String getString(String msg) throws Throwable {
        String[] msgs = new String[]{msg};
        return client.invoke("getString", msgs, String.class);
    }

}

```

## 不使用 Tomcat 传输，使用 Netty 传输

- HttpHandler

```
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @ClassName: HttpServerHandler
 * @Description: netty处理器
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public HttpHandlerImp handler = new HttpHandlerImp();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        handler.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg,
                      ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        handler.exceptionCaught(ctx, cause);
    }

    public static void writeJSON(ChannelHandlerContext ctx,
                                 HttpResponseStatus status, Object msg) {
    }

    public static void writeJSON(ChannelHandlerContext ctx, Object msg) {
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx,
                                   FullHttpRequest msg) throws Exception {
        handler.messageReceived(ctx, msg);
    }
}

```

- HttpHandlerImply

```
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

public class HttpHandlerImp {
    public void channelRead(final ChannelHandlerContext ctx, final Object msg)
            throws Exception {
        DefaultFullHttpRequest req = (DefaultFullHttpRequest) msg;
        String rpcType = req.headers().get("Rpc-Type");
        if (rpcType != null && rpcType.equals("shop")) {
            HttpServer.rpcServer.handle(new ByteBufInputStream(req.content()),
                    new ByteBufOutputStream(req.content()));
            System.out.println("req.getMethod(): "+req.getMethod());
            writeJSON(ctx, HttpResponseStatus.OK, req.content());
            ctx.flush();
        }
    }

    private static void writeJSON(ChannelHandlerContext ctx,
                                  HttpResponseStatus status, ByteBuf content) {
        if (ctx.channel().isWritable()) {
            FullHttpResponse msg;
            if (content != null) {
                msg = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                        content);
                msg.headers().set(HttpHeaders.Names.CONTENT_TYPE,
                        "application/json; charset=utf-8");
            } else {
                msg = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
            }
            if (msg.content() != null) {
                msg.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
                        msg.content().readableBytes());
            }
            // not keep-alive
            ctx.write(msg).addListener(ChannelFutureListener.CLOSE);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    }

    public void messageReceived(ChannelHandlerContext ctx, FullHttpRequest msg)
            throws Exception {

    }
}

```

- HttpServer

```
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import com.googlecode.jsonrpc4j.JsonRpcServer;

public class HttpServer {
	public static HttpServer inst;
	public static int port = 8400;
	private NioEventLoopGroup bossGroup = new NioEventLoopGroup();
	private NioEventLoopGroup workGroup = new NioEventLoopGroup();
	public static JsonRpcServer rpcServer;

	private HttpServer() {
	}

	public static HttpServer getInstance() {
		if (inst == null) {
			inst = new HttpServer();
			inst.initData();
		}
		return inst;
	}

	public void initData() {
		rpcServer = new JsonRpcServer(new DemoServiceImply(),
				DemoServiceImply.class);
	}

	public static void main(String[] args) {
		HttpServer.getInstance().start();
	}

	public void start() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workGroup);
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast("decoder", new HttpRequestDecoder());
				pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
				pipeline.addLast("encoder", new HttpResponseEncoder());
				pipeline.addLast("http-chunked", new ChunkedWriteHandler());
				pipeline.addLast("handler", new HttpHandler());
			}
		});
		System.out.println("端口" + port + "已绑定");
		bootstrap.bind(port);
	}

	public void shut() {
		workGroup.shutdownGracefully();
		workGroup.shutdownGracefully();
	}
}

```