import java.text.NumberFormat;
import java.util.Locale;

public class NumberFormatDemo {

    public static void main(String[] args) {
        double value = 987654.321;
        Locale cnLocale = new Locale("zh", "CN");
        Locale usLocale = new Locale("en", "US");
        Locale deLocale = new Locale("de", "DE");
        NumberFormat dNf = NumberFormat.getNumberInstance();
//        百分数
        NumberFormat pNf = NumberFormat.getPercentInstance();
        NumberFormat cnNf = NumberFormat.getNumberInstance(cnLocale);
        NumberFormat usNf = NumberFormat.getNumberInstance(usLocale);
        NumberFormat deNf = NumberFormat.getNumberInstance(deLocale);
        System.out.println("Default Percent Format:" + pNf.format(value));
        System.out.println("Default Number Format:" + dNf.format(value));
        System.out.println("China Number Format:" + cnNf.format(value));
        System.out.println("United Number Format:" + usNf.format(value));
//		德国使用逗号代表小数点，小数点分割长数字
        System.out.println("German Number Format:" + deNf.format(value));
        try {
            System.out.println(deNf.parse("3.14").doubleValue());
            System.out.println(dNf.parse("3.14F").doubleValue());
            System.out.println(dNf.parse("F3.14").doubleValue());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
