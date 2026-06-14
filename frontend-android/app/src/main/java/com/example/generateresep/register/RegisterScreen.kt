package com.example.generateresep.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.generateresep.R

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.generateresep.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = viewModel(),
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val lightGreen = Color(0xFFE8F5E9)
    val buttonDarkGreen = Color(0xFF6B9C00)

    LaunchedEffect(viewModel.isAuthSuccess) {
        if (viewModel.isAuthSuccess) {
            onRegisterClick()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_no_bg),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Username Field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Username",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = viewModel.username,
                onValueChange = { viewModel.username = it },
                placeholder = { Text("Username", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightGreen,
                    unfocusedContainerColor = lightGreen,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Email",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                placeholder = { Text("example82@gmail.com", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightGreen,
                    unfocusedContainerColor = lightGreen,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Password",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                placeholder = { Text("Password", color = Color.Gray) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = lightGreen,
                    unfocusedContainerColor = lightGreen,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Error Message
        viewModel.errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
        }

        // Button
        Button(
            onClick = {
                viewModel.register()
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonDarkGreen),
            shape = RoundedCornerShape(8.dp),
            enabled = !viewModel.isLoading
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text(text = "Buat Account", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Kembali
        Text(
            text = "Kembali",
            color = Color(0xFF6B9C00),
            fontSize = 14.sp,
            modifier = Modifier.clickable { onBackClick() }
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
