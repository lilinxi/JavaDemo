import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleDemo {

    public static void main(String[] args) {
//        使用默认的语言和国家
        ResourceBundle resb1 = ResourceBundle.getBundle("myres");
        System.out.println(resb1.getString("title"));
        System.out.println(resb1.getString("name"));
        System.out.println("-----------------------------");

        Locale localeEn = new Locale("en", "US");
        ResourceBundle resb2 = ResourceBundle.getBundle("myres", localeEn);
        System.out.println(resb2.getString("title"));
        System.out.println(resb2.getString("name"));
        System.out.println("-----------------------------");

        Locale localeZh = new Locale("zh", "CN");
        ResourceBundle resb3 = ResourceBundle.getBundle("myres", localeZh);
        System.out.println(resb1.getString("title"));
        System.out.println(resb1.getString("name"));
        System.out.println("-----------------------------");
    }

}
