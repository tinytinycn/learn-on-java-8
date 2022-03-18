// [1] 构造器形式

// [2] 传统形式，通过匿名内部类
interface Strategy {
    String approach(String msg);
}

class Soft implements Strategy {
    @Override
    public String approach(String msg) {
        return msg.toLowerCase() + "?";
    }
}

// [3] lambda表达式的形式

// [4] 方法引用的形式
class Unrelated {
    static String twice(String msg) {
        return msg + " " + msg;
    }
}

public class StrategyTest {
    Strategy strategy;// 定义策略
    String msg;// 定义行为结果

    StrategyTest(String msg) {
        strategy = new Soft(); // [1] 设置默认策略
        this.msg = msg;
    }

    // 获取行为结果
    void communicate() {
        System.out.println(strategy.approach(msg));
    }

    // [2] [3] [4] 变更策略
    void changeStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public static void main(String[] args) {
        StrategyTest test = new StrategyTest("Hello there");
        test.communicate();
        // [2] 传统形式，通过匿名内部类传递"变更的行为"代码逻辑，进行调整communicate的行为
        test.changeStrategy(new Strategy() {
            @Override
            public String approach(String msg) {
                return msg.toUpperCase() + "!";
            }
        });
        test.communicate();
        // [3] lambda表达式形式
        test.changeStrategy(msg -> msg.substring(0, 5));
        test.communicate();
        // [4] java 8 方法引用
        test.changeStrategy(Unrelated::twice);
        test.communicate();
    }
}
