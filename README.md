MockInjector
============
Use a single statement to populate your object under test with (Mockito-)mocks

What's new in Version 3.0?
--------------------------
No annotation needed on constructor, if there's just one constructor in the class that gets the dependencies injected. 


How do I use it?
================

**Best used with package scope fields!**

1. Optional: Annotate your dependencies
2. call injectMocks(ClassUnderTest.class)
3. configure your mocks if necessary

Runs out of the box with javax, jakarta, spring and guice annotations. Initializing all injected dependencies of a service with
mocks is as simple as:

      public void setUp() {
        serviceUnderTest = injectMocks(Service.class);
      ...

For an more complete example see `example/src/test/java/org/pfruender/mockinjector/example/SampleOrchestratingServiceUnitTest.java`

You can configure your own annotations using MockInjectorConfigurator.setInjectAnnotations() before the first call to
injectMocks().

You can find a more detailed introduction on https://tech.europace.de/use-mockinjector-and-package-protected-scope-for-dependencies-to-reduce-boilerplate-code/

Limitations
-----------
Since you do not create your mocks manually you need some way to access them afterwards if necessary. Our preferred way is to have
test and implementation classes in the same package and make your fields package local.

If the class of injected instance is not mockable (e.g. a final class) null or nothing at all is injected, depending on the
type of injection (parameter vs. field injection).


Release
=======

We do not want to release a new version of MockInjector every time when Mockito releases a new version.
Therefor we have decided to declare an open-ended version range for our mockito dependency
and we try to use only public api code of Mockito, that will hopefully not break.

If you use MockInjector, please do *not* rely on the transitive dependency on mockito that it brings.
Declare your own mockito dependency:

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>#your-favorite-version#</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.pfruender</groupId>
    <artifactId>mockito-mockinjector</artifactId>
    <version>3.0</version>
    <scope>test</scope>
</dependency>
```
Why?
----
The open-ended version range of MockInjector will be resolved to the latest available version of mockito.
If you do not fix the version, every time mockito releases a new version, you have a chance that your build breaks, although you have never changed anything.
If you want to have stable builds, fix the version.

Gradle
------
Gradle resolves the version to the latest available although you have declared your own version.
This will result in an instable build, too.
You should declare a resolution strategy:
```
dependencies {
  testCompile 'org.mockito:mockito-core:#your-favorite-version#'
  testCompile 'org.pfruender:mockito-mockinjector:3.0'
}

configurations.all {
  resolutionStrategy.force 'org.mockito:mockito-core:#your-favorite-version#'
}
```
