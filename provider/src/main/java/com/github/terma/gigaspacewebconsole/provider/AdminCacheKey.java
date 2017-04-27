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

class AdminCacheKey {

    public final String locators;
    public final String user;
    public final String password;
    public final boolean unmanaged;

    public AdminCacheKey(String locators, String user, String password, boolean unmanaged) {
        this.locators = locators;
        this.user = user;
        this.password = password;
        this.unmanaged = unmanaged;
    }

    @Override
    public String toString() {
        return "AdminCacheKey {locators: '" + locators + '\'' +
                ", user: '" + user + "\', password='****'" + ", unmanaged: " + unmanaged + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdminCacheKey that = (AdminCacheKey) o;

        if (unmanaged != that.unmanaged) return false;
        if (locators != null ? !locators.equals(that.locators) : that.locators != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        return password != null ? password.equals(that.password) : that.password == null;

    }

    @Override
    public int hashCode() {
        int result = locators != null ? locators.hashCode() : 0;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (unmanaged ? 1 : 0);
        return result;
    }

}
