package com.example.generateresep.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.generateresep.R
import com.example.generateresep.ui.theme.GreenMain
import com.example.generateresep.ui.theme.LightGreenBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCard(
    onClick: () -> Unit = {}
) {
    var isLiked by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    val density = LocalDensity.current
    val ratingBadgeShape = remember(density) {
        GenericShape { size, _ ->
            val cutWidth = with(density) { 15.dp.toPx() }
            moveTo(cutWidth, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
            lineTo(cutWidth, size.height)
            lineTo(0f, size.height / 2f)
            close()
        }
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreenBg),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.makanan),
                contentDescription = "Recipe Image",
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, top = 8.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(end = 48.dp)) {
                    Text(
                        text = "Healthy Little Cravings",
                        color = GreenMain,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Bahan-Bahan Utama :",
                        color = GreenMain,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    Row {
                        Text("• ", color = GreenMain, fontSize = 14.sp)
                        Text(
                            text = "Sayuran: 2 genggam Lamb's lettuce (selada kanon) atau bayam bayi segar ...",
                            color = GreenMain,
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            maxLines = 4
                        )
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.TopEnd).padding(end = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = { isLiked = !isLiked }, modifier = Modifier.size(38.dp)) {
                        Crossfade(targetState = isLiked, label = "LikeAnimation") { liked ->
                            Icon(
                                painter = painterResource(
                                    id = if (liked) R.drawable.suka_merah else R.drawable.suka_outline
                                ),
                                contentDescription = "Suka",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    IconButton(onClick = { isSaved = !isSaved }, modifier = Modifier.size(38.dp)) {
                        Crossfade(targetState = isSaved, label = "SaveAnimation") { saved ->
                            Icon(
                                painter = painterResource(
                                    id = if (saved) R.drawable.simpan_oranye else R.drawable.simpan_outline
                                ),
                                contentDescription = "Simpan",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                Surface(
                    color = GreenMain,
                    shape = ratingBadgeShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .width(70.dp)
                        .height(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "8.9",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
