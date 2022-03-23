package locks;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Counter {
    private final Lock lock = new ReentrantLock();
    private int count;

    public void add(int n) {
        lock.lock(); // 和synchronized一样，一个线程可以多次获取同一个锁。
        try {
            count += n;
        } finally {
            lock.unlock(); // 在finally中正确释放锁。
        }
    }

    public int getCount() {
        return count;
    }
}

public class ReentrantLockDemo {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.add(1);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.add(-1);
            }
        });
        t1.start();
        t2.start();
        t1.join();// 等待执行结束
        t2.join();// 等待执行结束
        System.out.println(counter.getCount()); // 结果为 0
    }
}
