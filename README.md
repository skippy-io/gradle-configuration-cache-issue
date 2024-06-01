# gradle-configuration-cache-issue

Sample project to demonstrate an issue with Gradle's Configuration Cache

Related Skippy issue: https://github.com/skippy-io/skippy/issues/159

# Project Overview

A Plugin creates a `TestListener` that is used by a custom task and the test tasks in the project:  

```
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
```

`ExampleTestListener` simply keeps track of the tests that have been executed:

```
class ExampleTestListener implements TestListener {
    
    List<TestDescriptor> tests = new ArrayList<>();

    ...
 
    @Override
    public void afterTest(TestDescriptor testDescriptor, TestResult testResult) {
        System.err.println("[ExampleTestListener] Test task uses instance %s".formatted(this));
        tests.add(testDescriptor);
    }
}
```

`ExampleTask` logs the data collected by `ExampleTestListener`:

```
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
```

# Execution (Configuration Cache disabled)

```
./gradlew clean test --no-configuration-cache

> Task :test
[ExampleTestListener] Test task uses instance com.example.ExampleTestListener@60e6df11

FooTest > testSomething() PASSED

> Task :exampleTask
[ExampleTask] Referenced ExampleTestListener instance: com.example.ExampleTestListener@60e6df11
[ExampleTask] Test count: 1
```

# Execution (Configuration Cache enabled)

```
./gradlew clean test --configuration-cache        
Calculating task graph as no configuration cache is available for tasks: clean test

> Task :test
[ExampleTestListener] Test task uses instance com.example.ExampleTestListener@51c45d55

FooTest > testSomething() PASSED

> Task :exampleTask
[ExampleTask] Referenced ExampleTestListener instance: com.example.ExampleTestListener@1fcdb88
[ExampleTask] Test count: 0
```

With Configuration Cache enabled, the Test tasks and `ExampleTask` reference different instances:
- `ExampleTestListener@51c45d55`
- `ExampleTestListener@1fcdb88`

This causes `ExampleTask` to read 0 tests being executed.