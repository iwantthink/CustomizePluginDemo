package com.hypers.invocation;

import com.hypers.utils.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by renbo on 2017/10/18.
 */

public class InvocationDispatcher implements InvocationHandler {

    private Log mLog;

    public InvocationDispatcher(Log log) {
        mLog = log;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        return null;
    }
}
