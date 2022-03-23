package threads;

import java.util.concurrent.TimeUnit;

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
