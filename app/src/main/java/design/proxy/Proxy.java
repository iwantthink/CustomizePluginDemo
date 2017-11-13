package design.proxy;

/**
 * Created by renbo on 2017/11/13.
 */

public class Proxy implements Subject {

    static Subject mSubject;

    public Proxy(Subject subject) {
        mSubject = subject;
    }

    void beforeRequest() {

    }

    void afterRequest() {

    }

    @Override
    public void request() {
        beforeRequest();
        mSubject.request();
        afterRequest();
    }
}
