package com.example.todo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.applandeo.materialcalendarview.CalendarView
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.applandeo.materialcalendarview.EventDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class CalenderView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender_view)
        val calenderView  = findViewById<CalendarView>(R.id.calenderView)
        val back:ImageView = findViewById(R.id.back)

        lifecycleScope.launch(Dispatchers.IO) {
            val todo = TodoDatabase.getDatabase(this@CalenderView).todoDao().getAll()

            withContext(Dispatchers.Main) {
                val events: MutableList<EventDay> = ArrayList<EventDay>()
                for (i in todo.indices) {
                    val calendar = Calendar.getInstance()
                    val items1: List<String> = todo[i].date!!.split("-")
                    val dd = items1[0]
                    val month = items1[1]
                    val year = items1[2]
                    calendar[Calendar.DAY_OF_MONTH] = dd.toInt()
                    calendar[Calendar.MONTH] = month.toInt() - 1
                    calendar[Calendar.YEAR] = year.toInt()
                    events.add(EventDay(calendar, R.drawable.dot))
                    calenderView.setEvents(events)

                }
            }
        }

        back.setOnClickListener{
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}