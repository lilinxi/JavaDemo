package server;

import java.util.List;
import java.util.Map;


public interface FooService {
    String hello(String name);

    int helloInt(int number1);

    double helloDouble(double number2);

    List<String> helloList(List<String> list);

    Map<String, List<String>> helloMap(Map<String, List<String>> map);

    DemoBean helloJavabean(DemoBean bean);
}
