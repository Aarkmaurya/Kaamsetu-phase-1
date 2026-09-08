package com.nexora.kaamsetu.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Minimal factory that lets each screen build its ViewModel with repositories
 * pulled from [AppContainer], without pulling in Hilt/Dagger for the MVP.
 */
class GenericViewModelFactory<T : ViewModel>(
    private val creator: () -> T
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        return creator() as VM
    }
}
