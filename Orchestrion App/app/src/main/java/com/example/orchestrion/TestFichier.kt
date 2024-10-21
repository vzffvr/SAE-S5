package com.example.orchestrion

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


@Composable
fun FileOperationsScreen(context: Context) {
    var fileContent by remember { mutableStateOf("") }
    var inputText by remember { mutableStateOf("") }
    val fileName = "example_file.txt"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Champ de saisie pour l'utilisateur
        TextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Écrire dans le fichier") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton pour écrire dans le fichier
        Button(onClick = {
            writeToFile(context, fileName, inputText)
            inputText = "" // Efface le champ après écriture
        }) {
            Text("Écrire dans le fichier")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton pour lire depuis le fichier
        Button(onClick = {
            fileContent = readFromFile(context, fileName)
        }) {
            Text("Lire le fichier")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Afficher le contenu lu du fichier
        Text(text = "Contenu du fichier : $fileContent")

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton pour supprimer le fichier
        Button(onClick = {
            deleteFile(context, fileName)
            fileContent = ""
        }) {
            Text("Supprimer le fichier")
        }
    }
}

// Fonction pour écrire dans un fichier
fun writeToFile(context: Context, fileName: String, data: String) {
    try {
        val fos: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        fos.write(data.toByteArray())
        fos.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

// Fonction pour lire à partir d'un fichier
fun readFromFile(context: Context, fileName: String): String {
    var fileContent = ""
    try {
        val fis: FileInputStream = context.openFileInput(fileName)
        val buffer = ByteArray(fis.available())
        fis.read(buffer)
        fileContent = String(buffer)
        fis.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return fileContent
}

// Fonction pour supprimer un fichier
fun deleteFile(context: Context, fileName: String) {
    context.deleteFile(fileName)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    // Ne pas inclure de context dans la Preview
}