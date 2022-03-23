package threads;

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
