package com.example.generateresep.catatan

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.generateresep.R
import com.example.generateresep.ui.theme.*

data class Note(
    val id: Int,
    var title: String,
    var ingredients: List<String>,
    var isExpanded: Boolean = false,
    var isEditing: Boolean = false
)

@Composable
fun CatatanScreen() {
    val notes = remember {
        mutableStateListOf(
            Note(
                id = 1,
                title = "Healthy Little Cravings",
                ingredients = listOf(
                    "Sayuran: 2 genggam Lamb's lettuce atau bayam bayi segar.",
                    "Buah: 1 buah Apel, iris melintang.",
                    "Protein: 50g Keju Feta, hancurkan kasar.",
                    "Taburan: Biji bunga matahari atau biji labu.",
                    "Dressing Rekomendasi: Campuran Balsamic vinaigrette atau madu dan lemon."
                ),
                isExpanded = true
            )
        )
    }

    val editingNote = notes.find { it.isEditing }
    val isAnyEditing = editingNote != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Catatan",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = GreenMain
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Catatan Kosong",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(notes, key = { it.id }) { note ->
                        CatatanCard(
                            note = note,
                            onExpandClick = {
                                val index = notes.indexOf(note)
                                if (index != -1) {
                                    notes[index] = note.copy(isExpanded = !note.isExpanded)
                                }
                            },
                            onDeleteClick = {
                                notes.remove(note)
                            },
                            onEditClick = {
                                val index = notes.indexOf(note)
                                if (index != -1) {
                                    notes[index] = note.copy(isEditing = true, isExpanded = true)
                                }
                            },
                            onTitleChange = { newTitle ->
                                val index = notes.indexOf(note)
                                if (index != -1) {
                                    notes[index] = note.copy(title = newTitle)
                                }
                            },
                            onIngredientsChange = { newIngredients ->
                                val index = notes.indexOf(note)
                                if (index != -1) {
                                    notes[index] = note.copy(ingredients = newIngredients)
                                }
                            }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                if (isAnyEditing) {
                    val index = notes.indexOf(editingNote)
                    if (index != -1) {
                        notes[index] = editingNote.copy(isEditing = false)
                    }
                } else {
                    val newNote = Note(
                        id = (notes.maxOfOrNull { it.id } ?: 0) + 1,
                        title = "",
                        ingredients = listOf(""),
                        isExpanded = true,
                        isEditing = true
                    )
                    notes.add(0, newNote)
                }
            },
            containerColor = GreenMain,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 8.dp)
                .size(64.dp),
            elevation = FloatingActionButtonDefaults.elevation(4.dp)
        ) {
            Icon(
                imageVector = if (isAnyEditing) Icons.Default.Check else Icons.Default.Add,
                contentDescription = if (isAnyEditing) "Simpan Catatan" else "Tambah Catatan",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatatanCard(
    note: Note,
    onExpandClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onIngredientsChange: (List<String>) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightGreenBg.copy(alpha = 0.9f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(durationMillis = 300)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (note.isEditing) {
                    NoteTitleEditor(
                        title = note.title,
                        onTitleChange = onTitleChange
                    )
                } else {
                    Text(
                        text = note.title.ifEmpty { "Tanpa Judul" },
                        color = GreenMain,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp
                    )
                }
                
                Text(
                    text = "Bahan-Bahan Utama :",
                    color = GreenMain.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
                
                if (note.isEditing) {
                    NoteIngredientsEditor(
                        ingredients = note.ingredients,
                        onIngredientsChange = onIngredientsChange
                    )
                } else {
                    NoteIngredientsDisplay(
                        ingredients = note.ingredients,
                        isExpanded = note.isExpanded
                    )
                }
            }

            NoteActionButtons(
                note = note,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onExpandClick = onExpandClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteTitleEditor(
    title: String,
    onTitleChange: (String) -> Unit
) {
    TextField(
        value = title,
        onValueChange = onTitleChange,
        placeholder = { Text("Judul Resep", color = GreenMain.copy(alpha = 0.4f)) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = GreenMain,
            cursorColor = GreenMain
        ),
        textStyle = LocalTextStyle.current.copy(
            color = GreenMain,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteIngredientsEditor(
    ingredients: List<String>,
    onIngredientsChange: (List<String>) -> Unit
) {
    ingredients.forEachIndexed { index, ingredient ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("• ", color = GreenMain, fontWeight = FontWeight.Bold)
            TextField(
                value = ingredient,
                onValueChange = { newVal ->
                    val newList = ingredients.toMutableList()
                    newList[index] = newVal
                    onIngredientsChange(newList)
                },
                placeholder = {
                    Text("Tambah bahan...",
                        fontSize = 14.sp,
                        color = GreenMain.copy(alpha = 0.4f)
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = GreenMain
                ),
                modifier = Modifier.weight(1f)
            )
            if (ingredients.size > 1) {
                IconButton(
                    onClick = {
                        val newList = ingredients.toMutableList()
                        newList.removeAt(index)
                        onIngredientsChange(newList)
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Hapus Baris",
                        tint = Color.Red.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
    TextButton(
        onClick = {
            val newList = ingredients.toMutableList()
            newList.add("")
            onIngredientsChange(newList)
        },
        contentPadding = PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = GreenMain
        )
        Spacer(Modifier.width(4.dp))
        Text("Tambah Bahan", color = GreenMain, fontSize = 14.sp)
    }
}

@Composable
private fun NoteIngredientsDisplay(
    ingredients: List<String>,
    isExpanded: Boolean
) {
    Column {
        ingredients.forEachIndexed { index, ingredient ->
            AnimatedVisibility(
                visible = isExpanded || index < 2,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                    Text("• ", color = GreenMain, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = ingredient,
                        color = GreenMain,
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteActionButtons(
    note: Note,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onExpandClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        if (!note.isEditing) {
            ActionIconButton(
                iconRes = R.drawable.edit,
                contentDescription = "Edit",
                onClick = onEditClick
            )
        }

        AnimatedVisibility(visible = note.isExpanded || note.isEditing) {
            ActionIconButton(
                iconRes = R.drawable.hapus,
                contentDescription = "Hapus",
                onClick = onDeleteClick,
                tint = if (note.isEditing) Color.Red.copy(alpha = 0.6f) else GreenMain
            )
        }

        if (!note.isEditing) {
            IconButton(
                onClick = onExpandClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(GreenMain.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.dropdown),
                    contentDescription = if (note.isExpanded) "Collapse" else "Expand",
                    tint = GreenMain,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (note.isExpanded) 180f else 0f) // Ubah rotasi jadi 180 agar lebih standar
                )
            }
        }
    }
}

@Composable
private fun ActionIconButton(
    iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = GreenMain
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .background(tint.copy(alpha = 0.1f), CircleShape)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CatatanScreenPreview() {
    GenerateResepTheme {
        CatatanScreen()
    }
}
