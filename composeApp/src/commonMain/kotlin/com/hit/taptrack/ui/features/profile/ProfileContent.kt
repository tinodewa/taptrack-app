package com.hit.taptrack.ui.features.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hit.taptrack.data.model.UserProfileModel
import com.hit.taptrack.ui.features.profile.components.DigitalLicenseCard
import com.hit.taptrack.ui.features.profile.components.ProfileDetailItem
import com.hit.taptrack.ui.features.profile.states.ProfileEvent

// --- TAMPILAN MOBILE ---
@Composable
fun ProfileCompact(profile: UserProfileModel, onEvent: (ProfileEvent) -> Unit, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp)
    ) {
        // Tombol Back
        IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-12).dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
        }

        Text("Lisensi Digital", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        // Kartu Profil Utama (Hero)
        DigitalLicenseCard(profile)

        Spacer(modifier = Modifier.height(32.dp))
        Text("Informasi Vital & Data Diri", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // List Detail
        ProfileDetailItem(icon = Icons.Default.Bloodtype, title = "Golongan Darah", value = profile.bloodType, isCritical = true)
        ProfileDetailItem(icon = Icons.Default.Emergency, title = "Kontak Darurat", value = profile.emergencyContact)
        ProfileDetailItem(icon = Icons.Default.Email, title = "Surel", value = profile.email)
        ProfileDetailItem(icon = Icons.Default.TwoWheeler, title = "Atribut Kendaraan", value = profile.vehicle)

        Spacer(modifier = Modifier.height(48.dp))

        // Tombol Logout
        Button(
            onClick = { onEvent(ProfileEvent.LogoutClicked) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.onError)
            Spacer(modifier = Modifier.width(8.dp))
            Text("KELUAR", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onError)
        }
    }
}

// --- TAMPILAN DESKTOP (Split View) ---
@Composable
fun ProfileExpanded(profile: UserProfileModel, onEvent: (ProfileEvent) -> Unit, onBackClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalArrangement = Arrangement.spacedBy(32.dp)) {
        // Kiri: Kartu Lisensi & Logout
        Column(modifier = Modifier.weight(0.8f)) {
            IconButton(onClick = onBackClick, modifier = Modifier.offset(x = (-12).dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
            }
            Spacer(modifier = Modifier.height(16.dp))
            DigitalLicenseCard(profile)
            Spacer(modifier = Modifier.weight(1f)) // Dorong tombol logout ke bawah
            Button(
                onClick = { onEvent(ProfileEvent.LogoutClicked) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.onError)
                Spacer(modifier = Modifier.width(8.dp))
                Text("KELUAR", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onError)
            }
        }

        // Kanan: Detail Informasi
        Card(
            modifier = Modifier.weight(1.2f).fillMaxHeight(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(32.dp).verticalScroll(rememberScrollState())) {
                Text("Informasi Vital & Data Diri", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(24.dp))
                ProfileDetailItem(icon = Icons.Default.Bloodtype, title = "Golongan Darah", value = profile.bloodType, isCritical = true)
                ProfileDetailItem(icon = Icons.Default.Emergency, title = "Kontak Darurat", value = profile.emergencyContact)
                ProfileDetailItem(icon = Icons.Default.Email, title = "Surel", value = profile.email)
                ProfileDetailItem(icon = Icons.Default.TwoWheeler, title = "Atribut Kendaraan", value = profile.vehicle)
            }
        }
    }
}