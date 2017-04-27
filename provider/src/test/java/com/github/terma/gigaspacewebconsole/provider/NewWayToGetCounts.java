/*
Copyright 2015-2017 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.gigaspacewebconsole.provider;

import com.github.terma.gigaspacewebconsole.provider.driver.GigaSpaceUtils;
import com.j_spaces.core.admin.JSpaceAdminProxy;
import org.openspaces.core.GigaSpace;

import java.rmi.RemoteException;

/**
 * Alternative way to get counts for space.
 * There are a few questions with that approach:
 * <ul>
 * <li>Because that approach is direct RMI call to Space I'm not sure how fast
 * and what pressure it creates for space. Another problem is any multi call will
 * go to GS. Compare with {@link org.openspaces.admin.Admin} where info cached locally</li>
 * <li>
 * How to get types per partitions?
 * </li>
 * </ul>
 */
public class NewWayToGetCounts {

    public static void main(String[] args) throws RemoteException {
//        GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("jini:/*/*/gs10?locators=127.0.0.1:4700");
        GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("jini:/*/*/gs95?locators=localhost:4300");
//        GigaSpace gigaSpace = GigaSpaceUtils.getGigaSpace("/./tttt");

        JSpaceAdminProxy jSpaceAdminProxy = (JSpaceAdminProxy) gigaSpace.getSpace().getAdmin();
        System.out.println(jSpaceAdminProxy.getRuntimeInfo().m_ClassNames);
        System.exit(1);
    }


}
