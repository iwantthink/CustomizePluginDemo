package design.builder;

import android.util.Log;

/**
 * Created by renbo on 2017/11/9.
 */

public class Benz extends BaseCar {

    @Override
    public void run() {
        Log.d("Benz", wheel);
        Log.d("Benz", body);
        Log.d("Benz", others);
    }
}
