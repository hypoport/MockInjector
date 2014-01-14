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
package org.hypoport.mockito.example;

import org.hypoport.mockito.MockProvider;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.hypoport.mockito.MockInjector.injectMocks;
import static org.hypoport.mockito.MockProvider.mockProvider;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class SampleOrchestratingServiceWithProvidersUnitTest {

  SampleOrchestratingServiceWithProviders sampleOrchestratingServiceWithProviders;
  ResultOfDependentServiceOne resultOfDependentServiceOne;
  private ServiceInputParameter firstParameter;
  private ServiceInputParameter secondParameter;

  @BeforeMethod
  public void setUp() {
    sampleOrchestratingServiceWithProviders = injectMocks(SampleOrchestratingServiceWithProviders.class);

    firstParameter = new ServiceInputParameter();
    secondParameter = new ServiceInputParameter();

    resultOfDependentServiceOne = new ResultOfDependentServiceOne();

    // by default, the Provider will return always the same instance ("SINGLETON" scope)
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
