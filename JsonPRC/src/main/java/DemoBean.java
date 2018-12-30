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
