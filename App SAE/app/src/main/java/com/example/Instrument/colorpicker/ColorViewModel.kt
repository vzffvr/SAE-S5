package com.example.Instrument.colorpicker

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

class ColorViewModel: ViewModel(){

    private var phare: Int = 0
    private var zone: Int = 0
    private var animation: Int = 1

    var textColor = Color(255, 255, 255, 255)
    private var textColor1: Color = Color(255, 255, 255, 255)
    private var textColor2: Color = Color(255, 255, 255, 255)
    private var textColor3: Color = Color(255, 255, 255, 255)

    var red10: Int = 0
    var green10: Int = 0
    var blue10: Int = 0
    var alpha10: Int = 0
    var intensity_ref: Int = 255

    var hexCode: String = ""

    fun majTextsColors(){
        when(getZone()){
            1-> textColor1 = textColor
            2-> textColor2 = textColor
            3-> textColor3 = textColor
        }
    }

    fun getTextsColors(): Color {
        when(getZone()){
            1-> return textColor1
            2-> return textColor2
            3-> return textColor3
        }
        return Color(255,0,80)
    }

    //TC-> Text Color
    fun setTC(color: Color)
    {
        textColor = color
    }

    //TC-> Text Color
    fun getTC(): Color{
        return textColor
    }
    fun setZone(vol: Int)
    {
        zone = vol
    }

    fun getZone(): Int
    {
        return zone
    }

    fun setPhare(Phare: Int)
    {
        phare = Phare
    }

    fun getPhare(): Int
    {
        return phare
    }

    fun setAnimation(Animation: Int)
    {
        animation = Animation
    }

    fun getAnimation(): Int
    {
        return animation
    }

}