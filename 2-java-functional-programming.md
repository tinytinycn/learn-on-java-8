# 函数式编程

## 困境

通常，传递给方法的参数数据不同，产生方法的行为之后结果会不同。但是，如果需要在调用方法时，为方法提供不同的行为定义，该怎么办呢？
结论是只要将代码传递给方法，就可以控制方法的行为。下面我们对比一下传统形式、Java8方法引用、lambda表达式三种情况的处理效果。

```java
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

```

## 理解lambda表达式

lambda表达式是使用最小可能语法便携的"函数定义"

- lambda表达式产生"函数"， 而不是类。虽然在 JVM（Java Virtual Machine，Java 虚拟机）上，一切都是类，但是幕后有各种操作执行让 Lambda 看起来像函数 —— 作为程序员，你可以高兴地假装它们“就是函数”。
- lambda表达式语法尽可能少

## 理解方法引用

方法引用组成：类名或对象名，后面跟上`::`，然后跟上方法名称。例如：ClassA::fa

```java
interface Callable {
    void call(String s);
}

// [1] Describe类的非静态方法show签名（参数类型、返回类型）符合Callable.call()的签名
class Describe {
    void show(String msg) {
        System.out.println(msg);
    }
}

public class MethodReference {
    // [2] MethodReference类的静态方法hello签名（参数类型、返回类型）符合Callable.call()的签名
    static void hello(String name) {
        System.out.println("hello, " + name);
    }

    static class Description {
        String about;

        Description(String desc) {
            about = desc;
        }

        // [3] Description 静态内部类的非静态方法help签名（参数类型、返回类型）符合Callable.call()的签名
        void help(String msg) {
            System.out.println("about " + msg);
        }
    }

    static class Helper {
        // Helper 静态内部类的静态方法 assist签名（参数类型、返回类型）符合Callable.call()的签名
        static void assist(String msg) {
            System.out.println(msg);
        }
    }

    // 测试
    public static void main(String[] args) {
        Describe describe = new Describe();
        Callable c = describe::show; // [1]
        c.call("call()"); // 通过call()方法，实际调用show()方法，因为Java将call() 映射到 show()
        c = MethodReference::hello; // [2]
        c.call("hello()");
        c = new Description("valuable")::help; // [3] 对已实例化对象的方法的引用，有时称为绑定方法引用。
        c.call("help()");
        c = Helper::assist; // [4]
        c.call("assist()");
    }
}
```

还有一种特殊的未绑定的方法引用，未绑定的方法引用是指没有关联对象的普通（非静态）方法。 使用未绑定的引用时，我们必须先提供对象：

```java
class X{
    String f() {
        return "X::f()";
    }
}

interface MakeString {
    String make();
}

interface TransformX{
    String transform(X x);
}

public class UnboundMethodReference {
    public static void main(String[] args) {
        // MakeString ms = X::f;  // 编译出错, 其实还需要另一个隐藏参数参与：我们的老朋友 this。 你不能在没有 X 对象的前提下调用 f()。 因此，X :: f 表示未绑定的方法引用，因为它尚未“绑定”到对象。
        TransformX sp = X::f;
        X x = new X();
        System.out.println(sp.transform(x));
        System.out.println(x.f());
    }
}
```

要解决这个问题，我们需要一个 X 对象，因此我们的接口实际上需要一个额外的参数，正如在 TransformX 中看到的那样。 如果将 X :: f 赋值给 TransformX，在 Java 中是允许的。我们必须做第二个心理调整——使用未绑定的引用时，函数式方法的签名（接口中的单个方法）不再与方法引用的签名完全匹配。 原因是：你需要一个对象来调用方法。

我拿到未绑定的方法引用，并且调用它的transform()方法，将一个X类的对象传递给它，最后使得 x.f() 以某种方式被调用。Java知道它必须拿第一个参数，该参数实际就是this 对象，然后对此调用方法。

```java
class This {
    void two(int i, double d) {
    }

    void three(int i, double d, String s) {
    }

    void four(int i, double d, String s, char c) {
    }
}

interface TwoArgs {
    void call2(This aThis, int i, double d);
}

interface ThreeArgs {
    void call3(This aThis, int i, double d, String s);
}

interface FourArgs {
    void call4(This aThis, int i, double d, String s, char c);
}

public class MultiUnbound {
    public static void main(String[] args) {
        TwoArgs twoArgs = This::two;
        ThreeArgs threeArgs = This::three;
        ;
        FourArgs fourArgs = This::four;
        // 此时还没有This对象存在，上述都是未绑定的方法引用。
        This aThis = new This();
        // 此时已经有了This对象, 传递这个对象，使得调用 callX()方法后，最终调用了 This.xxx() 里面的方法。
        twoArgs.call2(aThis, 11, 3.14);
        threeArgs.call3(aThis, 11, 3.14, "three");
        fourArgs.call4(aThis, 11, 3.14, "four", 'Z');
    }
}
```

