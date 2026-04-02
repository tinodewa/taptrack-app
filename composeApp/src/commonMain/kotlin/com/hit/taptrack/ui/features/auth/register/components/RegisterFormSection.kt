package com.hit.taptrack.ui.features.auth.register.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.hit.taptrack.ui.features.auth.register.states.RegisterEvent
import com.hit.taptrack.ui.features.auth.register.states.RegisterUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterFormSection(
    uiState: RegisterUiState,
    onEvent: (RegisterEvent) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var expandedBloodType by remember { mutableStateOf(false) }
    val bloodTypes = listOf("A", "B", "AB", "O")

    Column(modifier = Modifier.fillMaxWidth()) {
        // Nama Lengkap
        OutlinedTextField(
            value = uiState.fullName,
            onValueChange = { onEvent(RegisterEvent.FullNameChanged(it)) },
            label = { Text("Nama Lengkap") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Email
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { onEvent(RegisterEvent.EmailChanged(it)) },
            label = { Text("Surel (Email)") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Password
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { onEvent(RegisterEvent.PasswordChanged(it)) },
            label = { Text("Kata Sandi") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Password"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Row untuk Golongan Darah & Kontak Darurat (Agar hemat ruang vertikal)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dropdown Golongan Darah
            ExposedDropdownMenuBox(
                expanded = expandedBloodType,
                onExpandedChange = { expandedBloodType = !expandedBloodType },
                modifier = Modifier.weight(0.4f)
            ) {
                OutlinedTextField(
                    value = uiState.bloodType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Gol. Darah") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBloodType) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedBloodType,
                    onDismissRequest = { expandedBloodType = false }
                ) {
                    bloodTypes.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                onEvent(RegisterEvent.BloodTypeChanged(selectionOption))
                                expandedBloodType = false
                            }
                        )
                    }
                }
            }

            // Kontak Darurat
            OutlinedTextField(
                value = uiState.emergencyContact,
                onValueChange = { onEvent(RegisterEvent.EmergencyContactChanged(it)) },
                label = { Text("Kontak Darurat") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.weight(0.6f),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Data Kendaraan
        OutlinedTextField(
            value = uiState.vehicle,
            onValueChange = { onEvent(RegisterEvent.VehicleChanged(it)) },
            label = { Text("Atribut Kendaraan (Opsional)") },
            placeholder = { Text("Contoh: Yamaha YZ250F") },
            leadingIcon = { Icon(Icons.Default.DirectionsCar, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Register Button
        Button(
            onClick = { onEvent(RegisterEvent.SubmitRegister) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text("DAFTAR & MASUK", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}