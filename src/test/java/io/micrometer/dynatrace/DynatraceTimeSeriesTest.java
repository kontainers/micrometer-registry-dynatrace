/**
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.dynatrace;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

class DynatraceTimeSeriesTest {

    @Test
    void addsDimensionsValuesWhenAvailable() {
        final Map<String, String> dimensions = new HashMap<>();
        dimensions.put("first", "one");
        dimensions.put("second", "two");
        final DynatraceTimeSeries timeSeries = new DynatraceTimeSeries("custom:test.metric", 12345, 1, dimensions);
        assertThat(timeSeries.asJson()).isEqualTo("{\"timeseriesId\":\"custom:test.metric\",\"dataPoints\":[[12345,1]],\"dimensions\":{\"first\":\"one\",\"second\":\"two\"}}");
    }

    @Test
    void supportsLargeDoubles() throws Exception {
        final Map<String, String> dimensions = new HashMap<>();
        dimensions.put("first", "one");
        dimensions.put("second", "two");
        final DynatraceTimeSeries timeSeries = new DynatraceTimeSeries("custom:test.metric", 12345, 201712271200.0, dimensions);

        ExecutorService executorService = Executors.newFixedThreadPool(100);
        ArrayList<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            futures.add(executorService.submit(() -> timeSeries.asJson()));
        }
        for (Future<String> future : futures) {
            assertThat(future.get()).isEqualTo("{\"timeseriesId\":\"custom:test.metric\",\"dataPoints\":[[12345,201712271200]],\"dimensions\":{\"first\":\"one\",\"second\":\"two\"}}");
        }
        executorService.shutdownNow();
    }
}