package threads;

import java.util.concurrent.TimeUnit;

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
