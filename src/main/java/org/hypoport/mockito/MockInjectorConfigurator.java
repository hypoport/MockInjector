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

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MockInjectorConfigurator {

  private static HashSet<String> injectAnnotationClassesAsStrings;

  public static void setInjectAnnotations(Class<? extends Annotation>... classesToInject) {
    injectAnnotationClassesAsStrings = new HashSet<String>();
    for (Class<? extends Annotation> toInject : classesToInject) {
      injectAnnotationClassesAsStrings.add(toInject.getCanonicalName());
    }
  }

  public static Set<Class<? extends Annotation>> getInjectAnnotations() {
    if (injectAnnotationClassesAsStrings == null) {
      injectAnnotationClassesAsStrings = getDefaultInjectAnnotations();
    }

    return getClassesFromStrings(injectAnnotationClassesAsStrings);
  }

  public static Set<Class<? extends Annotation>> getClassesFromStrings(Collection<String> classesAsStrings) {
    Set<Class<? extends Annotation>> classesToInject = new HashSet<Class<? extends Annotation>>();
    for (String injectAnnotationAsString : classesAsStrings) {
      try {
        Class<?> injectAnnotationClass = Class.forName(injectAnnotationAsString);
        classesToInject.add((Class<? extends Annotation>) injectAnnotationClass);
      }
      catch (ClassNotFoundException ignore) {
        // not in classpath, is OK
      }
      catch (ClassCastException cce) {
        throw new RuntimeException("configured class " + injectAnnotationAsString + " is no annotation.", cce);
      }
    }
    return classesToInject;
  }

  public static HashSet<String> getDefaultInjectAnnotations() {
    HashSet<String> defaultAnnotations = new HashSet<String>();
    defaultAnnotations.add("jakarta.inject.Inject");
    defaultAnnotations.add("javax.inject.Inject");
    defaultAnnotations.add("javax.annotation.Resource");
    defaultAnnotations.add("org.springframework.beans.factory.annotation.Required");
    defaultAnnotations.add("org.springframework.beans.factory.annotation.Autowired");
    defaultAnnotations.add("com.google.inject.Inject");
    return defaultAnnotations;
  }
}
