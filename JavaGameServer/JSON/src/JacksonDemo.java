import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JacksonDemo {

    public static void main(String[] args) {
        String jsonString = null;
        // Java序列化为Json
        try {
            // 创建一个Java对象
//            ObjectMapper 的 writeValueAsString or readValue
            Map jsonMap = new HashMap();
            ObjectMapper mapper = new ObjectMapper();
            jsonMap.put("param1", "value1");
            jsonMap.put("param2", "value2");
            JsonObject obj = new JsonObject();
            obj.setId(1l);
            obj.setOrderId("2018_12_12");
            JsonSubObject subObject = new JsonSubObject();
            subObject.setCode(1001);
            subObject.setMsg("message");
            obj.setSubObject(subObject);
            // 转化为Json字符串
            jsonMap.put("param3", mapper.writeValueAsString(obj));
            jsonString = mapper.writeValueAsString(jsonMap);
            System.out.println("Json String:");
            System.out.println(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Json反序列化为Java
        try {
            ObjectMapper mapper2 = new ObjectMapper();
            Map jsonMap = mapper2.readValue(jsonString, Map.class);
            System.out.println("Java Object:");
            System.out.println(jsonMap);
            System.out.println("Json Node:");
            JsonNode root = mapper2.readTree(jsonString);
//            可以在不知道JSON字符串所属对象类型的情况下，对JSON字符串中的某些属性进行遍历和修改，比如，设置或查询一些报文头字段。
            JsonNode node = root.findValue("param3");
            System.out.println(node);
//            通过强制转换为ObjectNode，就可以对当前节点进行修改
            ((ObjectNode) root).put("pa", "v1");
            System.out.println(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
