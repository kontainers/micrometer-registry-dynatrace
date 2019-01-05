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

import io.micrometer.core.instrument.util.DoubleFormat;
import io.micrometer.core.lang.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

class DynatraceTimeSeries {
    private final String metricId;
    private final Map<String, String> dimensions;
    private final long time;
    private final double value;

    DynatraceTimeSeries(final String metricId, final long time, final double value, @Nullable final Map<String, String> dimensions) {
        this.metricId = metricId;
        this.dimensions = dimensions;
        this.time = time;
        this.value = value;
    }

    public String getMetricId() {
        return metricId;
    }

    String asJson() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("{\"timeseriesId\":\"")
            .append(metricId)
            .append('\"')
            .append(",\"dataPoints\":[[")
            .append(time)
            .append(',')
            .append(DoubleFormat.decimalOrWhole(value))
            .append("]]");

        if (dimensions != null && !dimensions.isEmpty()) {
            sb.append(",\"dimensions\":{")
              .append(dimensions.entrySet().stream()
                            .map(t -> "\"" + t.getKey() + "\":\"" + t.getValue() + "\"")
                            .collect(Collectors.joining(",")))
              .append('}');
        }
        sb.append('}');
        return sb.toString();
    }
}
