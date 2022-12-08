# MockInjector
Use a single statement to populate your object under test with (Mockito-)mocks

## What's new in Version 3.0?

* No annotation needed on constructor, if there's just one constructor in the class that gets the dependencies injected.
* support for jakarta out of the box


## How do I use it?

### maven

```xml
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>#your-favorite-version#</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.github.joerg-pfruender</groupId>
        <artifactId>mockito-mockinjector</artifactId>
        <version>3.0</version>
        <scope>test</scope>
    </dependency>
```

### gradle

```groovy
    dependencies {
      testImplementation 'org.mockito:mockito-core:#your-favorite-version#'
      testImplementation 'io.github.joerg-pfruender:mockito-mockinjector:3.0'
    }
    
    configurations.all {
      resolutionStrategy.force 'org.mockito:mockito-core:#your-favorite-version#'
    }
```

### Java

**Best used with package scope fields!**

Runs out of the box with javax, jakarta, spring and guice annotations.
 
Initializing all injected dependencies of a service with mocks is as simple as:

      public void setUp() {
        serviceUnderTest = injectMocks(Service.class);
      ...

For an more complete example see `example/src/test/java/org/pfruender/mockinjector/example/SampleOrchestratingServiceUnitTest.java`

You can configure your own annotations using `MockInjectorConfigurator.setInjectAnnotations()` before the first call to
`injectMocks()`.


### Additional help

You do not need to code the mock injection manually, but you can use code templates, when working with Intellij IDEA:

#### JUnit 5 template

```
        #parse("File Header.java")
        #if (${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
        
        #if ($NAME.endsWith("Test"))
        import org.junit.jupiter.api.BeforeEach;
        import org.junit.jupiter.api.Test;
        import static org.pfruender.mockinjector.MockInjector.injectMocks;
        #end
        
        #parse("Type Header.java")
        class ${NAME} {
        
        #if ($NAME.endsWith("Test"))
        
          $NAME.replace("Test", "") $NAME.substring(0, 1).toLowerCase()$NAME.replace("Test", "").substring(1);
        
          @BeforeEach
          void setUp() throws Exception {
            $NAME.substring(0, 1).toLowerCase()$NAME.replace("Test", "").substring(1) = injectMocks($NAME.replace("Test", "") .class);
          }
        
        #end
        }


```

#### TestNG template

```
        #parse("File Header.java")
        #if (${PACKAGE_NAME} != "")package ${PACKAGE_NAME};#end
        
        #if ($NAME.endsWith("Test"))
        import org.testng.annotations.BeforeMethod;
        import org.testng.annotations.Test;
        
        import static org.pfruender.mockinjector.MockInjector.injectMocks;
        #end
        
        #parse("Type Header.java")
        public class ${NAME} {
        
        #if ($NAME.endsWith("Test"))
        
          $NAME.replace("Test", "") $NAME.substring(0, 1).toLowerCase()$NAME.replace("Test", "").substring(1);
        
          @BeforeMethod
          public void setUp() throws Exception {
            $NAME.substring(0, 1).toLowerCase()$NAME.replace("Test", "").substring(1) = injectMocks($NAME.replace("Test", "") .class);
          }
        
        #end
        }
```

## Why?

We have been bored to write so much boilerplate code for mocking dependencies in our unit tests. That’s why we have written MockInjector to automatically inject all mocks into our class under test.

Think of this class:

```java
    @AllArgsConstructor
    class ImplementationClass {
      Dependency1 dependency1;
      Dependency2 dependency2;
    
      void doSomething() {
        dependency1.doSomething();
        dependency2.doAnything();
      }
    }
```

The traditional way of mocking dependency1 and dependency2 with Mockito is:

```java
    class ImplementationClassTest {
      ImplementationClass objectUnderTest;
      Dependency1 dependency1;
      Dependency2 dependency2;
    
      @BeforeEach 
      void setUp() {
        dependency1 = mock(Dependency1.class);
        dependency2 = mock(Dependency2.class);
        objectUnderTest = new ImplementationClass(dependency1, dependency2);
      }
    }
```

You can also do this using annotations, but that isn't much shorter:

```java
    class ImplementationClassTest {
      @InjectMocks ImplementationClass objectUnderTest;
      
      @Mock Dependency1 dependency1;
      
      @Mock Dependency2 dependency2;
    
      @BeforeEach 
      void setUp() {
        initMocks(this);
      }
    }
```

`MockInjector.injectMocks()` finds all annotated dependencies and injects Mocks for them.

There are no dependency variables in the test class, which saves us two lines of code for each dependency.

But how do you stub and verify the interactions with the mocks? Just use package protected scope for your dependencies:

```java

    @Test
    public void doSomething_calls_dependencies() {
      // when
      objectUnderTest.doSomething();
      // then
      verify(objectUnderTest.dependency1).doSomething();
      verify(objectUnderTest.dependency2).doAnything();
    }

```

Additional advantages: 
* If you refactor the field names in the implementation, you do not need to rename the fields in the tests accordingly.
* with file templates you do not need to code any mocking setup manually.


## Limitations
If the class of injected instance is not mockable (e.g. a final class) null or nothing at all is injected, depending on the
type of injection (parameter vs. field injection).


## fork

This project is a fork of [https://github.com/hypoport/MockInjector](https://github.com/hypoport/MockInjector).