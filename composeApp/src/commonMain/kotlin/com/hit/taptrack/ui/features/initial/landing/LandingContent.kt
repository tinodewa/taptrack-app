package com.hit.taptrack.ui.features.initial.landing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hit.taptrack.ui.features.initial.landing.components.CallToActionSection
import com.hit.taptrack.ui.features.initial.landing.components.FeaturesSection
import com.hit.taptrack.ui.features.initial.landing.components.HeroSection

// --- TAMPILAN MOBILE ---
@Composable
fun LandingCompact(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        HeroSection()
        Spacer(modifier = Modifier.height(48.dp))
        FeaturesSection()
        Spacer(modifier = Modifier.height(48.dp))
        CallToActionSection(onLoginClick, onRegisterClick)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// --- TAMPILAN DESKTOP ---
@Composable
fun LandingExpanded(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalArrangement = Arrangement.spacedBy(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sisi Kiri: Hero & Features
        Column(modifier = Modifier.weight(1.2f)) {
            HeroSection(isDesktop = true)
            Spacer(modifier = Modifier.height(48.dp))
            FeaturesSection(isDesktop = true)
        }

        // Sisi Kanan: Kartu Login/Register
        Card(
            modifier = Modifier.weight(0.8f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Mulai Balapanmu",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(32.dp))
                CallToActionSection(onLoginClick, onRegisterClick)
            }
        }
    }
}