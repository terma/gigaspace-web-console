package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.entry.VirtualEntry;

import java.util.Map;

public class SpaceDocumentConverter {

    private static final int PADDING = 2;

    public String convert(Object o) {
        if (o instanceof VirtualEntry) {
            final VirtualEntry virtualEntry = (VirtualEntry) o;
            final StringBuilder result = new StringBuilder();
            convert(result, 0, virtualEntry);
            return result.toString();
        }
        return null;
    }

    private void convert(final StringBuilder result, final int spaces, final VirtualEntry virtualEntry) {
        result.append(virtualEntry.getTypeName());
        result.append(" (").append(virtualEntry.getClass().getName()).append(")");

        result.append(" {");
        boolean first = true;
        final Map<String, Object> properties = virtualEntry.getProperties();
        for (final Map.Entry<String, Object> property : properties.entrySet()) {
            if (first) first = false;
            else result.append(",");

            result.append("\n");
            addSpaces(result, spaces + PADDING);
            result.append(property.getKey()).append(": ");

            final Object value = property.getValue();
            if (value instanceof CharSequence) result.append("\"").append(value).append("\"");
            else if (value instanceof VirtualEntry) {
                convert(result, spaces + PADDING, (VirtualEntry) value);
            } else result.append(value);
        }
        if (properties.size() > 0) {
            result.append("\n");
            addSpaces(result, spaces);
        }
        result.append("}");
    }

    private void addSpaces(final StringBuilder result, final int count) {
        for (int i = 0; i < count; i++) result.append(' ');
    }

}
