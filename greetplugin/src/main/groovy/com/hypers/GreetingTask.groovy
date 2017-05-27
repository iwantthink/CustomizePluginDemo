package com.hypers

import org.gradle.api.Plugin
import org.gradle.api.Project

class GreetingTask implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task('sayHi') {
            group 'hypers'
            doLast {
                println 'hello from GreetingTask'
            }
        }

    }
}

