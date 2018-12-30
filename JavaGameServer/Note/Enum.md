# 枚举类的创建

```
public enum Season {
    SPRING("春"), SUMMER("夏"), FALL("秋"), WINTER("冬");

    private String name;

    Season(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}

```

# 枚举类的使用

```
public class SeasonDemo {
	public static void main(String[] args) {
		System.out.println("Season 枚举类的所有实例值");
		for (Season s : Season.values()) {
			System.out.println(s);
		}
		System.out.println("---------------------");
		Season se = Season.valueOf("SUMMER");
		System.out.println(se);
		judge(se);
		System.out.println("---------------------");
		Season season = Season.WINTER;
		System.out.println(season);
		judge(season);
	}

	private static void judge(Season season) {
		switch (season) {
		case SPRING:
			System.out.println("春暖花开");
			break; 
		case SUMMER:
			System.out.println("夏日炎炎");
			break;
		case FALL:
			System.out.println("秋高气爽");
			break;
		case WINTER:
			System.out.println("冬日暖阳");
			break;
		}
	}

}

```

# 枚举类的方法

```
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

```