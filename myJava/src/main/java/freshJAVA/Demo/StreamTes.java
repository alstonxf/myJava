package freshJAVA.Demo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StreamTes {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList(
                "abc", "", "bc", "12345",
                "efg", "abcd","", "jkl");
        List<Integer> lengths = strings
                .stream()
                .filter(string -> !string.isEmpty())
                .map(s -> s.length())
                .collect(Collectors.toList());
        lengths.forEach((s) -> System.out.println(s));

        System.out.println("dabadfd".startsWith("daba"));
    }
}


