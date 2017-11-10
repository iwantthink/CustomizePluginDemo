package design.singleton;

/**
 * Created by renbo on 2017/11/10.
 */

public class Singleton {

    private Singleton() {

    }

    public static Singleton getSingleton() {
        return Holder.mHolder;
    }

    private static class Holder {
        static final Singleton mHolder = new Singleton();
    }

}
