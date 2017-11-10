package design.adapter.type2;

import design.adapter.Adaptee;
import design.adapter.Target;

/**
 * Created by renbo on 2017/11/10.
 */

public class Adapter2 implements Target {

    Adaptee mAdaptee;

    public Adapter2(Adaptee adaptee) {
        mAdaptee = adaptee;
    }

    public int getVolt200() {
        return mAdaptee.getVolt200();
    }

    @Override
    public int getVolt5() {
        return 5;
    }
}
