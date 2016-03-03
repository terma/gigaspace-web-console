/*
Copyright 2015 Artem Stasiuk

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

import com.github.terma.gigaspacewebconsole.core.GeneralRequest;
import com.github.terma.gigaspacewebconsole.provider.driver.GigaSpaceUtils;
import org.openspaces.admin.Admin;
import org.openspaces.core.GigaSpace;

import java.util.concurrent.TimeUnit;

public class AdminToEmbeddedSpace {

    public static void main(String[] args) {
        System.out.println("Create admin...");
        GeneralRequest request = new GeneralRequest();
        request.url = "jini:/*/*/embedded?locators=xuy:4174";
        Admin admin = new AdminCache().createOrGet(request).admin;

        System.out.println("Waiting space...");
        System.out.println(admin.getSpaces().waitFor("embedded", 10, TimeUnit.SECONDS).getInstances().length);
    }

}
