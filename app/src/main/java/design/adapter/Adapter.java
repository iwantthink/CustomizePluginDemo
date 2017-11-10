package design.adapter;

/**
 * Created by renbo on 2017/11/10.
 */

public class Adapter extends Adaptee implements Target {

    @Override
    public int getVolt5() {
        return 5;
    }
}
