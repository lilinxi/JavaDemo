java.util.Base64 工具类有 Basic、URL和 MIME 三种编码器和解码器。

```
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Demo {

	public static void main(String[] args) {
		String text = "Base64 class in Java 8!";

		String encoded = Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
		System.out.println(encoded);

		String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
		System.out.println(decoded);
	}

}

```