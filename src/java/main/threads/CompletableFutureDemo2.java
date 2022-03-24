package threads;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
