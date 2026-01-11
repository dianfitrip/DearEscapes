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

        // Initializer untuk DetailViewModel (PASTIKAN HANYA ADA SATU BLOK INI)
        initializer {
            DetailViewModel(
                repository = aplikasiHiburan().container.entriRepository
            )
        }

        initializer {
            UpdateViewModel(
                repository = aplikasiHiburan().container.entriRepository
            )
        }
    }
}

fun CreationExtras.aplikasiHiburan(): MyApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)