package com.example.diary.ui.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.scheduler.mainScheduler
import com.example.diary.*
import com.example.diary.ui.view.adapters.OnCheckBoxClickListener
import com.example.diary.ui.view.adapters.OnTaskClickListener
import com.example.diary.ui.view.adapters.TaskAdapter
import com.example.diary.ui.view.fragment.DatePickerFragment
import com.example.kmmsharedmodule.ViewModelBinding
import com.example.kmmsharedmodule.data.local.DbDriver
import com.example.kmmsharedmodule.domain.model.Task
import com.example.kmmsharedmodule.viewmodel.DiaryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), OnTaskClickListener, OnCheckBoxClickListener {

    private val mBuilding  = ViewModelBinding()
    private val viewModel = DiaryViewModel.getInstance(DbDriver(this))

    private var toolbar            : Toolbar                ? = null
    private var fltAddTask         : FloatingActionButton   ? = null
    private var viewCurtainMain    : View                   ? = null
    private var rvTaskList         : RecyclerView           ? = null

    //Dialog Add
    private var btnAddNewTask       : AppCompatButton       ? = null
    private var btnCancelNewTask    : AppCompatButton       ? = null
    private var cetNewTaskDesc      : TextInputLayout       ? = null
    private var cetNewTaskEnd       : TextInputLayout       ? = null
    private var cetNewTaskTitle     : TextInputLayout       ? = null
    private var etNewTaskDesc       : TextInputEditText     ? = null
    private var etNewTaskEnd        : TextInputEditText     ? = null
    private var etNewTaskTitle      : TextInputEditText     ? = null

    private val widthDialog : Int = 336
    private var filterEnabled = true
    private var taskListSorted = listOf<Task>()

    private val mTaskAdapter by lazy { TaskAdapter(this, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar             = findViewById(R.id.toolbar)
        fltAddTask          = findViewById(R.id.flt_add_task)
        viewCurtainMain     = findViewById(R.id.view_curtain_main)
        rvTaskList          = findViewById(R.id.rv_task_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setupAddTask()
        setupRecycler()
        subscribers()
    }

    private fun subscribers(){
        mBuilding.subscribe(viewModel.taskList.observeOn(mainScheduler), onNext = ::taskList)
    }

    private fun taskList(taskList: List<Task>) {
        taskListSorted = taskList.sortedByDescending { it.idTask }
        mTaskAdapter.setData(taskListSorted)
    }

    private fun setupRecycler(){
        rvTaskList?.apply {
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            adapter = mTaskAdapter
        }
    }

    private fun setupAddTask(){

        fltAddTask?.setOnClickListener {

            fltAddTask?.visibility = View.GONE
            viewCurtainMain?.visibility = View.VISIBLE
            customDialog(this, R.layout.dialog_add_new_task, widthDialog){ view, dialog ->

                //Dialog add binding
                btnAddNewTask       = view.findViewById(R.id.btn_add_new_task)
                btnCancelNewTask    = view.findViewById(R.id.btn_cancel_new_task)
                cetNewTaskTitle     = view.findViewById(R.id.cet_new_task_title)
                cetNewTaskDesc      = view.findViewById(R.id.cet_new_task_desc)
                cetNewTaskEnd       = view.findViewById(R.id.cet_new_task_end)
                etNewTaskTitle      = view.findViewById(R.id.et_new_task_title)
                etNewTaskDesc       = view.findViewById(R.id.et_new_task_desc)
                etNewTaskEnd        = view.findViewById(R.id.et_new_task_end)

                etNewTaskEnd?.setOnClickListener { showDatePickerDialog() }

                //Current date
                val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val currentDate = sdfDate.format(Date())

                btnAddNewTask?.apply {
                    isEnabled = false
                    setBackgroundResource(R.drawable.btn_corner_dissable)
                }

                val validate = afterTextChanged {
                    val newTitle = etNewTaskTitle?.text.toString().trim()
                    val newDesc  = etNewTaskDesc?.text.toString().trim()
                    val newEnd   = etNewTaskEnd?.text.toString().trim()

                    //Only show title error
                    cetNewTaskTitle?.error = when{
                        isNullOrEmpty(newTitle) -> { "Ingrese un título." }
                        else -> null
                    }

                    //Only show desc error
                    cetNewTaskDesc?.error =
                            if(isNullOrEmpty(newDesc))  "Ingrese un contendio."
                            else  null

                    //Only show endTask
                    cetNewTaskEnd?.error =
                        if(isNullOrEmpty(newEnd)) { "Ingrese una fecha" }
                        else  null

                    if(newEnd.length == 10){
                        if(!validateDay(newEnd)) {
                            cetNewTaskEnd?.error = "Ingrese una día valida"
                            return@afterTextChanged
                        } else cetNewTaskEnd?.error = null

                        if(!validateMonth(newEnd)) {
                            cetNewTaskEnd?.error = "Ingrese una mes valida"
                            return@afterTextChanged
                        } else cetNewTaskEnd?.error = null

                        if(!validateDate(newEnd)) {
                            cetNewTaskEnd?.error = "Ingrese una fecha valida"
                            return@afterTextChanged
                        }else cetNewTaskEnd?.error = null
                    }

                    btnAddNewTask?.apply {
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
                            val newTask = Task(0, newTitle, newDesc, currentDate, newEndFormatted, 0)
                            viewModel.insertTask(newTask)
                            dialog.dismiss()
                        }
                    }
                }

                etNewTaskTitle?.addTextChangedListener(validate)
                etNewTaskDesc?.addTextChangedListener(validate)
                etNewTaskEnd?.addTextChangedListener(validate)

                btnCancelNewTask?.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.setOnDismissListener {
                    viewCurtainMain?.visibility = View.GONE
                    fltAddTask?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year ->
            onDateSelected(day, month+1, year)
        }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int){
        val customDay = (if(day<10) "0" else "").plus(day)
        val customMonth = (if(month<10) "0" else "").plus(month)
        etNewTaskEnd?.setText("$customDay/${customMonth}/$year")
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
                    val taskListFinished = taskListSorted.filter { it.isFinished == 1 }
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

    override fun onTaskClicked(taskId: Int) {
        val intent = Intent(this, TaskDetailsActivity::class.java)
        intent.putExtra("idtask", taskId)
        startActivity(intent)
    }

    override fun updateTaskFinished(task: Task) {
        viewModel.updateTask(task)
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchTaskList()
    }

}