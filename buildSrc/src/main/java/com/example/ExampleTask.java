package com.example;

import org.gradle.api.DefaultTask;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;

import javax.inject.Inject;

abstract class ExampleTask extends DefaultTask {

    @Internal
    abstract Property<ExampleTestListener> getTestListener();

    @Inject
    public ExampleTask() {
        doLast(task -> {
            getLogger().lifecycle("[ExampleTask] Referenced ExampleTestListener instance: %s".formatted(getTestListener().get()));
            getLogger().lifecycle("[ExampleTask] Test count: %s".formatted(getTestListener().get().tests.size()));
        });
    }

}
