package com.komputerkit.cgpaapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.komputerkit.cgpaapp.ui.components.*
import com.komputerkit.cgpaapp.viewmodel.CGPAViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CGPACalculatorScreen(
    viewModel: CGPAViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val subjects by remember { derivedStateOf { viewModel.subjects } }
    val cgpaCalculation by viewModel.cgpaCalculation
    val previousCGPA by viewModel.previousCGPA
    val previousCredits by viewModel.previousCredits
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Title
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Kalkulator IPK",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Hitung IPK Semester & Kumulatif",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
        }
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                // Previous data input
                PreviousDataCard(
                    previousCGPA = previousCGPA,
                    previousCredits = previousCredits,
                    onUpdateData = { cgpa, credits ->
                        viewModel.updatePreviousData(cgpa, credits)
                    }
                )
            }
            
            item {
                // Subject input
                SubjectInputCard(
                    onAddSubject = { subject ->
                        viewModel.addSubject(subject)
                    }
                )
            }
            
            item {
                // Action buttons
                if (subjects.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.calculateCGPA() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hitung IPK",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        OutlinedButton(
                            onClick = { viewModel.clearSubjects() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hapus Semua",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            item {
                // CGPA result
                if (cgpaCalculation.totalCredits > 0) {
                    CGPAResultCard(cgpaCalculation = cgpaCalculation)
                }
            }
            
            item {
                // Subject list header
                if (subjects.isNotEmpty()) {
                    Text(
                        text = "Daftar Mata Kuliah (${subjects.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            
            // Subject list
            itemsIndexed(subjects) { index, subject ->
                SubjectListItem(
                    subject = subject,
                    onRemove = { viewModel.removeSubject(index) }
                )
            }
            
            item {
                // Empty state
                if (subjects.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Belum Ada Mata Kuliah",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tambahkan mata kuliah untuk mulai menghitung IPK",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}
