package com.example.generateresep.beranda

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.generateresep.R
import com.example.generateresep.ui.components.RecipeCard
import com.example.generateresep.ui.components.CameraPreview
import com.example.generateresep.ui.theme.*
import com.example.generateresep.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BerandaScreen(
    viewModel: RecipeViewModel = viewModel(),
    onRecipeClick: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val savedRecipes by viewModel.savedRecipes.collectAsState(initial = emptyList())
    var isCameraOpen by remember { mutableStateOf(false) }
    
    // Gallery Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val source = ImageDecoder.createSource(context.contentResolver, it)
            val bitmap = ImageDecoder.decodeBitmap(source)
            viewModel.detectIngredients(bitmap)
            isCameraOpen = false
        }
    }

    // Camera Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isCameraOpen = true
        }
    }

    // State Dock Kamera
    val dockHeight by animateDpAsState(
        targetValue = if (viewModel.isDockExpanded) 12.dp else 20.dp,
        label = "DockHeightAnimation"
    )

    // Navigasi ke detail resep jika berhasil generate
    LaunchedEffect(viewModel.generatedRecipe) {
        viewModel.generatedRecipe?.let { _ ->
            onRecipeClick(999)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // Search Bar Section
            Box(modifier = Modifier.zIndex(1f)) {
                Column {
                    TextField(
                        value = viewModel.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { 
                            Text("Search", color = GrayText, fontSize = 18.sp) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(30.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = LightGreenBg,
                            unfocusedContainerColor = LightGreenBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.vector),
                                contentDescription = "Search Icon",
                                tint = Color.Black,
                                modifier = Modifier.size(28.dp)
                            )
                        },
                        singleLine = true
                    )

                    // Dropdown Search Results
                    AnimatedVisibility(
                        visible = viewModel.searchQuery.isNotEmpty() && !viewModel.isGenerating,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = LightGreenBg),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                TextButton(
                                    onClick = { viewModel.generateRecipeFromText() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Generate Resep dengan AI ✨", color = GreenMain, fontWeight = FontWeight.Bold)
                                }
                                
                                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))

                                val results = viewModel.searchResults
                                if (results.isEmpty()) {
                                    Text(
                                        text = "Cari resep apa?",
                                        color = Color.Gray,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                } else {
                                    results.forEach { result ->
                                        TextButton(
                                            onClick = { viewModel.onSearchQueryChange(result) },
                                            modifier = Modifier.fillMaxWidth(),
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text(
                                                text = result,
                                                color = Color.Black,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                fontSize = 16.sp
                                            )
                                        }
                                        if (result != results.last()) {
                                            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Error Message Display
            viewModel.errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                text = "Riwayat Resep",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = GreenMain
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Resep List
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 140.dp) 
            ) {
                if (savedRecipes.isEmpty()) {
                    item {
                        Text(
                            "Belum ada resep yang disimpan.",
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    }
                } else {
                    items(savedRecipes.size) { index ->
                        val recipe = savedRecipes[index]
                        RecipeCard(onClick = { onRecipeClick(recipe.id) })
                    }
                }
            }
        }

        // Loading Overlay untuk Deteksi atau Generate
        if (viewModel.isGenerating || viewModel.isDetecting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    val loadingText = if (viewModel.isDetecting) "Sedang mengenali bahan..." else "Gemini sedang meracik resep..."
                    Text(loadingText, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- CAMERA DOCK SECTION ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedVisibility(
                    visible = viewModel.isDockExpanded,
                    enter = scaleIn(animationSpec = spring(dampingRatio = 0.5f)) + 
                            slideInVertically(initialOffsetY = { it }),
                    exit = scaleOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    FloatingActionButton(
                        onClick = { viewModel.onCameraClick() },
                        containerColor = GreenMain,
                        shape = CircleShape,
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .size(72.dp),
                        elevation = FloatingActionButtonDefaults.elevation(8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.kamera),
                            contentDescription = "Camera",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Surface(
                    onClick = { viewModel.toggleDock() },
                    color = GreenMain,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                    modifier = Modifier
                        .width(60.dp)
                        .height(dockHeight)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height(3.dp)
                                .background(Color.White.copy(alpha = 0.5f), CircleShape)
                        )
                    }
                }
            }
        }

        // Detection Results Dialog
        if (viewModel.detectedIngredients.isNotEmpty()) {
            AlertDialog(
                onDismissRequest = { viewModel.onIngredientsDetected(emptyList()) },
                title = { Text("Bahan Terdeteksi", color = GreenMain) },
                text = {
                    Column {
                        Text("Kami menemukan bahan-bahan berikut:")
                        Spacer(modifier = Modifier.height(8.dp))
                        viewModel.detectedIngredients.forEach { ingredient ->
                            Text("• $ingredient", fontWeight = FontWeight.Medium)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { 
                            viewModel.generateByIngredients()
                            viewModel.onIngredientsDetected(emptyList())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenMain)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate AI")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { 
                            viewModel.searchByIngredients()
                            viewModel.onIngredientsDetected(emptyList())
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GreenMain)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cari Resep")
                    }
                }
            )
        }

        // Handle camera click from ViewModel
        LaunchedEffect(viewModel.isDetectionActive) {
            if (viewModel.isDetectionActive) {
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
                viewModel.isDetectionActive = false
            }
        }

        // Camera Preview Overlay
        if (isCameraOpen) {
            Box(modifier = Modifier.fillMaxSize().zIndex(10f)) {
                CameraPreview(
                    onImageCaptured = { bitmap ->
                        viewModel.detectIngredients(bitmap)
                        isCameraOpen = false
                    },
                    onError = {
                        isCameraOpen = false
                    }
                )
                
                // Close Button
                IconButton(
                    onClick = { isCameraOpen = false },
                    modifier = Modifier.padding(top = 40.dp, start = 16.dp).align(Alignment.TopStart)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                }

                // Gallery Button
                IconButton(
                    onClick = { 
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier.padding(top = 40.dp, end = 16.dp).align(Alignment.TopEnd)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.vector), // Ganti dengan ikon galeri jika ada
                        contentDescription = "Buka Galeri",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
