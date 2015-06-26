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

class AdminCacheKey {

    public final String locators;
    public final String user;
    public final String password;

    public AdminCacheKey(String locators, String user, String password) {
        this.locators = locators;
        this.user = user;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdminCacheKey key = (AdminCacheKey) o;

        if (locators != null ? !locators.equals(key.locators) : key.locators != null) return false;
        if (password != null ? !password.equals(key.password) : key.password != null) return false;
        if (user != null ? !user.equals(key.user) : key.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = locators != null ? locators.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "locators: " + locators + ", user: " + user;
    }

}
