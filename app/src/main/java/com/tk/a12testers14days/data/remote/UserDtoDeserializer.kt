package com.tk.a12testers14days.data.remote

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class UserDtoDeserializer : JsonDeserializer<UserDto> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): UserDto {
        return if (json.isJsonPrimitive && json.asJsonPrimitive.isString) {
            // It's a string (ObjectId), so create a dummy UserDto with just the ID
            UserDto(
                _id = json.asString,
                displayName = "Unknown User",
                email = "",
                role = "tester" // Default fallback
            )
        } else {
            // It's an object, let Gson deserialize it normally
            // We need to avoid infinite recursion, so we manually parse or use a workaround
            // Simple way: Manually parse fields to be safe
            val obj = json.asJsonObject
            UserDto(
                _id = obj.get("_id")?.asString ?: "",
                displayName = obj.get("displayName")?.asString ?: "Unknown",
                email = obj.get("email")?.asString ?: "",
                role = obj.get("role")?.asString ?: "tester"
            )
        }
    }
}
