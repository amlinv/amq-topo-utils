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

package com.amlinv.activemq.topo.registry;

import com.amlinv.activemq.topo.registry.model.DestinationState;
import com.amlinv.registry.util.ConcurrentRegistry;

/**
 * Registry of destinations.  Note that no destination type is included in the registry, so each registry must be
 * specific to a single type of destinations.
 *
 * Created by art on 5/2/15.
 */
public class DestinationRegistry extends ConcurrentRegistry<String, DestinationState> {
}
