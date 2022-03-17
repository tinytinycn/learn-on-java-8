import java.util.function.Function;

public class CurringTest {
    // 未柯里化多参数函数
    static String uncurried(String a, String b) {
        return a + b;
    }

    public static void main(String[] args) {
        // 定义一个柯里化的函数
        Function<String, Function<String, String>> sum = a -> b -> a + b;
        // Function<T, R> 泛型接口
        // 如果使用 sum Function<T, R>对象的 R apply(T t); 那么，意味着输入T，返回R。
        // 定义的一个Function<String, Function<String, String>> 接口对象 sum, 输入String (a), 返回一个 Function<String, String> (b->a+b）
        Function<String, String> hi = sum.apply("hi,");
        String res = hi.apply("java");
        System.out.println("柯里化结果 = " + res); // hi,java
        // 部分应用
        Function<String, String> hup = sum.apply("hup,");
        System.out.println(hup.apply("ho")); // hup,ho
        System.out.println(hup.apply("hey")); // hup,hey
    }
}
