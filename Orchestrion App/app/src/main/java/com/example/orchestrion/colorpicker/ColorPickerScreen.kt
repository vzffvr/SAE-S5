package com.example.orchestrion.colorpicker


import com.example.orchestrion.BleManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.orchestrion.Spinner
import com.example.orchestrion.TextPreview
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Preview(
    showBackground = true, device = "spec:width=1440px,height=3216px,dpi=440",
    showSystemUi = false, backgroundColor = 0xFF000000
)
@Composable
fun ColorPickerScreen() {

    val viewModel: ColorViewModel = viewModel()

    ColorPicker(viewModel = viewModel, navController = null, BLeManager = null)
}

@Composable
fun ColorPicker(
    BLeManager: BleManager?,
    navController: NavController?, //PhotoPicker
    viewModel: ColorViewModel
) {

//    BackHandler()
//    {
//    }

    var animation: Int by remember {
        mutableIntStateOf(viewModel.animation)
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

    val options = listOf("Disabled", "Ambiance", "ColorWipe", "WipeCenter", "WipeEdge")

    var hexCode by remember { mutableStateOf(viewModel.hexCode) }
    var textColor by remember { mutableStateOf(viewModel.textColor) }

    val controller = rememberColorPickerController()



    Box(
        modifier = Modifier
            .fillMaxSize()
        //.background(Color.Black)
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
                    .fillMaxHeight(0.47f)
                    .padding(10.dp),
                controller = controller,
                initialColor = Color(255, 255, 255),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    textColor = colorEnvelope.color

                    hexCode = colorEnvelope.hexCode

                    alpha10 = (colorEnvelope.color.alpha * 255).toInt()

                    red10 = (colorEnvelope.color.red * 255).toInt()
                    green10 = (colorEnvelope.color.green * 255).toInt()
                    blue10 = (colorEnvelope.color.blue * 255).toInt()


                    viewModel.setTC(textColor)

                    animation = viewModel.animation

                    BLeManager?.sendColorOrder(red10, green10, blue10, animation)
                }
            )

            Spacer(modifier = Modifier.fillMaxHeight(0.1f))

            BrightnessSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .fillMaxHeight(0.1f),
                controller = controller,
                borderRadius = 6.dp,
                borderSize = 5.dp,
                borderColor = Color.LightGray,
            )


            Spacer(modifier = Modifier.fillMaxHeight(0.12f))

            Row(
                Modifier.fillMaxWidth(),
            ) {
                TextPreview(red10, green10, blue10, alpha10, textColor)

                Spacer(modifier = Modifier.fillMaxWidth(0.1f))


//                Column(
//                    horizontalAlignment = Alignment.End,
//                    )
//                {


                Spinner(viewModel, BLeManager, options, "Animation")


//                    Button(
//                        modifier = Modifier
//                            .height(80.dp)
//                            .fillMaxWidth()
//                            .padding(12.dp),
//                        shape = ShapeDefaults.ExtraLarge,
//                        border = BorderStroke(2.dp, Color.White),
//                        colors = ButtonColors(
//                            Color.Transparent, Color.White,
//                            Color.Transparent, Color.White
//                        ),
//                        onClick = {
//                            myMQTT.publish("phare", "3")
//
//                            myMQTT.publish("animation", "1")
//
//                            myMQTT.publish("red", "0")
//                            myMQTT.publish("blue", "0")
//                            myMQTT.publish("green", "0")
//
//                        }
//                    ) {
//                        Text(
//                            text = "ShutDown",
//                            color = Color.White,
//                            fontSize = 20.sp
//                        )
//
//                    }
//                }


            }

            Spacer(modifier = Modifier.fillMaxHeight(0.03f))

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
                    .fillMaxHeight(0.15f),
                controller = controller,
                tileOddColor = Color.White,
                tileEvenColor = Color.LightGray,
                tileSize = 60.dp,
            )

        }
    }
}



