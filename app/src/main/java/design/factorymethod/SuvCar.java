package design.factorymethod;

import android.util.Log;

/**
 * Created by renbo on 2017/11/9.
 */

public class SuvCar extends Car {
    @Override
    void run() {
        Log.d("SuvCar", "suv run....");
    }
}
