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
