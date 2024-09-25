package com.example.orchestrion

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.orchestrion.colorpicker.ColorPickerScreen
import com.example.orchestrion.colorpicker.ColorViewModel
import com.example.orchestrion.colorpicker.ImgColorPickerScreen
import com.example.orchestrion.piano.ui.PianoScreen
import com.example.orchestrion.theme.DefaultTheme
import kotlinx.serialization.Serializable


class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<SplashScreenViewModel>()
    private val colorViewModel by viewModels<ColorViewModel>()


    private lateinit var mqttClientManager: MqttClientManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            //Regarde la valeur de isready a chaque fois qu'une frame change sur l'ecran
            //Animation IN
            setKeepOnScreenCondition {
                !viewModel.isready.value
            }
            //Animation OUT
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_X,
                    0.4f,
                    0.0f
                )
                zoomX.interpolator = OvershootInterpolator()
                zoomX.duration = 100L
                zoomX.doOnEnd { screen.remove() }

                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView,
                    View.SCALE_Y,
                    0.4f,
                    0.0f
                )
                zoomY.interpolator = OvershootInterpolator()
                zoomY.duration = 100L
                zoomY.doOnEnd { screen.remove() }

                zoomX.start()
                zoomY.start()
            }
        }


        setContent {


            DefaultTheme {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )


                val mqttServerUri = "tcp://10.42.0.1:1883"
                mqttClientManager = MqttClientManager(mqttServerUri)

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = MainSelection
                )
                {
                    composable<MainSelection> {
                        MainSelectionScreen(
                            navController = navController,
                            viewmodel = colorViewModel,
                            myMQTT = mqttClientManager
                        )
                    }
                    composable<Colorpicker> {
                        ColorPickerScreen(
                            navController = navController,
                            viewModel = colorViewModel,
                            myMQTT = mqttClientManager
                        )
                    }
                    composable<ImgColorPicker> {
                        ImgColorPickerScreen(
                            myMQTT = mqttClientManager,
                            viewModel = colorViewModel,
                            navController = navController
                        )
                    }
                    composable<ZoneSelection> {
                        ZoneSelectionScreen(
                            navController = navController,
                            viewmodel = colorViewModel,
                            myMQTT = mqttClientManager
                        )
                    }
                    composable<Piano> {
                        PianoScreen(
                        )
                    }
                }
            }
        }
    }
}

@Serializable
object MainSelection

@Serializable
object ZoneSelection

@Serializable
object Colorpicker

@Serializable
object ImgColorPicker

@Serializable
object Piano





