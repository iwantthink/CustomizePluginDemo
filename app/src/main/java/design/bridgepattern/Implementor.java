package design.bridgepattern;

/**
 * Created by renbo on 2017/11/10.
 */

interface Implementor {
    public void operationImpl();
}

class ConcreateImplementorA implements Implementor {

    @Override
    public void operationImpl() {
        //具体实现
    }
}

class ConcreateImplementorB implements Implementor {

    @Override
    public void operationImpl() {
        //具体实现
    }
}

abstract class Abstraction {
    private Implementor mImplementor;//声明一个私有成员变量引用实现部分的对象

    public Abstraction(Implementor implementor) {
        mImplementor = implementor;
    }

    public void operation() {
        mImplementor.operationImpl();
    }
}

class RefinedAbstraction extends Abstraction {

    public RefinedAbstraction(Implementor implementor) {
        super(implementor);
    }

    /**
     * 对父类抽象部分中的方法进行扩展
     */
    public void refinedOperation() {
        //对Abstraction中的方法进行扩展
    }
}
