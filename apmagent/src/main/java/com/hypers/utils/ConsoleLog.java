package com.hypers.utils;

/**
 * Created by renbo on 2017/10/18.
 */

public class ConsoleLog implements Log {
    @Override
    public void v(String log) {
        System.out.println("[V] " + log);
    }

    @Override
    public void d(String log) {
        System.out.println("[D] " + log);
    }

    @Override
    public void w(String log) {
        System.out.println("[W] " + log);
    }

    @Override
    public void e(String log) {
        System.err.println("[E] " + log);
    }


}
