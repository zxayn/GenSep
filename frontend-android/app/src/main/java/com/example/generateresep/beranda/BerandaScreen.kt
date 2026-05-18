package com.example.generateresep.beranda

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.generateresep.R
import com.example.generateresep.ui.components.RecipeCard
import com.example.generateresep.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BerandaScreen() {
    val listState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf("") }
    
    // Simulasi data untuk dropdown search
    val searchResults = remember(searchQuery) {
        if (searchQuery.isEmpty()) emptyList()
        else listOf(
            "Healthy Salad",
            "Fruit Bowl",
            "Feta Cheese Pasta",
            "Green Smoothies"
        ).filter { it.contains(searchQuery, ignoreCase = true) }
    }
    
    // State Dock Kamera
    var isDockExpanded by remember { mutableStateOf(false) }
    val dockHeight by animateDpAsState(
        targetValue = if (isDockExpanded) 12.dp else 20.dp,
        label = "DockHeightAnimation"
    )

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
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
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
                        visible = searchQuery.isNotEmpty(),
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
                                if (searchResults.isEmpty()) {
                                    Text(
                                        text = "Cari resep apa?",
                                        color = Color.Gray,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                } else {
                                    searchResults.forEach { result ->
                                        TextButton(
                                            onClick = { searchQuery = result },
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
                                        if (result != searchResults.last()) {
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
                items(5) {
                    RecipeCard()
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
                    visible = isDockExpanded,
                    enter = scaleIn(animationSpec = spring(dampingRatio = 0.5f)) + 
                            slideInVertically(initialOffsetY = { it }),
                    exit = scaleOut() + slideOutVertically(targetOffsetY = { it })
                ) {
                    FloatingActionButton(
                        onClick = { isDockExpanded = false },
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
                    onClick = { isDockExpanded = !isDockExpanded },
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
    }
}

@Preview(showBackground = true)
@Composable
fun BerandaScreenPreview() {
    GenerateResepTheme {
        BerandaScreen()
    }
}
