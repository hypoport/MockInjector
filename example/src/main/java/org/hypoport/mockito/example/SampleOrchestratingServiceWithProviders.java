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

import javax.inject.Inject;
import javax.inject.Provider;

public class SampleOrchestratingServiceWithProviders {

  @Inject
  Provider<SampleDependentServiceOne> sampleDependentServiceOneProvider;

  @Inject
  Provider<SampleDependentServiceTwo> sampleDependentServiceTwoProvider;

  public ResultOfDependentServiceTwo doService(ServiceInputParameter param1, ServiceInputParameter param2) {
    ResultOfDependentServiceOne result1 = sampleDependentServiceOneProvider.get().getResult(param1, param2);
    return sampleDependentServiceTwoProvider.get().getResult(result1);
  }
}
