package com.example.diary

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Angelo on 2/21/2021
 */
fun afterTextChanged(function: (s: Editable) -> Unit): TextWatcher {
    return object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
        override fun afterTextChanged(s: Editable) {
            function(s)
        }
    }
}

fun onTextChanged(function: (s: CharSequence, start: Int, before: Int, count: Int) -> Unit): TextWatcher {
    return object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            function(s, start, before, count)
        }
        override fun afterTextChanged(s: Editable) {}
    }
}

@SuppressLint("ClickableViewAccessibility")
fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
    this.setOnTouchListener { v, event ->
        var hasConsumed = false
        if (v is EditText) {
            if (event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}

fun customDialog(ctx: Context, layout : Int, width:Int? = null, height:Int? = null, function: (View, AlertDialog) -> Unit ) {

    val width = width?.let { calculatePxToDps(ctx, it) } ?: WindowManager.LayoutParams.WRAP_CONTENT
    val height  = height?.let { calculatePxToDps(ctx, it) } ?: WindowManager.LayoutParams.WRAP_CONTENT
    val dialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) AlertDialog.Builder(
            ctx,
            R.style.CustomDialogBackground
    ) else AlertDialog.Builder(ctx)

    val layoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as android.view.LayoutInflater
    val view = layoutInflater.inflate(layout, null)
    val customDialog = dialog.create()
    customDialog.apply {
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        setView(view)
        function(view,this)
        show()
        window?.setLayout(width, height)
    }
}

fun calculatePxToDps(context: Context, pixels: Int): Int {
    val scale = context.resources.displayMetrics.density
    return (pixels*scale + 0.5f).toInt()
}

fun isNullOrEmpty(text: Any): Boolean{
    return when(text){
        is String -> TextUtils.isEmpty(text)
        else -> false
    }
}

fun getFormatDate(date: String) = date.substring(0,5)

fun validateDate(date: String): Boolean{
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val strDate = sdf.parse(date)
        return System.currentTimeMillis() < strDate.time
    }catch (e: Exception){
        false
    }
}

fun validateMonth(newEnd: String): Boolean{
    return try {
        val month = newEnd.substring(3,5).toInt()
        val calendar = Calendar.getInstance()
        val maxMonth = calendar.getMaximum(Calendar.MONTH) + 1
        return month<=maxMonth && month != 0
    }catch (e: Exception){
        false
    }
}

fun validateDay(newEnd: String): Boolean{
    return try {
        val day = newEnd.substring(0,2).toInt()
        val calendar = Calendar.getInstance()
        val maxDay = calendar.getMaximum(Calendar.DAY_OF_MONTH)
        return day<=maxDay && day != 0
    }catch (e: Exception){
        false
    }
}

fun transformDateSave(date: String): String{
    val input = SimpleDateFormat("dd/MM/yyyy")
    val output = SimpleDateFormat("yyyy-MM-dd")
    val inputDateStr = date
    val date = input.parse(inputDateStr)
    val outputDateStr = output.format(date)
    return outputDateStr
}

fun transformDateReceive(date: String): String{
    val input = SimpleDateFormat("yyyy-MM-dd")
    val output = SimpleDateFormat("dd/MM/yyyy")
    val inputDateStr = date
    val date = input.parse(inputDateStr)
    val outputDateStr = output.format(date)
    return outputDateStr
}


