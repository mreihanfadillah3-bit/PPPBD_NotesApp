package com.example.notesapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesapp.ui.theme.NotesAppTheme

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    vm: NotesViewModel = viewModel()
) {
    val notes by vm.notes.collectAsState()

    // State form tambah
    var inputTitle by rememberSaveable { mutableStateOf("") }
    var inputNote by rememberSaveable { mutableStateOf("") }

    // State mode edit
    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var editingTitle by rememberSaveable { mutableStateOf("") }
    var editingNote by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Catatan Saya", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        // ===== Form ADD (title & note) =====
        OutlinedTextField(
            value = inputTitle,
            onValueChange = { inputTitle = it },
            label = { Text("Judul") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = inputNote,
            onValueChange = { inputNote = it },
            label = { Text("Isi catatan") },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                vm.addNote(inputTitle, inputNote)
                inputTitle = ""
                inputNote = ""
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text("Tambah")
        }

        Text(
            text = "Jumlah catatan: ${notes.size}",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // ===== Daftar catatan (READ) =====
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada catatan", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(notes, key = { it.id }) { note ->
                    if (editingId == note.id) {
                        // ===== Form EDIT (title & note) =====
                        NoteItemEditing(
                            title = editingTitle,
                            note = editingNote,
                            onTitleChange = { editingTitle = it },
                            onNoteChange = { editingNote = it },
                            onSave = {
                                vm.updateNote(note, editingTitle, editingNote)
                                editingId = null
                            },
                            onCancel = { editingId = null }
                        )
                    } else {
                        NoteItem(
                            note = note,
                            onEdit = {
                                editingId = note.id
                                editingTitle = note.title
                                editingNote = note.note
                            },
                            onDelete = { vm.deleteNote(note) }
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    ListItem(
        // title
        headlineContent = {
            Text(note.title, fontWeight = FontWeight.Bold)
        },
        // note (dipotong maksimal 3 baris)
        supportingContent = {
            if (note.note.isNotEmpty()) {
                Text(note.note, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        },
        trailingContent = {
            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        painterResource(R.drawable.ic_edit), "Edit",
                        tint = Color(0xFF2196F3)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        painterResource(R.drawable.ic_delete), "Hapus",
                        tint = Color(0xFFF44336)
                    )
                }
            }
        }
    )
}

@Composable
fun NoteItemEditing(
    title: String,
    note: String,
    onTitleChange: (String) -> Unit,
    onNoteChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Ubah judul") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            label = { Text("Ubah isi catatan") },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onSave) {
                Icon(
                    painterResource(R.drawable.ic_check), "Simpan",
                    tint = Color(0xFF4CAF50)
                )
            }
            IconButton(onClick = onCancel) {
                Icon(painterResource(R.drawable.ic_close), "Batal")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteItemPreview() {
    NotesAppTheme {
        NoteItem(
            note = Note(
                id = 1,
                title = "Belajar Room Database",
                note = "Entity, DAO, dan Database adalah tiga komponen utama Room."
            )
        )
    }
}