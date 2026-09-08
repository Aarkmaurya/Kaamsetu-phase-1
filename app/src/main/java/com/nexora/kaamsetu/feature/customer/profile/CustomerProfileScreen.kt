package com.nexora.kaamsetu.feature.customer.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nexora.kaamsetu.core.theme.InfoCard
import com.nexora.kaamsetu.core.theme.ScreenTitle
import com.nexora.kaamsetu.domain.model.UserRole

@Composable
fun CustomerProfileScreen(onSwitchRole: (UserRole?) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ScreenTitle(title = "Profile")

        InfoCard {
            Text(text = "Demo Customer", style = MaterialTheme.typography.titleMedium)
            Text(text = "+91 99988 87770", style = MaterialTheme.typography.bodyMedium)
        }

        OutlinedButton(
            onClick = { onSwitchRole(null) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Switch role")
        }
    }
}

