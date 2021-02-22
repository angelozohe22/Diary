package com.example.diary.data.local.source.db

import android.provider.BaseColumns

/**
 * Created by Angelo on 2/22/2021
 */
class Contract private constructor() {

    companion object{
        //Creacion de tabla
        const val SQL_CREATE_TABLE_TASK =
            "CREATE TABLE ${TaskTable.TABLE_NAME} (" +
                    "${TaskTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "${TaskTable.NAME} TEXT NOT NULL,"+
                    "${TaskTable.DESCRIPTION} TEXT NOT NULL,"+
                    "${TaskTable.DATE_START} TEXT NOT NULL,"+
                    "${TaskTable.DATE_END} TEXT NOT NULL," +
                    "${TaskTable.IS_FINISHED} INTEGER NOT NULL)"

        const val SQL_CREATE_TABLE_COMMENT =
            "CREATE TABLE ${CommentTable.TABLE_NAME} (" +
                    "${CommentTable.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "${CommentTable.IMAGE} TEXT NOT NULL,"+
                    "${CommentTable.MESSAGE} TEXT NOT NULL,"+
                    "${CommentTable._ID_TASK} INT NOT NULL," +
                    " FOREIGN KEY(${CommentTable._ID_TASK}) REFERENCES ${TaskTable.TABLE_NAME}(${TaskTable.COLUMN_ID}) )"


        const val SQL_DELETE_TABLE_TASK = "DROP TABLE IF EXISTS ${TaskTable.TABLE_NAME}"
    }

    abstract class TaskTable : BaseColumns{
        companion object {
            const val TABLE_NAME    = "task"
            const val COLUMN_ID     = "id_task"
            const val NAME          = "name"
            const val DESCRIPTION   = "description"
            const val DATE_START    = "date_start"
            const val DATE_END      = "date_end"
            const val IS_FINISHED   = "is_finished"
        }
    }

    abstract class CommentTable : BaseColumns{
        companion object {
            const val TABLE_NAME    = "comment"
            const val COLUMN_ID     = "id_comment"
            const val IMAGE         = "image"
            const val MESSAGE       = "message"
            const val _ID_TASK      = "_id_task"
        }
    }


}