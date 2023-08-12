package com.example.todo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem.OnMenuItemClickListener
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class TodoAdapter(private val todo: ArrayList<Todo>, private val context: Context ): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){

    private val inputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    private lateinit var outputDateString:String
    private val dateFormat = SimpleDateFormat("EE dd MMM yyyy", Locale.US)


     class   TodoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

             var title: TextView = itemView.findViewById(R.id.tvTitle)
         var description: TextView = itemView.findViewById(R.id.tvDescription)
         var date: TextView = itemView.findViewById(R.id.tvDate)
         var time: TextView = itemView.findViewById(R.id.tvTime)
         var menu: ImageView= itemView.findViewById(R.id.menu)
         var day:TextView = itemView.findViewById(R.id.day)
         var month:TextView = itemView.findViewById(R.id.month)

     }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item,parent,false)
        return TodoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return todo.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {

         val currentItem = todo[position]
        holder.title.text = currentItem.title
        holder.time.text = currentItem.time
        holder.description.text = currentItem.description


        try {
            val date = inputDateFormat.parse(currentItem.date.toString())
            outputDateString = dateFormat.format(date!!)

            val items1 = outputDateString.split(" ")
            val day = items1[0]
            val dd = items1[1]
            val month = items1[2]

            holder.day.text = day
            holder.date.text = dd
            holder.month.text = month

        } catch (e:Exception) {
            e.printStackTrace()
        }

        holder.itemView.setOnClickListener{
            val intent  = Intent(context,UpdateTask::class.java)
            intent.putExtra("title",currentItem.title)
            intent.putExtra("description",currentItem.description)
            intent.putExtra("date",currentItem.date)
            intent.putExtra("time",currentItem.time)
            intent.putExtra("id",currentItem.id)

            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        holder.menu.setOnClickListener{
            val popupMenu = PopupMenu(context,holder.menu)
            popupMenu.inflate(R.menu.menu)
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener {
                when(it.itemId){
                    R.id.menuDelete-> {
                        val id = currentItem.id
                        GlobalScope.launch(Dispatchers.IO) {
                            TodoDatabase.getDatabase(context).todoDao().delete(id)
                        }
                        if (position == todo.size - 1|| position == 0) { // if last element is deleted, no need to shift
                            todo.removeAt(position)
                            notifyItemRemoved(position)
                        } else {
                            var shift = 1

                            while (true) {
                                try {
                                    todo.removeAt(position - shift)
                                    notifyItemRemoved(position)
                                    break
                                } catch (e:IndexOutOfBoundsException) {
                                    shift++
                                }
                            }
                        }
                    return@OnMenuItemClickListener true
                    }
                    R.id.menuDone-> {

                        val id = currentItem.id
                        GlobalScope.launch(Dispatchers.IO) {
                            TodoDatabase.getDatabase(context).todoDao().delete(id)
                        }
                        if (position == todo.size - 1|| position == 0) { // if last element is deleted, no need to shift
                            todo.removeAt(position)
                            notifyItemRemoved(position)
                        } else {
                            var shift = 1

                            while (true) {
                                try {
                                    todo.removeAt(position - shift)
                                    notifyItemRemoved(position)
                                    break
                                } catch (e:IndexOutOfBoundsException) {
                                    shift++
                                }
                            }
                        }
                        return@OnMenuItemClickListener true
                    }
                    else -> {
                        popupMenu.dismiss()
                        return@OnMenuItemClickListener true
                    }
                }
            })

        }

    }



}