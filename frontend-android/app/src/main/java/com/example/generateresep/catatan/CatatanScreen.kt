package com.example.generateresep.catatan

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.generateresep.R
import com.example.generateresep.model.Note
import com.example.generateresep.ui.theme.*
import com.example.generateresep.viewmodel.CatatanViewModel

@Composable
fun CatatanScreen(
    viewModel: CatatanViewModel = viewModel()
) {
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

            if (viewModel.notes.isEmpty()) {
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
                    items(viewModel.notes, key = { it.id }) { note ->
                        CatatanCard(
                            note = note,
                            onExpandClick = { viewModel.toggleExpand(note) },
                            onDeleteClick = { viewModel.deleteNote(note) },
                            onEditClick = { viewModel.startEditing(note) },
                            onTitleChange = { viewModel.updateTitle(note, it) },
                            onIngredientsChange = { viewModel.updateIngredients(note, it) }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                if (viewModel.isAnyEditing) {
                    viewModel.saveNote()
                } else {
                    viewModel.addNote()
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
                imageVector = if (viewModel.isAnyEditing) Icons.Default.Check else Icons.Default.Add,
                contentDescription = if (viewModel.isAnyEditing) "Simpan Catatan" else "Tambah Catatan",
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreenBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (note.isEditing) {
                    TextField(
                        value = note.title,
                        onValueChange = onTitleChange,
                        placeholder = { Text("Judul Resep") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = GreenMain
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            color = GreenMain,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                } else {
                    Text(
                        text = note.title.ifEmpty { "Tanpa Judul" },
                        color = GreenMain,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                
                Text(
                    text = "Bahan-Bahan Utama :",
                    color = GreenMain,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                
                if (note.isEditing) {
                    note.ingredients.forEachIndexed { index, ingredient ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("• ", color = GreenMain)
                            TextField(
                                value = ingredient,
                                onValueChange = { newVal ->
                                    val newList = note.ingredients.toMutableList()
                                    newList[index] = newVal
                                    onIngredientsChange(newList)
                                },
                                placeholder = { Text("Tambah bahan...") },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            if (note.ingredients.size > 1) {
                                IconButton(
                                    onClick = {
                                        val newList = note.ingredients.toMutableList()
                                        newList.removeAt(index)
                                        onIngredientsChange(newList)
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Hapus Baris",
                                        tint = Color.Red.copy(alpha = 0.6f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    TextButton(onClick = {
                        val newList = note.ingredients.toMutableList()
                        newList.add("")
                        onIngredientsChange(newList)
                    }) {
                        Text("+ Tambah Bahan", color = GreenMain)
                    }
                } else {
                    note.ingredients.forEachIndexed { index, ingredient ->
                        if (note.isExpanded || (index < 2)) {
                            Row(modifier = Modifier.padding(vertical = 1.dp)) {
                                Text("• ", color = GreenMain, fontSize = 13.sp)
                                Text(
                                    text = ingredient,
                                    color = GreenMain,
                                    fontSize = 13.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!note.isEditing) {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.edit),
                            contentDescription = "Edit",
                            tint = GreenMain,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (note.isExpanded || note.isEditing) {
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.hapus),
                            contentDescription = "Hapus",
                            tint = GreenMain,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                if (!note.isEditing) {
                    IconButton(onClick = onExpandClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.dropdown),
                            contentDescription = if (note.isExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(if (note.isExpanded) 90f else 0f)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatatanScreenPreview() {
    GenerateResepTheme {
        CatatanScreen()
    }
}
