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
