StreamAPI 能让代码更加简洁，极大地简化了集合框架的处理。

```
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

enum Status {
    OPEN, CLOSED
}

class Task {
    private final Status status;
    private final Integer points;

    Task(final Status status, final Integer points) {
        this.status = status;
        this.points = points;
    }

    public Integer getPoints() {
        return points;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("[%s, %d]", status, points);
    }
}

public class StreamAPIDemo {
    public static void main(String[] args) {
        Collection<Task> tasks = Arrays.asList(
                new Task(Status.OPEN, 5),
                new Task(Status.OPEN, 13),
                new Task(Status.CLOSED, 8));
        int sum = 0;
        for (Task t : tasks) {
            if (t.getStatus() == Status.OPEN) {
                sum += t.getPoints();
            }
        }
        System.out.println("for 循环统计状态为 Open 的任务总分为：" + sum);

        int totalPointsOfOpenTasks = tasks.stream()
                .filter(t -> t.getStatus() == Status.OPEN)
                .mapToInt(Task::getPoints).sum();

        System.out.println("Stream 流方式统计状态为 Open 的任务总分为："
                + totalPointsOfOpenTasks);

        int totalPoints = tasks.stream()
                .parallel()
                .map(Task::getPoints)
                .reduce(0, Integer::sum);

        System.out.println("所有任务总分为: " + totalPoints);

        Map<Status, List<Task>> map = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus));
        System.out.println(map);
    }
}

```