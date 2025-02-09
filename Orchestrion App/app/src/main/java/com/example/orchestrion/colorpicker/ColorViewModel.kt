package com.example.orchestrion.colorpicker

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.orchestrion.BleManager

class ColorViewModel: ViewModel(){

    var animation: Int = 1

    var textColor = Color(255, 255, 255, 255)

    var red10: Int = 0
    var green10: Int = 0
    var blue10: Int = 0
    var alpha10: Int = 0

    fun setColors(red: Int, green: Int, blue: Int){
        red10 = red
        green10 = green
        blue10 = blue
    }

    var hexCode: String = ""

    //TC-> Text Color
    fun setTC(color: Color)
    {
        textColor = color
    }

    fun getTC(): Color
    {
        return textColor
    }
}