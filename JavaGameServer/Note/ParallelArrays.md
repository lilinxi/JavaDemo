Java 8 中新增了数组的并行操作，可以使用 Lambda 表达式对数组进行排序、过滤和分组等操作，充分发挥了目前操作系统多核架构的优势，在速度、性能上能够提高数倍。

```
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ParallelArraysDemo {

    public static void main(String[] args) {
        Integer[] arrayOfLong = new Integer[20000];
//        为数组赋初值
        Arrays.parallelSetAll(arrayOfLong, index -> ThreadLocalRandom.current().nextInt(1000000));

        System.out.println("输出未排序前10个数");
        Arrays.stream(arrayOfLong).limit(10).forEach(i -> System.out.print(i + " "));
        System.out.println();

        Arrays.parallelSort(arrayOfLong);
        System.out.println("输出排序后前10个数");
        Arrays.stream(arrayOfLong).limit(10).forEach(i -> System.out.print(i + " "));
        System.out.println();

        List<Integer> list = Arrays.asList(arrayOfLong);
        Map<Boolean, List<Integer>> groupByOdd = list.parallelStream().collect(Collectors.groupingBy(x -> x % 2 == 0));
        System.out.println("输出前10个奇数");
        groupByOdd.get(false).parallelStream().limit(10).forEach(i -> System.out.print(i + " "));
        System.out.println();
        System.out.println("输出前10个偶数");
        groupByOdd.get(true).parallelStream().limit(10).forEach(i -> System.out.print(i + " "));
        System.out.println();

        System.out.println("输出前10个5的倍数");
        list.parallelStream().filter(x -> x % 5 == 0).limit(10).forEach(i -> System.out.print(i + " "));
        System.out.println();
    }
}

```