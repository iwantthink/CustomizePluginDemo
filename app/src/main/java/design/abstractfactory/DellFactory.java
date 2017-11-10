package design.abstractfactory;

/**
 * Created by renbo on 2017/11/9.
 */

public class DellFactory extends BaseComputerFactory {

    @Override
    BaseComputer createDestTop() {
        return new DellDestTop();
    }

    @Override
    BaseComputer createLaptop() {
        return new DellLaptop();
    }
}
