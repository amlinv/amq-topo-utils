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

package com.amlinv.activemq.topo.discovery;

import com.amlinv.activemq.topo.jmxutil.polling.JmxActiveMQUtil2;
import com.amlinv.activemq.topo.registry.DestinationRegistry;
import com.amlinv.activemq.topo.registry.model.DestinationState;
import com.amlinv.jmxutil.connection.MBeanAccessConnection;
import com.amlinv.jmxutil.connection.MBeanAccessConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by art on 5/20/15.
 */
public class MBeanDestinationDiscoverer {
    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(MBeanDestinationDiscoverer.class);

    private Logger log = DEFAULT_LOGGER;

    private final String brokerId;

    private String brokerName = "*";
    private String destinationType;

    private DestinationRegistry registry;
    private MBeanAccessConnectionFactory mBeanAccessConnectionFactory;
    private JmxActiveMQUtil2 jmxActiveMQUtil = new JmxActiveMQUtil2();

    /**
     * Type of destination to discover, and which are maintained in the given registry.
     *
     * @param destinationType type of destination; must match them type in the MBean name (destinationType attribute):
     *                        Queue, Topic, etc.
     */
    public MBeanDestinationDiscoverer(String destinationType, String brokerId) {
        this.destinationType = destinationType;
        this.brokerId = brokerId;
    }

    public DestinationRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(DestinationRegistry registry) {
        this.registry = registry;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }

    public MBeanAccessConnectionFactory getmBeanAccessConnectionFactory() {
        return mBeanAccessConnectionFactory;
    }

    public void setmBeanAccessConnectionFactory(MBeanAccessConnectionFactory mBeanAccessConnectionFactory) {
        this.mBeanAccessConnectionFactory = mBeanAccessConnectionFactory;
    }

    public JmxActiveMQUtil2 getJmxActiveMQUtil() {
        return jmxActiveMQUtil;
    }

    public void setJmxActiveMQUtil(JmxActiveMQUtil2 jmxActiveMQUtil) {
        this.jmxActiveMQUtil = jmxActiveMQUtil;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    /**
     * Poll for destinations and update the registry with new destinations found, and clean out destinations not found
     * and not existing on any broker.
     *
     * @throws IOException
     */
    public void pollOnce () throws IOException {
        MBeanAccessConnection connection = this.mBeanAccessConnectionFactory.createConnection();

        //
        // Set of Queues known at start in order to detect Queues lost.  This set may contain queues already known to
        //  have been lost.
        //
        Set<String> remainingQueues = new HashSet<>(this.registry.keys());

        try {
            ObjectName destinationPattern =
                    this.jmxActiveMQUtil.getDestinationObjectName(this.brokerName, "*", this.destinationType);

            Set<ObjectName> found = connection.queryNames(destinationPattern, null);

            //
            // Iterate over the mbean names matching the pattern, extract the destination name, and process.
            //
            for ( ObjectName oneDestOName : found ) {
                String destName = this.jmxActiveMQUtil.extractDestinationName(oneDestOName);

                this.onFoundDestination(destName);
                remainingQueues.remove(destName);
            }

            //
            // Mark any queues remaining in the expected queue set as not known by the broker.
            //
            for ( String missingQueue : remainingQueues ) {
                DestinationState destState = this.registry.get(missingQueue);
                if ( destState != null ) {
                    // Remove now if not known by any broker.
                    if ( ! destState.existsAnyBroker() ) {
                        this.registry.remove(missingQueue);
                    } else {
                        destState.putBrokerInfo(this.brokerId, false);
                    }
                }
            }
        } catch (MalformedObjectNameException monExc) {
            throw new RuntimeException("unexpected object name failure for destinationType=" + this.destinationType,
                    monExc);
        } finally {
            this.safeClose(connection);
        }
    }

    /**
     * For the destination represented by the named mbean, add the destination to the registry.
     *
     * @param destName name of the destination.
     */
    protected void onFoundDestination (String destName) {
        if ( ( destName != null ) && ( ! destName.isEmpty() ) ) {
            DestinationState destState =
                    this.registry.putIfAbsent(destName, new DestinationState(destName, this.brokerId));

            //
            // If it was already there, mark it as seen now by the broker.
            //
            if ( destState != null ) {
                destState.putBrokerInfo(this.brokerId, true);
            }
        }
    }

    protected void safeClose (MBeanAccessConnection connection) {
        try {
            connection.close();
        } catch ( IOException ioExc ) {
            log.warn("failed to close mbean access connection cleanly", ioExc);
        }
    }
}
