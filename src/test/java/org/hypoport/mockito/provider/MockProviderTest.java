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
package org.hypoport.mockito.provider;

import org.hypoport.mockito.MockProvider;
import org.testng.annotations.Test;

import static org.hypoport.mockito.MockInjector.injectMocks;
import static org.hypoport.mockito.MockProvider.mockProvider;
import static org.hypoport.mockito.MockProvider.mockProviderForMock;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class MockProviderTest {

  @Test
  public void providerAutoInjection() {

    MyClassWithProviders underTest = injectMocks(new MyClassWithProviders());

    assertThat(underTest.objectProvider.get()).isNotNull();
  }

  @Test
  public void providerAutoInjection_with_stubbing() {

    MyClassWithProviders underTest = injectMocks(new MyClassWithProviders());

    given(underTest.objectProvider.get().getSomething()).willReturn("stubbedString");

    assertThat(underTest.getSomething()).isNotNull().isEqualTo("stubbedString");
  }

  @Test
  public void mockProvider_with_SINGLETON_Scope() {

    MyClassWithProviders underTest = new MyClassWithProviders();
    underTest.objectProvider = mockProvider(IProvided.class, MockProvider.Scope.SINGLETON);

    Object firstObject = underTest.objectProvider.get();
    Object secondObject = underTest.objectProvider.get();

    assertThat(firstObject).isSameAs(secondObject);
  }

  @Test
  public void mockProvider_with_SINGLETON_Scope_and_stubbing() {

    MyClassWithProviders underTest = new MyClassWithProviders();
    underTest.objectProvider = mockProvider(IProvided.class, MockProvider.Scope.SINGLETON);

    given(underTest.objectProvider.get().getSomething()).willReturn("stubbedString");

    IProvided firstObject = underTest.objectProvider.get();
    IProvided secondObject = underTest.objectProvider.get();

    assertThat(underTest.getSomething()).isNotNull().isEqualTo("stubbedString");
    assertThat(underTest.getSomething()).isNotNull().isEqualTo("stubbedString");
    assertThat(firstObject.getSomething()).isNotNull().isEqualTo("stubbedString");
    assertThat(secondObject.getSomething()).isNotNull().isEqualTo("stubbedString");
  }

  @Test
  public void mockProvider_with_PROTOTYPE_Scope() {

    MyClassWithProviders underTest = new MyClassWithProviders();
    underTest.objectProvider = mockProvider(IProvided.class, MockProvider.Scope.PROTOTYPE);

    Object firstObject = underTest.objectProvider.get();
    Object secondObject = underTest.objectProvider.get();

    assertThat(firstObject).isNotSameAs(secondObject);
  }

  @Test
  public void mockProvider_with_deep_stubs() {
    MyClassWithProviders underTest = new MyClassWithProviders();
    underTest.objectProvider = mockProvider(IProvided.class, true);

    Object firstObject = underTest.getObject();

    assertThat(firstObject).isNotNull();
  }

  @Test
  public void mockProviderForMock_returns_the_provided_mock() {
    MyClassWithProviders underTest = new MyClassWithProviders();
    ProvidedCodedMock providedCodedMock = new ProvidedCodedMock();
    underTest.objectProvider = mockProviderForMock((IProvided) providedCodedMock);

    assertThat(underTest.objectProvider.get()).isSameAs(providedCodedMock);
    assertThat(underTest.getSomething()).isEqualTo(providedCodedMock.getSomething());
  }
}
