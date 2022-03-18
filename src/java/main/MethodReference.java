interface Callable {
    void call(String s);
}

// [1] Describe类的非静态方法show签名（参数类型、返回类型）符合Callable.call()的签名
class Describe {
    void show(String msg) {
        System.out.println(msg);
    }
}

public class MethodReference {
    // [2] MethodReference类的静态方法hello签名（参数类型、返回类型）符合Callable.call()的签名
    static void hello(String name) {
        System.out.println("hello, " + name);
    }

    static class Description {
        String about;

        Description(String desc) {
            about = desc;
        }

        // [3] Description 静态内部类的非静态方法help签名（参数类型、返回类型）符合Callable.call()的签名
        void help(String msg) {
            System.out.println("about " + msg);
        }
    }

    static class Helper {
        // Helper 静态内部类的静态方法 assist签名（参数类型、返回类型）符合Callable.call()的签名
        static void assist(String msg) {
            System.out.println(msg);
        }
    }

    // 测试
    public static void main(String[] args) {
        Describe describe = new Describe();
        Callable c = describe::show; // [1]
        c.call("call()"); // 通过call()方法，实际调用show()方法，因为Java将call() 映射到 show()
        c = MethodReference::hello; // [2]
        c.call("hello()");
        c = new Description("valuable")::help; // [3] 对已实例化对象的方法的引用，有时称为绑定方法引用。
        c.call("help()");
        c = Helper::assist; // [4]
        c.call("assist()");
    }
}
