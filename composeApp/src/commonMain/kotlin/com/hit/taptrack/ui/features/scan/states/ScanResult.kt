package com.hit.taptrack.ui.features.scan.states

sealed class ScanResult {
    // Ubah parameter agar sesuai dengan kembalian server
    data class Success(val message: String, val time: String) : ScanResult()
    data class Error(val message: String) : ScanResult()
}