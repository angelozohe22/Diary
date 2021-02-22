package com.example.diary.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diary.*
import com.example.diary.domain.model.Task
import com.example.diary.databinding.ActivityMainBinding
import com.example.diary.databinding.DialogAddNewTaskBinding
import com.example.diary.ui.view.adapters.OnCheckBoxClickListener
import com.example.diary.ui.view.adapters.OnTaskClickListener
import com.example.diary.ui.view.adapters.TaskAdapter
import com.example.diary.ui.viewmodel.DiaryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), OnTaskClickListener, OnCheckBoxClickListener {

    private lateinit var binding            : ActivityMainBinding
    private lateinit var toolbar            : Toolbar
    private lateinit var fltAddTask         : FloatingActionButton
    private lateinit var viewCurtainMain    : View
    private lateinit var rvTaskList         : RecyclerView

    private val widthDialog : Int = 336
    private var filterEnabled = true
    private var taskListSorted = listOf<Task>()

    private val mTaskAdapter by lazy { TaskAdapter(this, this) }
    private val viewModel = DiaryViewModel.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar             = binding.toolbar
        fltAddTask          = binding.fltAddTask
        viewCurtainMain     = binding.viewCurtainMain
        rvTaskList          = binding.rvTaskList

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupAddTask()
        setupRecycler()
    }

    private fun setupRecycler(){
        rvTaskList.apply {
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            adapter = mTaskAdapter
        }
    }

    private fun setupAddTask(){

        fltAddTask.setOnClickListener {
            fltAddTask.visibility = View.GONE
            viewCurtainMain.visibility = View.VISIBLE
            customDialog(this, R.layout.dialog_add_new_task, widthDialog){ view, dialog ->
                val addBinding = DialogAddNewTaskBinding.bind(view)

                //Current date
                val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val currentDate = sdfDate.format(Date())

                //Dialog add binding
                val btnAddNewTask    = addBinding.btnAddNewTask
                val btnCancelNewTask = addBinding.btnCancelNewTask
                val cetNewTaskDesc     = addBinding.cetNewTaskDesc
                val cetNewTaskEnd      = addBinding.cetNewTaskEnd
                val cetNewTaskTitle    = addBinding.cetNewTaskTitle
                val etNewTaskDesc     = addBinding.etNewTaskDesc
                val etNewTaskEnd      = addBinding.etNewTaskEnd
                val etNewTaskTitle    = addBinding.etNewTaskTitle

                btnAddNewTask.apply {
                    isEnabled = false
                    setBackgroundResource(R.drawable.btn_corner_dissable)
                }

                val validate = afterTextChanged {
                    val newTitle = etNewTaskTitle.text.toString().trim()
                    val newDesc  = etNewTaskDesc.text.toString().trim()
                    val newEnd   = etNewTaskEnd.text.toString().trim()

                    //Only show title error
                    if(!isNullOrEmpty(newTitle)){
                        cetNewTaskTitle.error = when {
                            newTitle.length < 2 -> { "Ingrese un título." }
                            else -> null
                        }
                    }

                    //Only show desc error
                    if(!isNullOrEmpty(newDesc)){
                        cetNewTaskDesc.error = when {
                            newDesc.length < 2 -> { "Ingrese un contendio." }
                            else -> null
                        }
                    }

                    //Only show endTask
                    if(!isNullOrEmpty(newEnd)){
                        cetNewTaskEnd.error = when {
                            newEnd.length < 2 -> { "Ingrese una fecha" }
                            else -> null
                        }
                    }

                    if(newEnd.length == 10){
                        if(!validateDay(newEnd)) {
                            cetNewTaskEnd.error = "Ingrese una día valida"
                            return@afterTextChanged
                        } else cetNewTaskEnd.error = null

                        if(!validateMonth(newEnd)) {
                            cetNewTaskEnd.error = "Ingrese una mes valida"
                            return@afterTextChanged
                        } else cetNewTaskEnd.error = null

                        if(!validateDate(newEnd)) {
                            cetNewTaskEnd.error = "Ingrese una fecha valida"
                            return@afterTextChanged
                        }else cetNewTaskEnd.error = null
                    }

                    btnAddNewTask.apply {
                        isEnabled = !isNullOrEmpty(newTitle) &&
                                    !isNullOrEmpty(newDesc) &&
                                    !isNullOrEmpty(newEnd) &&
                                    validateDate(newEnd) &&
                                    validateDay(newEnd) &&
                                    validateMonth(newEnd)
                        if(isEnabled) setBackgroundResource(R.drawable.btn_corner_outline)
                        else setBackgroundResource(R.drawable.btn_corner_dissable)

                        setOnClickListener {
                            val newEndFormatted = transformDateSave(newEnd)
                            val newTask = Task(5, newTitle, newDesc, currentDate, newEndFormatted, 0)
                            Log.e("err", newTask.toString())
                            viewModel.insertNewTask(newTask)
                            dialog.dismiss()
                        }
                    }
                }

                etNewTaskTitle.addTextChangedListener(validate)
                etNewTaskDesc.addTextChangedListener(validate)
                etNewTaskEnd.addTextChangedListener(validate)

                btnCancelNewTask.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.setOnDismissListener {
                    viewCurtainMain.visibility = View.GONE
                    fltAddTask.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupObserver(){
        viewModel.fetchTaskList()
        viewModel.taskList.observe(this, Observer {
            it?.let { taskList ->
                taskListSorted = taskList.sortedByDescending { it.idTask }
                mTaskAdapter.setData(taskListSorted)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.visible_item -> {
                if (filterEnabled) {
                    item.setIcon(R.drawable.ic_invisible)
                    val taskListFinished =taskListSorted.filter { it.isFinished == 1 }
                    mTaskAdapter.setData(taskListFinished)
                } else {
                    item.setIcon(R.drawable.ic_visibility)
                    mTaskAdapter.setData(taskListSorted)
                }
                filterEnabled = !filterEnabled
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onTaskClicked(task: Task) {
        val bundleTask = Bundle()
        bundleTask.putParcelable("task", task)
        val intent = Intent(this, TaskDetailsActivity::class.java)
        intent.putExtras(bundleTask)
        startActivity(intent)
    }

    override fun updateTaskFinished(task: Task) {
        viewModel.updateTask(task)
    }

    override fun onStart() {
        super.onStart()
        setupObserver()
    }

}