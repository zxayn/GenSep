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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.generateresep.R
import com.example.generateresep.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val darkGreenButton = Color(0xFF7CB342)
    val textFieldBg = Color(0xFFF1FDF4)
    val textColor = Color.Black

    // Navigasi yang Benar: Pindah ke login setelah registrasi sukses
    LaunchedEffect(viewModel.isAuthSuccess) {
        if (viewModel.isAuthSuccess) {
            onRegisterClick()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_no_bg),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Nama Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nama",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = viewModel.username,
                    onValueChange = { viewModel.username = it },
                    placeholder = { Text("Nama", color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = textFieldBg,
                        unfocusedContainerColor = textFieldBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = darkGreenButton
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Email Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    placeholder = { Text("example82@gmail.com", color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = textFieldBg,
                        unfocusedContainerColor = textFieldBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = darkGreenButton
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Password Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Password",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    placeholder = { Text("Password", color = Color.LightGray) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = textFieldBg,
                        unfocusedContainerColor = textFieldBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = darkGreenButton
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Penanganan Error: Tampilkan pesan error jika ada
            viewModel.errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp),
                    fontWeight = FontWeight.Medium
                )
            }

            // Button
            Button(
                onClick = { viewModel.register() }, // Memanggil fungsi registrasi nyata di ViewModel
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = darkGreenButton),
                shape = RoundedCornerShape(12.dp),
                enabled = !viewModel.isLoading // Disable saat loading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(text = "Buat Account", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Kembali",
                color = darkGreenButton,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .clickable { onBackClick() }
            )
        }
    }
}
