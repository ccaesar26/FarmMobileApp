package com.example.farmmobileapp.di

import com.example.farmmobileapp.ui.viewmodel.LoginViewModel
import com.example.farmmobileapp.ui.viewmodel.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { LoginViewModel() }
    viewModel { MainViewModel() }
}