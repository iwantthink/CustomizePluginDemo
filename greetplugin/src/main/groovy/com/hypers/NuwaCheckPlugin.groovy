package com.hypers

import org.gradle.api.Plugin
import org.gradle.api.Project

public class NuwaCheckPlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.afterEvaluate {
            project.android.applicationVariants.each { variant ->
                logE("variant.name = $variant.name")
                def preDexTask = project.tasks.findByName("preDex${variant.name.capitalize()}")
            }
        }
    }

    def logE(str) {
        System.err.println str
    }

}