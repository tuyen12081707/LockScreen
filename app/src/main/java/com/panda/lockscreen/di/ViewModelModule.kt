package com.panda.lockscreen.di

import com.panda.lockscreen.presentation.activity.TestActivity
import com.panda.lockscreen.presentation.viewmodel.ReminderViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    scope<TestActivity> {
        viewModel {
            ReminderViewModel(get())
        }
    }}