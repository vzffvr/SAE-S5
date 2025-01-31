package com.example.orchestrion

import BleManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.orchestrion.colorpicker.ColorViewModel

@Preview
@Composable
fun ConfigScreenPreview() {
    val viewModel: ColorViewModel = viewModel()

    ConfigScreen(
        navController = null,
        viewmodel = viewModel,
        BLeManager = null
    )
}

@Composable
fun ConfigScreen(
    navController: NavController?,
    viewmodel: ColorViewModel,
    BLeManager: BleManager?
) {


//    BackHandler()
//    {
//        //navController?.navigate(MainActivity.PhareScreen)
//    }

    //Background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
//        .paint(
//        painterResource(id = R.drawable.background2),
//        contentScale = ContentScale.FillHeight)
    ) {
        val buttonModifier: Modifier =
            Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(6.dp)

        val options = listOf("Sinusoidale", "Carre", "Triangulaire")

        var checked: Boolean by remember {
            mutableStateOf(true)
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(70.dp))

            //Logo
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                //Logo
                Box(
                    modifier = Modifier
//                        .paint(
//                            painterResource(id = R.drawable.logo_polygon),
//                            contentScale = ContentScale.FillWidth
//                        ),
                )
            }

            //Boutton
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Switch(
                    modifier = Modifier.semantics { contentDescription = "Demo" },
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                    },
                    thumbContent = if (checked) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        null
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))

                Spinner(viewmodel, myMQTT, options, "Forme du signal")

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    val context = LocalContext.current
                    Button(
                        shape = ShapeDefaults.ExtraLarge,
                        border = BorderStroke(2.dp, Color.White),
                        colors = ButtonColors(
                            Color.Transparent, Color.White,
                            Color.Transparent, Color.White
                        ),
                        modifier = Modifier
                            .padding(6.dp),
                        onClick = {
                            myMQTT.reconnectToMqttBroker(context = context)
                        }) {
                        Text(
                            text = "Reconnect",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                }
            }

        }
        Spacer(modifier = Modifier.height(70.dp))
    }
}


