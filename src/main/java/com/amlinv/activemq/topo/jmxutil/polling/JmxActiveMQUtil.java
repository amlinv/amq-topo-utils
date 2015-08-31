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

import com.amlinv.jmxutil.connection.MBeanAccessConnection;
import com.amlinv.jmxutil.connection.MBeanAccessConnectionFactory;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Set;

/**
 * Created by art on 3/31/15.
 */
public class JmxActiveMQUtil {
    public static final String AMQ_BROKER_QUERY = "org.apache.activemq:type=Broker,brokerName=*";
    public static final String AMQ_BROKER_DESTINATION_QUERY =
            "org.apache.activemq:type=Broker,brokerName=%s,destinationType=%s,destinationName=%s";
    public static final String AMQ_BROKER_QUEUE_QUERY =
            "org.apache.activemq:type=Broker,brokerName=%s,destinationType=Queue,destinationName=%s";

    public static final String AMQ_BROKER_NAME_KEY = "brokerName";
    public static final String AMQ_DEST_NAME_KEY = "destinationName";
    public static final String AMQ_QUEUE_NAME_KEY = "destinationName";

    private static MBeanAccessConnectionFactoryUtil connectionFactoryUtil = new MBeanAccessConnectionFactoryUtil();

    public static MBeanAccessConnectionFactoryUtil getConnectionFactoryUtil() {
        return connectionFactoryUtil;
    }

    public static void setConnectionFactoryUtil(MBeanAccessConnectionFactoryUtil connectionFactoryUtil) {
        JmxActiveMQUtil.connectionFactoryUtil = connectionFactoryUtil;
    }

    public static String formatJmxUrl (String hostname, int port) {
        return connectionFactoryUtil.formatJmxUrl(hostname, port);
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
        return connectionFactoryUtil.getLocationConnectionFactory(location);
    }

    public static String[] queryBrokerNames (String location) throws Exception {
        String[] names;

        Set<ObjectName> matches;
        matches = execLocationQuery(location, new ObjectName(AMQ_BROKER_QUERY));

        names = new String[matches.size()];
        int cur = 0;

        for ( ObjectName oneBrokerMBeanName : matches ) {
            names[cur] = oneBrokerMBeanName.getKeyProperty(AMQ_BROKER_NAME_KEY);
            cur++;
        }

        return  names;
    }

    public static String[] queryQueueNames (String location, String brokerName, String queueNamePattern)
            throws Exception {

        String[] names = null;

        Set<ObjectName> matches;
        String pattern = String.format(AMQ_BROKER_QUEUE_QUERY, brokerName, queueNamePattern);
        matches = execLocationQuery(location, new ObjectName(pattern));

        names = new String[matches.size()];
        int cur = 0;

        for ( ObjectName oneQueueMBeanName : matches ) {
            names[cur] = oneQueueMBeanName.getKeyProperty(AMQ_QUEUE_NAME_KEY);
            cur++;
        }

        return  names;
    }

    public static ObjectName getDestinationObjectName(String brokerName, String destinationName, String destinationType)
            throws MalformedObjectNameException {

        String name = String.format(AMQ_BROKER_DESTINATION_QUERY, brokerName, destinationType, destinationName);

        return new ObjectName(name);
    }

    public static String extractDestinationName (ObjectName mbeanName) {
        return mbeanName.getKeyProperty(AMQ_DEST_NAME_KEY);
    }

    protected static Set<ObjectName> execLocationQuery (String location, ObjectName pattern) throws Exception {
        MBeanAccessConnectionFactory factory = getLocationConnectionFactory(location);
        MBeanAccessConnection connection = factory.createConnection();

        Set<ObjectName> matches = connection.queryNames(pattern, null);

        connection.close();

        return  matches;
    }
}
