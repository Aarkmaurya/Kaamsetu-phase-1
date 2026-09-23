package com.nexora.kaamsetu.feature.technician.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nexora.kaamsetu.core.di.GenericViewModelFactory
import com.nexora.kaamsetu.core.di.LocalAppContainer
import com.nexora.kaamsetu.core.theme.InfoCard
import com.nexora.kaamsetu.core.theme.ScreenTitle

@Composable
fun TechnicianProfileScreen(onLogout: () -> Unit) {
    val container = LocalAppContainer.current
    val viewModel: TechnicianProfileViewModel = viewModel(
        factory = GenericViewModelFactory { TechnicianProfileViewModel(container.authRepository) }
    )
    val currentUser by viewModel.currentUser.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenTitle(title = "Profile")

        InfoCard {
            Text(text = currentUser?.name ?: "—", style = MaterialTheme.typography.titleMedium)
            Text(text = currentUser?.phone ?: "", style = MaterialTheme.typography.bodyMedium)
        }

        OutlinedButton(
            onClick = {
                viewModel.logout()
                onLogout()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Log out")
        }
    }
}
