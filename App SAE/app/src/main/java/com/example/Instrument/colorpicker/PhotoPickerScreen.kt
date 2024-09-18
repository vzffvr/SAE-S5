package com.example.Instrument.colorpicker

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.Instrument.MqttClientManager
import com.example.Instrument.R
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.ImageColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import androidx.compose.foundation.layout.Row
import com.example.Instrument.ZoneChoiceScreen


@Preview
@Composable
fun ImageColorPickerScreen() {
    val mqttServerUri = "tcp://10.42.0.1:1883"
    val myMQTT: MqttClientManager = MqttClientManager(mqttServerUri)

    val viewModel: ColorViewModel = viewModel()

    ImgColorPicker(myMQTT, viewModel, null)
}

@Composable
fun ImgColorPicker(myMQTT: MqttClientManager, viewModel: ColorViewModel, navController: NavController?)
{

    BackHandler()
    {
        navController?.navigate(ZoneChoiceScreen)
    }

    val phare: Int by remember {
        mutableIntStateOf(viewModel.getPhare())
    }
    val zone: Int by remember {
        mutableIntStateOf(viewModel.getZone())
    }
    var red10:Int by remember {
        mutableIntStateOf(viewModel.red10)
    }
    var green10:Int by remember {
        mutableIntStateOf(viewModel.green10)
    }
    var blue10:Int by remember {
        mutableIntStateOf(viewModel.blue10)
    }
    var alpha10:Int by remember {
        mutableIntStateOf(viewModel.alpha10)
    }

    var hexCode by remember { mutableStateOf(viewModel.hexCode) }
    var textColor by remember { mutableStateOf(viewModel.textColor) }

    val controller = rememberColorPickerController()
    controller.setDebounceDuration(200L)

    //Background
    Box (modifier = Modifier
        .fillMaxWidth()
        .background(Color.Black)
//        .paint(
//        painterResource(id = R.drawable.background),
//        contentScale = ContentScale.FillHeight)
    ) {

        Column (
            verticalArrangement = Arrangement.Bottom
        ){
            Spacer(modifier = Modifier.height(30.dp))
            ImageColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(10.dp),
                controller = controller,
                paletteImageBitmap = ImageBitmap.imageResource(R.drawable.palettebar),
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    hexCode = colorEnvelope.hexCode
                    textColor = colorEnvelope.color

                    red10 = (colorEnvelope.color.red * 255).toInt()
                    green10 = (colorEnvelope.color.green * 255).toInt()
                    blue10 = (colorEnvelope.color.blue * 255).toInt()
                    alpha10 = (colorEnvelope.color.alpha * 255).toInt()

                    viewModel.setTC(textColor)
                    viewModel.majTextsColors()//nom a changer

                    myMQTT.publish("phare","$phare")
                    myMQTT.publish("zone","$zone")
                    myMQTT.publish("red","$red10")
                    myMQTT.publish("blue","$blue10")
                    myMQTT.publish("green","$green10")
                    myMQTT.publish("intensite","$alpha10")
                },
            )

            PhotoPickerIcon(controller)

            Spacer(modifier = Modifier.height(110.dp))

            AlphaSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(35.dp)
                    .align(Alignment.CenterHorizontally),
                controller = controller,
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row(Modifier.fillMaxWidth())
            {
                TextPreview(
                    Red = red10,
                    Green = green10,
                    Blue = blue10,
                    Alpha = alpha10,
                    textColor = textColor
                )

                ExposedDropdownMenuSample(viewModel,myMQTT)
            }


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
                    .height(50.dp),
                controller = controller,
                tileOddColor = Color.White,
                tileEvenColor = Color.LightGray,
                tileSize = 60.dp,
            )
        }
    }

}