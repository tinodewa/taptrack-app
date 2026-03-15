package com.hit.taptrack.ui.features.scan.states

// Representasi hasil pindai untuk memberikan feedback visual ke Panitia
sealed class ScanResult {
    data class Success(val racerName: String, val racerNumber: String, val time: String) : ScanResult()
    data class Error(val message: String) : ScanResult()
}