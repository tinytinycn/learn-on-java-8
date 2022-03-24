# Java 中的并发安全操作

## 使用 Concurrent 并发集合类

| interface | non-thread-safe          | thread-safe                              |
|-----------|--------------------------|------------------------------------------|
| List      | ArrayList                   | CopyOnWriteArrayList                     |
| Map       | HashMap                     | ConcurrentHashMap                        |
| Set       | HashSet / TreeSet           | CopyOnWriteArraySet                      |
| Queue     | ArrayDeque / LinkedList     | ArrayBlockingQueue / LinkedBlockingQueue |
| Deque     | ArrayDeque / LinkedList     | LinkedBlockingDeque                      |

## 使用 Atomic

提供了一组原子操作的封装类，它们位于java.util.concurrent.atomic包。Atomic类是通过无锁（lock-free）的方式实现的线程安全（thread-safe）访问。它的主要原理是利用了CAS：Compare and Set。

Java 5 引入了专用的原子变量类，例如 AtomicInteger、AtomicLong、AtomicReference 等。这些提供了原子性升级。这些快速、无锁的操作，它们是利用了现代处理器上可用的机器级原子性。

通过 CAS 编写 incrementAndGet() 方法的大致逻辑如下：

```text
class AtomicInteger{
    // 以原子方式将当前值加一，返回更新的值。
    public final int incrementAndGet() {
        return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
    }
}

class Unsafe{
    // 以原子方式将给定值添加到给定对象o在给定offset处的字段或数组元素的当前值
    // delta 是要添加的值
    public final int getAndAddInt(Object o, long offset, int delta) {
        int v;
        do {
            v = getIntVolatile(o, offset); // 拿到 o 对象内存位置 offset 的最新值 v
        } while (!compareAndSwapInt(o, offset, v, v + delta)); // CAS修改不成功返回false，循环条件为true，继续循环。 CAS修改成功才跳出循环
        return v;
    }
    
    // 如果当前持有expected ，则以原子方式将 Java 变量更新为x 。
    // 如果修改成功，返回true。
    public final native boolean compareAndSwapInt(Object o, long offset,
                                                  int expected,
                                                  int x);
}
```

### CAS 是什么？

CAS （CompareAndSwap）比较并替换。CAS 需要有三个操作数：内存地址V，旧的预期值A，即将更新的目标值B。
CAS 指令执行时，当切仅当内存地址V的值与预期值A相等时，才将内存地址的值修改为目标值B，否则什么都不做。
整个比较并替换操作是一个原子操作。

CAS虽然高效地解决了原子操作问题，但仍存在一下问题：

- 循环时间长开销大, 如果CAS失败，会一直进行尝试。如果CAS长时间一直不成功，可能会给CPU带来很大的开销。
- 只能保证一个共享变量的原子操作, 对多个共享变量操作时，循环CAS就无法保证操作的原子性，这个时候就可以用锁来保证原子性。
- ABA问题, CAS 使用流程如下：第一步，读取内存地址V的值A，第二步，根据A计算目标值B，第三步，通过CAS以原子操作方式修改成目标值B。如果在第一步到第三步中间，其他线程将目标值B修改成目标值A，将会导致CAS操作误认为"从来没改变过"，从而导致了ABA问题。
  
Java 并发包提供了一个带有标记的原子引用类`AtomicStampedReference`，它可以通过控制变量值的版本来保证CAS的正确性。
因此，在使用 CAS 前要考虑清楚 ABA 问题是否会影响程序并发的正确性，如果需要解决 ABA 问题，改用传统的互斥同步可能会比原子类更高效。

## 使用线程池

创建线程需要操作系统资源（线程资源，栈空间等），频繁创建和销毁大量线程需要消耗大量时间。
Java标准库提供了ExecutorService接口表示线程池, 可以复用一组线程，并对其进行管理。

- FixedThreadPool 线程数固定的线程池
- CachedThreadPool 线程数根据任务动态调整的线程池
- SingleThreadPool 仅单个线程的线程池
- ScheduledThreadPool 定期反复执行任务的线程池

## 使用 Future

