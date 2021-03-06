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

package com.github.terma.gigaspacewebconsole.core.config;

import java.util.ArrayList;
import java.util.List;

public class UserConfig {

    public String version;
    public List<ConfigLink> links = new ArrayList<>();
    public List<String> converters = new ArrayList<>();
    public List<ConfigDatabase> gigaspaces = new ArrayList<>();
    public List<ConfigDriver> drivers = new ArrayList<>();
    public List<ConfigTemplate> templates = new ArrayList<>();

    @Override
    public String toString() {
        return "UserConfig {" +
                "version: '" + version + '\'' +
                ", links: " + links +
                ", converters: " + converters +
                ", gigaspaces: " + gigaspaces +
                ", drivers: " + drivers +
                ", templates: " + templates +
                '}';
    }

}
