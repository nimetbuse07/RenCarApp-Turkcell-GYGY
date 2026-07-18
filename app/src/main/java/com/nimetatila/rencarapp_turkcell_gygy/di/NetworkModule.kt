package com.nimetatila.rencarapp_turkcell_gygy.di

import com.nimetatila.rencarapp_turkcell_gygy.data.auth.AuthApi
import com.nimetatila.rencarapp_turkcell_gygy.data.network.AuthInterceptor
import com.nimetatila.rencarapp_turkcell_gygy.data.vehicle.VehicleApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaType
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://rencarv2.halitkalayci.com/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLicenseApi(retrofit: Retrofit): com.nimetatila.rencarapp_turkcell_gygy.data.license.LicenseApi {
        return retrofit.create(com.nimetatila.rencarapp_turkcell_gygy.data.license.LicenseApi::class.java)
    }

    @Provides
    @Singleton
    fun provideVehicleApi(retrofit: Retrofit): VehicleApi {
        return retrofit.create(VehicleApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRentalApi(retrofit: Retrofit): com.nimetatila.rencarapp_turkcell_gygy.data.rental.RentalApi {
        return retrofit.create(com.nimetatila.rencarapp_turkcell_gygy.data.rental.RentalApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCardApi(retrofit: Retrofit): com.nimetatila.rencarapp_turkcell_gygy.data.card.CardApi {
        return retrofit.create(com.nimetatila.rencarapp_turkcell_gygy.data.card.CardApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWalletApi(retrofit: Retrofit): com.nimetatila.rencarapp_turkcell_gygy.data.wallet.WalletApi {
        return retrofit.create(com.nimetatila.rencarapp_turkcell_gygy.data.wallet.WalletApi::class.java)
    }
}
