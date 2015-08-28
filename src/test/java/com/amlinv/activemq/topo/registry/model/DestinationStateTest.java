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

package com.amlinv.activemq.topo.registry.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by art on 8/27/15.
 */
public class DestinationStateTest {

    private DestinationState destinationState;

    @Before
    public void setupTest() throws Exception {
        this.destinationState = new DestinationState("x-dest-name-x");
    }

    @Test
    public void testAlternateConstructor() throws Exception {
        this.destinationState = new DestinationState("x-dest-name-x", "x-broker1-x");
        assertTrue(this.destinationState.existsAnyBroker());

        this.destinationState.putBrokerInfo("x-broker1-x", false);
        assertFalse(this.destinationState.existsAnyBroker());
    }

    @Test
    public void testAlternateConstructorWithDestinationInfo() throws Exception {
        this.destinationState = new DestinationState(new DestinationInfo("x-dest-name-x"));
        assertFalse(this.destinationState.existsAnyBroker());
    }

    @Test
    public void testPutBrokerInfo() throws Exception {
        assertFalse(this.destinationState.existsAnyBroker());

        this.destinationState.putBrokerInfo("x-broker1-x", true);
        assertTrue(this.destinationState.existsAnyBroker());

        this.destinationState.putBrokerInfo("x-broker2-x", true);
        assertTrue(this.destinationState.existsAnyBroker());

        this.destinationState.putBrokerInfo("x-broker1-x", false);
        assertTrue(this.destinationState.existsAnyBroker());

        this.destinationState.putBrokerInfo("x-broker2-x", false);
        assertFalse(this.destinationState.existsAnyBroker());
    }

    @Test
    public void testPutBrokerInfoFirstAsUnseen() throws Exception {
        assertFalse(this.destinationState.existsAnyBroker());

        this.destinationState.putBrokerInfo("x-broker1-x", false);
        assertFalse(this.destinationState.existsAnyBroker());
    }
}