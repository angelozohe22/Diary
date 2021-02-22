package com.example.diary.data.local.source.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.diary.core.ctx
import com.example.diary.domain.model.Comment
import com.example.diary.domain.model.Task

/**
 * Created by Angelo on 2/22/2021
 */
class DBHelper(context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION ) {

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "Diary.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(Contract.SQL_CREATE_TABLE_TASK)
        db?.execSQL(Contract.SQL_CREATE_TABLE_COMMENT)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(Contract.SQL_DELETE_TABLE_TASK)
        onCreate(db)
    }

    fun insertTask(task: Task){
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.apply {
            put(Contract.TaskTable.NAME, task.name)
            put(Contract.TaskTable.DESCRIPTION, task.description)
            put(Contract.TaskTable.DATE_START, task.startDate)
            put(Contract.TaskTable.DATE_END, task.endDate)
            put(Contract.TaskTable.IS_FINISHED, task.isFinished)
        }
        db.insert(Contract.TaskTable.TABLE_NAME, null, cv)
    }

    fun getTaskList(): Cursor?{
        val query = "SELECT * FROM ${Contract.TaskTable.TABLE_NAME}"
        val db = this.readableDatabase
        var cursor: Cursor?= null
        if(db != null){
            cursor = db.rawQuery(query,null)
        }
        return cursor
    }

    fun updateTask(task: Task){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.apply {
            put(Contract.TaskTable.NAME, task.name)
            put(Contract.TaskTable.DESCRIPTION, task.description)
            put(Contract.TaskTable.DATE_START, task.startDate)
            put(Contract.TaskTable.DATE_END, task.endDate)
            put(Contract.TaskTable.IS_FINISHED, task.isFinished)
        }
        val result = db.update(Contract.TaskTable.TABLE_NAME, cv, "id_task=?", arrayOf(task.idTask.toString()) )

    }

    fun deleteTask(task: Task){
        val db = this.writableDatabase
        val result = db.delete(Contract.TaskTable.TABLE_NAME, "id_task=?", arrayOf(task.idTask.toString()))
    }

    fun insertComment(comment: Comment){
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.apply {
            put(Contract.CommentTable.IMAGE, comment.image)
            put(Contract.CommentTable.MESSAGE, comment.message)
            put(Contract.CommentTable._ID_TASK, comment._idTask)
        }
        db.insert(Contract.CommentTable.TABLE_NAME, null, cv)
    }

    fun getCommentListById(idTask: Int): Cursor?{
        val query = "SELECT * FROM ${Contract.CommentTable.TABLE_NAME} WHERE ${Contract.CommentTable._ID_TASK} = $idTask"
        val db = this.readableDatabase
        var cursor: Cursor?= null
        if(db != null){
            cursor = db.rawQuery(query,null)
        }
        return cursor
    }

    fun deleteAllCommentsById(idTask: Int){
        val db = this.writableDatabase
        val result = db.delete(Contract.CommentTable.TABLE_NAME, "_id_task=?", arrayOf(idTask.toString()))
    }

}