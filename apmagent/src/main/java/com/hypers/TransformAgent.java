package com.hypers;

import com.hypers.invocation.InvocationDispatcher;
import com.hypers.utils.ConsoleLog;
import com.hypers.utils.FileLog;
import com.hypers.utils.Log;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by renbo on 2017/10/12.
 */

public class TransformAgent {

    public static final Class LOGGER = Logger.class;
    public static final Set<String> dx = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(new String[]{"dx", "dx.bat"})));
    public static final Set<String> java = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(new String[]{"java", "java.exe"})));


    //1.5++
    public static void premain(String args, Instrumentation inst) {
        agentmain(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        Map<String, String> params = Collections.emptyMap();
        params = parseArguments(args);
        String logFilePath = params.get("logFilePath");
        System.out.println("logFilePath = " + logFilePath);
        com.hypers.utils.Log log = logFilePath == null ? new ConsoleLog() : new FileLog(logFilePath);
        createInvocationDispatcher(log);
        inst.addTransformer(new MyClassTransformer(log), true);


    }

    private static void createInvocationDispatcher(Log log) {
        try {
            Field treeLock = LOGGER.getDeclaredField("treeLock");
            treeLock.setAccessible(true);
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(treeLock, treeLock.getModifiers() & 0xFFFFFFEF);
            if (!(treeLock.get(null) instanceof InvocationDispatcher)) {
                treeLock.set(null, new InvocationDispatcher(log));
            }
        } catch (Exception e) {
            log.e(e.getMessage());
        }
    }

    /**
     * param passed when loadAgent
     * xxx=xxx;yyy=yyy;
     *
     * @param args
     * @return
     */
    private static Map<String, String> parseArguments(String args) {
        if (args == null) {
            return Collections.emptyMap();
        }
        HashMap<String, String> result = new HashMap<>();
        String[] array = args.split(";");
        for (String s : array) {
            String[] strs = s.split("=");
            if (strs.length != 2) {
                throw new IllegalArgumentException("wrong args");
            }
            System.out.println("parseArguments --- key = " + strs[0] + ",value = " + strs[1]);
            result.put(strs[0], strs[1]);
        }
        return result;
    }
}
