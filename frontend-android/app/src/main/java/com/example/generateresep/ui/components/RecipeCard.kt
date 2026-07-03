package com.example.generateresep.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.generateresep.R
import com.example.generateresep.data.RecipeEntity
import com.example.generateresep.ui.theme.GreenMain
import com.example.generateresep.ui.theme.LightGreenBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCard(
    recipe: RecipeEntity? = null,
    onClick: () -> Unit = {}
) {
    var isLiked by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(true) } // Karena ini dari riwayat

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
            .height(160.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.makanan),
                contentDescription = "Recipe Image",
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(end = 40.dp)) {
                    Text(
                        text = recipe?.title ?: "Healthy Little Cravings",
                        color = GreenMain,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = recipe?.description ?: "Deskripsi resep lezat...",
                        color = Color.DarkGray,
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Tag Waktu
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = GreenMain, modifier = Modifier.size(14.dp))
                            Text(text = recipe?.cookingTime ?: "15 Min", color = GreenMain, fontSize = 12.sp, modifier = Modifier.padding(start = 2.dp))
                        }
                        // Tag Kesulitan
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = GreenMain, modifier = Modifier.size(14.dp))
                            Text(text = recipe?.difficulty ?: "Mudah", color = GreenMain, fontSize = 12.sp, modifier = Modifier.padding(start = 2.dp))
                        }
                    }
                }

                IconButton(
                    onClick = { isLiked = !isLiked },
                    modifier = Modifier.align(Alignment.TopEnd).size(32.dp)
                ) {
                    Crossfade(targetState = isLiked, label = "LikeAnimation") { liked ->
                        Icon(
                            painter = painterResource(
                                id = if (liked) R.drawable.suka_merah else R.drawable.suka_outline
                            ),
                            contentDescription = "Suka",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Surface(
                    color = GreenMain,
                    shape = ratingBadgeShape,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .width(60.dp)
                        .height(22.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "4.5",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
