public class HelloWorldJNI {
    static {
        System.loadLibrary("hello");
    }
    
    public static void main(String[] args) {
        new HelloWorldJNI().sayHello();
    }

    private native void sayHello();
}