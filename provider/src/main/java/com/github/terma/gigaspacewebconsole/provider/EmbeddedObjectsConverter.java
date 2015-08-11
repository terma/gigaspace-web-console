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

import com.gigaspaces.document.DocumentProperties;
import com.gigaspaces.entry.VirtualEntry;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

public class EmbeddedObjectsConverter {

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
        if (o instanceof VirtualEntry
                || o instanceof DocumentProperties
                || o instanceof Map
                || o instanceof Iterable)
            return GSON.toJson(o);
        else return null;
    }

}
