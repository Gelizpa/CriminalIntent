package com.bignerdranch.android.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

private const val TAG = "CrimeListFragment"
class CrimeListFragment : Fragment() {
    /**
     * Требуемый интерфейс
     */
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }
    private var callbacks: Callbacks? = null
    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? =  CrimeAdapter(emptyList())
    private val crimeListViewModel:
            CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }//Функция жизненного цикла Fragment.onAttach(Context) вызывается, когда фрагмент прикреплется к activity.Здесь вы помещаете аргумент Context,переданный функции onAttach(...), в свойство callback.
//Помните, что Activity является подклассом Context, поэтому функция onAttach(...) передает в качестве параметра объект Context, который более гибок.

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
    }//В функции CrimeListFragment.onCreate(Bundle?)сообщаем FragmentManager, что экземплярCrimeListFragment должен получать обратные вызовы меню.

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            val view =
                inflater.inflate(R.layout.fragment_crime_list,
                    container, false)
            crimeRecyclerView =
                view.findViewById(R.id.crime_recycler_view) as RecyclerView
            crimeRecyclerView.layoutManager =
                LinearLayoutManager(context)
            crimeRecyclerView.adapter = adapter
            return view
        }
    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)//Fragment.onViewCreated(...) вызывается после возврата Fragment.onCreateView(...), давая понять, что иерархия представления фрагмента находится на месте.
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner, Observer { crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes${crimes.size}")
                    updateUI(crimes)//Когда список преступлений готов, наблюдатель выводит сообщение и посылает список в updateUI()для подготовки адаптера
                }
            })
    }
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }//Здесь переменную устанавливают равной нулю, так как в дальнейшем вы не сможете получить доступ к activity или рассчитывать на то, что она будет продолжать существовать.

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu,
            inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }//заполняет меню, определенное в файле fragment_crime_list.xml
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }//функция onOptionsItemSelected(MenuItem), реагирует на выбор команды меню. Реализация создает новый объект Crime,добавляет его в базу данных и затем уведомляет родительскую activity о том, что запрошено добавление нового преступления.

    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }//параметр, позволяющий брать на вход список преступлений.


    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener {
        private lateinit var crime: Crime
        private val titleTextView: TextView =
            itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView =
            itemView.findViewById(R.id.crime_solved)
        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime)
        {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text =
                this.crime.date.toString()
            solvedImageView.visibility = if
                                                 (crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }

            dateTextView.text =
                this.crime.date.toString()}

        override fun onClick(v: View) {
                callbacks?.onCrimeSelected(crime.id)//обновить слушателя кликов для отдельных  элементов в списке преступлений таким образом, чтобы  нажатие на преступление уведомляло хост-activity через  интерфейс Callbacks.
        }
    }



    private inner class CrimeAdapter(var crimes:List<Crime>): RecyclerView.Adapter<CrimeHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
        )
                : CrimeHolder {
            val view =
                layoutInflater.inflate(
                    R.layout.list_item_crime, parent, false
                )
            return CrimeHolder(view)
        }

        override fun getItemCount() =
            crimes.size

        override fun onBindViewHolder(
            holder:
            CrimeHolder, position: Int
        ) {
            val crime = crimes[position]
            holder.bind(crime)

        }
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }}
