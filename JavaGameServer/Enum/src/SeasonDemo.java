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
