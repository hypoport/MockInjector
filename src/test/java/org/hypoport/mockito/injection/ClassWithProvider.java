package org.hypoport.mockito.injection;


import javax.inject.Inject;
import javax.inject.Provider;

public class ClassWithProvider {

  @Inject
  Provider<SubClass<String>> classProvider;

  public static class SubClass<T> {

  }
}
