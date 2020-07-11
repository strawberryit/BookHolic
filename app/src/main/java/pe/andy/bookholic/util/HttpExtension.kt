package pe.andy.bookholic.util

import okhttp3.HttpUrl
import okhttp3.Request
import java.nio.charset.Charset

interface HttpExtension {

    fun HttpUrl.Builder.addQueryParameters(map: Map<String, Any>): HttpUrl.Builder {
        map.forEach { (key, value) ->
            addQueryParameter(key, value.toString())
        }
        return this
    }

    val Encoding_EUCKR: Charset
        get() = Charset.forName("EUC-KR")

    fun Request.Builder.accept(encoding: Charset): Request.Builder {
        val accept = when(encoding) {
            Encoding_EUCKR -> "text/html; charset=euc-kr"
            else -> "text/html; charset=utf-8"
        }
        return addHeader("accept", accept)
    }

    fun Request.Builder.userAgent(userAgent: String): Request.Builder {
        return addHeader("user-agent", userAgent)
    }
}