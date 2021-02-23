package com.example.kmmsharedmodule.kmmsharedmodule

import com.example.kmmsharedmodule.diarydb
import com.example.shared.Comment
import com.example.shared.DiaryQueries
import com.example.shared.Task
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.TransacterImpl
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.internal.copyOnWriteList
import kotlin.Any
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.MutableList
import kotlin.jvm.JvmField
import kotlin.reflect.KClass

internal val KClass<diarydb>.schema: SqlDriver.Schema
  get() = diarydbImpl.Schema

internal fun KClass<diarydb>.newInstance(driver: SqlDriver): diarydb = diarydbImpl(driver)

private class diarydbImpl(
  driver: SqlDriver
) : TransacterImpl(driver), diarydb {
  override val diaryQueries: DiaryQueriesImpl = DiaryQueriesImpl(this, driver)

  object Schema : SqlDriver.Schema {
    override val version: Int
      get() = 1

    override fun create(driver: SqlDriver) {
      driver.execute(null, """
          |CREATE TABLE task(
          |    id_task INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    name TEXT NOT NULL,
          |    description TEXT NOT NULL,
          |    date_start TEXT NOT NULL,
          |    date_end TEXT NOT NULL,
          |    is_finished INTEGER)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE comment(
          |    id_comment INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    image TEXT NOT NULL,
          |    message TEXT NOT NULL,
          |    _id_task INTEGER NOT NULL,
          |    FOREIGN KEY(_id_task) REFERENCES task(id_task))
          """.trimMargin(), 0)
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Int,
      newVersion: Int
    ) {
    }
  }
}

private class DiaryQueriesImpl(
  private val database: diarydbImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), DiaryQueries {
  internal val SelectAllTasks: MutableList<Query<*>> = copyOnWriteList()

  internal val SelectTaskById: MutableList<Query<*>> = copyOnWriteList()

  internal val SelectCommentListById: MutableList<Query<*>> = copyOnWriteList()

  override fun <T : Any> SelectAllTasks(mapper: (
    id_task: Long,
    name: String,
    description: String,
    date_start: String,
    date_end: String,
    is_finished: Long?
  ) -> T): Query<T> = Query(1309598934, SelectAllTasks, driver, "diary.sq", "SelectAllTasks",
      "SELECT * FROM task") { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getLong(5)
    )
  }

  override fun SelectAllTasks(): Query<Task> = SelectAllTasks { id_task, name, description,
      date_start, date_end, is_finished ->
    com.example.shared.Task(
      id_task,
      name,
      description,
      date_start,
      date_end,
      is_finished
    )
  }

  override fun <T : Any> SelectTaskById(id_task: Long, mapper: (
    id_task: Long,
    name: String,
    description: String,
    date_start: String,
    date_end: String,
    is_finished: Long?
  ) -> T): Query<T> = SelectTaskByIdQuery(id_task) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getLong(5)
    )
  }

  override fun SelectTaskById(id_task: Long): Query<Task> = SelectTaskById(id_task) { id_task, name,
      description, date_start, date_end, is_finished ->
    com.example.shared.Task(
      id_task,
      name,
      description,
      date_start,
      date_end,
      is_finished
    )
  }

  override fun <T : Any> SelectCommentListById(_id_task: Long, mapper: (
    id_comment: Long,
    image: String,
    message: String,
    _id_task: Long
  ) -> T): Query<T> = SelectCommentListByIdQuery(_id_task) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getLong(3)!!
    )
  }

  override fun SelectCommentListById(_id_task: Long): Query<Comment> =
      SelectCommentListById(_id_task) { id_comment, image, message, _id_task ->
    com.example.shared.Comment(
      id_comment,
      image,
      message,
      _id_task
    )
  }

  override fun InsertTask(
    name: String,
    description: String,
    date_start: String,
    date_end: String,
    is_finished: Long?
  ) {
    driver.execute(1305030571, """
    |INSERT INTO task(name, description, date_start, date_end, is_finished)
    | VALUES (?, ?, ?, ?, ?)
    """.trimMargin(), 5) {
      bindString(1, name)
      bindString(2, description)
      bindString(3, date_start)
      bindString(4, date_end)
      bindLong(5, is_finished)
    }
    notifyQueries(1305030571, {database.diaryQueries.SelectAllTasks +
        database.diaryQueries.SelectTaskById})
  }

  override fun UpdateTask(
    name: String,
    description: String,
    date_start: String,
    date_end: String,
    is_finished: Long?,
    id_task: Long
  ) {
    driver.execute(42933691, """
    |UPDATE task
    | SET name = ?,
    |     description = ?,
    |     date_start = ?,
    |     date_end = ?,
    |     is_finished = ?
    | WHERE id_task = ?
    """.trimMargin(), 6) {
      bindString(1, name)
      bindString(2, description)
      bindString(3, date_start)
      bindString(4, date_end)
      bindLong(5, is_finished)
      bindLong(6, id_task)
    }
    notifyQueries(42933691, {database.diaryQueries.SelectAllTasks +
        database.diaryQueries.SelectTaskById})
  }

  override fun DeleteTask(id_task: Long) {
    driver.execute(2103454109, """DELETE FROM task WHERE id_task = ?""", 1) {
      bindLong(1, id_task)
    }
    notifyQueries(2103454109, {database.diaryQueries.SelectAllTasks +
        database.diaryQueries.SelectTaskById})
  }

  override fun InsertComment(
    image: String,
    message: String,
    _id_task: Long
  ) {
    driver.execute(-1685456263, """
    |INSERT INTO comment(image, message, _id_task)
    | VALUES (?, ?, ?)
    """.trimMargin(), 3) {
      bindString(1, image)
      bindString(2, message)
      bindLong(3, _id_task)
    }
    notifyQueries(-1685456263, {database.diaryQueries.SelectCommentListById})
  }

  override fun DeleteAllCommentsById(_id_task: Long) {
    driver.execute(414385423, """DELETE FROM comment WHERE _id_task = ?""", 1) {
      bindLong(1, _id_task)
    }
    notifyQueries(414385423, {database.diaryQueries.SelectCommentListById})
  }

  private inner class SelectTaskByIdQuery<out T : Any>(
    @JvmField
    val id_task: Long,
    mapper: (SqlCursor) -> T
  ) : Query<T>(SelectTaskById, mapper) {
    override fun execute(): SqlCursor = driver.executeQuery(-888622752,
        """SELECT * FROM task WHERE id_task = ?""", 1) {
      bindLong(1, id_task)
    }

    override fun toString(): String = "diary.sq:SelectTaskById"
  }

  private inner class SelectCommentListByIdQuery<out T : Any>(
    @JvmField
    val _id_task: Long,
    mapper: (SqlCursor) -> T
  ) : Query<T>(SelectCommentListById, mapper) {
    override fun execute(): SqlCursor = driver.executeQuery(1056716422,
        """SELECT * FROM comment WHERE _id_task = ?""", 1) {
      bindLong(1, _id_task)
    }

    override fun toString(): String = "diary.sq:SelectCommentListById"
  }
}
