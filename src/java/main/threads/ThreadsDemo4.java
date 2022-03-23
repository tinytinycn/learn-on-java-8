package threads;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

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
        t.setDaemon(true);
        t.start();
        System.out.println("start daemon thread");
        TimeUnit.MILLISECONDS.sleep(10000);
        System.out.println("end");
    }
}
