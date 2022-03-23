package locks;


import java.util.concurrent.locks.StampedLock;

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

public class StampedLockDemo {
}
