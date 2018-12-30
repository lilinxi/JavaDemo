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
