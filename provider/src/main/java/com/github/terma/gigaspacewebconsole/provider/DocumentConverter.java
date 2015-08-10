package com.github.terma.gigaspacewebconsole.provider;

import com.gigaspaces.document.DocumentProperties;
import com.gigaspaces.entry.VirtualEntry;
import com.google.gson.*;

import java.lang.reflect.Type;

public class DocumentConverter {

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
        if (o instanceof VirtualEntry || o instanceof DocumentProperties) return GSON.toJson(o);
        else return null;
    }

}
