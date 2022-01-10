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

import org.fest.assertions.Assertions;
import org.hypoport.mockito.MockInjector;
import org.hypoport.mockito.MockInjectorConfigurator;
import org.mockito.internal.util.MockUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

public class MockInjectorWithAdditionalInjectConfigurationTest {

  @BeforeClass
  public void configureMockInjector() {
    MockInjectorConfigurator.setInjectAnnotations(AdditionalInject.class);
  }

  @Test
  public void should_allow_configuration_of_extra_Annotations() {
    AClassWithAdditionalInject clazz = MockInjector.injectMocks(AClassWithAdditionalInject.class);
    Assertions.assertThat(MockUtil.isMock(clazz.someBean)).isTrue();
  }
}
