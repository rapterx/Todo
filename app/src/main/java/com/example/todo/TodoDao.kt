package com.example.todo

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Ignore
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TodoDao {
    @Query("SELECT * FROM Todo")
    fun getAll(): List<Todo>

    @Query("SELECT * FROM Todo WHERE id= :id")
     fun findById(id: String?): Todo

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todo: Todo)

    @Query("DELETE FROM Todo WHERE id= :id")
    suspend fun delete(id:Int)

    @Query("UPDATE Todo SET title= :title , description= :description ,  date= :date , time= :time WHERE id= :id")
    suspend fun update(title:String,description:String,date:String,time:String, id:Int)

}