package com.example.todo


import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CalendarView
import com.applandeo.materialcalendarview.EventDay
import android.widget.ImageButton
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class MainActivity : AppCompatActivity() {


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnCalender:ImageButton = findViewById(R.id.btnCalender)
        val addBtn = findViewById<FloatingActionButton>(R.id.add_note_btn)
        val rv = findViewById<RecyclerView>(R.id.recyler_view)


        addBtn.setOnClickListener {
            val intent = Intent(this, AddTask::class.java)
            startActivity(intent)
        }

        val recyclerView = this
        lifecycleScope.launch(Dispatchers.IO) {
            val todo = TodoDatabase.getDatabase(this@MainActivity).todoDao().getAll()

                withContext(Dispatchers.Main) {
                var adapter = TodoAdapter(todo as ArrayList<Todo>, this@MainActivity)
                rv.layoutManager = LinearLayoutManager(this@MainActivity)
                rv.adapter = adapter

            }
        }

        btnCalender.setOnClickListener{
            val intent = Intent(this,CalenderView::class.java)
            startActivity(intent)
        }
    }


}