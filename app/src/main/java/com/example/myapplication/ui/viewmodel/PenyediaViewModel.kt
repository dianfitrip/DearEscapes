package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myapplication.MyApplication

object PenyediaViewModel {
    val Factory = viewModelFactory {

        // Initializer untuk LoginViewModel
        initializer {
            LoginViewModel(
                userPreferences = aplikasiHiburan().container.userPreferences
            )
        }

        // Initializer untuk EntryViewModel
        initializer {
            EntryViewModel(
                repository = aplikasiHiburan().container.entriRepository,
                userPreferences = aplikasiHiburan().container.userPreferences
            )
        }

        // Initializer untuk HomeViewModel
        initializer {
            HomeViewModel(
                repository = aplikasiHiburan().container.entriRepository,
                userPreferences = aplikasiHiburan().container.userPreferences
            )
        }

        // Initializer untuk DetailViewModel
        initializer {
            DetailViewModel(
                repository = aplikasiHiburan().container.entriRepository
            )
        }

        // Initializer untuk UpdateViewModel
        initializer {
            UpdateViewModel(
                repository = aplikasiHiburan().container.entriRepository
            )
        }

        //ProfileViewModel
        initializer {
            ProfileViewModel(
                userPreferences = aplikasiHiburan().container.userPreferences,
                repository = aplikasiHiburan().container.entriRepository
            )
        }

        //EditProfileViewModel
        initializer {
            EditProfileViewModel(
                userPreferences = aplikasiHiburan().container.userPreferences
            )
        }

        //Initializer untuk StatistikViewModel
        initializer {
            StatistikViewModel(
                userPreferences = aplikasiHiburan().container.userPreferences
            )
        }

        initializer {
            SearchViewModel(
                repository = aplikasiHiburan().container.entriRepository,
                userPreferences = aplikasiHiburan().container.userPreferences
            )
        }
    }
}

// Extension function untuk mempermudah akses ke Application
fun CreationExtras.aplikasiHiburan(): MyApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)