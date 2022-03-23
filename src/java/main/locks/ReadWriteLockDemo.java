package locks;


import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Counter2 {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(); // 可重入读写锁
    private final Lock rLock = readWriteLock.readLock();
    private final Lock wLock = readWriteLock.writeLock();
    private int[] counts = new int[10];

    public void inc(int index) {
        wLock.lock();
        try {
            counts[index] += 1;
        } finally {
            wLock.unlock();
        }
    }

    public int[] get() {
        rLock.lock();
        try {
            return Arrays.copyOf(counts, counts.length);
        } finally {
            rLock.unlock();
        }
    }
}

public class ReadWriteLockDemo {
    public static void main(String[] args) throws InterruptedException {
        Counter2 counter2 = new Counter2();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                counter2.inc(i % 10);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                int[] ints = counter2.get();
                System.out.println(Arrays.toString(ints));
            }
        });
        t1.start();
        t2.start();
        t1.join();// 等待执行结束
        t2.join();// 等待执行结束
        System.out.println(Arrays.toString(counter2.get())); // 结果为 0
    }
}
