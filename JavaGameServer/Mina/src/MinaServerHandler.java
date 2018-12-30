import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public class MinaServerHandler extends IoHandlerAdapter {
    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        System.out.println("服务端收到的消息：" + message.toString());
        session.write("服务端发送消息 " + message);
        session.write("msg from server");
        session.write("quit");
//        session.close(true);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        if (session.isConnected()) {
            session.close(true);
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) {
        System.out.println("服务端发送的消息：" + message.toString());
    }

    @Override
    public void sessionClosed(IoSession session) {
        System.out.println("sessionClosed");
    }

    @Override
    public void sessionCreated(IoSession session) {
        session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30000);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        session.close(true);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
    }
}