package com.eddiez.plantirrigsys.retrofit.apiChat

import com.eddiez.plantirrigsys.base.MyApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RetrofitChatModule {

    private const val BASE_URL = "http://54.169.246.109:5000/"
//    private const val BASE_URL = "http://192.168.68.50:3003/"

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HeaderInterceptor())
        .build()

    @Provides
    @Singleton
    @Named("ChatRetrofit")
    fun provideChatRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("ProvideApiChatService")
    fun provideApiChatService(@Named("ChatRetrofit") retrofit: Retrofit): ApiChatService =
        retrofit.create(ApiChatService::class.java)
}

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .addHeader("x-api-key", MyApplication.getApiChatKey())
            .build()
        return chain.proceed(request)
    }
}