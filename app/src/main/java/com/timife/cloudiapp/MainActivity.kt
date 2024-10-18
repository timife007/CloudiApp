package com.timife.cloudiapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.timife.cloudiapp.ui.theme.CloudiAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            CloudiAppTheme {
                var videoProgress by remember { mutableDoubleStateOf(0.0) }  // Track progress in state
                var imageProgress by remember { mutableDoubleStateOf(0.0) }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Screen(
                            padding = innerPadding, onClick = {
                                lifecycleScope.launch {
                                    uploadVideo(
                                        this@MainActivity,
                                        onProgressTracked = { currentProgress ->
                                            videoProgress = currentProgress
                                        }
                                    )
                                }
                            },
                            progress = videoProgress,
                            isVideo = true
                        )
                        Screen(
                            padding = innerPadding, onClick = {
                                lifecycleScope.launch {
                                    uploadImage(
                                        this@MainActivity,
                                        onProgressTracked = { currentProgress ->
                                            imageProgress = currentProgress
                                        }
                                    )
                                }
                            },
                            progress = imageProgress,
                            isVideo = false
                        )
                    }

                }
            }
        }
    }

    //Video upload
    private suspend fun uploadVideo(context: Context, onProgressTracked: (Double) -> Unit) {
        val url =
            "https://res.cloudinary.com/dlujwccdb/video/upload/v1728910228/Stridez/posts/test_uy4d9t.mp4"
        try {
            val file = withContext(Dispatchers.IO) {
                downloadFile(url, context)
            }
            if (file != null) {
                val requestId =
                    MediaManager.get().upload(file.path).callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {
                            Log.d("MainActivity", "onStart: $requestId")
                        }

                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                            Log.d("MainActivity", "onProgress: $bytes of $totalBytes")
                            val progress = bytes.toDouble() / totalBytes
                            onProgressTracked(progress)
                            Log.d("MainActivity", "onProgress: $progress")

                        }

                        override fun onSuccess(
                            requestId: String?,
                            resultData: MutableMap<Any?, Any?>?
                        ) {
                            Log.d("MainActivity", "onSuccess: $resultData")
                        }

                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            Log.d("MainActivity", "onError: $error")
                        }

                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                            Log.d("MainActivity", "onReschedule: $error")
                        }
                    }).dispatch()
                Log.d("MainActivity", "uploadVideo: $requestId")
            } else {
                Log.d("MainActivity", "uploadVideo: File is null")
            }
        } catch (e: Exception) {
            Log.d("MainActivity", "uploadVideo: ${e.message}")
        }
    }

    //download image from url and upload
    private suspend fun uploadImage(context: Context, onProgressTracked: (Double) -> Unit) {
        val url =
            "https://res.cloudinary.com/dlujwccdb/video/upload/v1728910228/Stridez/posts/test_uy4d9t.jpg"
        try {
            val file = withContext(Dispatchers.IO) {
                downloadFile(url, context)
            }

            if (file != null) {

                val requestId =
                    MediaManager.get().upload(file.path).callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {
                            Log.d("MainActivity", "onStart: $requestId")
                        }

                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                            val progress = bytes.toDouble() / totalBytes
                            onProgressTracked(progress)
                            Log.d("MainActivity percentage", "onProgress: $progress")
                            Log.d("MainActivity", "onProgress: $bytes of $totalBytes")
                        }

                        override fun onSuccess(
                            requestId: String?,
                            resultData: MutableMap<Any?, Any?>?
                        ) {
                            Log.d("MainActivity", "onSuccess: $resultData")
                        }

                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            Log.d("MainActivity", "onError: $error")
                        }

                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                            Log.d("MainActivity", "onReschedule: $error")
                        }
                    }).dispatch()
                Log.d("MainActivity", "uploadImage: $requestId")
            } else {
                Log.d("MainActivity", "uploadImage: File is null")
            }
        } catch (e: Exception) {
            Log.d("Cloudinary", "uploadImage: ${e.message}")
        }

    }

//    //upload large video
//    private suspend fun uploadLargeVideo(context: Context) {
//        val url =
//            "https://res.cloudinary.com/dlujwccdb/video/upload/v1728910228/Stridez/posts/test_uy4d9t.mp4"
//
//        try {
//            val file = withContext(Dispatchers.IO) {
//                downloadFile(url, context)
//            }
//            if (file != null) {
//                val requestId = MediaManager.get().upload()
//            }
//        }catch (e:Exception){
//            Log.d("MainActivity", "uploadLargeVideo: ${e.message}")
//        }
//    }

    // Helper function to download a file from a URL
    private fun downloadFile(fileUrl: String, context: Context): File? {
        val url = URL(fileUrl)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.connect()

        val inputStream: InputStream = connection.inputStream
        val file = File(context.cacheDir, "temp_file")

        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        return file
    }

}

@Composable
fun Screen(
    padding: PaddingValues,
    progress: Double,  // Receive progress value
    onClick: () -> Unit,
    isVideo: Boolean
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Show progress as a ProgressBar
        LinearProgressIndicator(
            progress = { progress.toFloat() },
            modifier = Modifier.fillMaxWidth(0.8f),
        )

        Text(text = "${(progress * 100).toInt()}%")
        Button(onClick = onClick) {
            Text(text = if (isVideo) "Upload Video" else "Upload Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CloudiAppTheme {
//        Screen(PaddingValues(10.dp), 0.0) { }
    }
}