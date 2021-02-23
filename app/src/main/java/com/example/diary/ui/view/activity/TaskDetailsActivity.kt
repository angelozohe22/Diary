package com.example.diary.ui.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.scheduler.mainScheduler
import com.example.diary.*
import com.example.diary.ui.view.adapters.CommentAdapter
import com.example.diary.ui.view.fragment.DatePickerFragment
import com.example.kmmsharedmodule.ViewModelBinding
import com.example.kmmsharedmodule.data.local.DbDriver
import com.example.kmmsharedmodule.domain.model.Comment
import com.example.kmmsharedmodule.domain.model.Task
import com.example.kmmsharedmodule.viewmodel.DiaryViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailsActivity : AppCompatActivity() {

    private val mBuilding  = ViewModelBinding()
    private val viewModel = DiaryViewModel.getInstance(DbDriver(this))

    private var toolbarDetails     : Toolbar                ? = null
    private var etInputChat        : AppCompatEditText      ? = null
    private var viewCurtainDetails : View                   ? = null

    private var tvTitleTaskName    : AppCompatTextView      ? = null
    private var tvStartTaskDetails : AppCompatTextView      ? = null
    private var tvEndTaskDetails   : AppCompatTextView      ? = null
    private var tvDescTaskDetails  : AppCompatTextView      ? = null
    private var tvCommentsQuantity : AppCompatTextView      ? = null
    private var rvCommentsList     : RecyclerView           ? = null

    //Dialog Edit
    private var titleDialogAdd      : AppCompatTextView     ? = null
    private var btnEditTask         : AppCompatButton       ? = null
    private var btnCancelTask       : AppCompatButton       ? = null
    private var cetNewTaskDesc      : TextInputLayout       ? = null
    private var cetNewTaskEnd       : TextInputLayout       ? = null
    private var cetNewTaskTitle     : TextInputLayout       ? = null
    private var etNewTaskDesc       : TextInputEditText     ? = null
    private var etNewTaskEnd        : TextInputEditText     ? = null
    private var etNewTaskTitle      : TextInputEditText     ? = null

    //Dialog Delete
    private var titleDialogDelete   : AppCompatTextView     ? = null
    private var btnConfirmDeleteTask: AppCompatButton       ? = null
    private var btnCancelDeleteTask : AppCompatButton       ? = null

    private val widthDialog : Int = 336
    private lateinit var currentTask: Task
    private var currentTaskId: Int = 0

    private val mCommentAdapter by lazy { CommentAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_details)

        toolbarDetails      = findViewById(R.id.toolbar_details)
        etInputChat         = findViewById(R.id.et_input_chat)
        viewCurtainDetails  = findViewById(R.id.view_curtain_details)

        tvTitleTaskName     = findViewById(R.id.tv_title_task_name)
        tvStartTaskDetails  = findViewById(R.id.tv_start_task_details)
        tvEndTaskDetails    = findViewById(R.id.tv_end_task_details)
        tvDescTaskDetails   = findViewById(R.id.tv_desc_task_details)
        tvCommentsQuantity  = findViewById(R.id.tv_comments_quantity)
        rvCommentsList      = findViewById(R.id.rv_comments_list)

        setSupportActionBar(toolbarDetails)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(intent.extras != null){
            currentTaskId = intent.getIntExtra("idtask", 0)
        }

        viewModel.fetchTaskById(currentTaskId)
        setupInputChat()
        setupRecycler()
        subscribers()
    }

    private fun setupInputChat(){
        var message = ""
        val clearDrawable = ContextCompat.getDrawable(this, R.drawable.ic_send)
        clearDrawable?.setBounds(0,0, clearDrawable.intrinsicWidth, clearDrawable.intrinsicHeight)

        val validation = afterTextChanged {
            message = etInputChat?.text.toString().trim()

            etInputChat?.setCompoundDrawables(null, null,
                if (message.isNotEmpty()) clearDrawable else null,
                null)
        }
        etInputChat?.addTextChangedListener(validation)

        etInputChat?.onRightDrawableClicked { input ->
            val comment = Comment(0, "", message, currentTask.idTask)
            viewModel.insertComment(comment)
            input.text.clear()
            input.setCompoundDrawables(null, null, null, null)
            input.requestFocus()
        }
    }

    private fun setupRecycler(){
        rvCommentsList?.apply{
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            adapter = mCommentAdapter
        }
    }

    private fun subscribers(){
        mBuilding.subscribe(viewModel.task.observeOn(mainScheduler), onNext = ::task)
        mBuilding.subscribe(viewModel.commentList.observeOn(mainScheduler), onNext = ::commentList)
    }

    private fun commentList(commentList: List<Comment>) {
        val size = commentList.size
        val commentListSorted =  commentList.sortedByDescending { it.idComment }
        tvCommentsQuantity?.text = "Comentarios ($size)"
        mCommentAdapter.setData(commentListSorted)
    }

    private fun task(task: Task) {
        currentTask = task
        tvTitleTaskName?.text = task.name
        tvStartTaskDetails?.text = getString(R.string.title_start_task).plus(" ").plus(getFormatDate(transformDateReceive(task.startDate)))
        tvEndTaskDetails?.text   = getString(R.string.title_end_task).plus(" ").plus(getFormatDate(transformDateReceive(task.endDate)))
        tvDescTaskDetails?.text  = task.description
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.delete_item -> {
                viewCurtainDetails?.visibility = View.VISIBLE
                customDialog(this, R.layout.dialog_delete_task, widthDialog) { view, dialog ->

                    titleDialogDelete       = view.findViewById(R.id.title_dialog_delete)
                    btnConfirmDeleteTask    = view.findViewById(R.id.btn_confirm_delete_task)
                    btnCancelDeleteTask     = view.findViewById(R.id.btn_cancel_delete_task)

                    titleDialogDelete?.text = getString(R.string.title_delete_request).plus(" ").plus(tvTitleTaskName?.text).plus(" ?")

                    btnConfirmDeleteTask?.setOnClickListener {
                        viewModel.deleteTask(currentTask)
                        Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                        dialog.dismiss()
                    }

                    btnCancelDeleteTask?.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.setOnDismissListener {
                        viewCurtainDetails?.visibility = View.GONE
                    }
                }
                true
            }
            R.id.edit_item ->{
                viewCurtainDetails?.visibility = View.VISIBLE
                customDialog(this, R.layout.dialog_add_new_task, widthDialog){ view, dialog ->

                    //Current date
                    val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val currentDate = sdfDate.format(Date())

                    //Dialog edit binding
                    titleDialogAdd      = view.findViewById(R.id.title_dialog_add)
                    btnEditTask         = view.findViewById(R.id.btn_add_new_task)
                    btnCancelTask       = view.findViewById(R.id.btn_cancel_new_task)
                    cetNewTaskTitle     = view.findViewById(R.id.cet_new_task_title)
                    cetNewTaskDesc      = view.findViewById(R.id.cet_new_task_desc)
                    cetNewTaskEnd       = view.findViewById(R.id.cet_new_task_end)
                    etNewTaskTitle      = view.findViewById(R.id.et_new_task_title)
                    etNewTaskDesc       = view.findViewById(R.id.et_new_task_desc)
                    etNewTaskEnd        = view.findViewById(R.id.et_new_task_end)

                    etNewTaskEnd?.setOnClickListener { showDatePickerDialog() }

                    btnEditTask?.apply {
                        isEnabled = false
                        setBackgroundResource(R.drawable.btn_corner_dissable)
                    }

                    titleDialogAdd?.text = getString(R.string.title_edit_dialog)
                    etNewTaskTitle?.setText(currentTask.name)
                    etNewTaskDesc?.setText(currentTask.description)
                    etNewTaskEnd?.setText(transformDateReceive(currentTask.endDate))

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
                        if(!isNullOrEmpty(newEnd)){
                            cetNewTaskEnd?.error = when {
                                newEnd.length < 10 -> { "Ingrese una fecha" }
                                else -> null
                            }
                        }

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

                        btnEditTask?.apply {
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
                                val newTask = Task(currentTask.idTask, newTitle, newDesc, currentDate, newEndFormatted, 0)
                                viewModel.updateTask(newTask)
                                dialog.dismiss()
                            }
                        }
                    }

                    etNewTaskTitle?.addTextChangedListener(validate)
                    etNewTaskDesc?.addTextChangedListener(validate)
                    etNewTaskEnd?.addTextChangedListener(validate)

                    btnCancelTask?.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.setOnDismissListener {
                        viewCurtainDetails?.visibility = View.GONE
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
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

    override fun onStart() {
        super.onStart()
        viewModel.fetchTaskById(currentTaskId)
        viewModel.fetchAllCommentsById(currentTaskId)
    }

}