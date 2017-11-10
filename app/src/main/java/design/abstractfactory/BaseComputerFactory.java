package design.abstractfactory;

/**
 * Created by renbo on 2017/11/9.
 */

public abstract class BaseComputerFactory {

    abstract BaseComputer createDestTop();

    abstract BaseComputer createLaptop();
}
