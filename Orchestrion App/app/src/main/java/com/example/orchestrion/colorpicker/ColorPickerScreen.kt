package com.example.orchestrion.colorpicker


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.orchestrion.MqttClientManager
import com.example.orchestrion.Spinner
import com.example.orchestrion.TextPreview
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Preview
@Composable
fun ColorPickerScreen() {
    val mqttServerUri = "tcp://10.42.0.1:1883"
    val myMQTT: MqttClientManager = MqttClientManager(mqttServerUri, LocalContext.current)

    val viewModel: ColorViewModel = viewModel()

    ColorPicker(myMQTT = myMQTT, viewModel = viewModel, navController = null)
}

@Composable
fun ColorPicker(
    myMQTT: MqttClientManager,
    navController: NavController?,
    viewModel: ColorViewModel
) {
    
//    BackHandler()
//    {
//        if (viewModel.getPhare() == 3) {
//            navController?.navigate(PhareScreen)
//        } else {
//            navController?.navigate(PhareScreen)
//        }
//    }

    val phare: Int by remember {
        mutableIntStateOf(viewModel.getPhare())
    }
    val zone: Int by remember {
        mutableIntStateOf(viewModel.getZone())
    }
    var animation: Int by remember {
        mutableIntStateOf(viewModel.getAnimation())
    }
    var red10: Int by remember {
        mutableIntStateOf(viewModel.red10)
    }
    var green10: Int by remember {
        mutableIntStateOf(viewModel.green10)
    }
    var blue10: Int by remember {
        mutableIntStateOf(viewModel.blue10)
    }
    var alpha10: Int by remember {
        mutableIntStateOf(viewModel.alpha10)
    }

    var intensity_ref: Int by remember {
        mutableIntStateOf(viewModel.intensity_ref)
    }

    val options = listOf("Ambiance", "Griffes", "clignotant")

    var hexCode by remember { mutableStateOf(viewModel.hexCode) }
    var textColor by remember { mutableStateOf(viewModel.textColor) }

    val controller = rememberColorPickerController()


    LaunchedEffect(Unit) { // Le paramètre "Unit" garantit que l'effet est exécuté une seule fois à la création
        myMQTT.publish("phare", "$phare")
        myMQTT.publish("zone", "$zone")

        myMQTT.publish("animation", "$animation")

        myMQTT.publish("red", "$red10")
        myMQTT.publish("blue", "$blue10")
        myMQTT.publish("green", "$green10")
        myMQTT.publish("intensite", "$alpha10")

        myMQTT.publish("intensite_ref", "${viewModel.intensity_ref}")
    }

    // Envoyer des données à la reprise du composable
    LaunchedEffect(key1 = Unit) { // Le paramètre "key1" permet de redéclencher l'effet si nécessaire
        myMQTT.publish("phare", "$phare")
        myMQTT.publish("zone", "$zone")

        myMQTT.publish("animation", "$animation")

        myMQTT.publish("red", "$red10")
        myMQTT.publish("blue", "$blue10")
        myMQTT.publish("green", "$green10")
        myMQTT.publish("intensite", "$alpha10")

        myMQTT.publish("intensite_ref", "${viewModel.intensity_ref}")
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
//        .paint(
//        painterResource(id = R.drawable.background),
//        contentScale = ContentScale.FillHeight)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .padding(10.dp),
                controller = controller,
                initialColor = Color(255, 255, 255),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    textColor = colorEnvelope.color

                    hexCode = colorEnvelope.hexCode

                    red10 = (colorEnvelope.color.red * 255).toInt()
                    green10 = (colorEnvelope.color.green * 255).toInt()
                    blue10 = (colorEnvelope.color.blue * 255).toInt()
                    alpha10 = (colorEnvelope.color.alpha * 255).toInt()

                    viewModel.setTC(textColor)
                    viewModel.majTextsColors()//nom a changer

                    animation = viewModel.getAnimation()

                    myMQTT.publish("phare", "${viewModel.getPhare()}")
                    myMQTT.publish("zone", "$zone")

                    myMQTT.publish("animation", "$animation")

                    myMQTT.publish("red", "$red10")
                    myMQTT.publish("blue", "$blue10")
                    myMQTT.publish("green", "$green10")
                    myMQTT.publish("intensite", "$alpha10")

                    myMQTT.publish("intensite_ref", "${viewModel.intensity_ref}")
                }
            )

            AlphaSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(35.dp),
                controller = controller,
                borderRadius = 6.dp,
                borderSize = 5.dp,
                borderColor = Color.LightGray,
            )


            Spacer(modifier = Modifier.height(50.dp))


            Row(
                Modifier.fillMaxWidth(),
            ) {
                TextPreview(red10, green10, blue10, alpha10, textColor)

                Spacer(modifier = Modifier.width(50.dp))

                Column(
                    horizontalAlignment = Alignment.End,

                    )
                {
                    Spinner(viewModel, myMQTT, options, "Animation")
                    //CounterWithButtons(myMQTT,viewModel)

                    Button(
                        modifier = Modifier
                            .height(80.dp)
                            .fillMaxWidth()
                            .padding(12.dp),
                        shape = ShapeDefaults.ExtraLarge,
                        border = BorderStroke(2.dp, Color.White),
                        colors = ButtonColors(
                            Color.Transparent, Color.White,
                            Color.Transparent, Color.White
                        ),
                        onClick = {
                            myMQTT.publish("phare", "3")

                            myMQTT.publish("animation", "1")

                            myMQTT.publish("red", "0")
                            myMQTT.publish("blue", "0")
                            myMQTT.publish("green", "0")

                        }
                    ) {
                        Text(
                            text = "ShutDown",
                            color = Color.White,
                            fontSize = 20.sp
                        )

                    }
                }


            }

            Spacer(modifier = Modifier.height(25.dp))

            Text(
                text = "#$hexCode",
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            AlphaTile(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                controller = controller,
                tileOddColor = Color.White,
                tileEvenColor = Color.LightGray,
                tileSize = 60.dp,
            )

        }
    }
}



