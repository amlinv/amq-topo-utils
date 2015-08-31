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

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
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

    @Test
    public void testExistsOnBroker() throws Exception {
        assertFalse(this.destinationState.existsOnBroker("x-broker1-x"));

        this.destinationState.putBrokerInfo("x-broker1-x", true);
        assertTrue(this.destinationState.existsOnBroker("x-broker1-x"));

        this.destinationState.putBrokerInfo("x-broker1-x", false);
        assertFalse(this.destinationState.existsOnBroker("x-broker1-x"));
    }

    @Test
    public void testEquals() throws Exception {
        assertFalse(this.destinationState.equals("xxx"));
        assertFalse(this.destinationState.equals(null));
        assertTrue(this.destinationState.equals(this.destinationState));
        assertTrue(this.destinationState.equals(new DestinationState("x-dest-name-x")));

        assertFalse(this.destinationState.equals(new DestinationState("x-dest-name2-x")));
    }

    @Test
    public void testEqualsOnBrokerDetails() throws Exception {
        this.destinationState.putBrokerInfo("x-broker-id-x", true);

        DestinationState state2 = new DestinationState("x-dest-name-x");
        assertFalse(this.destinationState.equals(state2));

        state2.putBrokerInfo("x-broker-id-x", true);
        assertTrue(this.destinationState.equals(state2));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals((31 * "x-dest-name-x".hashCode() ) + new HashMap<>().hashCode(), this.destinationState.hashCode());
        assertEquals(new HashMap<>().hashCode(), new DestinationState((String) null).hashCode());
    }

    @Test
    public void testPerBrokerEquals() throws Exception {
        DestinationState.PerBrokerInfo info1 = new DestinationState.PerBrokerInfo(11, true);
        assertTrue(info1.equals(info1));
        assertTrue(info1.equals(new DestinationState.PerBrokerInfo(11, true)));
        assertTrue(info1.equals(new DestinationState.PerBrokerInfo(12, true)));

        assertFalse(info1.equals(new DestinationState.PerBrokerInfo(11, false)));
        assertFalse(info1.equals("other-object-type"));
        assertFalse(info1.equals(null));
    }
}