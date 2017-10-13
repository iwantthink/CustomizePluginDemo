

import com.sun.tools.attach.VirtualMachine;

import java.lang.management.ManagementFactory;
import java.util.Properties;
import java.util.Scanner;

public class MyClass {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);      
        System.out.println("what do u need ?");
        String input;
        while (!(input = s.nextLine()).equals("gg")) {
            if (input.equals("agent")) {
                try {
                    String pid = ManagementFactory.getRuntimeMXBean().getName();
                    int indexOf = pid.indexOf('@');
                    if (indexOf > 0) {
                        pid = pid.substring(0, indexOf);
                    }
                    System.out.println("pid = " + pid);

                    // attach to target VM
                    VirtualMachine vm = VirtualMachine.attach(pid);

                    // get system properties in target VM
                    Properties props = vm.getSystemProperties();

                    // construct path to management agent
                    String agent = "E:\\github\\CustomizePluginDemo\\agenttest\\libs\\apmagent.jar";

                    // load agent into target VM
                    vm.loadAgent(agent, null);

                    // detach
                    vm.detach();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                test();
            }
        }
    }

    public static void test() {
        System.out.println("i am method test");
    }
}
