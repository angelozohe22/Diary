package com.example.kmmsharedmodule.data.local

import android.content.Context
import com.example.kmmsharedmodule.diarydb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

/**
 * Created by Angelo on 2/23/2021
 */
actual class DbDriver(private val context: Context) {
    actual fun createDriver(): SqlDriver = AndroidSqliteDriver(
            schema = diarydb.Schema,
            context = context,
            name = "diarydb"
    )
}