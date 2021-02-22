package com.example.diary.ui.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.diary.R
import com.example.diary.databinding.ItemTaskBinding
import com.example.diary.domain.model.Task
import com.example.diary.getFormatDate
import com.example.diary.transformDateReceive

/**
 * Created by Angelo on 2/20/2021
 */
class TaskAdapter(
        private val onTaskClickListener: OnTaskClickListener,
        private val onCheckBoxClickListener: OnCheckBoxClickListener
): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var _taskList = emptyList<Task>()

    fun setData(data: List<Task>){
        this._taskList = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = _taskList[position]
        holder.bindView(task)
    }

    override fun getItemCount(): Int = _taskList.size

    inner class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val binding = ItemTaskBinding.bind(itemView)
        fun bindView(task: Task){
            binding.apply {
                itemIdTask.text         = (if(task.idTask<10) "0" else "").plus(task.idTask)
                itemTvTaskName.text     = task.name.capitalize()
                itemTvTaskDesc.text     = task.description.capitalize()
                itemTvStartTask.text    = ("Creada: ").plus(getFormatDate(transformDateReceive(task.startDate)))
                itemTvEndTask.text      = ("Finaliza: ").plus(getFormatDate(transformDateReceive(task.endDate)))

                itemCbxFinishedTask.apply {
                    isChecked = task.isFinished == 1
                    setOnClickListener {
                        if(task.isFinished == 0) onCheckBoxClickListener.updateTaskFinished(Task(task.idTask, task.name, task.description, task.startDate, task.endDate, 1))
                        else onCheckBoxClickListener.updateTaskFinished(Task(task.idTask, task.name, task.description, task.startDate, task.endDate, 0))
                    }
                }

                itemCardTaskContainer.setOnClickListener {
                    onTaskClickListener.onTaskClicked(task)
                }
            }
        }
    }

}