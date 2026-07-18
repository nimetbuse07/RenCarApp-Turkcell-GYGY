package com.nimetatila.rencarapp_turkcell_gygy.di

import com.nimetatila.rencarapp_turkcell_gygy.data.reservation.ReservationApi
import com.nimetatila.rencarapp_turkcell_gygy.data.reservation.ReservationRepository
import com.nimetatila.rencarapp_turkcell_gygy.data.reservation.ReservationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReservationModule {

    @Provides
    @Singleton
    fun provideReservationApi(retrofit: Retrofit): ReservationApi {
        return retrofit.create(ReservationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideReservationRepository(impl: ReservationRepositoryImpl): ReservationRepository {
        return impl
    }
}
