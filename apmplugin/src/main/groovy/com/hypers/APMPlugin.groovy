package com.hypers

import com.sun.tools.attach.VirtualMachine
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.lang.management.ManagementFactory

class APMPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        String nameOfRunningVm = ManagementFactory.getRuntimeMXBean().getName()
        int p = nameOfRunningVm.indexOf('@')
        String pid = nameOfRunningVm.substring(0, p)

        try {
            String jarFilePath = TransformAgent.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()
            jarFilePath = new File(jarFilePath).getCanonicalPath()
            VirtualMachine vm = VirtualMachine.attach(pid)
            System.out.println("APMPlugin start ")
            System.out.println("jarFilePath = " + jarFilePath)
            System.out.println("file path = "+project.buildFile.absolutePath)
            vm.loadAgent(jarFilePath, "logFilePath=C:\\Users\\renbo\\Desktop\\java\\haha.txt;")
            vm.detach()
        } catch (Exception e) {
            e.printStackTrace()
        }

    }
}