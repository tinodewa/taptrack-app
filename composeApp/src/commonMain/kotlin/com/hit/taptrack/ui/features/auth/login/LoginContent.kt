package com.hit.taptrack.ui.features.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hit.taptrack.ui.features.auth.login.components.LoginFormSection
import com.hit.taptrack.ui.features.auth.login.states.LoginEvent
import com.hit.taptrack.ui.features.auth.login.states.LoginUiState

// --- TAMPILAN MOBILE (Compact) ---
@Composable
fun LoginCompact(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "TAPTRACK",
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary // Disesuaikan dengan tema
        )
        Text(
            text = "Sign in to continue racing",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
        )

        LoginFormSection(
            uiState = uiState,
            onEvent = onEvent
        )
    }
}

// --- TAMPILAN DESKTOP (Expanded - Split View) ---
@Composable
fun LoginExpanded(
    uiState: LoginUiState,
    onEvent: (LoginEvent) -> Unit,
    onBackClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Kiri: Visual / Hero Image
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "TAPTRACK",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = (48 * 1.1).sp
                )
                Text(
                    text = "Sistem Manajemen Balapan",
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(24.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        // Kanan: Form Login
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 450.dp)
                    .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Login",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
                LoginFormSection(
                    uiState = uiState,
                    onEvent = onEvent
                )
            }
        }
    }
}