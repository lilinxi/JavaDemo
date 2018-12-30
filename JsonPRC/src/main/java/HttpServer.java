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
