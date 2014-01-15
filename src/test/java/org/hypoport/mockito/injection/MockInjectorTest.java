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
package org.hypoport.mockito.injection;

import org.hypoport.mockito.MockInjector;
import org.hypoport.mockito.MockInjectorConfigurator;
import org.fest.assertions.Assertions;
import org.mockito.internal.util.MockUtil;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.lang.reflect.Field;

public class MockInjectorTest {

  @BeforeSuite
  public void initInjection() {
    MockInjectorConfigurator.setInjectAnnotations(Inject.class, Resource.class);
  }

  @Test
  public void injectMocks_injects_mocks_into_annotated_fields() {
    MyClass object = new MyClass();

    MockInjector.injectMocks(object);

    MockUtil mockUtil = new MockUtil();
    Assertions.assertThat(mockUtil.isMock(object.injected)).isTrue();
    Assertions.assertThat(mockUtil.isMock(object.autowired)).isTrue();
    Assertions.assertThat(mockUtil.isMock(object.resource)).isTrue();
    Assertions.assertThat(mockUtil.isMock(object.notInjected)).isFalse();
    Assertions.assertThat(mockUtil.isMock(object.injectedProvider.get())).isTrue();
  }

  @Test
  public void injectMocks_injects_mocks_into_annotated_private_fields() throws NoSuchFieldException, IllegalAccessException {
    MyClass object = new MyClass();

    MockInjector.injectMocks(object);

    MockUtil mockUtil = new MockUtil();

    Field privateField = object.getClass().getDeclaredField("privateField");
    privateField.setAccessible(true);

    Object mockedValueOfPrivateField = privateField.get(object);

    Assertions.assertThat(mockUtil.isMock(mockedValueOfPrivateField)).isTrue();
  }

  @Test
  public void injectMocks_with_Class_parameter_injects_mocks_into_annotated_fields() {
    MyClass object = MockInjector.injectMocks(MyClass.class);

    MockUtil mockUtil = new MockUtil();
    Assertions.assertThat(mockUtil.isMock(object.injected)).isTrue();
    Assertions.assertThat(mockUtil.isMock(object.autowired)).isTrue();
    Assertions.assertThat(mockUtil.isMock(object.resource)).isTrue();
    Assertions.assertThat(mockUtil.isMock(object.notInjected)).isFalse();
    Assertions.assertThat(mockUtil.isMock(object.injectedProvider.get())).isTrue();
  }

  @Test
  public void injectMocks_injects_mocks_into_annotated_setter() {
    MyClass object = new MyClass();

    MockInjector.injectMocks(object);

    MockUtil mockUtil = new MockUtil();
    Assertions.assertThat(mockUtil.isMock(object.setterInjectedField)).isTrue();
    Assertions.assertThat(mockUtil.isMock(object.setter1InjectedField)).isTrue();
    Assertions.assertThat(mockUtil.isMock(object.setter2InjectedField)).isTrue();
    Assertions.assertThat(mockUtil.isMock(object.setterWithoutInject)).isFalse();
  }

  @Test
  public void injectMocks_with_Class_can_handle_Constructor_Injection() {
    ConstructorInjectionClass object = MockInjector.injectMocks(ConstructorInjectionClass.class);

    MockUtil mockUtil = new MockUtil();
    Assertions.assertThat(mockUtil.isMock(object.toBeInjected1)).isTrue();
    Assertions.assertThat(mockUtil.isMock(object.toBeInjected2)).isTrue();
  }

  @Test
  public void mockProvider_mocks_a_Provider_for_a_class_with_generic_type() {
    ClassWithProvider object = new ClassWithProvider();

    MockInjector.injectMocks(object);

    MockUtil mockUtil = new MockUtil();
    Assertions.assertThat(mockUtil.isMock(object.classProvider)).isTrue();
    Assertions.assertThat(mockUtil.isMock(object.classProvider.get())).isTrue();
    Assertions.assertThat(object.classProvider.get()).isInstanceOf(ClassWithProvider.SubClass.class);
  }
}
