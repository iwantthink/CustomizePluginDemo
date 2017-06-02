package com.hypers

import org.gradle.api.Plugin
import org.gradle.api.Project

class GreetingPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
//        NamedDomainObjectContainer<Person> persons = project.container(Person)
//        Team team = new Team(persons)
//        project.extensions.add('team', team)
//
//        project.task('showTeam') {
//            group 'hypers'
//            doLast {
//                def teams = project.extensions.getByName('team')
//
//                println teams.toString()
//            }
//        }
        //接收参数
        project.extensions.create('PluginCfg', PluginCfg)
        //创建 task:sayHi group:hypers
        project.task('sayHi') {
            group 'hypers'
            doLast {
                //取参数的方式1
                Closure cl = project['PluginCfg'].func
                cl 'lucy'
                println project['PluginCfg'].address
                //去取参数的方式2
//                project.PluginCfg.func('jack')
//                println project.PluginCfg.address
            }
        }

    }
}


class PluginCfg {
    Closure func;
    String address;
}