package com.example.kmmsharedmodule

import com.example.kmmsharedmodule.kmmsharedmodule.newInstance
import com.example.kmmsharedmodule.kmmsharedmodule.schema
import com.example.shared.DiaryQueries
import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.db.SqlDriver

interface diarydb : Transacter {
  val diaryQueries: DiaryQueries

  companion object {
    val Schema: SqlDriver.Schema
      get() = diarydb::class.schema

    operator fun invoke(driver: SqlDriver): diarydb = diarydb::class.newInstance(driver)}
}
