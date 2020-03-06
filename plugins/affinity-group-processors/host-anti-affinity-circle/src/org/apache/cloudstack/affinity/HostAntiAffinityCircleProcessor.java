// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package org.apache.cloudstack.affinity;

import com.cloud.deploy.DeployDestination;
import com.cloud.deploy.DeploymentPlan;
import com.cloud.deploy.DeploymentPlanner.ExcludeList;
import com.cloud.exception.AffinityConflictException;
import com.cloud.vm.VMInstanceVO;
import com.cloud.vm.VirtualMachine;
import com.cloud.vm.VirtualMachineProfile;
import com.cloud.vm.dao.VMInstanceDao;
import org.apache.cloudstack.affinity.dao.AffinityGroupDao;
import org.apache.cloudstack.affinity.dao.AffinityGroupVMMapDao;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostAntiAffinityCircleProcessor extends AffinityProcessorBase implements AffinityGroupProcessor {

    private static final Logger s_logger = Logger.getLogger(HostAntiAffinityCircleProcessor.class);

    @Inject
    protected VMInstanceDao _vmInstanceDao;
    @Inject
    protected AffinityGroupDao _affinityGroupDao;
    @Inject
    protected AffinityGroupVMMapDao _affinityGroupVMMapDao;

    @Override
    public void process(VirtualMachineProfile vmProfile, DeploymentPlan plan, ExcludeList avoid) throws AffinityConflictException {
        VirtualMachine vm = vmProfile.getVirtualMachine();
        List<AffinityGroupVMMapVO> vmGroupMappings = _affinityGroupVMMapDao.findByVmIdType(vm.getId(), getType());

        Map<Long, Integer> hostCounters = new HashMap<>();

        for (AffinityGroupVMMapVO vmGroupMapping : vmGroupMappings) {
            if (vmGroupMapping == null) {
                continue;
            }
            AffinityGroupVO group = _affinityGroupDao.findById(vmGroupMapping.getAffinityGroupId());

            if (s_logger.isDebugEnabled()) {
                s_logger.debug("Processing affinity group " + group.getName() + " for VM Id: " + vm.getId());
            }

            List<Long> groupVMIds = _affinityGroupVMMapDao.listVmIdsByAffinityGroup(group.getId());
            groupVMIds.remove(vm.getId());

            for (Long groupVMId : groupVMIds) {
                VMInstanceVO groupVM = _vmInstanceDao.findById(groupVMId);
                if (groupVM == null || groupVM.isRemoved()) {
                    continue;
                }
                Long hostId = groupVM.getHostId();
                if (VirtualMachine.State.Stopped.equals(groupVM.getState()) && groupVM.getLastHostId() != null) {
                    hostId = groupVM.getLastHostId();
                }
                if (hostId == null) {
                    continue;
                }

                if (hostCounters.containsKey(hostId)) {
                    hostCounters.put(hostId, hostCounters.get(hostId) + 1);
                } else {
                    hostCounters.put(hostId, 1);
                }
            }
        }
        if (hostCounters.isEmpty() || this.haveSameNumberOfVms(hostCounters)) {
            return;
        }
        int minValue = this.minValueOfVms(hostCounters);

        for (Long hostId : hostCounters.keySet()) {
            if (hostCounters.get(hostId) == minValue) {
                continue;
            }
            avoid.addHost(hostId);
        }
    }

    @Override
    public boolean check(VirtualMachineProfile vmProfile, DeployDestination plannedDestination) throws AffinityConflictException {

        return true;
    }

    private int minValueOfVms(Map<Long, Integer> hostCounters) {
        Integer minValue = hostCounters.values().iterator().next();
        for (Integer nb : hostCounters.values()) {
            if (nb < minValue) {
                minValue = nb;
            }
        }
        return minValue;
    }

    private boolean haveSameNumberOfVms(Map<Long, Integer> hostCounters) {
        int previousNumber = hostCounters.values().iterator().next();
        for (Integer nb : hostCounters.values()) {
            if (nb != previousNumber) {
                return false;
            }
            previousNumber = nb;
        }
        return true;
    }

}
