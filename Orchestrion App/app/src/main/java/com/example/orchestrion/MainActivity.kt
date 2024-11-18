package com.example.orchestrion

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.orchestrion.colorpicker.ColorPicker
import com.example.orchestrion.colorpicker.ColorViewModel
import com.example.orchestrion.theme.DefaultTheme
import kotlinx.serialization.Serializable


class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<SplashScreenViewModel>()
    private val colorViewModel by viewModels<ColorViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {


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

        super.onCreate(savedInstanceState)



        setContent {
            DefaultTheme {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )

                val navController = rememberNavController()

                val context = LocalContext.current
                val mqttServerUri = "tcp://10.42.0.1:1883"
                val mqttClientManager: MqttClientManager = MqttClientManager(mqttServerUri, context)

                NavHost(
                    navController = navController,
                    startDestination = PhareScreen
                )
                {
                    composable<PhareScreen> {
                        MainScreen(
                            viewmodel = colorViewModel,
                            navController = navController,
                            myMQTT = mqttClientManager
                        )
                    }
                    composable<Colorpicker> {
                        ColorPicker(
                            viewModel = colorViewModel,
                            navController = navController,
                            myMQTT = mqttClientManager
                        )
                    }
//                    composable<ImgColorPicker> {
//                        ImgColorPicker(
//                            viewModel = colorViewModel,
//                            navController = navController,
//                            myMQTT = mqttClientManager,
//                        )
//                    }
                    composable<ConfigScreen> {
                        ConfigScreen(
                            viewmodel = colorViewModel,
                            navController = navController,
                            myMQTT = mqttClientManager
                        )
                    }

                    composable<TestFichier> {
                        FileOperationsScreen(LocalContext.current)
                    }
                }
            }
        }
    }
}

@Serializable
object PhareScreen

@Serializable
object ConfigScreen

@Serializable
object Colorpicker

@Serializable
object ImgColorPicker

@Serializable
object TestFichier






