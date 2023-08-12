package com.example.todo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Todo(
                @ColumnInfo(name="title") val title:String?,
                @ColumnInfo(name="description") val description:String?,
                @ColumnInfo(name="date") val date:String?,
                @ColumnInfo(name="time") val time:String?
               ){
    @PrimaryKey(autoGenerate = true) var id = 0
}
