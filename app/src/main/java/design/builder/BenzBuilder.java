package design.builder;

/**
 * Created by renbo on 2017/11/9.
 */

public class BenzBuilder extends BaseCarBuilder {

    @Override
    void createCar() {
        mBaseCar = new Benz();
    }

    @Override
    void buildWheel(String wheel) {
        mBaseCar.wheel = wheel;
    }

    @Override
    void buildBody(String body) {
        mBaseCar.body = body;
    }

    @Override
    void buildOthers(String others) {
        mBaseCar.others = others;
    }

    @Override
    BaseCar getCar() {
        return mBaseCar;
    }
}
