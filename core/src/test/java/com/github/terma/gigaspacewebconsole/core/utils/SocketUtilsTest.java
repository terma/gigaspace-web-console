/*
Copyright 2015-2016 Artem Stasiuk

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

package com.github.terma.gigaspacewebconsole.core.utils;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;

public class SocketUtilsTest {

    @Test(expected = InvocationTargetException.class)
    public void justUtilClassCantCreateNew() throws Exception {
        Constructor<SocketUtils> constructor = SocketUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void returnTrueIfSpecificPortOpenAcceptConnections() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            Assert.assertTrue("Port is busy!",
                    SocketUtils.isPortOpen("127.0.0.1", serverSocket.getLocalPort()));
        }
    }

    @Test
    public void returnFalseIfPort() throws IOException {
        // we expect that just closed port will be free =)
        int freePort;
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            freePort = serverSocket.getLocalPort();
        }

        Assert.assertFalse("Port is not free!", SocketUtils.isPortOpen("127.0.0.1", freePort));
    }

}
