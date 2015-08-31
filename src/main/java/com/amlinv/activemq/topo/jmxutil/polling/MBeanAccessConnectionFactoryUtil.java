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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by art on 8/30/15.
 */
public class MBeanAccessConnectionFactoryUtil {
    public static final String JMX_URL_PREFIX = "service:jmx:rmi:///jndi/rmi://";
    public static final String JMX_URL_SUFFIX = "/jmxrmi";

    private final List<MBeanAccessConnectionFactorySupplier> suppliers = new LinkedList<>();

    public static String formatJmxUrl (String hostname, int port) {
        StringBuilder result = new StringBuilder();

        result.append(JMX_URL_PREFIX);
        result.append(hostname);
        result.append(":");
        result.append(Integer.toString(port));
        result.append(JMX_URL_SUFFIX);

        return  result.toString();
    }

    public MBeanAccessConnectionFactoryUtil() {
        this.registerKnownSuppliers();
    }

    public void registerMBeanAccessConnectionFactorySupplier (MBeanAccessConnectionFactorySupplier newSupplier) {
        synchronized ( this.suppliers ) {
            this.suppliers.add(newSupplier);
        }
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
    public MBeanAccessConnectionFactory getLocationConnectionFactory (String location) throws Exception {
        MBeanAccessConnectionFactorySupplier factorySupplier;
        MBeanAccessConnectionFactory result;

        factorySupplier = this.findSupplier(location);

        if ( factorySupplier != null ) {
            result = factorySupplier.createFactory(location);
        } else {
            throw new Exception("invalid location: " + location);
        }

        return  result;
    }

    protected MBeanAccessConnectionFactorySupplier findSupplier (String location) {
        MBeanAccessConnectionFactorySupplier result = null;

        synchronized ( this.suppliers ) {
            Iterator<MBeanAccessConnectionFactorySupplier> iter = this.suppliers.iterator();
            while ( ( result == null ) && ( iter.hasNext() ) ) {
                MBeanAccessConnectionFactorySupplier candidate = iter.next();
                if ( candidate.handlesLocation(location) ) {
                    result = candidate;
                }
            }
        }

        return result;
    }

    protected void registerKnownSuppliers() {
        this.registerMBeanAccessConnectionFactorySupplier(new JMXRemoteUrlConnectionFactorySupplier());
        this.registerMBeanAccessConnectionFactorySupplier(new JMXJvmIdConnectionFactorySupplier());
        this.registerMBeanAccessConnectionFactorySupplier(new JolokiaConnectionFactorySupplier());
        this.registerMBeanAccessConnectionFactorySupplier(new JMXHostPortConnectionFactorySupplier());
    }
}
