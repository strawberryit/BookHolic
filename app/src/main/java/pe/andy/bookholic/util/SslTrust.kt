package pe.andy.bookholic.util

import okhttp3.OkHttpClient
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class SslTrust {

    companion object {
        private val trustAllSslContext: SSLContext
        private val trustAllSslSocketFactory: SSLSocketFactory

        private val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        })

        init {
            try {
                trustAllSslContext = SSLContext.getInstance("SSL")
                trustAllSslContext.init(null, trustAllCerts, java.security.SecureRandom())

                trustAllSslSocketFactory = trustAllSslContext.socketFactory
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            } catch (e: KeyManagementException) {
                throw RuntimeException(e)
            }
        }

        @JvmStatic
        fun trustAllSslClient(client: OkHttpClient): OkHttpClient {
            val builder = client.newBuilder()
            builder.sslSocketFactory(trustAllSslSocketFactory, trustAllCerts[0] as X509TrustManager)

            // SSL hostname 검증 비활성화
            builder.hostnameVerifier { _, _ -> true }
            return builder.build()
        }

    }

}
