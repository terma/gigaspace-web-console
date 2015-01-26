package com.github.terma.gigaspacesqlconsole.provider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GigaSpaceUrl {

    private static final Pattern locatorPattern = Pattern.compile("locators=([.:a-zA-Z0-9,]+)");
    private static final Pattern spacePattern = Pattern.compile("/([]a-zA-Z0-9-]+)\\?");

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
