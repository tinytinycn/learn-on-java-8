package threads;

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
