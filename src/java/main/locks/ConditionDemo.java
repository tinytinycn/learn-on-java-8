package locks;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class TaskQueue {
    private final Lock lock = new ReentrantLock(); // 锁
    private final Condition condition = lock.newCondition(); // 条件
    private Queue<String> queue = new LinkedList<>(); // 任务队列

    public void addTask(String s) {
        lock.lock();
        try {
            queue.add(s);
            condition.signalAll(); // [2] 唤醒所有等待的线程
        } finally {
            lock.unlock();
        }
    }

    public String getTask() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                condition.await(); // [1] 释放当前锁，进入等待状态
                // [3] 唤醒线程从await() 返回后，需要重新获得锁，才能继续执行接下来的代码。
            }
            return queue.remove();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }

    public int getTaskCount() {
        return queue.size();
    }
}

public class ConditionDemo {
    public static void main(String[] args) throws InterruptedException {
        TaskQueue queue = new TaskQueue();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                queue.addTask("task@" + i);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                String task = queue.getTask();
                System.out.println(task);
            }
        });
        t1.start();
        t2.start();
        t1.join();// 等待执行结束
        t2.join();// 等待执行结束
        System.out.println(queue.getTaskCount()); // 结果为 0
    }
}
