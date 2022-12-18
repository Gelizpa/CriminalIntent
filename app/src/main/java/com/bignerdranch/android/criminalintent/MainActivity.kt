package com.bignerdranch.android.criminalintent


import CrimeFragment
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*


private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks {

    override fun onCreate(savedInstanceState:
                          Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment == null) {
            val fragment = CrimeListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
    override fun onCrimeSelected(crimeId: UUID) {
        val fragment = CrimeFragment.newInstance(crimeId)//класс MainActivity вызывает CrimeFragment.newInstance(UUID) каждый раз, когда ему потребуется создать CrimeFragment. При вызове передается значение UUID, полученное из MainActivity.onCrimeSelected(UUID).
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)//Теперь при нажатии пользователем кнопки «Назад»  транзакция будет обращена. Таким образом, CrimeFragment  будет заменен на CrimeListFragment.
            .commit()
    }//Функция FragmentTransaction.replace(Int,Fragment) заменяет фрагмент, размещенный в activity (в контейнере с указанным целочисленным идентификатором ресурса), на новый фрагмент. Если фрагмент еще не размещен в указанном контейнере, то добавляется новый фрагмент

}
