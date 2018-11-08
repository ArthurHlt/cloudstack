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
package com.cloud.network;

import com.cloud.utils.net.Ip;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class IpAddressComparatorTest {

    @Test
    public void compare() {
        List<IpAddress> addresses = Arrays.asList(
                new IPAddressTest("123.4.245.23"),
                new IPAddressTest("104.244.253.29"),
                new IPAddressTest("1.198.3.93"),
                new IPAddressTest("32.183.93.40"),
                new IPAddressTest("104.30.244.2"),
                new IPAddressTest("104.244.4.1")
        );
        Collections.sort(addresses, new IpAddressComparator());
        Assert.assertArrayEquals(addresses.toArray(), Arrays.asList(
                new IPAddressTest("1.198.3.93"),
                new IPAddressTest("32.183.93.40"),
                new IPAddressTest("104.30.244.2"),
                new IPAddressTest("104.244.4.1"),
                new IPAddressTest("104.244.253.29"),
                new IPAddressTest("123.4.245.23")
        ).toArray());
    }

    private class IPAddressTest implements IpAddress {

        private Ip ip;

        IPAddressTest(String ipAddress) {
            ip = new Ip(ipAddress);
        }

        @Override
        public long getDataCenterId() {
            return 0;
        }

        @Override
        public Ip getAddress() {
            return ip;
        }

        @Override
        public Date getAllocatedTime() {
            return null;
        }

        @Override
        public boolean isSourceNat() {
            return false;
        }

        @Override
        public long getVlanId() {
            return 0;
        }

        @Override
        public boolean isOneToOneNat() {
            return false;
        }

        @Override
        public State getState() {
            return null;
        }

        @Override
        public boolean readyToUse() {
            return false;
        }

        @Override
        public Long getAssociatedWithNetworkId() {
            return null;
        }

        @Override
        public Long getAssociatedWithVmId() {
            return null;
        }

        @Override
        public Long getPhysicalNetworkId() {
            return null;
        }

        @Override
        public void setState(State state) {

        }

        @Override
        public Long getAllocatedToAccountId() {
            return null;
        }

        @Override
        public Long getAllocatedInDomainId() {
            return null;
        }

        @Override
        public boolean getSystem() {
            return false;
        }

        @Override
        public Long getVpcId() {
            return null;
        }

        @Override
        public String getVmIp() {
            return null;
        }

        @Override
        public boolean isPortable() {
            return false;
        }

        @Override
        public Long getNetworkId() {
            return null;
        }

        @Override
        public boolean isDisplay() {
            return false;
        }

        @Override
        public Date getRemoved() {
            return null;
        }

        @Override
        public Date getCreated() {
            return null;
        }

        @Override
        public State getRuleState() {
            return null;
        }

        @Override
        public void setRuleState(State ruleState) {

        }

        @Override
        public Class<?> getEntityType() {
            return null;
        }

        @Override
        public long getDomainId() {
            return 0;
        }

        @Override
        public long getAccountId() {
            return 0;
        }

        @Override
        public String getUuid() {
            return null;
        }

        @Override
        public long getId() {
            return 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(ip);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IPAddressTest)) return false;
            IPAddressTest that = (IPAddressTest) o;
            return Objects.equals(ip, that.ip);
        }

        @Override
        public String toString() {
            return ip.toString();
        }
    }
}

