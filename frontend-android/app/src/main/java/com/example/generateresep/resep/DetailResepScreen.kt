package com.example.generateresep.resep

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.generateresep.viewmodel.RecipeViewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.generateresep.R
import com.example.generateresep.model.Recipe
import com.example.generateresep.ui.theme.GreenMain
import com.example.generateresep.ui.theme.LightGreenBg
import com.example.generateresep.utils.VoiceAssistantManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailResepScreen(
    recipeId: Int,
    onBackClick: () -> Unit,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isCookingMode by remember { mutableStateOf(false) }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var repeatTrigger by remember { mutableIntStateOf(0) }
    
    // Permission state
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    // Effect untuk memuat resep dari database jika ID valid
    LaunchedEffect(recipeId) {
        if (recipeId != 0 && recipeId != 999) {
            viewModel.loadRecipeById(recipeId)
        }
    }

    // Tentukan resep mana yang ditampilkan (prioritaskan hasil generate baru jika ID 999)
    val recipe = if (recipeId == 999) {
        viewModel.generatedRecipe
    } else {
        viewModel.savedRecipeDetail
    } ?: Recipe(
        id = recipeId,
        title = "Resep Sedang Dimuat",
        description = "Harap tunggu sebentar, koki kami sedang meracik resep terbaik untuk Anda...",
        imageResId = R.drawable.logo_no_bg,
        cookingTime = "...",
        difficulty = "...",
        ingredients = listOf("Sedang menyiapkan bahan..."),
        steps = listOf("Sedang menyusun langkah...")
    )

    // Penting: Pastikan lambda onCommand menggunakan state terbaru
    val onCommandLatest = rememberUpdatedState { command: String ->
        if (command == "lanjut") {
            if (currentStepIndex < recipe.steps.size - 1) {
                currentStepIndex++
            } else {
                isCookingMode = false
            }
        } else if (command == "ulang") {
            repeatTrigger++
        }
    }

    val voiceManager = remember {
        VoiceAssistantManager(context) { command ->
            onCommandLatest.value(command)
        }
    }

    // Effect to speak when step changes or repeat is triggered
    LaunchedEffect(isCookingMode, currentStepIndex, repeatTrigger) {
        if (isCookingMode) {
            val prefix = if (repeatTrigger > 0 && currentStepIndex >= 0) "Mengulang " else ""
            val stepText = "${prefix}Langkah ${currentStepIndex + 1}: ${recipe.steps[currentStepIndex]}"
            voiceManager.speak(stepText) {
                voiceManager.startListening()
            }
        } else {
            voiceManager.stopListening()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            voiceManager.destroy()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Resep", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (recipeId != 999 && recipeId != 0) {
                        IconButton(onClick = { 
                            viewModel.deleteRecipe(recipeId)
                            onBackClick()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Hapus Resep",
                                tint = Color.White
                            )
                        }
                    } else {
                        IconButton(onClick = { viewModel.saveCurrentRecipe() }) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Simpan Resep",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenMain)
            )
        },
        floatingActionButton = {
            if (!isCookingMode) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (hasAudioPermission) {
                            currentStepIndex = 0
                            isCookingMode = true
                        } else {
                            launcher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                    text = { Text("Mulai Memasak") },
                    containerColor = GreenMain,
                    contentColor = Color.White
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
            ) {
            // Gambar Resep
            item {
                Image(
                    painter = painterResource(id = recipe.imageResId),
                    contentDescription = recipe.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(LightGreenBg),
                    contentScale = ContentScale.Fit
                )
            }

            // Informasi Utama
            item {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = recipe.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenMain
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = recipe.cookingTime,
                            modifier = Modifier.padding(start = 4.dp),
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Kesulitan: ${recipe.difficulty}",
                            color = Color.Gray
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = recipe.description,
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        lineHeight = 22.sp
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
                    
                    Text(
                        text = "Bahan-Bahan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenMain
                    )
                }
            }

            // Daftar Bahan
            itemsIndexed(recipe.ingredients) { _, ingredient ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 4.dp)
                        .fillMaxWidth()
                ) {
                    Text(text = "•", color = GreenMain, modifier = Modifier.width(20.dp))
                    Text(text = ingredient, fontSize = 16.sp, color = Color.Black)
                }
            }

            // Header Langkah
            item {
                Text(
                    text = "Langkah Memasak",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GreenMain,
                    modifier = Modifier.padding(start = 24.dp, top = 32.dp, bottom = 16.dp)
                )
            }

            // Langkah-langkah
            itemsIndexed(recipe.steps) { index, step ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = LightGreenBg),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Surface(
                            modifier = Modifier.size(28.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = GreenMain
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (index + 1).toString(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = step, fontSize = 16.sp, color = Color.Black)
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        if (isCookingMode) {
            CookingModeOverlay(
                step = recipe.steps[currentStepIndex],
                stepNumber = currentStepIndex + 1,
                totalSteps = recipe.steps.size,
                onClose = {
                    isCookingMode = false
                    voiceManager.stopListening()
                }
            )
        }
    }
}
}

@Composable
fun CookingModeOverlay(
    step: String,
    stepNumber: Int,
    totalSteps: Int,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(enabled = false) {} // Konsumsi klik agar tidak tembus
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mode Memasak",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            Surface(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(30.dp),
                color = GreenMain
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$stepNumber",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = step,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Katakan \"Lanjut\" atau \"Ulang\"",
                color = LightGreenBg,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(16.dp))

            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = null,
                tint = GreenMain,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { stepNumber.toFloat() / totalSteps.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = GreenMain,
                trackColor = Color.Gray
            )

            Text(
                text = "$stepNumber dari $totalSteps",
                color = Color.LightGray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
