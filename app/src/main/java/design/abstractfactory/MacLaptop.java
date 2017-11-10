package design.abstractfactory;

import android.util.Log;

/**
 * Created by renbo on 2017/11/9.
 */

public class MacLaptop extends BaseComputer {
    @Override
    void compute() {
        Log.d("MacLaptop", "mac laptop is computing");
    }
}
