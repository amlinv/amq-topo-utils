/*
 * Copyright 2015 AML Innovation & Consulting LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.amlinv.activemq.topo.registry;

import com.amlinv.activemq.topo.registry.model.DestinationState;
import org.junit.Before;
import org.junit.Test;

/**
 * Validate operation of the DestinationRegistry.  Note that the implementation currently does not contain any logic
 * that isn't inherted, so the test code here is really more of a placeholder than anything else at this point.
 *
 * Created by art on 8/27/15.
 */
public class DestinationRegistryTest {

    private DestinationRegistry registry;

    @Before
    public void setupTest() throws Exception {
        this.registry = new DestinationRegistry();
    }

    @Test
    public void testAddDestination() throws Exception {
        this.registry.put("x-dest-name-x", new DestinationState("x-dest-name-x"));
    }
}