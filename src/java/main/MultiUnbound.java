class This {
    void two(int i, double d) {
    }

    void three(int i, double d, String s) {
    }

    void four(int i, double d, String s, char c) {
    }
}

interface TwoArgs {
    void call2(This aThis, int i, double d);
}

interface ThreeArgs {
    void call3(This aThis, int i, double d, String s);
}

interface FourArgs {
    void call4(This aThis, int i, double d, String s, char c);
}

public class MultiUnbound {
    public static void main(String[] args) {
        TwoArgs twoArgs = This::two;
        ThreeArgs threeArgs = This::three;
        ;
        FourArgs fourArgs = This::four;
        // 此时还没有This对象存在，上述都是未绑定的方法引用。
        This aThis = new This();
        // 此时已经有了This对象, 传递这个对象，使得调用 callX()方法后，最终调用了 This.xxx() 里面的方法。
        twoArgs.call2(aThis, 11, 3.14);
        threeArgs.call3(aThis, 11, 3.14, "three");
        fourArgs.call4(aThis, 11, 3.14, "four", 'Z');
    }
}
