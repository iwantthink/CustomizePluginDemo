package com.hypers

import org.gradle.api.NamedDomainObjectContainer
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

class Person {
    String name;
    int age;
    String sex;

    public Person(String name) {

    }


    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}

class Team {
    NamedDomainObjectContainer<Person> persons;

    String name;

    int count;

    public Team(NamedDomainObjectContainer<Person> persons) {
        this.persons = persons;
    }

    def persons(Closure closure) {
        persons.configure(closure)
    }


    @Override
    public String toString() {
        return "Team{" +
                "count=" + count +
                ", persons=" + persons +
                ", name='" + name + '\'' +
                '}';
    }
}