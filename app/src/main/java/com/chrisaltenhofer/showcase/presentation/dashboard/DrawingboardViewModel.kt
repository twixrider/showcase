package com.chrisaltenhofer.showcase.presentation.dashboard

import android.graphics.Canvas
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chrisaltenhofer.showcase.data.models.DrawingboardDataModel

class DrawingboardViewModel : ViewModel() {

    // hold the drawing in a proper
    private lateinit var canvas: Canvas

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text

    fun resetClicked() {
        _text.postValue("Reset")
    }

    fun sendClicked() {
        _text.postValue("Send")
    }
}