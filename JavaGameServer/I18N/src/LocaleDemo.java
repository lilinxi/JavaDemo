import java.util.Locale;

public class LocaleDemo {

    public static void main(String[] args) {
        Locale[] localeList = Locale.getAvailableLocales();
        for (int i = 0; i < localeList.length; i++) {
            System.out.println(localeList[i].getDisplayCountry()
                    + "=" + localeList[i].getCountry() + " "
                    + localeList[i].getDisplayLanguage()
                    + "=" + localeList[i].getLanguage());
        }
        Locale locale = Locale.getDefault();
        System.out.println("Language        : " + locale.getLanguage());
        System.out.println("Country         : " + locale.getCountry());
        System.out.println("DisplayLanguage : " + locale.getDisplayLanguage());
        System.out.println("DisplayCountry  : " + locale.getDisplayCountry());
        System.out.println("locale : " + locale);
        // 重新设置默认的 Locale 信息
        Locale newLocale = new Locale("en", "US");
        Locale.setDefault(newLocale);
        locale = Locale.getDefault();
        System.out.println("Language        : " + locale.getLanguage());
        System.out.println("Country         : " + locale.getCountry());
        System.out.println("DisplayLanguage : " + locale.getDisplayLanguage());
        System.out.println("DisplayCountry  : " + locale.getDisplayCountry());
        System.out.println("locale : " + locale);
    }

}
