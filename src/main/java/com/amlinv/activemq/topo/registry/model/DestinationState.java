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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * State of a destination stored in the DestinationRegistry.
 *
 * Created by art on 5/2/15.
 */
public class DestinationState extends DestinationInfo {
    private final Map<String, PerBrokerInfo> brokerDetails = new HashMap<String, PerBrokerInfo>();

    public DestinationState(String name) {
        super(name);
    }

    public DestinationState(String name, String brokerName) {
        this(name);
        this.putBrokerInfo(brokerName, true);
    }

    public DestinationState(DestinationInfo dup) {
        super(dup.getName());
    }

    public void putBrokerInfo (String brokerId, boolean exists) {
        long timestamp = System.nanoTime();

        synchronized ( this.brokerDetails ) {
            //
            // If the destination no longer exists for the broker, keep the old timestamp, if available.
            //
            if ( ! exists ) {
                PerBrokerInfo info = this.brokerDetails.get(brokerId);
                if ( info != null ) {
                    timestamp = info.lastSeen;
                } else {
                    timestamp = -1;
                }
            }

            this.brokerDetails.put(brokerId, new PerBrokerInfo(timestamp, exists));
        }
    }

    /**
     * Determine if this destination currently is known to exist on any broker registered for the destination.
     *
     * @return true => the destination is known to exist on at least one broker; false => the destination is not known
     * to exist on any broker.
     */
    public boolean existsAnyBroker () {
        boolean result = false;

        synchronized ( this.brokerDetails ) {
            Iterator<PerBrokerInfo> iter = this.brokerDetails.values().iterator();

            while ( ( ! result ) && ( iter.hasNext() ) ) {
                result = iter.next().exists;
            }
        }

        return result;
    }

    /**
     * Determine whether this destination exists on the broker with the given ID.
     *
     * @param brokerId ID of the broker on which to check for existence of the destination.
     * @return true => if the destination exists on the broker with the given ID; false if the destination does not
     * exist on the broker with the given ID regardless of whether the destination has even been seen on the same
     * broker.
     */
    public boolean existsOnBroker (String brokerId) {
        boolean result = false;

        PerBrokerInfo info;
        synchronized (this.brokerDetails) {
            info = this.brokerDetails.get(brokerId);
        }

        if ((info != null) && (info.exists)) {
            result = true;
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DestinationState that = (DestinationState) o;

        boolean result = brokerDetails.equals(that.brokerDetails);

        if (result) {
            result = super.equals(o);
        }

        return result;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        return ( 31 * result ) + brokerDetails.hashCode();
    }


                                                 ////             ////
                                                 ////  INTERNALS  ////
                                                 ////             ////

    /**
     * Keep details for the destination as it pertains to a single broker.
     */
    protected static class PerBrokerInfo {
        public long lastSeen;
        public boolean exists;

        public PerBrokerInfo(long lastSeen, boolean exists) {
            this.lastSeen = lastSeen;
            this.exists = exists;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PerBrokerInfo that = (PerBrokerInfo) o;

            return exists == that.exists;

        }
    }
}
