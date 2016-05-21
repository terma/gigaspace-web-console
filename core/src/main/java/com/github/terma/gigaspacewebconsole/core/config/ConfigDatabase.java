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

package com.github.terma.gigaspacewebconsole.core.config;

import com.github.terma.gigaspacewebconsole.core.CountsRequest;

public class ConfigDatabase {

    public String name;
    public String url;
    public String user;
    public String password;
    public String driver;
    public boolean secure;

    /**
     * Valid only for GigaSpace
     * <p>
     * That settings we use only for special case: when your space is running not by GSC (real GS cluster)
     * but that's embedded space in your application. {@link org.openspaces.admin.Admin} can't discover
     * that space by default. You will not see {@link CountsRequest}
     * To fix that set that property
     * <p>
     * {@link org.openspaces.admin.AdminFactory#discoverUnmanagedSpaces}
     */
    public boolean unmanaged;

    @Override
    public String toString() {
        return "ConfigDatabase {" + "name:'" + name + '\'' + ", url:'" + url + '\'' + ", user:'" + user + '\'' +
                ", driver:'" + driver + '\'' + ", secure:" + secure + ", unmanaged: " + unmanaged + "}";
    }

}
