/*
 * Copyright (c) 2016 Intel Corporation  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.neutron.spi;

import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.junit.Assert;
import org.junit.Test;

public class NeutronPortQosJAXBTest {

    private static final String NEUTRON_PORT_QOS_ENABLED_SOURCE_JSON = "{ " + "\"status\": \"ACTIVE\", "
            + "\"name\": \"net1\", " + "\"admin_state_up\": true, "
            + "\"tenant_id\": \"9bacb3c5d39d41a79512987f338cf177\", "
            + "\"fixed_ips\": [ { \"ip_address\":\"192.168.111.3\" , "
            + "\"subnet_id\": \"22b44fc2-4ffb-4de4-b0f9-69d58b37ae27\" } ],"
            + "\"binding:vif_details\": {\"port_filter\": \"true\" , \"ovs_hybrid_plug\": \"false\" }, "
            + "\"extra_dhcp_opts\": [\"\"], " + "\"security_groups\": [\"\"], " + "\"allowed_address_pairs\": [\"\"], "
            + "\"device_id\": \"257614cc-e178-4c92-9c61-3b28d40eca44\", " + "\"device_owner\": \"\", "
            + "\"binding:host_id\": \"\", " + "\"binding:vif_type\": \"unbound\", "
            + "\"binding:vnic_type\": \"normal\", " + "\"mac_address\": \"fa:16:3e:c9:cb:f0\", "
            + "\"network_id\": \"e9330b1f-a2ef-4160-a991-169e56ab17f5\", "
            + "\"qos_policy_id\": \"d6220bbb-35f3-48ab-8eae-69c60aef3546\", "
            + "\"id\": \"4e8e5957-649f-477b-9e5b-f1f75b21c03c\" }";

    @Test
    public void test_PortQosEnabled_JAXB() throws JAXBException {
        NeutronPort testObject = new NeutronPort();

        NeutronPort neutronObject = (NeutronPort) JaxbTestHelper.jaxbUnmarshall(testObject,
                NEUTRON_PORT_QOS_ENABLED_SOURCE_JSON);
        Assert.assertEquals("NeutronPort JAXB Test 1: Testing id failed", "4e8e5957-649f-477b-9e5b-f1f75b21c03c",
                neutronObject.getID());

        Assert.assertEquals("NeutronPort JAXB Test 2: Testing tenant_id failed", "9bacb3c5d39d41a79512987f338cf177",
                neutronObject.getTenantID());

        Assert.assertEquals("NeutronPort JAXB Test 3: Testing network_id failed",
                "e9330b1f-a2ef-4160-a991-169e56ab17f5", neutronObject.getNetworkUUID());

        Assert.assertEquals("NeutronPort JAXB Test 4: Testing status failed", "ACTIVE", neutronObject.getStatus());

        List<NeutronIps> fixedIps = neutronObject.getFixedIps();
        Assert.assertEquals(" NeutronPort JAXB Test 5.1: Testing fixed_ips list length failed", 1, fixedIps.size());

        Assert.assertEquals(" NeutronPort JAXB Test 5.2: Testing ip_address value failed", "192.168.111.3",
                fixedIps.get(0).ipAddress);

        Assert.assertEquals(" NeutronPort JAXB Test 5.3: Testing subnet_id value failed",
                "22b44fc2-4ffb-4de4-b0f9-69d58b37ae27", fixedIps.get(0).subnetUUID);

        List<NeutronPortExtraDHCPOption> extraDHCPOptions = neutronObject.getExtraDHCPOptions();

        Assert.assertEquals("NeutronPort JAXB Test 6: Testing extra_dhcp_opts list length failed", 1,
                extraDHCPOptions.size());

        List<NeutronSecurityGroup> securityGroups = neutronObject.getSecurityGroups();
        Assert.assertEquals("NeutronPort JAXB Test 7: Testing security_groups list length failed", 1,
                securityGroups.size());

        List<NeutronPortAllowedAddressPairs> allowedAddressPairs = neutronObject.getAllowedAddressPairs();
        Assert.assertEquals("NeutronPort JAXB Test 8: Testing allowed_address_pairs list length failed", 1,
                allowedAddressPairs.size());

        Map<String, String> vifDetails = neutronObject.getVIFDetails();
        Assert.assertEquals("NeutronPort JAXB Test 9.1: Testing vif_details list length failed", 2, vifDetails.size());

        Assert.assertEquals("NeutronPort JAXB Test 9.2: Testing port_filter value failed", "true",
                vifDetails.get("port_filter"));

        Assert.assertEquals("NeutronPort JAXB Test 9.3: Testing ovs_hybrid_plug value failed", "false",
                vifDetails.get("ovs_hybrid_plug"));

        Assert.assertEquals("NeutronPort JAXB Test 10: Testing name failed", "net1", neutronObject.getName());

        Assert.assertEquals("NeutronPort JAXB Test 11: Testing admin_state_up failed", true,
                neutronObject.getAdminStateUp());

        Assert.assertEquals("NeutronPort JAXB Test 12: Testing binding:vif_type failed", "unbound",
                neutronObject.getBindingvifType());

        Assert.assertEquals("NeutronPort JAXB Test 13: Testing binding:vnic_type failed", "normal",
                neutronObject.getBindingvnicType());

        Assert.assertEquals("NeutronPort JAXB Test 14: Testing mac_address failed", "fa:16:3e:c9:cb:f0",
                neutronObject.getMacAddress());

        Assert.assertEquals("NeutronPort JAXB Test 15: Testing device_id failed",
                "257614cc-e178-4c92-9c61-3b28d40eca44", neutronObject.getDeviceID());

        Assert.assertEquals("NeutronPort JAXB Test 16: Testing device_owner failed", "",
                neutronObject.getDeviceOwner());

        Assert.assertEquals("NeutronPort JAXB Test 17: Testing binding:host_id failed", "",
                neutronObject.getBindinghostID());

        Assert.assertEquals("NeutronPort JAXB Test 18: Testing qos_policy_id failed",
                "d6220bbb-35f3-48ab-8eae-69c60aef3546", neutronObject.getQosPolicyId());
    }
}
