package design.factorymethod;

/**
 * Created by renbo on 2017/11/9.
 */

public class SuvFactory extends BaseCarFactory {

    @Override
    Car createCar() {
        return new SuvCar();
    }
}
