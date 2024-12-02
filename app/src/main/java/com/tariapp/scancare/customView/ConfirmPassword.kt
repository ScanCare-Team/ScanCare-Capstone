package com.tariapp.scancare.customView

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.tariapp.scancare.R

class ConfirmPassword @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = context.getString(R.string.confirm_pass)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}