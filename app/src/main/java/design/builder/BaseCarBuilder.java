package design.builder;

/**
 * Created by renbo on 2017/11/9.
 */

public abstract class BaseCarBuilder {
    protected BaseCar mBaseCar;

    abstract void createCar();

    abstract void buildWheel(String wheel);

    abstract void buildBody(String body);

    abstract void buildOthers(String others);

    abstract BaseCar getCar();
}
