# Java 多线程

1. 启动线程

```java
public class ThreadsDemo1 {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + "@Hello");
        });
        System.out.println(Thread.currentThread().getName() + "@start");
        t.start();
//        t.join();// 等待执行结束, 才继续执行下一行代码
        System.out.println(Thread.currentThread().getName() + "@end");
    }
}
```

2. 中断线程

- 其他线程对目标线程调用interrupt()方法
- 目标线程需要检测自身状态是否为interrupt状态

例子一：

```java
class MyThread3 extends Thread {
    @Override
    public void run() {
        int n = 0;
        while (!isInterrupted()) { // [2] 目标线程需要检测自身状态是否为interrupt状态
            n++;
            System.out.println(n + " hello");
        }
    }
}

public class ThreadsDemo3 {
    public static void main(String[] args) throws InterruptedException {
        MyThread3 t = new MyThread3();
        t.start();
        TimeUnit.MILLISECONDS.sleep(1);
        t.interrupt(); // [1] 其他线程对目标线程调用interrupt()方法
    }
}
```

例子二：

```java
class MyThread extends Thread {

    // 线程间共享变量需要使用volatile关键字标记，确保每个线程都能读取到更新后的变量值。
    // 每次访问变量时，总是获取主内存的最新值；
    // 每次修改变量后，立刻回写到主内存。
    public volatile boolean running = true;

    @Override
    public void run() {
        int n = 0;
        while (running) { // [2] 目标线程检测自身状态
            n++;
            System.out.println("echo :" + n);
        }
        System.out.println("end!");
    }
}

public class ThreadsDemo2 {
    public static void main(String[] args) throws InterruptedException {
        MyThread t = new MyThread();
        t.start();
        TimeUnit.MILLISECONDS.sleep(1);
        t.running = false; // [1] 标志位置为false
    }
}
```

3. 守护线程

- jvm启动main线程，main线程可以启动其他线程。当所有线程执行结束，jvm退出，进程结束。
- 如果一个线程没有结束，那么jvm进程不会退出。

守护线程是指"为其他线程服务"的线程。

- 在JVM中，所有非守护线程都执行结束后，无论是没有守护线程存在，JVM都会自动退出，结束进程。
- JVM退出时，不关心守护线程是否任务结束。（即守护线程随着JVM退出而结束）

```java
class MyThread4 extends Thread {
    @Override
    public void run() {
        while (true) {
            System.out.println(LocalDate.now());
            try {
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

public class ThreadsDemo4 {
    public static void main(String[] args) throws InterruptedException {
        MyThread4 t = new MyThread4();
        t.setDaemon(true); // 在调用start()方法前，调用setDaemon(true)把该线程标记为守护线程
        t.start();
        System.out.println("start daemon thread");
        TimeUnit.MILLISECONDS.sleep(2000);
        System.out.println("end");
    }
}
```

4. 线程同步

多个线程同时运行时，线程的调度由操作系统决定，程序本身无法决定。一个线程可能在任何指令处被操作系统暂停，然后过一段时候继续执行。

多线程同时读写共享变量，会出现数据不一致问题。需要进行同步操作，来防止。

例子一：

```java
class Counter {
    public static int count = 0; // 共享变量
    public static final Object lock = new Object();// 锁对象
}

class IncrThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            synchronized (Counter.lock) {
                Counter.count += 1;
            }
        }
    }
}

class DecrThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            synchronized (Counter.lock) {
                Counter.count -= 1;
            }
        }
    }
}

public class ThreadsDemo5 {
    public static void main(String[] args) throws InterruptedException {
        IncrThread incrThread = new IncrThread();
        DecrThread decrThread = new DecrThread();
        incrThread.start();
        decrThread.start();
        incrThread.join();
        decrThread.join();
        System.out.println("最后结果: " + Counter.count);
    }
}
```

例子二：让线程自己选择锁对象往往使得代码逻辑混乱，不利于封装。使用同步方法逻辑封装是一个更好的办法。

```java
class Counter6 {
    public static int count = 0; // 共享变量
    public static final Object lock = new Object();// 锁对象

    public void incr(int n) {
        synchronized (this) {
            count += n;
        }
    }

    public void decr(int n) {
        synchronized (this) {
            count -= n;
        }
    }

    public int get() {
        return count;
    }
}

public class ThreadsDemo6 {
    public static void main(String[] args) {
        // synchronized锁住的对象是this，即当前实例，这又使得创建多个Counter实例的时候，它们之间互不影响，可以并发执行
        Counter6 counter61 = new Counter6();
        Counter6 counter62 = new Counter6();
        // counter61 控制线程操作
        new Thread(()->{counter61.incr(1);}).start();
        new Thread(()->{counter61.decr(1);}).start();
        // counter62 控制另外的线程操作
        new Thread(()->{counter62.incr(1);}).start();
        new Thread(()->{counter62.decr(1);}).start();
    }
}
```

注意，实例方法与静态方法同步的区别：

```java
class Counter7 {
    public static int count = 0; // 共享变量
    public static final Object lock = new Object();// 锁对象

    // 用synchronized修饰的方法就是同步方法，它表示整个方法都必须用this实例加锁。
    public synchronized void incr(int n) {
            count += n;
    }

    // 用synchronized修饰的方法就是同步方法，它表示整个方法都必须用this实例加锁。
    public synchronized void decr(int n) {
            count -= n;
    }

    public static void test(int n) {
        synchronized (Counter.class) {
            // 对于static方法，是没有this实例的，因为static方法是针对类而不是实例。但是我们注意到任何一个类都有一个由JVM自动创建的Class实例，因此，对static方法添加synchronized，锁住的是该类的Class实例。
        }
    }

    public int get() {
        return count;
    }
}
```

5. 死锁

死锁产生的条件是多线程格子持有不同的锁，并互相试图获取对方持有的锁，导致无限等待。避免死锁的方法，确保多线程获取锁的顺序要一致。

6. 事实上 synchronized 并没有解决多线程的协调问题。例如下面这个例子

```java
class TaskQueue {
    Queue<String> queue = new LinkedList<>();

    public synchronized void addTask(String s) {
        this.queue.add(s);
    }

    public synchronized String getTask() {
        while (queue.isEmpty()) { // getTask()入口获取了this的锁，addTask()无法获取锁，无法往队列添加任务。多线程之间的任务并没有协调好。
        }
        return queue.remove();
    }
}
```

多线程协调的原则：当条件不满足时，线程进入等待状态wait; 当条件满足时，线程被唤醒，继续执行任务。

```java
class TaskQueue {
    Queue<String> queue = new LinkedList<>();

    public synchronized void addTask(String s) {
        this.queue.add(s);
        this.notify(); // 唤醒在this锁等待的线程
    }

    public synchronized String getTask() {
        while (queue.isEmpty()) { 
            this.wait(); // 线程进入等待状态，wait()方法不会返回，直到将来某个时刻，线程从等待状态被其他线程唤醒后，wait()方法才会返回，然后，继续执行下一条语句。
            // 必须在synchronized块中才能调用wait()方法，因为wait()方法调用时，会释放线程获得的锁，wait()方法返回后，线程又会重新试图获得锁。
            // 调用notify() 或 notifyAll() , 已唤醒的线程还需要重新获得锁后才能继续执行。
        }
        return queue.remove();
    }
}
```
