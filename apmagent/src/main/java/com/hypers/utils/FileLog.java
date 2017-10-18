package com.hypers.utils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by renbo on 2017/10/18.
 */

public class FileLog implements Log {

    private PrintWriter mPrintWriter;

    public FileLog(String path) {
        try {
            mPrintWriter = new PrintWriter(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void v(String log) {
        writeLog("V", log);
    }

    @Override
    public void d(String log) {
        writeLog("D", log);
    }

    @Override
    public void w(String log) {
        writeLog("W", log);
    }

    @Override
    public void e(String log) {
        writeLog("E", log);
    }

    public void writeLog(String tag, String log) {
        mPrintWriter.println("[" + tag + "] " + log);
        mPrintWriter.flush();
    }

    public void close() {
        mPrintWriter.close();
    }
}
