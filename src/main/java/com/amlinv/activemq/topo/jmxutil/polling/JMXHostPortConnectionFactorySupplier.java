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
import com.amlinv.jmxutil.connection.impl.JMXRemoteUrlConnectionFactory;

/**
 * Created by art on 8/30/15.
 */
public class JMXHostPortConnectionFactorySupplier implements MBeanAccessConnectionFactorySupplier {
    @Override
    public boolean handlesLocation(String location) {
        String[] parts = location.split(":");
        if ( parts.length == 2 ) {
            try {
                Integer.valueOf(parts[1]);
                return true;
            } catch ( NumberFormatException nfExc ) {
                return false;
            }
        }
        return false;
    }

    @Override
    public MBeanAccessConnectionFactory createFactory(String location) throws Exception {
        String[] parts = location.split(":");
        int port;
        if ( parts.length == 2 ) {
            port = Integer.valueOf(parts[1]);
        } else {
            throw new Exception("invalid location: " + location);
        }

        String fullUrl = MBeanAccessConnectionFactoryUtil.formatJmxUrl(parts[0], port);

        return new JMXRemoteUrlConnectionFactory(fullUrl);
    }
}
