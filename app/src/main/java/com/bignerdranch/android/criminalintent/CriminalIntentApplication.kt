package com.bignerdranch.android.criminalintent

import android.app.Application


class CriminalIntentApplication : Application()
{
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}//Application. Он позволит вам получить информацию о жизненном цикле самого приложения
