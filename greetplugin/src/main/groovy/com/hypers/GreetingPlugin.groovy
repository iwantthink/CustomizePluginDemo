package com.hypers

import org.gradle.api.Plugin
import org.gradle.api.Project

class GreetingPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create('greetingExt', GreetingPluginExtension)
        project.task('sayHi') {
            group 'hypers'
            doLast {
                println 'hello from GreetingPlugin\n'
                println project.greetingExt.message
            }
        }

    }
}

class GreetingPluginExtension {
    String message = 'HHHHHH from extension'
}