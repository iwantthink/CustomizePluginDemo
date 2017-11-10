package design.Decorator;

/**
 * Created by renbo on 2017/11/10.
 */

abstract class Component {

    /**
     * 被拓展的类
     * 抽象的方法，可以任意定制
     */
    abstract void operate();
}

class ConcreateComponent extends Component {

    @Override
    void operate() {
        //具体实现
    }
}

abstract class Decorator extends Component {
    private Component mComponent;

    public Decorator(Component component) {
        //传入具体的实现类
        mComponent = component;
    }

    @Override
    void operate() {
        //具体实现 由传入的实现类决定
        mComponent.operate();
    }
}

class DecoratorA extends Decorator {

    public DecoratorA(Component component) {
        super(component);
    }

    @Override
    void operate() {
        operateA();
        super.operate();
        operateB();
    }

    //拓展方法
    void operateA() {

    }

    //拓展方法
    void operateB() {

    }
}
