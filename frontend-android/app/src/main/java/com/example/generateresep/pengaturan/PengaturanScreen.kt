package com.example.generateresep.pengaturan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.generateresep.ui.theme.*
import com.example.generateresep.viewmodel.AuthViewModel
import com.example.generateresep.viewmodel.SettingsViewModel

@Composable
fun PengaturanScreen(
    viewModel: SettingsViewModel = viewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    onLogoutClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Pengaturan",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = GreenMain
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Pengaturan",
            fontSize = 18.sp,
            color = GreenMain,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Item Notifikasi
        SettingToggleItem(
            title = "Notifikasi",
            isChecked = viewModel.isNotificationEnabled,
            onCheckedChange = { viewModel.toggleNotification(it) }
        )

        // Item Tema
        SettingToggleItem(
            title = "Tema",
            isChecked = viewModel.isDarkTheme,
            onCheckedChange = { _ -> viewModel.toggleTheme() }
        )

        // Item Versi Aplikasi
        SettingClickableItem(
            title = "Versi Aplikasi",
            subtitle = "V1.0.1",
            onClick = { /* Aksi cek update */ }
        )

        // Item Tentang
        SettingClickableItem(
            title = "Tentang",
            onClick = { /* Aksi tentang aplikasi */ }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Tombol Logout
        Button(
            onClick = { 
                authViewModel.logout()
                onLogoutClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Keluar Akun", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

@Composable
fun SettingToggleItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = GreenMain,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun SettingClickableItem(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = GreenMain,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PengaturanScreenPreview() {
    PengaturanScreen()
}
