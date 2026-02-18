package com.tk.a12testers14days.di

import com.tk.a12testers14days.MainViewModel
import com.tk.a12testers14days.AuthViewModel
import com.tk.a12testers14days.DeveloperViewModel
import com.tk.a12testers14days.TesterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { DeveloperViewModel(get(), get()) }
    viewModel { TesterViewModel(get(), get()) }
}