## 理解函数式接口 functional interface

方法引用和 Lambda 表达式都必须被赋值，同时赋值需要类型信息才能使编译器保证类型的正确性。

Java 8 引入了 java.util.function 包。它包含一组接口，这些接口是 Lambda 表达式和方法引用的目标类型。 每个接口只包含一个抽象方法，称为 函数式方法 。

Java 8 允许我们将函数赋值给接口，这样的语法更加简单漂亮。

java.util.function 创建一组完整的目标接口，基本命名准则：

- 如果只处理对象而非基本类型，则名称为 Function,Consumer,Predicate等。参数类型通过泛型添加。
- 如果接收的参数是基本类型，则名称为 LongConsumer,DoubleFunction,IntPredicate等。
- 如果返回基本类型，则名称为 ToLongFunction,IntToLongFunction等。
- 如果返回类型与参数类型相同，则名称为 Operator 单个参数 UnaryOperator, 两个参数 BinaryOperator。
- 如果接收参数并返回一个布尔值，则名称为 Predicate。
- 如果接收两个参数类型不同，则名称中有一个 Bi

在使用函数接口时，名称无关紧要——只要参数类型和返回类型相同。 Java 会将你的方法映射到接口方法。

## 理解高阶函数

高阶函数只是一个消费或产生函数的"函数"。

```java
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

```

## 理解闭包 Closure

Java 8 提供了有限并合理的闭包支持。如何理解闭包呢？如果在Java中使用过程中，返回一个lambda函数，而Java语言能够自动地让lambda函数，使用其函数作用域之外的变量，则称作"支持闭包"或者称作"词法定界lexically
scoped"。

```java
import java.util.function.IntSupplier;

// 作为闭包的lambda函数，捕获实例变量
class ClosureTest {
    int i = 0; // 实际上，垃圾收集器几乎肯定会保留以这种方式被绑定到现存函数的对象。当然，如果你对同一个对象多次调用 makeFun() ，你最终会得到多个函数，它们共享 i 的存储空间

    IntSupplier makeFun(int x) {
        return () -> x + i;
    }
}

class Test {
    public static void main(String[] args) {
        ClosureTest closureTest = new ClosureTest();
        IntSupplier f1 = closureTest.makeFun(0);
        IntSupplier f2 = closureTest.makeFun(0);
        IntSupplier f3 = closureTest.makeFun(0);
        System.out.println(f1.getAsInt());
        System.out.println(f2.getAsInt());
        System.out.println(f3.getAsInt());
    }
}
```

上述是lambda函数访问其函数作用域之外的实例变量，我们再看看访问其函数作用域之外的局部变量，这一情况。

```java
// 作为闭包的lambda函数，捕获局部变量
class ClosureTest {
    IntSupplier makeFun(int x) {
        int i = 0; // 在一般情况下，当 makeFun() 完成时 i 就消失。但是，这里 i,x 变量被 IntSupplier lambda函数 "关住了"，makeFun() 执行完毕后，调用返回的函数时，i,x 仍然有效（如何理解仍然有效？）。
        // int i ++; // 编译报错！！！
        return () -> x + i;
    }
}

// 作为闭包的内部类，捕获局部变量
class ClosureTest {
    IntSupplier makeFun(int x) {
        int i = 0;
        // i ++; 编译报错
        // x ++; 编译报错
        return new IntSupplier() {
            @Override
            public int getAsInt() {
                return x + i; // 实际上只要有内部类，就会有闭包。
            }
        };
    }
}
```

为什么lambda表达式访问局部变量会有限制？

1.

实例变量存储在堆区，是线程共享的。局部变量存储在栈上。由于对闭包的支持，需要lambda表达式可以直接访问局部变量，如果lambda表达式所在的线程A和分配局部变量的线程B不是同一个线程，线程B将局部变量回收后，线程A的lambda表达式可能会访问该局部变量。为了避免这个问题，Java语言中，lambda表达式访问自由局部变量实际上是访问它的副本，而不是原始局部变量。为了保证局部变量和lambda表达式中访问的副本变量的数据一致性，必须要求语法上有final的限制。

2. Java不鼓励使用改变lambda函数作用域之外的变量的编程模式。

## 理解柯里化 Currying

柯里化: 将一个多参数的函数，转换成一系列单参数的函数。

```java
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
```

柯里化有点类似数学中的 f(x,y) = f1(x)f2(y)

# 参考

- [onJava8中文版#函数式编程](https://github.com/prettykernel/OnJava8/blob/main/13-Functional-Programming.md)