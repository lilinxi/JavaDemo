import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatDemo {

	public static void print(Date date, Locale locale) {
		DateFormat df1 = DateFormat.getDateTimeInstance(DateFormat.FULL,
				DateFormat.FULL, locale);
		DateFormat df2 = DateFormat.getDateTimeInstance(DateFormat.LONG,
				DateFormat.LONG, locale);
		DateFormat df3 = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,
				DateFormat.DEFAULT, locale);
		DateFormat df4 = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT, locale);
		System.out.println(df1.format(date));
		System.out.println(df2.format(date));
		System.out.println(df3.format(date));
		System.out.println(df4.format(date));
	}

	public static void main(String[] args) {
		Date now = new Date();
		Locale cnLocale = new Locale("zh", "CN");
		Locale usLocale = new Locale("en", "US");
		System.out.println("中文格式");
		print(now, cnLocale);
		System.out.println("英文格式");
		print(now, usLocale);
	}

}
