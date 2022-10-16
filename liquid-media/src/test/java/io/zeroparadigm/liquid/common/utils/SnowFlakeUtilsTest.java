/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.zeroparadigm.liquid.common.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.zeroparadigm.liquid.media.utils.SnowFlakeUtils;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class SnowFlakeUtilsTest {

    @Test
    void testInvalidDatacenterOrMachine() {
        assertThatThrownBy(() -> new SnowFlakeUtils(-1, 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SnowFlakeUtils(1, -1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SnowFlakeUtils(-1, -2147483647))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testHighConcurrency() {
        SnowFlakeUtils snowFlake = new SnowFlakeUtils(1, 1);
        int generateIdNum = 1000;

        Set<Long> ids = Stream.generate(snowFlake::getNextId)
                .parallel()
                .limit(generateIdNum)
                .collect(Collectors.toSet());

        assertThat(ids).hasSize(generateIdNum);
    }
}
