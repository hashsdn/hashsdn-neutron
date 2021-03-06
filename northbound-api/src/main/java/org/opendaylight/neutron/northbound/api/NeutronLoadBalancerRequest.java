/*
 * Copyright (c) 2014, 2015 Red Hat, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.neutron.northbound.api;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.opendaylight.neutron.spi.NeutronLoadBalancer;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public final class NeutronLoadBalancerRequest implements INeutronRequest<NeutronLoadBalancer> {
    /**
     * See OpenStack Network API v2.0 Reference for description of the following link.
     * http://docs.openstack.org/api/openstack-network/2.0/content/
     */

    @XmlElement(name = "loadbalancer")
    NeutronLoadBalancer singleton;

    @XmlElement(name = "loadbalancers")
    List<NeutronLoadBalancer> bulkRequest;

    NeutronLoadBalancerRequest() {
    }

    NeutronLoadBalancerRequest(List<NeutronLoadBalancer> bulk) {
        bulkRequest = bulk;
    }

    NeutronLoadBalancerRequest(NeutronLoadBalancer group) {
        singleton = group;
    }
}
