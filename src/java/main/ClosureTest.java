import java.util.function.IntSupplier;

class ClosureTest {
    IntSupplier makeFun(int x) {
        int i = 0;
        // i ++; 编译报错
        // x ++; 编译报错
        return new IntSupplier() {
            @Override
            public int getAsInt() {
                return x + i;
            }
        };
    }
}

class Test {
    public static void main(String[] args) {
        ClosureTest closureTest = new ClosureTest();
        IntSupplier f1 = closureTest.makeFun(0);
        IntSupplier f2 = closureTest.makeFun(0);
        IntSupplier f3 = closureTest.makeFun(0);
        System.out.println(f1.getAsInt());
        System.out.println(f2.getAsInt());
        System.out.println(f3.getAsInt());
    }
}
