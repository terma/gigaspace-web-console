package com.github.terma.gigaspacesqlconsole.provider;

import com.gigaspaces.entry.VirtualEntry;
import com.google.gson.*;

import java.lang.reflect.Type;

public class VirtualEntryConverter {

    private static final JsonSerializer<VirtualEntry> VIRTUAL_ENTRY_SERIALIZER =
            new JsonSerializer<VirtualEntry>() {

                @Override
                public JsonElement serialize(
                        final VirtualEntry virtualEntry, final Type type,
                        final JsonSerializationContext jsonSerializationContext) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("typeName", virtualEntry.getTypeName());
                    jsonObject.add("properties", jsonSerializationContext.serialize(virtualEntry.getProperties()));
                    return jsonObject;
                }

            };

    private static final Gson GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(VirtualEntry.class, VIRTUAL_ENTRY_SERIALIZER)
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static String convert(final Object o) {
        return GSON.toJson(o);
    }

}
