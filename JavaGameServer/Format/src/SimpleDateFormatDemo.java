import java.text.SimpleDateFormat;
import java.util.Date;

public class SimpleDateFormatDemo {

	public static void main(String[] args) {
		Date now = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf1.format(now));
		SimpleDateFormat sdf2 = 
		new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
		System.out.println(sdf2.format(now));
		SimpleDateFormat sdf3 = 
		new SimpleDateFormat("现在是 yyyy年 MM 月 dd 日，是今年的第 D 天");
		System.out.println(sdf3.format(now));
	}

}
