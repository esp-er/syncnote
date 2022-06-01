package com.raywenderlich.android.jetnotes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.raywenderlich.android.jetnotes.domain.QRAnalyzer
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import com.raywenderlich.android.jetnotes.theme.JetNotesTheme
import com.raywenderlich.android.jetnotes.ui.screens.NotesScreen
import com.raywenderlich.android.jetnotes.ui.screens.SaveNoteScreen
import com.raywenderlich.android.jetnotes.ui.screens.ArchiveScreen
import com.raywenderlich.android.jetnotes.ui.screens.SyncScreen
import com.raywenderlich.jetnotes.MainViewModel
import org.koin.androidx.compose.getViewModel
import java.lang.Exception


/**
 * Main activity for the app.
 */

@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      JetNotesTheme{
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {

          val context = LocalContext.current
          val lifecycleOwner = LocalLifecycleOwner.current
          val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

          var code by remember { mutableStateOf("") }
          var hasCameraPermission by remember {
            mutableStateOf(
              ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
              ) == PackageManager.PERMISSION_GRANTED
            )
          }
          val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { granted ->
              hasCameraPermission = granted
            }
          )

          LaunchedEffect(key1 = true) {
            launcher.launch(Manifest.permission.CAMERA)
          }

          Column(modifier = Modifier.fillMaxSize()) {
            if (hasCameraPermission) {
              AndroidView(
                factory = { context ->
                  val previewView = PreviewView(context)
                  val preview = Preview.Builder().build()
                  val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                  preview.setSurfaceProvider(previewView.surfaceProvider)
                  val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                  imageAnalysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    QRAnalyzer { result ->
                      result?.let { code = it }
                    }
                  )

                  try {
                    cameraProviderFuture.get().bindToLifecycle(
                      lifecycleOwner,
                      selector,
                      preview,
                      imageAnalysis
                    )
                  } catch (e: Exception) {
                    e.printStackTrace()
                  }

                  return@AndroidView previewView
                },
                modifier = Modifier.weight(1f)
              )
              Text(
                text = code,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(32.dp)
              )
            }
          }
        }
      }

    }
  }
}

@ExperimentalMaterialApi
@Composable
private fun MainActivityScreen(viewModel: MainViewModel = getViewModel()) { //Koin injects viewmodel
  Surface {
    when (NotesRouter.currentScreen) {
      is Screen.Notes -> NotesScreen(viewModel)
      is Screen.SaveNote -> SaveNoteScreen(viewModel)
      is Screen.Archive -> ArchiveScreen(viewModel) //ArchiveScreen(viewModel)
      is Screen.Sync -> SyncScreen(viewModel) //ArchiveScreen(viewModel)
      //is Screen.Chat -> TestChatScreen(viewModel)
    }
  }
}
