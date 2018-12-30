import com.google.gson.Gson;
import org.bson.Document;

/**
 * Document 和 JavaBean 之间的相互转换，中间借助 Gson 作为两者转换的媒介
 *
 * @author ilala
 */
public class DocumentUtil {
    private static Gson gson = new Gson();
    /**
     * 把实体bean对象转换成Document
     */
    public static <T> Document bean2Document(T bean) {
        if (bean == null) {
            return null;
        }
        // 使用Gson转换javabean为json字符串
        String json = new Gson().toJson(bean);
        // 使用Mongo提供的Json工具类转为Document对象
        return Document.parse(json);
    }

    /**
     * 把Document转换成bean对象
     */
    public static <T> T document2Bean(Document document, Class<T> clz) {
        if (document == null) {
            return null;
        }
        // 使用Gson把Document对象转化为json字符串
        String json = gson.toJson(document);
        // 使用Gson把Json字符串转换为Javabean
        T obj = gson.fromJson(json, clz);
        return obj;
    }

}
