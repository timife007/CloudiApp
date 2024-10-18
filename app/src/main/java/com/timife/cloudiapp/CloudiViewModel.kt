package com.timife.cloudiapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class CloudiViewModel @Inject constructor(

) : ViewModel() {
    private val _videoUploadState = mutableStateOf("loading")
    val videoUploadState = _videoUploadState


    fun downloadVideo(url: String) {

    }


    fun uploadVideo(url: String) {

    }

    // Helper function to download a file from a URL

}