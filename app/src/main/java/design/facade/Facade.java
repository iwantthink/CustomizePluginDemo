package design.facade;

/**
 * Created by renbo on 2017/11/13.
 */

public class Facade {
    private SubSystemA mSubSystemA = new SubSystemA();
    private SubSystemB mSubSystemB = new SubSystemB();

    public void callSystemA() {
        mSubSystemA.showSomething();
    }

    public void callSystemB() {
        mSubSystemB.doSomething();
    }
}
