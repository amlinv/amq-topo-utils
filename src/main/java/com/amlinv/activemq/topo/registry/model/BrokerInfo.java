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

/**
 * Immutable identification of a broker and the primary URL used to connect to the broker.
 *
 * Created by art on 5/2/15.
 */
public class BrokerInfo {
    private final String brokerId;
    private final String brokerName;
    private final String brokerUrl;

    /**
     * Create the broker info with the given ID, name and URL.
     *
     * @param brokerId ID of the broker.
     * @param brokerName name of the broker; this should match the name assigned to the broker.
     * @param brokerUrl primary URL used to connect to the broker.
     */
    public BrokerInfo(String brokerId, String brokerName, String brokerUrl) {
        this.brokerId = brokerId;
        this.brokerName = brokerName;
        this.brokerUrl = brokerUrl;
    }

    /**
     * Retrieve the ID of the broker.
     *
     * @return broker ID.
     */
    public String getBrokerId() {
        return brokerId;
    }

    /**
     * Retrieve the name of the broker.  This should match the name assigned to the broker.
     *
     * @return broker name.
     */
    public String getBrokerName() {
        return brokerName;
    }

    /**
     * Retrieve the primary URL for connecting to the broker.
     *
     * @return broker URL.
     */
    public String getBrokerUrl() {
        return brokerUrl;
    }
}
