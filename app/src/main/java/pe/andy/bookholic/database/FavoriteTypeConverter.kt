package pe.andy.bookholic.database

import androidx.room.TypeConverter
import androidx.room.TypeConverters

@TypeConverters
class FavoriteTypeConverter {

    @TypeConverter
    fun fromAnyToString(property: Any?): String {
        return when (property) {
            null -> ""
            else -> toString()
        }
    }

    @TypeConverter
    fun fromStringToAny(attribute: String?): Any {
        return when(attribute) {
            null -> ""
            else -> attribute
        }
    }
}