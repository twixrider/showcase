package com.chrisaltenhofer.showcase.presentation.ar

import android.view.Choreographer
import android.view.SurfaceView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.filament.utils.ModelViewer

class ArViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is AR Fragment"
    }
    val text: LiveData<String> = _text

}