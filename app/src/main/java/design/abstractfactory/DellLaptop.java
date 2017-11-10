package design.abstractfactory;

import android.util.Log;

/**
 * Created by renbo on 2017/11/9.
 */

public class DellLaptop extends BaseComputer {
    @Override
    void compute() {
        Log.d("DellLaptop", "dell laptop is computing ");
    }
}
