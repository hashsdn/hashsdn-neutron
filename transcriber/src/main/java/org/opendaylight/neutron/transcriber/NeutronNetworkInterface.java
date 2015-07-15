/*
 * Copyright IBM Corporation, 2013.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.neutron.transcriber;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.neutron.spi.INeutronNetworkCRUD;
import org.opendaylight.neutron.spi.NeutronNetwork;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.Uuid;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.networks.rev141002.networks.attributes.Networks;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.networks.rev141002.networks.attributes.networks.Network;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.networks.rev141002.networks.attributes.networks.NetworkBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.neutron.rev150325.Neutron;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeutronNetworkInterface extends AbstractNeutronInterface<Network,NeutronNetwork> implements INeutronNetworkCRUD {
    private static final Logger LOGGER = LoggerFactory.getLogger(NeutronNetworkInterface.class);
    private ConcurrentMap<String, NeutronNetwork> networkDB = new ConcurrentHashMap<String, NeutronNetwork>();

    NeutronNetworkInterface(ProviderContext providerContext) {
        super(providerContext);
    }

    // IfNBNetworkCRUD methods

    @Override
    public boolean networkExists(String uuid) {
        return networkDB.containsKey(uuid);
    }

    @Override
    public NeutronNetwork getNetwork(String uuid) {
        if (!networkExists(uuid)) {
            return null;
        }
        return networkDB.get(uuid);
    }

    @Override
    public List<NeutronNetwork> getAllNetworks() {
        Set<NeutronNetwork> allNetworks = new HashSet<NeutronNetwork>();
        for (Entry<String, NeutronNetwork> entry : networkDB.entrySet()) {
            NeutronNetwork network = entry.getValue();
            allNetworks.add(network);
        }
        LOGGER.debug("Exiting getAllNetworks, Found {} OpenStackNetworks", allNetworks.size());
        List<NeutronNetwork> ans = new ArrayList<NeutronNetwork>();
        ans.addAll(allNetworks);
        return ans;
    }

    @Override
    public boolean addNetwork(NeutronNetwork input) {
        if (networkExists(input.getID())) {
            return false;
        }
        networkDB.putIfAbsent(input.getID(), input);
        addMd(input);
      //TODO: add code to find INeutronNetworkAware services and call newtorkCreated on them
        return true;
    }

    @Override
    public boolean removeNetwork(String uuid) {
        if (!networkExists(uuid)) {
            return false;
        }
        networkDB.remove(uuid);
        removeMd(toMd(uuid));
      //TODO: add code to find INeutronNetworkAware services and call newtorkDeleted on them
        return true;
    }

    @Override
    public boolean updateNetwork(String uuid, NeutronNetwork delta) {
        if (!networkExists(uuid)) {
            return false;
        }
/* note: because what we get is *not* a delta but (at this point) the updated
 * object, this is much simpler - just replace the value and update the mdsal
 * with it */
        networkDB.replace(uuid, delta);
        updateMd(delta);
        return true;
    }

    @Override
    public boolean networkInUse(String netUUID) {
        if (!networkExists(netUUID)) {
            return true;
        }
        NeutronNetwork target = networkDB.get(netUUID);
        if (target.getPortsOnNetwork().size() > 0) {
            return true;
        }
        return false;
    }

    protected Network toMd(NeutronNetwork network) {
        NetworkBuilder networkBuilder = new NetworkBuilder();
        networkBuilder.setAdminStateUp(network.getAdminStateUp());
        if (network.getNetworkName() != null) {
            networkBuilder.setName(network.getNetworkName());
        }
        if (network.getShared() != null) {
            networkBuilder.setShared(network.getShared());
        }
        if (network.getStatus() != null) {
            networkBuilder.setStatus(network.getStatus());
        }
        if (network.getSubnets() != null) {
            List<Uuid> subnets = new ArrayList<Uuid>();
            for( String subnet : network.getSubnets()) {
                subnets.add(toUuid(subnet));
            }
            networkBuilder.setSubnets(subnets);
        }
        if (network.getTenantID() != null) {
            networkBuilder.setTenantId(toUuid(network.getTenantID()));
        }
        if (network.getNetworkUUID() != null) {
            networkBuilder.setUuid(toUuid(network.getNetworkUUID()));
        } else {
            LOGGER.warn("Attempting to write neutron network without UUID");
        }
        return networkBuilder.build();
    }

    protected Network toMd(String uuid) {
        NetworkBuilder networkBuilder = new NetworkBuilder();
        networkBuilder.setUuid(toUuid(uuid));
        return networkBuilder.build();
    }

    @Override
    protected InstanceIdentifier<Network> createInstanceIdentifier(Network network) {
        return InstanceIdentifier.create(Neutron.class)
                .child(Networks.class)
                .child(Network.class,network.getKey());
    }

    public static void registerNewInterface(BundleContext context,
                                            ProviderContext providerContext,
                                            List<ServiceRegistration<?>> registrations) {
        NeutronNetworkInterface neutronNetworkInterface = new NeutronNetworkInterface(providerContext);
        ServiceRegistration<INeutronNetworkCRUD> neutronNetworkInterfaceRegistration = context.registerService(INeutronNetworkCRUD.class, neutronNetworkInterface, null);
        if(neutronNetworkInterfaceRegistration != null) {
            registrations.add(neutronNetworkInterfaceRegistration);
        }
    }
}
