import com.alibaba.fastjson.JSON;

public class FastjsonDemo {
    public static void main(String[] args) {
        String jsonString;
        // Java序列化为Json
        JsonObject obj = new JsonObject();
        obj.setId(1l);
        obj.setOrderId("2018_12_12");
        JsonSubObject subObject = new JsonSubObject();
        subObject.setCode(1001);
        subObject.setMsg("message");
        obj.setSubObject(subObject);
//        JSON 对象的 toJSONString or parseObject
        jsonString = JSON.toJSONString(obj);
        System.out.println("json string:");
        System.out.println(jsonString);
        // Json反序列化为Java
        obj = JSON.parseObject(jsonString, JsonObject.class);
        System.out.println("Java Object:");
        System.out.println(obj);
        System.out.println("id:" + obj.getId());
        System.out.println("orderId:" + obj.getOrderId());
        System.out.println("sub code:" + obj.getSubObject().getCode());
        System.out.println("sub msg:" + obj.getSubObject().getMsg());
    }

}
