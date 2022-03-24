package threads;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureDemo3 {

    static String fetchResult(String name, String url) {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return name;
    }

    static Double fetchResult2(String name, String url) {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 5 + Math.random() * 43;
    }

    public static void main(String[] args) throws InterruptedException {
        // 异步执行两个任务
        CompletableFuture<String> cfa = CompletableFuture.supplyAsync(() -> {
            return fetchResult("A", "A.com");
        });
        CompletableFuture<String> cfb = CompletableFuture.supplyAsync(() -> {
            return fetchResult("B", "B.com");
        });
        // anyOf 合并为一个新的cf
        CompletableFuture<Object> cfc = CompletableFuture.anyOf(cfa, cfb);
        CompletableFuture<Double> cfc1 = cfc.thenApplyAsync(name -> {
            return fetchResult2((String) name, "C.com");
        });
        CompletableFuture<Double> cfc2 = cfc.thenApplyAsync(name -> {
            return fetchResult2((String) name, "D.com");
        });
        // anyOf 合并为一个新的cf
        CompletableFuture<Object> cfd = CompletableFuture.anyOf(cfc1, cfc2);
        cfd.thenAccept(res -> {
            System.out.println("res = " + res);
        });
        TimeUnit.MILLISECONDS.sleep(2000);
    }
}
