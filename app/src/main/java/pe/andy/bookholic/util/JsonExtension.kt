package pe.andy.bookholic.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import java.text.DateFormat
import java.text.SimpleDateFormat

interface JsonExtension {

    val formatter: DateFormat
        get() = SimpleDateFormat.getDateTimeInstance()

    fun String.parseInt(jsonPtrExpr: String): Int {
        try {
            return ObjectMapper()
                    .setDateFormat(formatter)
                    .readTree(this)
                    .at(jsonPtrExpr)
                    .asInt(-1)
        } catch (e: Exception) {
            e.printStackTrace()
            return -1
        }
    }

    fun String.parseToListMap(jsonPtrExpr: String): List<Map<String, String>> {
        try {
            val typeRef = object : TypeReference<Map<String, String>>() {}
            val mapper = ObjectMapper()
                    .setDateFormat(formatter)

            return mapper.readTree(this)
                    .at(jsonPtrExpr)
                    .asSequence()
                    .map {
                        mapper.readValue(it.traverse(), typeRef)
                    }
                    .toList()
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
}