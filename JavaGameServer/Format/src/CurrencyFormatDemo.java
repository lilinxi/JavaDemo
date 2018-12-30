import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatDemo {

    public static void main(String[] args) {
        double value = 987654.321;
        Locale cnLocale = new Locale("zh", "CN");
        Locale usLocale = new Locale("en", "US");
        Locale deLocale = new Locale("de", "DE");

        NumberFormat cnNf = NumberFormat.getCurrencyInstance(cnLocale);
        NumberFormat usNf = NumberFormat.getCurrencyInstance(usLocale);
        NumberFormat deNf = NumberFormat.getCurrencyInstance(deLocale);
        System.out.println("China Currency Format:" + cnNf.format(value));
        System.out.println("United Currency Format:" + usNf.format(value));
        System.out.println("German Currency Format:" + deNf.format(value));
    }

}
