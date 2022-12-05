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
package org.pfruender.mockito.example;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.pfruender.mockito.MockInjector.injectMocks;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SampleOrchestratingServiceUnitTest {

  SampleOrchestratingService sampleOrchestratingService;
  ResultOfDependentServiceOne resultOfDependentServiceOne;
  private ServiceInputParameter firstParameter;
  private ServiceInputParameter secondParameter;

  @BeforeMethod
  public void setUp() {
    sampleOrchestratingService = injectMocks(SampleOrchestratingService.class);

    firstParameter = new ServiceInputParameter();
    secondParameter = new ServiceInputParameter();

    resultOfDependentServiceOne = new ResultOfDependentServiceOne();
    when(sampleOrchestratingService.dependentServiceOne.getResult(any(ServiceInputParameter.class), any(ServiceInputParameter.class))).thenReturn(resultOfDependentServiceOne);
  }

  @Test
  public void doService_calls_SampleDependentServiceOne() {
    // when
    sampleOrchestratingService.doService(firstParameter, secondParameter);

    // then
    verify(sampleOrchestratingService.dependentServiceOne).getResult(firstParameter, secondParameter);
  }

  @Test
  public void doService_uses_output_of_dependentServiceOne_as_input_for_dependentServiceTwo() {
    // given
    given(sampleOrchestratingService.dependentServiceOne.getResult(any(ServiceInputParameter.class), any(ServiceInputParameter.class)))
        .willReturn(resultOfDependentServiceOne);

    // when
    sampleOrchestratingService.doService(firstParameter, secondParameter);

    // then
    verify(sampleOrchestratingService.dependentServiceTwo).getResult(same(resultOfDependentServiceOne));
  }

  @Test
  public void doService_returns_the_output_of_dependentServiceTwo() {

    // given
    ResultOfDependentServiceTwo resultOfServiceTwo = new ResultOfDependentServiceTwo();
    given(sampleOrchestratingService.dependentServiceTwo.getResult(any(ResultOfDependentServiceOne.class))).willReturn(resultOfServiceTwo);

    // when
    ResultOfDependentServiceTwo resultOfOrchestratingService = sampleOrchestratingService.doService(firstParameter, secondParameter);

    // then
    assertThat(resultOfOrchestratingService).isSameAs(resultOfServiceTwo);
  }
}
