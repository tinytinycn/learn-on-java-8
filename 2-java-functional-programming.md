# 函数式编程

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

1. 实例变量存储在堆区，是线程共享的。局部变量存储在栈上。由于对闭包的支持，需要lambda表达式可以直接访问局部变量，如果lambda表达式所在的线程A和分配局部变量的线程B不是同一个线程，线程B将局部变量回收后，线程A的lambda表达式可能会访问该局部变量。为了避免这个问题，Java语言中，lambda表达式访问自由局部变量实际上是访问它的副本，而不是原始局部变量。为了保证局部变量和lambda表达式中访问的副本变量的数据一致性，必须要求语法上有final的限制。
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