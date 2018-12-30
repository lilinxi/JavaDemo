import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;


/**
 * MessageFormat 的 Pattern：{n,[,formatType][,formatStyle]}
 *
 * Type:    Style
 *      number  integer,currency,percent,#.##
 *      date    full,long,medium,short
 *      time    full,long,medium,short
 */
public class MessageFormatDemo {

    /**
     * 定义消息格式化方法：msgFormat()
     *
     * @param pattern   模式字符串
     * @param locale    语言环境
     * @param msgParams 占位符参数
     */
    public static void msgFormat(String pattern, Locale locale,
                                 Object[] msgParams) {
        MessageFormat formatter = new MessageFormat(pattern);
        formatter.setLocale(locale);
        System.out.println(formatter.format(msgParams));
    }

    public static void main(String[] args) {
        String pattern1 = "{0}，你好！你在{1}访问本系统！";
        Locale locale1 = Locale.getDefault();
        System.out.println(locale1.getCountry());
        Object[] msgParams1 = {"用户名", new Date()};
        msgFormat(pattern1, locale1, msgParams1);
        String pattern2 = "{0}，你好！你在{1,date,long}访问本系统！";
        msgFormat(pattern2, locale1, msgParams1);
        System.out.println("--------------------------------------");
        Locale locale2 = new Locale("en", "US");
        System.out.println(locale2.getCountry());
        Object[] msgParams2 = {"Username", new Date()};
        msgFormat(pattern1, locale2, msgParams2);
        msgFormat(pattern2, locale2, msgParams2);
    }

}