1) 如果提交的任务实现Runnable接口，让线程池执行。但是runnable接口不能返回值。
2) Java 标准库提供 Callable接口，支持返回值。但是任务需要异步执行，如果获取结果呢？
3) Executor.submit()方法返回一个Future类型，Future代表一个未来的结果对象。

```text
ExecutorService executor = Executors.newFixedThreadPool(4); 
Callable<String> task = new Task();
Future<String> future = executor.submit(task); // 返回一个Future类型对象，代表未来结果
String result = future.get(); // 获取异步任务的结果是同步阻塞的，获取到结果之后才能执行下面的代码
System.out.println(result);
```

## 使用 CompletableFuture

使用Future获得异步执行结果时，要么调用阻塞方法get()，要么轮询看isDone()是否为true。 两种方法都不是十分友好，同时主线程也会被迫处于等待状态。

Java 8 引入 CompletableFuture, 针对 Future进行改进，支持传入回调对象，当异步任务完成或发生异常时，自动调用回调对象的方法。

CompletableFuture 的优点：

- 异步任务结束, 会自动回调某个对象的方法(例如，thenAccept()处理正常结果)
- 异步任务异常，会自动回调某个对象的方法(例如，exceptional()处理异常结果)
- 主线程设置好回调，不用关心异步任务的执行过程。
- 可以串行执行多个异步任务，也可以并行执行多个异步任务。(thenApplyAsync()串行任务，anyOf()/allOf()并行任务)

例子一：自动回调

```java
public class CompletableFutureDemo {

    static Double fetchResult() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Math.random() < 0.3) {
            throw new RuntimeException("fetch result failed.");
        }
        return 5 + Math.random() * 43;
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建异步任务
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(CompletableFutureDemo::fetchResult);
        // 成功，执行回调
        cf.thenAccept(result -> {
            System.out.println("结果是：" + result);
        });
        // 异常，执行回调
        cf.exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
        // 主线程需要等待，否则CompletableFuture默认线程池会立刻关闭，无法测试
        TimeUnit.MILLISECONDS.sleep(200);
    }
}
```

例子二：串行执行

```java
public class CompletableFutureDemo2 {

    static String fetchResult(String name) {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return name;
    }

    static Double fetchResult2(String name) {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 5 + Math.random() * 43;
    }

    public static void main(String[] args) throws InterruptedException {
        // 第一个异步任务
        CompletableFuture<String> cfa = CompletableFuture.supplyAsync(() -> {
            return fetchResult("A");
        });
        // 第二个异步任务
        CompletableFuture<Double> cfb = cfa.thenApplyAsync(name -> {
            return fetchResult2(name);
        });
        cfb.thenAccept(result -> {
            System.out.println("result = " + result);
        });
        TimeUnit.MILLISECONDS.sleep(2000);
    }

}
```

例子三：并行执行

```java
public class CompletableFutureDemo3 {

    static String fetchResult(String name, String url) {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return name;
    }

    static Double fetchResult2(String name, String url) {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 5 + Math.random() * 43;
    }

    public static void main(String[] args) throws InterruptedException {
        // 异步执行两个任务
        CompletableFuture<String> cfa = CompletableFuture.supplyAsync(() -> {
            return fetchResult("A", "A.com");
        });
        CompletableFuture<String> cfb = CompletableFuture.supplyAsync(() -> {
            return fetchResult("B", "B.com");
        });
        // anyOf 合并为一个新的cf
        CompletableFuture<Object> cfc = CompletableFuture.anyOf(cfa, cfb);
        CompletableFuture<Double> cfc1 = cfc.thenApplyAsync(name -> {
            return fetchResult2((String) name, "C.com");
        });
        CompletableFuture<Double> cfc2 = cfc.thenApplyAsync(name -> {
            return fetchResult2((String) name, "D.com");
        });
        // anyOf 合并为一个新的cf
        CompletableFuture<Object> cfd = CompletableFuture.anyOf(cfc1, cfc2);
        cfd.thenAccept(res -> {
            System.out.println("res = " + res);
        });
        TimeUnit.MILLISECONDS.sleep(2000);
    }
}
```