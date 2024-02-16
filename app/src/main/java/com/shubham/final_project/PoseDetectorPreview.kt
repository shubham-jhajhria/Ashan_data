package com.shubham.final_project

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.SystemClock
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor
import kotlin.math.max

@SuppressLint("UnrememberedMutableState")
@Composable
fun PoseDetectionPreview(
    poseLandmarker: PoseLandmarker,
    executor: Executor,
    resultBundleState: MutableState<PoseDetector.ResultBundle?>,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = {
            val view = PreviewView(context)
            view

        },
        update = { previewView ->
            val cameraProvider1 = ProcessCameraProvider.getInstance(context)
            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(executor) { image ->
                        val mpImage = imageProxyToMPImage(image)
                        val frameTime = SystemClock.uptimeMillis()
                        poseLandmarker.detectAsync(mpImage, frameTime)
                        image.close()
                    }

                }



            cameraProvider1.addListener(
                {
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    val cameraProvider = cameraProvider1.get()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        imageAnalyzer
                    )
                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)

                },
                ContextCompat.getMainExecutor(context)
            )
        }
    )
    CreateHeader()
    DrawPosesOnPreview(resultBundleState)
}

@Composable
private fun DrawPosesOnPreview(resultBundleState: MutableState<PoseDetector.ResultBundle?>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val resultBundle = resultBundleState.value
        if (resultBundle != null) {
            val scale = max(size.width * 1f / resultBundle.inputImageWidth, size.height * 1f / resultBundle.inputImageHeight)
            resultBundle.results.forEach { result ->
                result.landmarks().forEach { poseLandmarks ->
                    poseLandmarks.forEach { landmark ->
                        drawCircle(
                            color = Color.Yellow,
                            radius = 10F,
                            center = Offset(landmark.x() * scale*resultBundle.inputImageWidth, landmark.y() * scale*resultBundle.inputImageHeight)
                        )
                    }
                }
            }
        }
    }
}


private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
    val yBuffer = imageProxy.planes[0].buffer // Y plane
    val uBuffer = imageProxy.planes[1].buffer // U plane
    val vBuffer = imageProxy.planes[2].buffer // V plane

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    // Copy Y, U, and V buffers into nv21 array
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    // Create a YuvImage from the nv21 array
    val yuvImage = YuvImage(nv21, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)

    // Create a ByteArrayOutputStream to store the JPEG data
    val outputStream = ByteArrayOutputStream()

    // Compress the YuvImage into JPEG format
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, outputStream)

    // Create a Bitmap from the JPEG data
    val jpegByteArray = outputStream.toByteArray()

    // Convert JPEG bytes to Bitmap
    val bitmap = BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.size)

    // Rotate the bitmap based on rotationDegrees
    val matrix = Matrix().apply {
        postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

// Function to convert ImageProxy to MPImage
private fun imageProxyToMPImage(imageProxy: ImageProxy): MPImage {
    val bitmap = imageProxyToBitmap(imageProxy)
    return BitmapImageBuilder(bitmap).build()
}
