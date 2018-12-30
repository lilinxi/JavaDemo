public class EnumMethodDemo {

    public static void main(String[] args) {
        System.out.println("Season 枚举类的所有实例值及其顺序号");
        for (Season s : Season.values()) {
            System.out.println(s + "--" + s.ordinal());
        }
        System.out.println("---------------------");
        Season s1, s2, s3, s4;
        s1 = Season.SPRING;
        s2 = Season.SUMMER;
        s3 = Season.FALL;
        s4 = Enum.valueOf(Season.class, "FALL");
        if (s1.compareTo(s2) < 0) {
            System.out.println(s1 + "在" + s2 + "֮之前");
        }
        if (s3.equals(s4)) {
            System.out.println(s3 + "等于" + s4);
        }
        if (s3 == s4) {
            System.out.println(s3 + "等于" + s4);
        }
    }
}
