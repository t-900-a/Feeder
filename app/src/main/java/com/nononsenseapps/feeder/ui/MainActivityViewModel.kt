package com.nononsenseapps.feeder.ui

import androidx.lifecycle.viewModelScope
import com.nononsenseapps.feeder.archmodel.DarkThemePreferences
import com.nononsenseapps.feeder.archmodel.Repository
import com.nononsenseapps.feeder.archmodel.ThemeOptions
import com.nononsenseapps.feeder.base.DIAwareViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.instance
import org.threeten.bp.Instant

class MainActivityViewModel(di: DI) : DIAwareViewModel(di) {
    private val repository: Repository by instance()

    fun setResumeTime() {
        repository.setResumeTime(Instant.now())
    }

    val shouldSyncOnResume: Boolean =
        repository.syncOnResume.value

    val currentTheme: StateFlow<ThemeOptions> =
        repository.currentTheme

    val darkThemePreference: StateFlow<DarkThemePreferences> =
        repository.preferredDarkTheme

    fun ensurePeriodicSyncConfigured() = viewModelScope.launch {
        repository.ensurePeriodicSyncConfigured()
    }
}
