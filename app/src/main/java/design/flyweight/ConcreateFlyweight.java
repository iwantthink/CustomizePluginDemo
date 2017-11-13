package design.flyweight;

import android.util.Log;

/**
 * Created by renbo on 2017/11/13.
 */

public class ConcreateFlyweight implements Flyweight {

    String paramA;
    String paramB;
    String paramC;

    public ConcreateFlyweight(String paramA) {
        this.paramA = paramA;
    }

    @Override
    public void doSomeThing(String paramC) {
        Log.d("ConcreateFlyweight", paramA + paramB + paramC);
    }
}
