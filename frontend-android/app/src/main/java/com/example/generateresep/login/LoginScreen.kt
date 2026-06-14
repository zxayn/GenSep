package com.example.generateresep.login

import androidx.compose.foundation.Image
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

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginClick: () -> Unit,
    onCreateAccountClick: () -> Unit
) {
    val lightGreen = Color(0xFFE8F5E9)
    val buttonLightGreen = Color(0xFFE8F5E9)
    val buttonDarkGreen = Color(0xFF6B9C00)

    LaunchedEffect(viewModel.isAuthSuccess) {
        if (viewModel.isAuthSuccess) {
            onLoginClick()
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

        Spacer(modifier = Modifier.height(24.dp))

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

        // Buttons
        Button(
            onClick = {
                viewModel.login()
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonLightGreen),
            shape = RoundedCornerShape(8.dp),
            enabled = !viewModel.isLoading
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF6B9C00))
            } else {
                Text(text = "Login", color = Color(0xFF6B9C00), fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onCreateAccountClick,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = buttonDarkGreen),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Buat Account", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
