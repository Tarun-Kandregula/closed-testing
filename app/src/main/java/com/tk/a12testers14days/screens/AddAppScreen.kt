package com.tk.a12testers14days.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tk.a12testers14days.DeveloperViewModel
import com.tk.a12testers14days.data.remote.AppDto
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    isEmbedded: Boolean = false,
    appToEdit: AppDto? = null,
    viewModel: DeveloperViewModel = koinViewModel()
) {
    var appName by remember(appToEdit) { mutableStateOf(appToEdit?.appName ?: "") }
    var packageName by remember(appToEdit) { mutableStateOf(appToEdit?.packageName ?: "") }
    var appVersion by remember(appToEdit) { mutableStateOf(appToEdit?.appVersion ?: "") }
    var closedTestingLink by remember(appToEdit) { mutableStateOf(appToEdit?.closedTestingLink ?: "") }
    var appDescription by remember(appToEdit) { mutableStateOf(appToEdit?.appDescription ?: "") }
    var paymentAmount by remember(appToEdit) { mutableStateOf(appToEdit?.paymentAmount?.toString() ?: "399") }
    var maxTesters by remember(appToEdit) { mutableStateOf(appToEdit?.maxTesters?.toString() ?: "20") }
    var durationDays by remember(appToEdit) { mutableStateOf(appToEdit?.durationDays?.toString() ?: "15") }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.submissionSuccess.collectLatest {
            onSuccess()
        }
    }

    Scaffold(
        // topBar removed as it is handled by MainActivity
        modifier = Modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = appName,
                onValueChange = { appName = it },
                label = { Text("App Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = packageName,
                onValueChange = { packageName = it },
                label = { Text("Package Name (com.example.app)") },
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = appVersion,
                onValueChange = { appVersion = it },
                label = { Text("App Version (e.g. 1.0.0)") },
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = closedTestingLink,
                onValueChange = { closedTestingLink = it },
                label = { Text("Google Play Web Link") },
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = appDescription,
                onValueChange = { appDescription = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedTextField(
                    value = maxTesters,
                    onValueChange = { maxTesters = it },
                    label = { Text("Testers") },
                    modifier = Modifier.weight(1f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = durationDays,
                    onValueChange = { durationDays = it },
                    label = { Text("Days") },
                    modifier = Modifier.weight(1f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = paymentAmount,
                onValueChange = { paymentAmount = it },
                label = { Text("Total Cost (₹)") },
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    if (appToEdit != null) {
                        viewModel.updateApp(
                            appToEdit.id,
                            appName, packageName, appVersion, closedTestingLink,
                            appDescription, paymentAmount.toInt(), maxTesters.toInt(), durationDays.toInt()
                        )
                    } else {
                        viewModel.submitApp(
                            appName, packageName, appVersion, closedTestingLink,
                            appDescription, paymentAmount.toInt(), maxTesters.toInt(), durationDays.toInt()
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading && appName.isNotEmpty() && packageName.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (appToEdit != null) "Update App" else "Pay & Submit App")
                }
            }
        }
    }
}
