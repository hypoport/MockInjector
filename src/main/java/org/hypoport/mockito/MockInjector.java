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

import org.mockito.internal.util.MockCreationValidator;

import javax.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Tool to inject all fields of any class <ul> <li>supports constructor injection, field injection and setter injection</li>
 * <li>does not inject via setter, if one parameter is not mockable (e.g.: String) in order to avoid NullPointerExceptions</li>
 * <li>supports javax, spring and guice annotations by default</li> <li>supported annotations can be configured using {@link
 * MockInjectorConfigurator} before the first call</li> <li>supports javax.inject.Provider as provider by default returning the
 * same mock object every time {@link org.hypoport.mockito.MockProvider}</li> </ul>
 */
public class MockInjector {

  static final Set<Class<? extends Annotation>> INJECTION_ANNOTATIONS = MockInjectorConfigurator.getInjectAnnotations();

  /**
   * injects all injection annotated fields with mocks no constructor injection possible ;-)
   *
   * @param object to be filled with mocks
   * @param <T>
   *
   * @return the mock object from the argument
   */
  public static <T> T injectMocks(T object) {
    try {
      injectFieldsAndSetters(object, object.getClass());
    }
    catch (RuntimeException e) {
      throw e;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    return object;
  }

  /**
   * creates an instance of the given class and injects all injection annotated fields with mocks
   *
   * @param class to be instantiated and filled with mocks
   * @param <T>
   *
   * @return the instantiated object
   */
  public static <T> T injectMocks(Class<T> clazz) {
    try {
      Constructor<?>[] constructors = clazz.getDeclaredConstructors();
      for (Constructor<?> constructor : constructors) {
        if (shouldBeInjected(constructor.getDeclaredAnnotations()) || constructor.getParameterTypes().length == 0) {
          constructor.setAccessible(true);
          Class<?>[] parameterTypes = constructor.getParameterTypes();
          Object[] mocks = createMocksForParameterTypes(parameterTypes);
          T instantiated = (T) constructor.newInstance(mocks);
          injectFieldsAndSetters(instantiated, clazz);
          return instantiated;
        }
      }
      T object = clazz.newInstance();
      injectFieldsAndSetters(object, clazz);
      return object;
    }
    catch (RuntimeException e) {
      throw e;
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void injectFieldsAndSetters(Object object, Class<?> objectClass) throws IllegalAccessException, InvocationTargetException {
    injectFields(object, objectClass);
    injectSetter(object, objectClass);
  }

  public static void injectFields(Object object, Class<?> objectClass) throws IllegalAccessException, InvocationTargetException {
    Class<?> superclass = objectClass.getSuperclass();
    if (superclass != null) {
      injectFields(object, superclass);
    }
    mockFieldsIfInjected(object, objectClass);
  }

  public static void injectSetter(Object object, Class<?> objectClass) throws IllegalAccessException, InvocationTargetException {
    Class<?> superclass = objectClass.getSuperclass();
    if (superclass != null) {
      injectSetter(object, superclass);
    }
    mockSetterIfInjected(object, objectClass);
  }

  private static void mockFieldsIfInjected(Object object, Class<?> objectClass) throws IllegalAccessException {
    for (Field field : objectClass.getDeclaredFields()) {
      mockFieldIfInjected(object, field);
    }
  }

  private static void mockSetterIfInjected(Object object, Class<?> objectClass) throws InvocationTargetException, IllegalAccessException {
    for (Method method : objectClass.getDeclaredMethods()) {
      injectMockIfInjectionSetterExists(object, method);
    }
  }

  private static void injectMockIfInjectionSetterExists(Object object, Method method) throws InvocationTargetException, IllegalAccessException {
    if (shouldBeInjected(method.getDeclaredAnnotations())) {
      mockSetter(object, method);
    }
  }

  private static void mockFieldIfInjected(Object object, Field field) throws IllegalAccessException {
    if (shouldBeInjected(field.getAnnotations())) {
      mockField(object, field);
    }
  }

  private static boolean shouldBeInjected(Annotation[] annotations) {
    for (Annotation annotation : annotations) {
      if (INJECTION_ANNOTATIONS.contains(annotation.annotationType())) {
        return true;
      }
    }
    return false;
  }

  private static void mockSetter(Object object, Method method) throws InvocationTargetException, IllegalAccessException {
    method.setAccessible(true);
    Class<?>[] parameterTypes = method.getParameterTypes();

    Object[] mocks = createMocksForParameterTypes(parameterTypes);
    if (!anyNull(mocks)) { // avoid NullPointerExceptions in setter-code
      method.invoke(object, mocks);
    }
  }

  private static boolean anyNull(Object[] mocks) {
    for (Object mock : mocks) {
      if (mock == null) {
        return true;
      }
    }
    return false;
  }

  private static Object[] createMocksForParameterTypes(Class<?>[] parameterTypes) {
    Object[] mocks = new Object[parameterTypes.length];
    for (int i = 0; i < parameterTypes.length; i++) {
      mocks[i] = mockIfMockable(parameterTypes[i]);
    }
    return mocks;
  }

  private static Object mockIfMockable(Class parameterType) {
    if (new MockCreationValidator().isTypeMockable(parameterType)) {
      return mock(parameterType);
    }
    return null;
  }

  private static void mockField(Object object, Field field) throws IllegalAccessException {
    field.setAccessible(true);
    if (Provider.class.isAssignableFrom(field.getType())) {
      field.set(object, mockProvider(field.getGenericType()));
    }
    else if (!Modifier.isFinal(field.getType().getModifiers())) {
      Object mock = mockIfMockable(field.getType());
      if (mock != null) {
        field.set(object, mock);
      }
    }
  }

  private static MockProvider mockProvider(Type fieldGenericType) {
    Type providedType = ((ParameterizedType) fieldGenericType).getActualTypeArguments()[0];
    if (providedType instanceof ParameterizedType) {
      providedType = ((ParameterizedType) providedType).getRawType();
    }
    return spy(MockProvider.mockProvider((Class) providedType));
  }
}
