package com.example.orchestrion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.orchestrion.colorpicker.ColorViewModel


@Composable
fun TextPreview(red: Int, green: Int, blue: Int, alpha: Int, textColor: Color) {

    Column {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "RED = $red",
            color = textColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "GREEN = $green",
            color = textColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "BLUE = $blue",
            color = textColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "ALPHA = $alpha",
            color = textColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SpinnerAnim(
    viewModel: ColorViewModel,
    bleManager: BleManager?,
    options: List<String>,
    titre: String?,
) {

    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            value = text,
            onValueChange = {
                bleManager?.sendColorOrder(
                    viewModel.red10,
                    viewModel.green10,
                    viewModel.blue10,
                    viewModel.animation
                )
            },
            readOnly = true,
            singleLine = true,
            label = {
                if (titre != null) {
                    Text(titre)
                }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                disabledContainerColor = Color(255, 0, 0, 255),
                focusedBorderColor = Color.Gray
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        text = option
                        expanded = false
                        viewModel.animation = (options.indexOf(option) + 1)
                        bleManager?.sendColorOrder(
                            viewModel.red10,
                            viewModel.green10,
                            viewModel.blue10,
                            options.indexOf(option) + 1
                        )
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpinnerConfig(
    viewModel: ColorViewModel,
    bleManager: BleManager?,
    options: List<String>,
    titre: String?,
) {

    var expanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
            value = text,
            onValueChange = {
//                bleManager?.sendGenericOrder(
//                    viewModel.red10,
//                    viewModel.green10,
//                    viewModel.blue10,
//                    viewModel.animation
//                )
            },
            readOnly = true,
            singleLine = true,
            label = {
                if (titre != null) {
                    Text(titre)
                }
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                disabledContainerColor = Color(255, 0, 0, 255),
                focusedBorderColor = Color.Gray
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                    onClick = {
                        text = option
                        expanded = false
                        bleManager?.sendGenericOrder(
                            options.indexOf(option) + 1,
                            0,
                            0,
                            0
                        )
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

//
//@Composable
//fun NumberTextField(myMQTT: MqttClientManager, viewModel: ColorViewModel) {
//    var text by remember { mutableStateOf("255") }
//
//    OutlinedTextField(
//        value = text,
//        onValueChange = {
//            text = it
//            // Filter out non-numeric characters
//
//
//        },
//        label = { Text("Intensité de reference") },
//        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//
//        modifier = Modifier
//            .padding(12.dp)
//            .onKeyEvent {
//                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
//                    text = text.filter { it.isDigit() && it < 255.toChar() }
//                    viewModel.intensity_ref = text.toInt()
//                    myMQTT.publish("intensite_ref", "${viewModel.intensity_ref}")
//                    true // Consommer l'événement pour éviter d'autres actions
//                } else {
//                    false // Ne pas consommer l'événement
//                }
//            }
//    )
//}
//
//
//@Composable
//fun CounterWithButtons(myMQTT: MqttClientManager?, viewModel: ColorViewModel) {
//
//    Spacer(modifier = Modifier.height(10.dp))
//
//    var intensity_ref: Int by remember {
//        mutableIntStateOf(viewModel.intensity_ref)
//    }
//
//    Column {
//        Text(
//            text = "Intensité de reference",
//            fontSize = 8.sp,
//            color = Color.White,
//        )
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceAround,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Button(
//                onClick = {
//                    if (intensity_ref > 0) {
//                        intensity_ref--
//                        viewModel.intensity_ref = intensity_ref
//                        myMQTT?.publish("intensite_ref", "$intensity_ref")
//                    }
//                }
//            ) {
//                Text("-")
//            }
//
//            Text(
//                text = intensity_ref.toString(),
//                style = MaterialTheme.typography.headlineMedium,
//                color = Color.White,
//            )
//
//            Button(
//                onClick = {
//                    if (intensity_ref < 255) {
//                        intensity_ref++
//                        viewModel.intensity_ref = intensity_ref
//                        myMQTT?.publish("intensite_ref", "$intensity_ref")
//                    }
//                }
//            ) {
//                Text("+")
//            }
//        }
//    }
//}