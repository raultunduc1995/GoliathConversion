package com.example.goliathconversion.dependency

import com.example.goliathconversion.BuildConfig
import com.example.goliathconversion.repository.TransactionsRepository
import com.example.goliathconversion.repository.TransactionsRepositoryImpl
import com.example.goliathconversion.repository.api.TransactionsApi
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val ACCEPT_HEADER = "Accept"

    @Provides
    @Singleton
    fun getOkHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    @Provides
    @Singleton
    fun getOkHttpHeaderInterceptor(): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val originalRequest = chain.request()
                val originalUrl = originalRequest.url
                val url = originalUrl.newBuilder()
                    .addQueryParameter(ACCEPT_HEADER, "application/json")
                    .build()
                val request = originalRequest.newBuilder().url(url).build()

                return chain.proceed(request)
            }
        }
    }

    @Provides
    @Singleton
    fun getOkHttpClient(
        okHttpLoggingInterceptor: HttpLoggingInterceptor,
        okHttpHeaderInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(okHttpLoggingInterceptor)
            .addInterceptor(okHttpHeaderInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun getRetrofitApi(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.AIRTOUCH_MEDIA_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun getTransactionApi(retrofit: Retrofit): TransactionsApi {
        return retrofit.create(TransactionsApi::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class BindingModule {

    @Binds
    @Singleton
    abstract fun bindTransactionsRepository(
        transactionsRepositoryImpl: TransactionsRepositoryImpl
    ): TransactionsRepository
}
