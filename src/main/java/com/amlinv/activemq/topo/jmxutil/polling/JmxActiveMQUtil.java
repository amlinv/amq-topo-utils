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

package com.amlinv.activemq.topo.jmxutil.polling;

import com.amlinv.jmxutil.connection.MBeanAccessConnectionFactory;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Set;

/**
 * JMX ActiveMQ Utility.  Please use JmxActiveMQUtil2.  This class will be removed in a subsequent release.
 *
 * Created by art on 3/31/15.
 */
@Deprecated
public class JmxActiveMQUtil {
    private static final JmxActiveMQUtil2 INSTANCE = new JmxActiveMQUtil2();

    public static MBeanAccessConnectionFactoryUtil getConnectionFactoryUtil() {
        return INSTANCE.getConnectionFactoryUtil();
    }

    public static void setConnectionFactoryUtil(MBeanAccessConnectionFactoryUtil connectionFactoryUtil) {
        INSTANCE.setConnectionFactoryUtil(connectionFactoryUtil);
    }

    public static String formatJmxUrl (String hostname, int port) {
        return INSTANCE.formatJmxUrl(hostname, port);
    }

    /**
     * Supports the following formats:
     * <ul>
     *     <li>Full JMX url starting with "service:" (e.g. service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi)</li>
     *     <li>JVM ID starting with "jvmId=" or "pid="</li>
     *     <li>Jolokia URL starting with "jolokia:" (e.g. jolokia:http://localhost:8161/api/jolokia)</li>
     *     <li>Broker hostname and port separated by a colon (e.g. localhost:1099)</li>
     * </ul>
     * @param location
     * @return
     */
    public static MBeanAccessConnectionFactory getLocationConnectionFactory (String location) throws Exception {
        return INSTANCE.getLocationConnectionFactory(location);
    }

    public static String[] queryBrokerNames (String location) throws Exception {
        return INSTANCE.queryBrokerNames(location);
    }

    public static String[] queryQueueNames (String location, String brokerName, String queueNamePattern)
            throws Exception {
        return INSTANCE.queryQueueNames(location, brokerName, queueNamePattern);
    }

    public static ObjectName getDestinationObjectName(String brokerName, String destinationName, String destinationType)
            throws MalformedObjectNameException {

        return INSTANCE.getDestinationObjectName(brokerName, destinationName, destinationType);
    }

    public static String extractDestinationName (ObjectName mbeanName) {
        return INSTANCE.extractDestinationName(mbeanName);
    }
}
