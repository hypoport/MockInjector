/**
 * Copyright 2012 HYPOPORT AG
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.hypoport.mockito;

import javax.inject.Provider;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.RETURNS_DEFAULTS;
import static org.mockito.Mockito.mock;

public class MockProvider<T> implements Provider<T> {

  public static final boolean PROVIDE_DEEP_MOCK_DEFAULT = false;

  private Class<T> providedClass;
  private boolean provideDeepStubs;
  private T singletonMock;
  private Scope scope = Scope.SINGLETON;

  public static enum Scope {
    SINGLETON,
    PROTOTYPE
  }

  public MockProvider(T providedMock) {
    this.singletonMock = providedMock;
  }

  public MockProvider(Class<T> providedClass, boolean provideDeepStubs) {
    this.providedClass = providedClass;
    this.provideDeepStubs = provideDeepStubs;
  }

  public static <T> MockProvider<T> mockProvider(Class<T> providedClass, boolean provideDeepStubs) {
    return new MockProvider<T>(providedClass, provideDeepStubs);
  }

  public static <T> MockProvider<T> mockProvider(Class<T> providedClass) {
    return new MockProvider<T>(providedClass, PROVIDE_DEEP_MOCK_DEFAULT);
  }

  public static <T> MockProvider<T> mockProvider(Class<T> providedClass, Scope scope) {
    MockProvider<T> provider = new MockProvider<T>(providedClass, PROVIDE_DEEP_MOCK_DEFAULT);
    provider.scope = scope;
    return provider;
  }

  public static <T> MockProvider<T> mockProviderForMock(T providedMock) {
    return new MockProvider<T>(providedMock);
  }

  @Override
  public T get() {
    if (scope == Scope.SINGLETON) {
      return singletonMock();
    }
    else {
      return createMock();
    }
  }

  private T singletonMock() {
    if (singletonMock == null) {
      singletonMock = createMock();
    }
    return singletonMock;
  }

  private T createMock() {
    return (T) mock(providedClass, provideDeepStubs ? RETURNS_DEEP_STUBS : RETURNS_DEFAULTS);
  }
}
