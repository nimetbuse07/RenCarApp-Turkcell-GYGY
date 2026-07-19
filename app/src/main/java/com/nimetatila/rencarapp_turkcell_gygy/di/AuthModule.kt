package com.nimetatila.rencarapp_turkcell_gygy.di

import com.nimetatila.rencarapp_turkcell_gygy.data.auth.AuthRepository
import com.nimetatila.rencarapp_turkcell_gygy.data.auth.AuthRepositoryImpl
import com.nimetatila.rencarapp_turkcell_gygy.data.vehicle.VehicleRepository
import com.nimetatila.rencarapp_turkcell_gygy.data.vehicle.VehicleRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLicenseRepository(
        licenseRepositoryImpl: com.nimetatila.rencarapp_turkcell_gygy.data.license.LicenseRepositoryImpl
    ): com.nimetatila.rencarapp_turkcell_gygy.data.license.LicenseRepository

    @Binds
    @Singleton
    abstract fun bindVehicleRepository(
        vehicleRepositoryImpl: VehicleRepositoryImpl
    ): VehicleRepository

    @Binds
    @Singleton
    abstract fun bindRentalRepository(
        rentalRepositoryImpl: com.nimetatila.rencarapp_turkcell_gygy.data.rental.RentalRepositoryImpl
    ): com.nimetatila.rencarapp_turkcell_gygy.data.rental.RentalRepository

    @Binds
    @Singleton
    abstract fun bindCardRepository(
        cardRepositoryImpl: com.nimetatila.rencarapp_turkcell_gygy.data.card.CardRepositoryImpl
    ): com.nimetatila.rencarapp_turkcell_gygy.data.card.CardRepository

    @Binds
    @Singleton
    abstract fun bindWalletRepository(
        walletRepositoryImpl: com.nimetatila.rencarapp_turkcell_gygy.data.wallet.WalletRepositoryImpl
    ): com.nimetatila.rencarapp_turkcell_gygy.data.wallet.WalletRepository
}
