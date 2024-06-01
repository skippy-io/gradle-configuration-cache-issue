package com.example;

import org.gradle.api.Project;
import org.gradle.api.tasks.testing.Test;

class ExamplePlugin implements org.gradle.api.Plugin<Project> {

    @Override
    public void apply(Project project) {
        var testFailedListener = new ExampleTestListener();

        project.getTasks().register("exampleTask", ExampleTask.class, task -> {
            task.getTestListener().set(testFailedListener);
        });
        project.getTasks().withType(Test.class, testTask -> {
            testTask.finalizedBy("exampleTask");
            testTask.addTestListener(testFailedListener);
        });
    }

}