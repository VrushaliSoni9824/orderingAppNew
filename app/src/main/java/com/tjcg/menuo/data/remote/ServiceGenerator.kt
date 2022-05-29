package com.tjcg.menuo.data.remote

import com.tjcg.menuo.utils.Constants
import com.tjcg.menuo.utils.Constants.CONNECTION_TIMEOUT
import com.tjcg.menuo.utils.Constants.READ_TIMEOUT
import com.tjcg.menuo.utils.Constants.WRITE_TIMEOUT
import com.tjcg.menuo.utils.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import org.apache.http.conn.ssl.SSLSocketFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.Exception
import java.lang.RuntimeException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
//import kotlin.Throws


object ServiceGenerator {
    private val client = OkHttpClient.Builder() // establish connection to server
        .connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS) // time between each byte read from the server
        .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS) // time between each byte sent to server
        .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build()
    private val retrofitBuilder = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(client)
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
    private val retrofit = retrofitBuilder.build()
    @JvmStatic
    val nentoApi = retrofit.create(ApiInterface::class.java)


    //new

    var interceptor = TokenInterceptor()
    var client1 = OkHttpClient.Builder().addInterceptor(interceptor).build()


    var retrofitBuilder2 = Retrofit.Builder()
        .client(client1)
        .baseUrl(Constants.BASE_URL_2)
        .addConverterFactory(GsonConverterFactory.create())

    private val retrofit2 = retrofitBuilder2.build()


    @JvmStatic
    val nentoApi2 = retrofit2.create(ApiInterface::class.java)

    var retrofitBuilderIntermediator = Retrofit.Builder()
        .client(getUnsafeOkHttpClient())
        .baseUrl(Constants.NOTIFY_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
    private val retrofitIntermediator = retrofitBuilderIntermediator.build()

    val nentoApiIntermediator = retrofitIntermediator.create(ApiInterface::class.java)


    fun getUnsafeOkHttpClient(): OkHttpClient? {
        return try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> =
                arrayOf<TrustManager>(object : X509TrustManager {
                    @kotlin.jvm.Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    @kotlin.jvm.Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }

                })

            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("TLS")
            sslContext.init(
                null, trustAllCerts,
                SecureRandom()
            )
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: javax.net.ssl.SSLSocketFactory? = sslContext
                .getSocketFactory()
            var okHttpClient = OkHttpClient()
            okHttpClient = okHttpClient.newBuilder()
                .sslSocketFactory(sslSocketFactory)
                .hostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build()
            okHttpClient
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

}