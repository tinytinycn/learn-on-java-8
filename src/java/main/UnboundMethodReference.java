
class X{
    String f() {
        return "X::f()";
    }
}

interface MakeString {
    String make();
}

interface TransformX{
    String transform(X x);
}

public class UnboundMethodReference {
    public static void main(String[] args) {
        // MakeString ms = X::f;  // 编译出错, 其实还需要另一个隐藏参数参与：我们的老朋友 this。 你不能在没有 X 对象的前提下调用 f()。 因此，X :: f 表示未绑定的方法引用，因为它尚未“绑定”到对象。
        TransformX sp = X::f;
        X x = new X();
        System.out.println(sp.transform(x));
        System.out.println(x.f());
    }
}
