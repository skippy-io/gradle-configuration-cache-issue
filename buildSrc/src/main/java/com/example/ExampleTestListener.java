package com.example;

import org.gradle.api.tasks.testing.TestDescriptor;
import org.gradle.api.tasks.testing.TestListener;
import org.gradle.api.tasks.testing.TestResult;

import java.util.ArrayList;
import java.util.List;

class ExampleTestListener implements TestListener {
    List<TestDescriptor> tests = new ArrayList<>();

    @Override
    public void beforeSuite(TestDescriptor testDescriptor) {
    }

    @Override
    public void afterSuite(TestDescriptor testDescriptor, TestResult testResult) {
    }

    @Override
    public void beforeTest(TestDescriptor testDescriptor) {
    }

    @Override
    public void afterTest(TestDescriptor testDescriptor, TestResult testResult) {
        System.err.println("[ExampleTestListener] Test task uses instance %s".formatted(this));
        tests.add(testDescriptor);
    }
}
