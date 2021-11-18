package com.app.azovatask.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.azovatask.R
import com.app.azovatask.adapter.NewsFeedAdapter
import com.app.azovatask.databinding.ActivityNewsFeedBinding
import com.app.azovatask.utils.Utils
import com.app.azovatask.viewModel.NewsFeedViewModel
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import kotlinx.android.synthetic.main.calender_item.view.*
import okhttp3.internal.Util
import timber.log.Timber
import java.util.*

class ActivityNewsFeed : AppCompatActivity() {

    private lateinit var binding: ActivityNewsFeedBinding
    private lateinit var viewModel: NewsFeedViewModel
    private lateinit var adapter: NewsFeedAdapter
    private val calender = Calendar.getInstance()
    private var currentMonth = 0
    private lateinit var today: Date
    private lateinit var tomorrow: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsFeedBinding.inflate(layoutInflater)
        val root = binding.root
        setContentView(root)

        viewModel = ViewModelProvider(this).get(NewsFeedViewModel::class.java)

        viewModel.getNewsFeeds().observe(this, {
            adapter.notifyDataSetChanged()
//            if (it.isEmpty()) {
//                binding.tvNoDataFound.visibility = View.VISIBLE
//                binding.recyclerView.visibility = View.GONE
//            }else {
//                binding.tvNoDataFound.visibility = View.GONE
//                binding.recyclerView.visibility = View.VISIBLE
//            }
        })

        initNews()
    }

    private fun initNews() {

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)

        adapter = NewsFeedAdapter(this, viewModel.getNewsFeeds())
        binding.recyclerView.adapter = adapter

        setCalender()

        binding.etSearch.addTextChangedListener {
            Timber.e(it.toString())
            if (it?.trim().toString().isNotEmpty())
                viewModel.searchNews(today, tomorrow, it.toString())
            else
                viewModel.searchNews(today, tomorrow, "")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCalender() {
        calender.time = Date()
        currentMonth = calender[Calendar.MONTH]

        binding.tvCurrentMonthYear.text = "${DateUtils.getMonthName(Date())}, ${
            DateUtils.getYear(Date())
        }"

        binding.singleRowCalendar.calendarViewManager = object : CalendarViewManager {

            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                /**
                 * calender custom date item layout set here
                 */
                return if (isSelected)
                    R.layout.selected_calender_item
                else
                    R.layout.calender_item
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                /**
                 * calender date filling
                 */
                holder.itemView.tvDate.text = DateUtils.getDayNumber(date)
                holder.itemView.tvDay.text = DateUtils.getDay3LettersName(date)
            }
        }

        binding.singleRowCalendar.calendarChangesObserver = object : CalendarChangesObserver {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {
                super.whenSelectionChanged(isSelected, position, date)
                /**
                 * Here put your data fetching logic
                 */
                today = Utils.todayDate(date)
                tomorrow = Utils.tomorrowDate(date)

                Timber.e("old date %s",today.toString())
                Timber.e("new date %s",tomorrow.toString())

                viewModel.getDateWiseNews(today, tomorrow)
//                adapter.notifyDataSetChanged()
            }
        }

        binding.singleRowCalendar.calendarSelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                /**
                 * make this true if you want to make your calender item to be selectable
                 */
                return true
            }
        }

        binding.singleRowCalendar.setDates(getFutureDatesOfCurrentMonth())

        binding.singleRowCalendar.initialPositionIndex = Date().date - 1

        binding.singleRowCalendar.init()

        binding.singleRowCalendar.select(Date().date - 1)
    }

    private fun getFutureDatesOfCurrentMonth(): List<Date> {
        // get all next dates of current month
        currentMonth = calender[Calendar.MONTH]
        return getDates(mutableListOf())
    }


    private fun getDates(list: MutableList<Date>): List<Date> {
        // load dates of whole month
        calender.set(Calendar.MONTH, currentMonth)
        calender.set(Calendar.DAY_OF_MONTH, 1)
        list.add(calender.time)
        while (currentMonth == calender[Calendar.MONTH]) {
            calender.add(Calendar.DATE, +1)
            if (calender[Calendar.MONTH] == currentMonth)
                list.add(calender.time)
        }
        calender.add(Calendar.DATE, -1)
        return list
    }
}