package com.bignerdranch.android.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*
private const val ARG_DATE = "date"

class DatePickerFragment : DialogFragment() {
    interface Callbacks {
        fun onDateSelected(date: Date)
    }//интерфейс обратного вызова с единственной вызываемой функцией onDateSelected().
    override fun
            onCreateDialog(savedInstanceState: Bundle?):
            Dialog {
        val dateListener =
            DatePickerDialog.OnDateSetListener {//OnDateSetListener используется для получения выбранной пользователем даты.DatePicker, от которого исходит результат
                    _: DatePicker, year: Int, month: Int, day: Int ->

                val resultDate : Date =
                    GregorianCalendar(year, month, day).time//Выбранная дата предоставляется в формате года, месяца и дня, но для отправки обратно в CrimeFragment необходим объект Date. Вы передаете эти значения вGregorianCalendar и получаете доступ к свойству time, чтобы получить объект Date.
            //Полученная дата должна передаваться обратно в CrimeFragment. В свойстве targetFragment хранится экземпляр фрагмента, который запустил ваш DatePickerFragment.
                targetFragment?.let {
                        fragment ->
                    (fragment as Callbacks).onDateSelected(resultDate)//Затем экземпляр фрагмента передается в интерфейс Callbacks и вызывается функция onDateSelected(), передающая новую дату
                }
            }

        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date

        val initialYear =
            calendar.get(Calendar.YEAR)
        val initialMonth =
            calendar.get(Calendar.MONTH)
        val initialDay =
            calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(
            requireContext(),//контекстный объект, который необходим для доступа к необходимым ресурсам элемента
            dateListener, //слушатель дат
            initialYear,//это год
            initialMonth,//месяц
            initialDay// день,к которым должно быть инициализировано окно выбора даты.
        )
    }
    companion object {
        fun newInstance(date: Date):
                DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return DatePickerFragment().apply {
                arguments = args
            }
        }// функция newInstance Чтобы передать дату преступления в DatePickerFragment
    }

}