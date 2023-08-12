package com.example.todo

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UpdateTask : AppCompatActivity() {

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_task)

        val etTitle = findViewById<EditText>(R.id.upTitle)
        val etDescription = findViewById<EditText>(R.id.upDescription)
        val etDate = findViewById<EditText>(R.id.upDate)
        val etTime = findViewById<EditText>(R.id.upTime)
        val btnUpdate = findViewById<Button>(R.id.btnUpdate)

        etTitle.setText(intent.getStringExtra("title"))
        etDescription.setText(intent.getStringExtra("description"))
        etDate.setText(intent.getStringExtra("date"))
        etTime.setText(intent.getStringExtra("time"))


        btnUpdate.setOnClickListener{
            val todoTitle = etTitle.text.toString()
            val todoDescription = etDescription.text.toString()
            val todoDate = etDate.text.toString()
            val todoTime = etTime.text.toString()
            val todo = Todo(todoTitle,todoDescription,todoDate,todoTime)
            val id = intent.getStringExtra("id")?.toInt()

            lifecycleScope.launch(Dispatchers.IO) {

                TodoDatabase.getDatabase(this@UpdateTask).todoDao().update(todoTitle,todoDescription,todoDate,todoTime,id!!)
            }

            val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)

        }

    }
}