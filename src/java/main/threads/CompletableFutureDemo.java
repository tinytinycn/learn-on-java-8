package threads;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureDemo {

    static Double fetchResult() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (Math.random() < 0.3) {
            throw new RuntimeException("fetch result failed.");
        }
        return 5 + Math.random() * 43;
    }

    public static void main(String[] args) throws InterruptedException {
        // 创建异步任务
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(CompletableFutureDemo::fetchResult);
        // 成功，执行回调
        cf.thenAccept(result -> {
            System.out.println("结果是：" + result);
        });
        // 异常，执行回调
        cf.exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
        // 主线程需要等待，否则CompletableFuture默认线程池会立刻关闭，无法测试
        TimeUnit.MILLISECONDS.sleep(200);
    }
}
