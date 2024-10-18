package com.timife.cloudiapp

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
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