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
package org.pfruender.mockinjector.injection;

import org.pfruender.mockinjector.MockInjector;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.util.MockUtil.isMock;

public class MockInjectorTest {

  @Test
  public void injectMocks_injects_mocks_into_annotated_fields() {
    MyClass object = new MyClass();

    MockInjector.injectMocks(object);

    assertThat(isMock(object.injected)).isTrue();
    assertThat(isMock(object.autowired)).isTrue();
    assertThat(isMock(object.resource)).isTrue();
    assertThat(isMock(object.notInjected)).isFalse();
    assertThat(isMock(object.injectedProvider)).isTrue();
    assertThat(isMock(object.jakartaInjected)).isTrue();
  }

  @Test
  public void injectMocks_injects_mocks_into_annotated_private_fields() throws NoSuchFieldException, IllegalAccessException {
    MyClass object = new MyClass();

    MockInjector.injectMocks(object);

    Field privateField = object.getClass().getDeclaredField("privateField");
    privateField.setAccessible(true);

    Object mockedValueOfPrivateField = privateField.get(object);

    assertThat(isMock(mockedValueOfPrivateField)).isTrue();
  }

  @Test
  public void injectMocks_with_Class_parameter_injects_mocks_into_annotated_fields() {
    MyClass object = MockInjector.injectMocks(MyClass.class);

    assertThat(isMock(object.injected)).isTrue();
    assertThat(isMock(object.autowired)).isTrue();
    assertThat(isMock(object.resource)).isTrue();
    assertThat(isMock(object.notInjected)).isFalse();
    assertThat(isMock(object.injectedProvider)).isTrue();
    assertThat(isMock(object.jakartaInjected)).isTrue();
  }

  @Test
  public void injectMocks_injects_mocks_into_annotated_setter() {
    MyClass object = new MyClass();

    MockInjector.injectMocks(object);

    assertThat(isMock(object.setterInjectedField)).isTrue();
    assertThat(isMock(object.setter1InjectedField)).isTrue();
    assertThat(isMock(object.setter2InjectedField)).isTrue();
    assertThat(isMock(object.setterWithoutInject)).isFalse();
  }

  @Test
  public void injectMocks_with_Class_can_handle_Constructor_Injection() {
    ConstructorInjectionClass object = MockInjector.injectMocks(ConstructorInjectionClass.class);

    assertThat(isMock(object.toBeInjected1)).isTrue();
    assertThat(isMock(object.toBeInjected2)).isTrue();
  }

  @Test
  public void injectMocks_with_Class_can_handle_Constructor_Injection_without_annotation() {
    ConstructorInjectionClassWithoutAnnotation object = MockInjector.injectMocks(ConstructorInjectionClassWithoutAnnotation.class);

    assertThat(isMock(object.toBeInjected1)).isTrue();
    assertThat(isMock(object.toBeInjected2)).isTrue();
  }

}
