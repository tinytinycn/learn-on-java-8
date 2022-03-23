# Java 中的锁分类

1. 线程要不要锁住同步资源

    - 锁住（悲观锁）
    - 不锁住（乐观锁）

2. 多线程能否共享一把锁

    - 可以（共享锁），ReentrantReadWriteLock中包含读锁和写锁，其中读锁是可以多线程共享的，即共享锁，而写锁是排他锁。
    - 不可以（独占锁）

3. 多线程竞争时，是否排队

    - 排队（公平锁）
    - 先尝试插队，插队失败再排队（非公平锁）

4. 同一个线程是否可以重复获取同一把锁

    - 可以（可重入锁），同一个线程在外层方法获取锁的时候，再次进入该线程内层方法会自动获取锁，不会因为之前已经获取过还没释放而阻塞。ReentrantLock,synchronized都是可重入锁。
    - 不可以（不可重入锁）

5. 是否可中断

    - 可以（可中断锁）
    - 不可以（不可中断锁）

6. 等锁的过程

    - 自旋（自旋锁）
    - 阻塞（非自旋锁）

# Java 中的 ReentrantLock 与 synchronized 区别

- synchronized 使用方便，编译器来加锁，是非公平锁。 ReentrantLock 使用灵活，锁的公平性可以定制, 默认实现是非公平锁，可以提高效率，避免线程唤醒带来的空档期。
- synchronized 是依赖于JVM实现的，而 ReentrantLock 是JDK实现的。
- ReentrantLock 实现的是一种自旋锁，通过循环调用 CAS 操作来实现加锁，性能较好，避免了线程进入内核态的阻塞状态。

# 高级并发工具(java.util.concurrent)

## 使用 ReentrantLock 可重入锁

ReentrantLock 可以代替 synchronized 进行同步，ReentrantLock 获取锁更安全，也可以使用 `tryLock()` 尝试获取锁。

```java
class Counter {
    private final Lock lock = new ReentrantLock();
    private int count;

    public void add(int n) {
        lock.lock(); // 和synchronized一样，一个线程可以多次获取同一个锁。
        try {
            count += n;
        } finally {
            lock.unlock(); // 在finally中正确释放锁。
        }
    }

    public int getCount() {
        return count;
    }
}

public class ReentrantLockDemo {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.add(1);
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.add(-1);
            }
        });
        t1.start();
        t2.start();
        t1.join();// 等待执行结束
        t2.join();// 等待执行结束
        System.out.println(counter.getCount()); // 结果为 0
    }
}
```

## 使用 Condition

使用 Condition 对象来实现 wait 和 notify 的功能。

```java
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
```

## 使用 ReadWriteLock 

ReadWriteLock 读写锁可以保证，只允许一个线程写入，其他线程不能写入也不能读取；没有写入，多个线程允许同时读取（提高性能）；

ReadWriteLock适合读多写少的场景。

```java
class Counter2 {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(); // 可重入读写锁
    private final Lock rLock = readWriteLock.readLock(); // 把读写操作分别用读锁和写锁来加锁，在读取时，多个线程可以同时获得读锁，这样就大大提高了并发读的执行效率。
    private final Lock wLock = readWriteLock.writeLock();
    private int[] counts = new int[10];

    public void inc(int index) {
        wLock.lock();
        try {
            counts[index] += 1;
        } finally {
            wLock.unlock();
        }
    }

    public int[] get() {
        rLock.lock(); 
        try {
            return Arrays.copyOf(counts, counts.length);
        } finally {
            rLock.unlock();
        }
    }
}
```

## 使用 StampedLock

ReadWriteLock 有个潜在的问题：如果有线程正在读，写线程需要等待并获取读线程释放的锁，即读的过程中不允许写，是一种悲观的读锁。

Java 8 引入 StampedLock , StampedLock把读锁细分为乐观读和悲观读，能进一步提升并发效率。但这也是有代价的：一是代码更加复杂，二是StampedLock是不可重入锁，不能在一个线程中反复获取同一个锁。

在读的过程中允许获取写锁后写入。但会导致读的过程中数据不一致，需要额外的代码来判断是否在读的过程中可否写入，是一种乐观的读锁。

- 乐观锁，乐观地估计"读的过程中"大概率不会有"写入"。乐观锁并发效率更高，但小概率的写入导致读取数据不一致，需要额外代码检测出来，再读一遍。
- 悲观锁，悲观地觉得"读的过程中"大概率有"写入"，所以拒绝写入，写入必须等待。

```java
class Point {
    private final StampedLock stampedLock = new StampedLock();
    private double x;
    private double y;

    public void move(double deltaX, double deltaY) {
        long stamp = stampedLock.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

    public double distanceFromOrigin() {
        long stamp = stampedLock.tryOptimisticRead(); // 获取一个乐观锁，并返回版本号。
        // 注意：下面不是原子操作
        // 假设 x,y = 100,200
        double currentX = x; // 读取到 x=100, 此时被写线程修改为 300,400
        double currentY = y; // 如果y=400 没有写入，读入是正确的 100,200; 如果有写入，读入是错误的 100,400
        // 关键检测： 通过validate()去验证版本号，如果在读取过程中没有写入，版本号不变，验证成功，我们就可以放心地继续后续操作。
        if (!stampedLock.validate(stamp)) { // 检查乐观读锁是否有其他的写锁发生
            stamp = stampedLock.readLock(); // 发生版本变化，获取一个悲观读锁
            try {
                currentX = x;
                currentY = y;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}
```