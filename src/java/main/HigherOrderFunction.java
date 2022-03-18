import java.util.function.Function;

// 定义一个专用接口
interface FuncSS extends Function<String, String> {
}

class One{}
class Two{}

public class HigherOrderFunction {
    // produce() 就是一个高阶函数
    static FuncSS produce() {
        return s -> s.toLowerCase(); // 返回lambda函数
    }

    // consume() 也是一个高阶函数
    static Two consume(Function<One, Two> oneTwo) {
        return oneTwo.apply(new One());
    }

    public static void main(String[] args) {
        // 测试生产
        FuncSS f = produce();
        System.out.println(f.apply("higher..."));

        // 测试消费
        Two t = consume(a -> new Two());
        System.out.println("t: "+ t);
    }
}
