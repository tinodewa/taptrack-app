package com.hit.taptrack.di

import com.hit.taptrack.data.repositories.AuthRepository
import com.hit.taptrack.data.repositories.TapTrackRepository
import com.hit.taptrack.ui.features.auth.login.LoginScreenModel
import com.hit.taptrack.ui.features.auth.register.RegisterScreenModel
import com.hit.taptrack.ui.features.eventCreate.EventCreateScreenModel
import com.hit.taptrack.ui.features.eventDetail.EventDetailScreenModel
import com.hit.taptrack.ui.features.eventDiscovery.EventDiscoveryScreenModel
import com.hit.taptrack.ui.features.eventManage.EventManageScreenModel
import com.hit.taptrack.ui.features.leaderboard.LeaderboardScreenModel
import com.hit.taptrack.ui.features.myTicket.MyTicketsScreenModel
import com.hit.taptrack.ui.features.profile.ProfileScreenModel
import com.hit.taptrack.ui.features.scan.ScanScreenModel
import com.hit.taptrack.utils.helpers.UserSettings
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

// 2. Modul Common: Isinya ScreenModel/ViewModel
val commonModule = module {

    // Application Settings as a singleton, work like shared preferences
    singleOf(::UserSettings)

    single { TapTrackRepository(get(), get(), get(), get()) }
    single { AuthRepository(get(), get()) }

    factoryOf(::LoginScreenModel)
    factoryOf(::RegisterScreenModel)
    factoryOf(::EventDiscoveryScreenModel)
    factoryOf(::EventDetailScreenModel)
    factoryOf(::MyTicketsScreenModel)
    factoryOf(::LeaderboardScreenModel)
    factoryOf(::ProfileScreenModel)
    factoryOf(::ScanScreenModel)
    factoryOf(::EventManageScreenModel)
    factoryOf(::EventCreateScreenModel)
}