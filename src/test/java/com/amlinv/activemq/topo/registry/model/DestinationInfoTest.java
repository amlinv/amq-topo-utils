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

import static org.junit.Assert.*;

/**
 * Created by art on 8/27/15.
 */
public class DestinationInfoTest {

    private DestinationInfo destinationInfo;

    @Before
    public void setupTest() {
        this.destinationInfo = new DestinationInfo("x-dest-name-x");
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("x-dest-name-x", this.destinationInfo.getName());
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(this.destinationInfo.equals(this.destinationInfo));
        assertTrue(this.destinationInfo.equals(new DestinationInfo("x-dest-name-x")));
        assertTrue(new DestinationInfo(null).equals(new DestinationInfo(null)));

        assertFalse(this.destinationInfo.equals(null));
        assertFalse(this.destinationInfo.equals("x-non-destinfo-x"));
        assertFalse(this.destinationInfo.equals(new DestinationInfo("x-dest-name2-x")));
        assertFalse(new DestinationInfo(null).equals(new DestinationInfo("x-dest-name-x")));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals("x-dest-name-x".hashCode(), this.destinationInfo.hashCode());
        assertEquals("x-dest-name2-x".hashCode(), new DestinationInfo("x-dest-name2-x").hashCode());
        assertEquals(0, new DestinationInfo(null).hashCode());
    }
}
