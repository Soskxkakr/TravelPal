package com.example.travelpal.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class TPButton(context : Context, attrs : AttributeSet) : AppCompatButton(context, attrs) {
    init {
        applyFont()
    }

    private fun applyFont() {
        val typeFace : Typeface =
            Typeface.createFromAsset(context.assets, "Oswald-Regular.ttf")
        setTypeface(typeFace)
    }
}