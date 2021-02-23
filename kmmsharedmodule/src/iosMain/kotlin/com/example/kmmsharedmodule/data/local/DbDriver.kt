package com.example.kmmsharedmodule.data.local

import com.squareup.sqldelight.db.SqlDriver

/**
 * Created by Angelo on 2/23/2021
 */
actual class DbDriver {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(AppDatabase.Schema, "diarydb")
    }
}