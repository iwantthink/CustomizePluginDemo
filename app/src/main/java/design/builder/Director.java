package design.builder;

/**
 * Created by renbo on 2017/11/9.
 */

public class Director {

    private static BaseCarBuilder mBcb;

    public static BaseCar construct() {
        mBcb.buildBody("benz body");
        mBcb.buildWheel("benz wheel");
        mBcb.buildOthers("benz others");
        return mBcb.getCar();
    }

    public static void setBuilder(BaseCarBuilder bcb) {
        mBcb = bcb;
    }

}
