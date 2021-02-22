package com.example.diary.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.diary.*
import com.example.diary.databinding.ActivityTaskDetailsBinding
import com.example.diary.databinding.DialogAddNewTaskBinding
import com.example.diary.databinding.DialogDeleteTaskBinding
import com.example.diary.domain.model.Comment
import com.example.diary.domain.model.Task
import com.example.diary.ui.view.adapters.CommentAdapter
import com.example.diary.ui.viewmodel.DiaryViewModel
import java.text.SimpleDateFormat
import java.util.*

class TaskDetailsActivity : AppCompatActivity() {

    private lateinit var binding            : ActivityTaskDetailsBinding
    private lateinit var toolbarDetails     : Toolbar
    private lateinit var etInputChat        : AppCompatEditText
    private lateinit var viewCurtainDetails : View

    private lateinit var tvTitleTaskName    : AppCompatTextView
    private lateinit var tvStartTaskDetails : AppCompatTextView
    private lateinit var tvEndTaskDetails   : AppCompatTextView
    private lateinit var tvDescTaskDetails  : AppCompatTextView
    private lateinit var tvCommentsQuantity : AppCompatTextView
    private lateinit var rvCommentsList     : RecyclerView

    private val widthDialog : Int = 336
    private lateinit var currentTask: Task

    private val mCommentAdapter by lazy { CommentAdapter() }
    private val viewModel = DiaryViewModel.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbarDetails      = binding.toolbarDetails
        etInputChat         = binding.etInputChat
        viewCurtainDetails  = binding.viewCurtainDetails

        tvTitleTaskName     = binding.tvTitleTaskName
        tvStartTaskDetails  = binding.tvStartTaskDetails
        tvEndTaskDetails    = binding.tvEndTaskDetails
        tvDescTaskDetails   = binding.tvDescTaskDetails
        tvCommentsQuantity  = binding.tvCommentsQuantity
        rvCommentsList      = binding.rvCommentsList

        if(intent.extras != null){
            currentTask = intent.getParcelableExtra<Task>("task") as Task
        }

        setSupportActionBar(toolbarDetails)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupInputChat()
        setupRecycler()
        setupObserver()
    }

    private fun setupInputChat(){
        var message = ""
        val clearDrawable = ContextCompat.getDrawable(this, R.drawable.ic_send)
        clearDrawable?.setBounds(0,0, clearDrawable.intrinsicWidth, clearDrawable.intrinsicHeight)

        val validation = afterTextChanged {
            message = etInputChat.text.toString().trim()

            etInputChat.setCompoundDrawables(null, null,
                if (message.isNotEmpty()) clearDrawable else null,
                null)
        }
        etInputChat.addTextChangedListener(validation)

        etInputChat.onRightDrawableClicked { input ->
            val comment = Comment(5, "", message, currentTask.idTask)
            viewModel.insertComment(comment)
            input.text.clear()
            input.setCompoundDrawables(null, null, null, null)
            input.requestFocus()

        }
    }

    private fun setupRecycler(){
        rvCommentsList.apply{
            layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            adapter = mCommentAdapter
        }
    }

    private fun setupObserver(){
        viewModel.fetchTaskById(currentTask.idTask)
        viewModel.task.observe(this, Observer {
            it?.let { task ->
                currentTask = task
                tvTitleTaskName.text = task.name
                tvStartTaskDetails.text = getString(R.string.title_start_task).plus(" ").plus(getFormatDate(transformDateReceive(task.startDate)))
                tvEndTaskDetails.text   = getString(R.string.title_end_task).plus(" ").plus(getFormatDate(transformDateReceive(task.endDate)))
                tvDescTaskDetails.text  = task.description

            }
        })

        viewModel.fetchAllCommentsById(currentTask.idTask)
        viewModel.commentList.observe(this, Observer {
            it?.let { commentList ->
                val size = commentList.size
                val commentListSorted =  commentList.sortedByDescending { it.idComment }
                tvCommentsQuantity.text = "Comentarios ($size)"
                mCommentAdapter.setData(commentListSorted)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.delete_item -> {
                viewCurtainDetails.visibility = View.VISIBLE
                customDialog(this, R.layout.dialog_delete_task, widthDialog) { view, dialog ->
                    val deleteBinding = DialogDeleteTaskBinding.bind(view)
                    val titleDialogDelete = deleteBinding.titleDialogDelete
                    val btnConfirmDeleteTask = deleteBinding.btnConfirmDeleteTask
                    val btnCancelDeleteTask = deleteBinding.btnCancelDeleteTask

                    titleDialogDelete.text = getString(R.string.title_delete_request).plus(" ").plus(tvTitleTaskName.text).plus(" ?")

                    btnConfirmDeleteTask.setOnClickListener {
                        viewModel.deleteTask(currentTask)
                        Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
                        onBackPressed()
                        dialog.dismiss()
                    }

                    btnCancelDeleteTask.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.setOnDismissListener {
                        viewCurtainDetails.visibility = View.GONE
                    }
                }
                true
            }
            R.id.edit_item ->{
                viewCurtainDetails.visibility = View.VISIBLE
                customDialog(this, R.layout.dialog_add_new_task, widthDialog){ view, dialog ->
                    val editBinding = DialogAddNewTaskBinding.bind(view)

                    //Current date
                    val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val currentDate = sdfDate.format(Date())

                    //Dialog add binding
                    val titleDialogAdd = editBinding.titleDialogAdd
                    val btnEditTask      = editBinding.btnAddNewTask
                    val btnCancelNewTask = editBinding.btnCancelNewTask
                    val cetNewTaskDesc     = editBinding.cetNewTaskDesc
                    val cetNewTaskEnd      = editBinding.cetNewTaskEnd
                    val cetNewTaskTitle    = editBinding.cetNewTaskTitle
                    val etNewTaskDesc     = editBinding.etNewTaskDesc
                    val etNewTaskEnd      = editBinding.etNewTaskEnd
                    val etNewTaskTitle    = editBinding.etNewTaskTitle

                    btnEditTask.apply {
                        isEnabled = false
                        setBackgroundResource(R.drawable.btn_corner_dissable)
                    }

                    titleDialogAdd.text = getString(R.string.title_edit_dialog)
                    etNewTaskTitle.setText(currentTask.name)
                    etNewTaskDesc.setText(currentTask.description)
                    etNewTaskEnd.setText(transformDateReceive(currentTask.endDate))

                    val validate = afterTextChanged {
                        val newTitle = etNewTaskTitle.text.toString().trim()
                        val newDesc  = etNewTaskDesc.text.toString().trim()
                        val newEnd   = etNewTaskEnd.text.toString().trim()

                        //Only show title error
                        cetNewTaskTitle.error = when{
                            isNullOrEmpty(newTitle) -> { "Ingrese un título." }
                            else -> null
                        }

                        //Only show desc error
                        cetNewTaskDesc.error =
                                if(isNullOrEmpty(newDesc))  "Ingrese un contendio."
                                else  null

                        //Only show endTask
                        if(!isNullOrEmpty(newEnd)){
                            cetNewTaskEnd.error = when {
                                newEnd.length < 10 -> { "Ingrese una fecha" }
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

                        btnEditTask.apply {
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

                    etNewTaskTitle.addTextChangedListener(validate)
                    etNewTaskDesc.addTextChangedListener(validate)
                    etNewTaskEnd.addTextChangedListener(validate)

                    btnCancelNewTask.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.setOnDismissListener {
                        viewCurtainDetails.visibility = View.GONE
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}