package com.hypers

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory


class HelloPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        def extension = project.extensions.create('clickcc', NestedClass, project.objects);
        project.task('hello') << {
            println "$extension.message from $extension.person.name"
        }
    }
}

class Person {
    String name
}

class NestedClass {
    String message
    final Person person

    @javax.inject.Inject
    public NestedClass(ObjectFactory objectFactory) {
        // Create a Person instance
        person = objectFactory.newInstance(Person)
    }

    void person(Action<? super Person> action) {
        action.execute(person)
    }
}

