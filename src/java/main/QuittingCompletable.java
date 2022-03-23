import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Nap {
    public Nap(double t) { // Seconds
        try {
            TimeUnit.MILLISECONDS.sleep((int) (1000 * t));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Nap(double t, String msg) {
        this(t);
        System.out.println(msg);
    }
}

class QuittableTask implements Runnable {
    final int id;

    public QuittableTask(int id) {
        this.id = id;
    }

    // AtomicBoolean 可以防止多个任务同时实际修改 running ，从而使 quit() 方法成为线程安全的。
    private AtomicBoolean running = new AtomicBoolean(true);

    public void quit() {
        running.set(false);
    }

    @Override
    public void run() {
        while (running.get()) {
            new Nap(0.1);
        }
        System.out.print(id + " ");
    }
}


public class QuittingCompletable {
    public static void main(String[] args) {
        List<QuittableTask> tasks = IntStream.range(1, 100).mapToObj(QuittableTask::new).collect(Collectors.toList());
        List<CompletableFuture<Void>> cFutures = tasks.stream().map(CompletableFuture::runAsync).collect(Collectors.toList());
        new Nap(1);
        tasks.forEach(QuittableTask::quit);// 退出任务
        cFutures.forEach(CompletableFuture::join);// 等待完成
    }
}
