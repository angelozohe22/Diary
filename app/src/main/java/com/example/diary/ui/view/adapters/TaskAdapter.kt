package com.example.diary.ui.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.diary.R
import com.example.diary.getFormatDate
import com.example.diary.transformDateReceive
import com.example.kmmsharedmodule.domain.model.Task

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
        private val itemIdTask              = itemView.findViewById<AppCompatTextView>(R.id.item_id_task)
        private val itemTvTaskName          = itemView.findViewById<AppCompatTextView>(R.id.item_tv_task_name)
        private val itemTvTaskDesc          = itemView.findViewById<AppCompatTextView>(R.id.item_tv_task_desc)
        private val itemTvStartTask         = itemView.findViewById<AppCompatTextView>(R.id.item_tv_start_task)
        private val itemTvEndTask           = itemView.findViewById<AppCompatTextView>(R.id.item_tv_end_task)
        private val itemCbxFinishedTask    = itemView.findViewById<AppCompatCheckBox>(R.id.item_cbx_finished_task)
        private val itemCardTaskContainer           = itemView.findViewById<CardView>(R.id.item_card_task_container)

        fun bindView(task: Task){
                itemIdTask.text         = (if(task.idTask<10) "0" else "").plus(task.idTask)
                itemTvTaskName.text     = task.name.capitalize()
                itemTvTaskDesc.text     = task.description.capitalize()
                itemTvStartTask.text    = ("Creada: ").plus(getFormatDate(transformDateReceive(task.startDate)))
                itemTvEndTask.text      = ("Finaliza: ").plus(getFormatDate(transformDateReceive(task.endDate)))

                itemCbxFinishedTask.apply {
                    isChecked = task.isFinished == 1
                    setOnClickListener {
                        if(task.isFinished == 0) onCheckBoxClickListener.updateTaskFinished(task.copy(isFinished = 1))
                        else onCheckBoxClickListener.updateTaskFinished(task.copy(isFinished = 0))
                    }
                }

                itemCardTaskContainer.setOnClickListener {
                    onTaskClickListener.onTaskClicked(task.idTask)
                }
        }
    }

}