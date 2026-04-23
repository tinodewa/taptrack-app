package com.hit.taptrack.ui.features.eventDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hit.taptrack.ui.features.eventDetail.states.EventDetailEvent
import com.hit.taptrack.ui.features.leaderboard.LeaderboardScreen
import com.hit.taptrack.ui.features.eventManage.EventManageScreen
import com.hit.taptrack.ui.features.myTicket.MyTicketsScreen
import com.hit.taptrack.ui.shared.RmsScaffold
import org.koin.core.parameter.parametersOf
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.hit.taptrack.ui.features.myTicket.components.QrCodeDialog
import com.hit.taptrack.ui.features.myTicket.states.MyTicketsEvent

// Kita tambahkan parameter eventId di class Screen
data class EventDetailScreen(val eventId: String) : Screen {
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }

        // Inject ScreenModel dan berikan parameter eventId
        val screenModel = koinScreenModel<EventDetailScreenModel> { parametersOf(eventId) }
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState.isJoinSuccess) {
            if (uiState.isJoinSuccess) {
                snackbarHostState.showSnackbar(uiState.joinSuccessMessage ?: "Berhasil mendaftar!")
                // Opsional: Langsung arahkan user ke halaman MyTicketsScreen
                // navigator.push(MyTicketsScreen())
            }
        }

        @OptIn(ExperimentalMaterial3Api::class)
        RmsScaffold(
            backgroundColor = MaterialTheme.colorScheme.background,
            snackbarHostState = snackbarHostState
        ) { innerPadding ->
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { screenModel.onEvent(EventDetailEvent.RefreshData) },
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    if (uiState.isLoading && !uiState.isRefreshing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        uiState.eventDetail?.let { detail ->
                            if (maxWidth < 800.dp) {
                                EventDetailCompact(
                                    detail,
                                    uiState,
                                    screenModel::onEvent,
                                    onNavigateToTickets = { navigator.push(MyTicketsScreen()) },
                                    onNavigateToLeaderboard = {
                                        navigator.push(
                                            LeaderboardScreen(
                                                detail.id
                                            )
                                        )
                                    },
                                    onNavigateToManageEvent = {
                                        navigator.push(
                                            EventManageScreen(
                                                detail.id
                                            )
                                        )
                                    },
                                    onBackClick = { navigator.pop() },
                                )
                            } else {
                                EventDetailExpanded(
                                    detail,
                                    uiState,
                                    screenModel::onEvent,
                                    onNavigateToTickets = { navigator.push(MyTicketsScreen()) },
                                    onNavigateToLeaderboard = {
                                        navigator.push(
                                            LeaderboardScreen(
                                                detail.id
                                            )
                                        )
                                    },
                                    onNavigateToManageEvent = {
                                        navigator.push(
                                            EventManageScreen(
                                                detail.id
                                            )
                                        )
                                    },
                                    onBackClick = { navigator.pop() }
                                )
                            }
                        }
                    }
                }
            }

            // Pop-up Dialog Input Nomor Lambung & Kategori
            if (uiState.showJoinDialog) {
                // Ambil daftar kategori dari state
                val availableCategories = uiState.eventDetail?.categories ?: emptyList()
                var expanded by remember { mutableStateOf(false) }

                // Cari nama kategori yang sedang dipilih untuk ditampilkan di TextField
                val selectedCategoryName =
                    availableCategories.find { it.id == uiState.selectedCategoryId }?.name
                        ?: "Pilih Kelas Balapan..."

                AlertDialog(
                    onDismissRequest = { screenModel.onEvent(EventDetailEvent.HideJoinDialog) },
                    title = { Text("Pendaftaran Balapan") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                            // 1. Input Nomor Lambung
                            OutlinedTextField(
                                value = uiState.raceNumberInput,
                                onValueChange = {
                                    screenModel.onEvent(
                                        EventDetailEvent.RaceNumberChanged(
                                            it
                                        )
                                    )
                                },
                                label = { Text("Nomor Start (Cth: 99)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // 2. Dropdown Pilih Kategori
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                OutlinedTextField(
                                    value = selectedCategoryName,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Kelas") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expanded
                                        )
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                                )

                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    availableCategories.forEach { category ->
                                        DropdownMenuItem(
                                            text = { Text(category.name) },
                                            onClick = {
                                                screenModel.onEvent(
                                                    EventDetailEvent.CategorySelected(
                                                        category.id
                                                    )
                                                )
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { screenModel.onEvent(EventDetailEvent.SubmitJoinEvent) },
                            // Aktif hanya jika nomor terisi DAN kategori dipilih
                            enabled = !uiState.isJoinLoading && uiState.raceNumberInput.isNotBlank() && uiState.selectedCategoryId != null
                        ) {
                            if (uiState.isJoinLoading) CircularProgressIndicator(
                                modifier = Modifier.size(
                                    20.dp
                                )
                            )
                            else Text("KONFIRMASI")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { screenModel.onEvent(EventDetailEvent.HideJoinDialog) },
                            enabled = !uiState.isJoinLoading
                        ) {
                            Text("Batal")
                        }
                    }
                )
            }

            // Pop-up Dialog Tiket (Jika sudah terdaftar dan tombol diklik)
            if (uiState.showTicketDialog && uiState.ticketData != null) {
                QrCodeDialog(
                    ticket = uiState.ticketData!!,
                    onDismiss = { screenModel.onEvent(EventDetailEvent.HideTicketDialog) }
                )
//                Dialog(onDismissRequest = { screenModel.onEvent(EventDetailEvent.HideTicketDialog) }) {
//                    Card(
//                        shape = RoundedCornerShape(24.dp),
//                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//                        elevation = CardDefaults.cardElevation(8.dp)
//                    ) {
//                        Column(
//                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Text(
//                                text = "TIKET BALAPAN",
//                                style = MaterialTheme.typography.titleLarge,
//                                fontWeight = FontWeight.Black,
//                                color = MaterialTheme.colorScheme.primary
//                            )
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Text(
//                                text = uiState.eventDetail?.name ?: "Event",
//                                style = MaterialTheme.typography.bodyMedium,
//                                color = Color.Gray
//                            )
//
//                            Spacer(modifier = Modifier.height(24.dp))
//
//                            // Placeholder QR Code (Bisa diganti dengan bitmap ZXing)
//                            Box(
//                                modifier = Modifier
//                                    .size(200.dp)
//                                    .background(Color.White, RoundedCornerShape(12.dp))
//                                    .padding(16.dp),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                // Ganti dengan Image bitmap jika menggunakan Zxing
//                                Icon(
//                                    Icons.Default.QrCode2,
//                                    contentDescription = "QR Code",
//                                    modifier = Modifier.fillMaxSize(),
//                                    tint = Color.Black
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.height(24.dp))
//
//                            // Nomor Lambung User
//                            Box(
//                                modifier = Modifier
//                                    .background(
//                                        MaterialTheme.colorScheme.secondaryContainer,
//                                        RoundedCornerShape(8.dp)
//                                    )
//                                    .padding(horizontal = 24.dp, vertical = 8.dp)
//                            ) {
//                                Text(
//                                    text = "#${uiState.ticketData!!.raceNumber}",
//                                    style = MaterialTheme.typography.displaySmall,
//                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
//                                    fontWeight = FontWeight.Black
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.height(24.dp))
//                            Button(
//                                onClick = { screenModel.onEvent(EventDetailEvent.HideTicketDialog) },
//                                modifier = Modifier.fillMaxWidth()
//                            ) {
//                                Text("TUTUP")
//                            }
//                        }
//                    }
//                }
            }
        }
    }
}