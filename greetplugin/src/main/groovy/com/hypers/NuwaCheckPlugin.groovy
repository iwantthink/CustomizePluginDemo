package com.hypers

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
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

class MyTransform extends Transform {


    @Override
    String getName() {
        return null
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return null
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return null
    }

    @Override
    boolean isIncremental() {
        return false
    }
}