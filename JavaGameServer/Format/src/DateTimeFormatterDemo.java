import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class DateTimeFormatterDemo {

    public static void formateMethod() {
        DateTimeFormatter[] formatters = new DateTimeFormatter[]{
//                常量创建
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ISO_LOCAL_TIME,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
//                本地化创建
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL,
                        FormatStyle.MEDIUM),
                DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM),
//                模式字符串创建
//				三个 MMM 为12月，两个 MM 为12
                DateTimeFormatter.ofPattern("Gyyyy MMM dd HH:mm:ss")};
        LocalDateTime date = LocalDateTime.now();
        for (int i = 0; i < formatters.length; i++) {
            System.out.println(date.format(formatters[i]));
            System.out.println(formatters[i].format(date));
        }
    }

    public static void parseMethod() {
        String str = "2018==12==02 01时06分09秒";
        DateTimeFormatter fomatter = DateTimeFormatter
                .ofPattern("yyyy==MM==dd HH时mm分ss秒");
        LocalDateTime dt = LocalDateTime.parse(str, fomatter);
        System.out.println(dt);
    }

    public static void main(String[] args) {
        formateMethod();
        System.out.println("-----------------");
		parseMethod();
    }

}
