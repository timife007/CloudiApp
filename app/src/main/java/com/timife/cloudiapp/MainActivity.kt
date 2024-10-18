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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.timife.cloudiapp.ui.theme.CloudiAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
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
                val context = LocalContext.current
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        //Video upload
                        UploadScreen(
                            context = context,
                            url = "https://res.cloudinary.com/dlujwccdb/video/upload/v1728910228/Stridez/posts/test_uy4d9t.mp4",
                            isVideo = true
                        )
                        Spacer(modifier = Modifier.height(22.dp))
                        //Image upload
                        UploadScreen(
                            context = context,
                            url = "https://res.cloudinary.com/dlujwccdb/video/upload/v1728910228/Stridez/posts/test_uy4d9t.jpg",
                            isVideo = false
                        )
                    }

                }
            }
        }
    }

    @Composable
    fun UploadScreen(
        context: Context,
        url: String,
        isVideo: Boolean,
        scope: CoroutineScope = rememberCoroutineScope()
    ) {
        // Track the progress individually for each screen
        var progress by remember { mutableDoubleStateOf(0.0) }
        var uploaded by remember { mutableStateOf("") }

        Screen(
            padding = PaddingValues(),  // Adjust this as needed
            onClick = {
                scope.launch {
                    uploadMedia(
                        context = context,
                        url = url,
                        onProgressTracked = { bytes, totalBytes ->
                            val currentProgress = bytes.toDouble() / totalBytes
                            progress = currentProgress  // Update progress
                            // Convert bytes to megabytes (MB) and update the uploaded state
                            val uploadedMB = bytes / (1024.0 * 1024.0) // Bytes to MB
                            val totalMB = totalBytes / (1024.0 * 1024.0) // Total MB

                            uploaded = String.format(
                                "%.2f MB / %.2f MB (%d%%)",
                                uploadedMB,
                                totalMB,
                                (currentProgress * 100).toInt()
                            )
                        }
                    )
                }
            },
            progress = progress,
            isVideo = isVideo,
            onResetProgress = {
                progress = 0.0  // Reset progress
                uploaded = ""
            },
            uploaded = uploaded
        )
    }

    @Composable
    fun Screen(
        padding: PaddingValues,
        progress: Double,  // Receive progress value
        onClick: () -> Unit,
        isVideo: Boolean,
        onResetProgress: () -> Unit = {},
        uploaded: String
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
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = uploaded)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = onClick) {
                    Text(text = if (isVideo) "Upload Video" else "Upload Image")
                }

                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    onResetProgress()
                }) {
                    Text(text = "Reset Progress")
                }
            }

        }
    }

    // Reusable upload function
    private suspend fun uploadMedia(
        context: Context,
        url: String,
        onProgressTracked: (Long, Long) -> Unit
    ) {
        try {
            val file = withContext(Dispatchers.IO) {
                downloadFile(url, context)
            }

            if (file != null) {
                val requestId = MediaManager.get()
                    .upload(file.path)
                    .callback(createUploadCallback(onProgressTracked))
                    .dispatch()

                Log.d("MainActivity", "uploadMedia: $requestId")
            } else {
                Log.d("MainActivity", "uploadMedia: File is null")
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "uploadMedia Error: ${e.message}")
        }
    }

    // Helper: Create a reusable UploadCallback
    private fun createUploadCallback(onProgressTracked: (Long, Long) -> Unit) =
        object : UploadCallback {
            override fun onStart(requestId: String?) {
                Log.d("MainActivity", "onStart: $requestId")
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                onProgressTracked(bytes, totalBytes)
                Log.d("MainActivity", "Progress:($bytes of $totalBytes)")
            }

            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                Log.d("MainActivity", "onSuccess: $resultData")
            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                Log.e("MainActivity", "onError: $error")
            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                Log.w("MainActivity", "onReschedule: $error")
            }
        }


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