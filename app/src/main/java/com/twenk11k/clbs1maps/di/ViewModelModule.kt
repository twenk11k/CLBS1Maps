package com.twenk11k.clbs1maps.di

import com.twenk11k.clbs1maps.ui.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { MainViewModel() }

}