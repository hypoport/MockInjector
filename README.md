MockInjector
============
Use a single statement to populate your object under test with (Mockito-)mocks

How do I use it?
----------------
1. Annotate your dependencies (you already did that, right?)
2. call injectMocks(classUnderTest)
3. configure your mocks if necessary

Runs out of the box with javax, spring and guice annotations. Initializing all injected dependencies of a service with
mocks is as simple as:

      public void setUp() {
        serviceUnderTest = injectMocks(Service.class);
      ...

For an more complete example see `example/src/test/java/org/hypoport/mockito/example/SampleOrchestratingServiceUnitTest.java`

You can configure your own annotations using MockInjectorConfigurator.setInjectAnnotations() before the first call to
injectMocks().

Limitations
-----------
Since you do not create your mocks by hand you need some way to access them afterwards if necessary. One option is to have
test and implementation in the same package and make your fields package local.

If the class of injected instance is not mockable (e.g. a final class) null or nothing at all is injected, depending on the
type of injection (parameter vs. field injection).

Bugs
----
There might be bugs. If you find one feel free to report it or even better fix it.

Release
-------

MockInjector is available on maven central:

<dependency>
    <groupId>org.hypoport</groupId>
    <artifactId>mockito-mockinjector</artifactId>
    <version>1.0</version>
</dependency>



Provider
--------
Provider injection is somewhat work in progress

-   We currently do not support google.inject.Provider
-   You can configure your MockProvider with SINGLETON or PROTOTYPE strategy.
    - SINGLETON is the default and returns the same mock object every time
    - PROTOTYPE returns a new mock object for every call
-   Often it is preferable to “manually” stub providers.

Example:

    import javax.inject.Inject;
    import javax.inject.Provider;

    public class SampleOrchestratingServiceWithProviders {

      @Inject
      Provider<SampleDependentServiceOne> sampleDependentServiceOneProvider;

      @Inject
      Provider<SampleDependentServiceTwo> sampleDependentServiceTwoProvider;

    }

    import static org.hypoport.mockito.MockInjector.injectMocks;
    import static org.hypoport.mockito.MockProvider.mockProvider;

    public class SampleOrchestratingServiceWithProvidersUnitTest {

      SampleOrchestratingServiceWithProviders sampleOrchestratingServiceWithProviders;
      ResultOfDependentServiceOne resultOfDependentServiceOne;

      @BeforeMethod
      public void setUp() {
        sampleOrchestratingServiceWithProviders = injectMocks(SampleOrchestratingServiceWithProviders.class);

        // by default, the Provider will return always the same instance ("SINGLETON" scope), then you can stub the result
        resultOfDependentServiceOne = new ResultOfDependentServiceOne();
        when(sampleOrchestratingServiceWithProviders.sampleDependentServiceOneProvider.get().getResult(any(ServiceInputParameter.class), any(ServiceInputParameter.class))).thenReturn(resultOfDependentServiceOne);

        // this will return always a new instance.
        sampleOrchestratingServiceWithProviders.sampleDependentServiceTwoProvider = mockProvider(SampleDependentServiceTwo.class, MockProvider.Scope.PROTOTYPE);
      }

      @Test
      public void happyPath() {

        // when
        ResultOfDependentServiceTwo resultOfOrchestratingService = sampleOrchestratingServiceWithProviders.doService(firstParameter, secondParameter);

        // Since we can not do stubbing on every new created instance the result will always be null.
        assertThat(resultOfOrchestratingService).isNull();
      }
    }

Todos
-----
-   Improve MockProvider
    - add support for google - Provider
    - remove compile time dependency for javax.inject
    - Idea: dynamic proxy implementation.
