package design.proxy;

import android.util.Log;

/**
 * Created by renbo on 2017/11/13.
 */

public class RealSubject implements Subject {
    @Override
    public void request() {
        Log.d("RealSubject", "do something");
    }
}
