package com.example.shared

import kotlin.Long
import kotlin.String

data class Task(
  val id_task: Long,
  val name: String,
  val description: String,
  val date_start: String,
  val date_end: String,
  val is_finished: Long?
) {
  override fun toString(): String = """
  |Task [
  |  id_task: $id_task
  |  name: $name
  |  description: $description
  |  date_start: $date_start
  |  date_end: $date_end
  |  is_finished: $is_finished
  |]
  """.trimMargin()
}
