package design.abstractfactory;

/**
 * Created by renbo on 2017/11/9.
 */

public class MacFactory extends BaseComputerFactory {

    @Override
    BaseComputer createDestTop() {
        return new MacDestTop();
    }

    @Override
    BaseComputer createLaptop() {
        return new MacLaptop();
    }
}
