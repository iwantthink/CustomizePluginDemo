package design.flyweight;

import java.util.HashMap;

/**
 * Created by renbo on 2017/11/13.
 */

public class FlyweightFactory {
    private static HashMap<String, Flyweight> mMap = new HashMap<>();

    public static Flyweight getFlyweight(String paramA, String paramB) {
        String key = paramA + "-" + paramB;

        if (mMap.containsKey(key)) {
            return mMap.get(key);
        } else {
            Flyweight flyweight = new ConcreateFlyweight(paramA);
            mMap.put(key, flyweight);
            return flyweight;
        }
    }
}
