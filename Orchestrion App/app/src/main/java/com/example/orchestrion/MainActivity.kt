package com.example.orchestrion

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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

    private val viewModel by viewModels<ViewModel>()
    private val colorViewModel by viewModels<ColorViewModel>()

    private lateinit var bleManager: BleManager

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

        // Initialiser le com.example.orchestrion.BleManager avec le contexte de l'activité
        bleManager = BleManager(this)

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.all { it.value }) {
                // Toutes les permissions sont accordées
                bleManager.startScan()
            } else {
                // Afficher un message d'erreur si les permissions ne sont pas accordées
                Log.e("BLE", "Permissions refusées")
            }
        }

        // Vérifier et demander les permissions
        if (bleManager.hasPermissions()) {
            bleManager.askBluetoothActivation(this)
            bleManager.startScan()
        } else {
            // Demander les permissions
            requestPermissionLauncher.launch(bleManager.requiredPermissions)
        }

        setContent {
            DefaultTheme {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                )

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = MenuPrincipal
                )
                {
                    composable<MenuPrincipal> {
                        MainScreen(
                            viewmodel = colorViewModel,
                            navController = navController,
                            bleManager =  bleManager
                        )
                    }
                    composable<Colorpicker> {
                        ColorPicker(
                            viewModel = colorViewModel,
                            navController = navController,
                            BLeManager = bleManager
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
                            BLeManager = bleManager
                        )
                    }
                    composable<Piano> {

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
object MenuPrincipal

@Serializable
object ConfigScreen

@Serializable
object Colorpicker

@Serializable
object ImgColorPicker

@Serializable
object Piano

@Serializable
object TestFichier







