# 增强 for-in 循环

```java
public class ControlFlowTest {
    public static void main(String[] args) {
        Random rand = new Random(47);
        float[] f = new float[10];
        // 传统循环
        for (int i = 0; i < 10; i++) {
            f[i] = rand.nextFloat();
        }
        // for-in 增强
        for (float x : f) {
            System.out.println(x);
        }
        // 遍历对象为null
//        f = null;
//        for (float x : f) { // NPE 运行时异常
//            System.out.println(x);
//        }
    }
}
```