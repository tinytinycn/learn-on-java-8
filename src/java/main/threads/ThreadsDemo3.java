package threads;


import java.util.concurrent.TimeUnit;

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
