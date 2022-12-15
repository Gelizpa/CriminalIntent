package com.bignerdranch.android.criminalintent

import androidx.room.Room
import database.CrimeDatabase
import android.content.Context
import androidx.lifecycle.LiveData
import java.util.*


private const val DATABASE_NAME = "crimedatabase"

class CrimeRepository private constructor(context: android.content.Context) {


    private val database : CrimeDatabase = Room.databaseBuilder(context.applicationContext,
        CrimeDatabase::class.java, DATABASE_NAME).build()
    private val crimeDao = database.crimeDao()
    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }//Функция запроса будет работать не очень хорошо, если не вызвать перед ней функцию Initialize(). Она выбросит исключение IllegalStateException
        }
        fun get(): CrimeRepository {
            return INSTANCE ?:
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }//CrimeRepository — это одноэлементный класс (синглтон).Это означает, что в вашем процессе приложения единовременно существует только один его экземпляр.
}


