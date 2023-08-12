package com.example.todo

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MotionEvent
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.GregorianCalendar


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class AddTask : AppCompatActivity() {

    private lateinit var etTitle: EditText
    private lateinit var etDescription: EditText
    private lateinit var etDate: EditText
    private lateinit var etTime: EditText
    private var mDay: Int = 0
    private var mMonth: Int = 0
    private var mYear: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var timePickerDialog: TimePickerDialog
    private var count = 0
    private val PERMISSION_REQUEST_CODE = 0
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val postNotifications = Manifest.permission.POST_NOTIFICATIONS
    private lateinit var alarmManager:AlarmManager


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)


        etTitle = findViewById(R.id.title)
        etDescription = findViewById(R.id.description)
        etDate = findViewById(R.id.date)
        etTime = findViewById(R.id.time)
        val btnAdd = findViewById<Button>(R.id.btnAdd)


        btnAdd.setOnClickListener {
            if (
                etTitle.text.toString().isNotEmpty() &&
                etDate.text.toString().isNotEmpty() &&
                etTime.text.toString().isNotEmpty()
            ) {
                alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                addNote()
                requestRuntimePermissions()
            } else {
                Toast.makeText(this, "Please enter valid details", Toast.LENGTH_SHORT).show()
            }
        }

        etDate.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action === MotionEvent.ACTION_UP) {
                val c: Calendar = Calendar.getInstance()
                mYear = c.get(Calendar.YEAR)
                mMonth = c.get(Calendar.MONTH)
                mDay = c.get(Calendar.DAY_OF_MONTH)
                datePickerDialog = DatePickerDialog(
                    this,
                    { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        etDate.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                        datePickerDialog.dismiss()
                    }, mYear, mMonth, mDay
                )
                datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
                datePickerDialog.show()
            }
            true
        }

        etTime.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action === MotionEvent.ACTION_UP) {
                // Get Current Time
                val c = Calendar.getInstance()
                mHour = c[Calendar.HOUR_OF_DAY]
                mMinute = c[Calendar.MINUTE]

                // Launch Time Picker Dialog
                timePickerDialog = TimePickerDialog(
                    this,
                    { _: TimePicker?, hourOfDay: Int, minute: Int ->
                        etTime.setText("$hourOfDay:$minute")
                        timePickerDialog.dismiss()
                    }, mHour, mMinute, false
                )
                timePickerDialog.show()
            }
            true
        }
    }

    private fun addNote() {
        val todoTitle = etTitle.text.toString()
        val todoDescription = etDescription.text.toString()
        val todoDate = etDate.text.toString()
        val todoTime = etTime.text.toString()

        val database = TodoDatabase.getDatabase(applicationContext)
        val todoDao = database.todoDao()

        val todo = Todo(todoTitle, todoDescription, todoDate, todoTime)
        lifecycleScope.launch(Dispatchers.IO) {
            todoDao.insert(todo)
        }
        createAlarm(todo)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestRuntimePermissions(){
        if(ActivityCompat.checkSelfPermission(this,postNotifications)== PackageManager.PERMISSION_GRANTED){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }else if(ActivityCompat.shouldShowRequestPermissionRationale(this,postNotifications)){
            AlertDialog.Builder(this).setMessage("Please turn on notification feature")
                .setTitle("Permission Required")
                .setCancelable(false)
                .setPositiveButton("Ok",DialogInterface.OnClickListener { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(postNotifications),PERMISSION_REQUEST_CODE)
                    dialog.dismiss()
                }).setNegativeButton("Cancel",DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(postNotifications),PERMISSION_REQUEST_CODE)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode==PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"done",Toast.LENGTH_SHORT)
            }
            else if(ActivityCompat.shouldShowRequestPermissionRationale(this,postNotifications)){

                AlertDialog.Builder(this).setMessage("Turn on notification permission")
                    .setTitle("Permission Required")
                    .setCancelable(false)
                    .setNegativeButton("Cancel",DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    }).setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->

                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package",packageName,null)
                        intent.data = uri
                        startActivity(intent)

                        dialog.dismiss()
                    }).show()
            }else{
                requestRuntimePermissions()
            }
        }
    }

    private fun createAlarm(todo:Todo) {
        try {
            val items1: List<String> = todo.date.toString().split("-")
            val dd = items1[0]
            val month = items1[1]
            val year = items1[2]
            val itemTime: List<String> = todo.time.toString().split(":")
            val hour = itemTime[0]
            val min = itemTime[1]
            val cur_cal: Calendar = GregorianCalendar()
            cur_cal.timeInMillis = System.currentTimeMillis()
            val cal: Calendar = GregorianCalendar()
            cal[Calendar.HOUR_OF_DAY] = hour.toInt()
            cal[Calendar.MINUTE] = min.toInt()
            cal[Calendar.SECOND] = 0
            cal[Calendar.MILLISECOND] = 0
            cal[Calendar.DATE] = dd.toInt()
            val alarmIntent = Intent(applicationContext, NotificationService::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                applicationContext,
                count,
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC,
                cal.timeInMillis,
                pendingIntent
            )
            val intent = PendingIntent.getBroadcast(applicationContext, count, alarmIntent, PendingIntent.FLAG_IMMUTABLE)
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC,
                cal.timeInMillis - 600000,
                intent
            )
            count++
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}

