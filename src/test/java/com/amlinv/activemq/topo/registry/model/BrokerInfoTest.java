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
public class BrokerInfoTest {

  private BrokerInfo brokerInfo;

  @Before
  public void setupTest() throws Exception {
    this.brokerInfo = new BrokerInfo("x-broker-id-x", "x-broker-name-x", "x-broker-url-x");
  }

  @Test
  public void testGetBrokerId() throws Exception {
    assertEquals("x-broker-id-x", this.brokerInfo.getBrokerId());
  }

  @Test
  public void testGetBrokerName() throws Exception {
    assertEquals("x-broker-name-x", this.brokerInfo.getBrokerName());
  }

  @Test
  public void testGetBrokerUrl() throws Exception {
    assertEquals("x-broker-url-x", this.brokerInfo.getBrokerUrl());
  }
}