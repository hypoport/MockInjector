/*
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

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MockInjectorConfigurator {

  private static final Set<String> injectAnnotationClassesAsStrings = Set.of(
          "javax.inject.Inject",
          "javax.annotation.Resource",
          "org.springframework.beans.factory.annotation.Required",
          "org.springframework.beans.factory.annotation.Autowired",
          "com.google.inject.Inject",
          "io.quarkus.arc.log.LoggerName"
  );

  public static Set<Class<? extends Annotation>> getInjectAnnotations() {
    return injectAnnotationClassesAsStrings.stream()
            .map(MockInjectorConfigurator::findClassForName)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
  }

  private static Class<? extends Annotation>  findClassForName(String className) {
    try {
      Class<?> injectAnnotationClass = Class.forName(className);
      return (Class<? extends Annotation>) injectAnnotationClass;
    }
    catch (ClassNotFoundException ignore) {
      // not in classpath, is OK
      return null;
    }
    catch (ClassCastException cce) {
      throw new RuntimeException("configured class " + className + " is no annotation.", cce);
    }
  }
}
