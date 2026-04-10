package com.hit.taptrack.ui.features.scan.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.BarcodeView

@Composable
actual fun QRScannerView(modifier: Modifier, onScan: (String) -> Unit) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarcodeView(context).apply {
                decodeContinuous(object : BarcodeCallback {
                    override fun barcodeResult(result: BarcodeResult?) {
                        // Pass the scanned text back to your Compose UI
                        result?.text?.let { onScan(it) }
                    }
                    override fun possibleResultPoints(resultPoints: MutableList<ResultPoint>?) {}
                })
                // Start the camera automatically
                resume()
            }
        },
        update = { view ->
            view.resume()
        }
    )
}