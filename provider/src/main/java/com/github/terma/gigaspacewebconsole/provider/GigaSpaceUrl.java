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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GigaSpaceUrl {

    private static final Pattern locatorPattern = Pattern.compile("locators=([.:a-zA-Z0-9,]+)");
    private static final Pattern spacePattern = Pattern.compile("/([]a-zA-Z0-9-]+)\\?");

    private GigaSpaceUrl() {
    }

    public static String parseLocators(final String url) {
        final Matcher m = locatorPattern.matcher(url);
        if (m.find()) return m.group(1);
        else throw new IllegalArgumentException("Can't find locator in url: " + url);
    }

    public static String parseSpace(final String url) {
        final Matcher m = spacePattern.matcher(url);
        if (m.find()) return m.group(1);
        else throw new IllegalArgumentException("Can't find space in url: " + url);
    }

    public static boolean isLocal(String url) {
        return url.startsWith("/./");
    }

}
