package com.example.orchestrion

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.core.animation.doOnEnd
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.orchestrion.colorpicker.ColorPicker
import com.example.orchestrion.colorpicker.ColorViewModel
import com.example.orchestrion.theme.DefaultTheme
import kotlinx.serialization.Serializable
import java.util.UUID


class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<SplashScreenViewModel>()
    private val colorViewModel by viewModels<ColorViewModel>()

    private val REQUEST_ENABLE_BT = 1
    private val SCAN_PERIOD: Long = 10000


    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private var scanning = false
    private lateinit var handler: Handler
    var bluetoothGatt: BluetoothGatt? = null

    private var writeCharacteristic: BluetoothGattCharacteristic? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        scanning = false

        handler = Handler()

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

        val permissionsNeeded = listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )

        var allPermissionsGranted = true
        permissionsNeeded.forEach { permission ->
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                allPermissionsGranted = false
                return@forEach // Exit the loop early if a permission is missing
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                REQUEST_ENABLE_BT
            )
        } else {
            // All permissions granted, proceed with your operations
        }

        if (!bluetoothAdapter.isEnabled) { // Si le bluetooth n'est pas activé, demande l'activation
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

            if (ActivityCompat.checkSelfPermission( // Regarde les permissions
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) { // Si n'a pas la permission demande la permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_CONNECT
                    ),
                    REQUEST_ENABLE_BT
                )
            } else { //Si permission, demande l'activation du bluetooth
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }


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
                        startScan()
//                        scanLeDevice()
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

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device

            if (result.device.name != null && result.device.name.equals("ESP32 BLE Instrument")) {
                Toast.makeText(
                    this@MainActivity,
                    "Device found: ${result.device.name}",
                    Toast.LENGTH_SHORT
                ).show()

                Log.d("BLE_SCAN", "Device found: ${device.name} - ${device.address}")
//                bluetoothGatt = device.connectGatt(this, false, gattCallback)
                stopScan()

                device.connectGatt(this@MainActivity, true, gattCallback)
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        // Callbacks pour gérer la connexion GATT
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bluetoothGatt = gatt // Stocker l'objet GATT pour les futures opérations
                gatt.discoverServices() // Découvrir les services BLE après connexion
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BLE", "Déconnecté")
                bluetoothGatt?.close() // Libérer les ressources
                bluetoothGatt = null
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE", "Services découverts")

                // UUID à modifier selon ton ESP32
                val serviceUUID = UUID.fromString("12345678-1234-5678-1234-56789abcdef0")
                val characteristicUUID = UUID.fromString("abcdef01-1234-5678-1234-56789abcdef0")

                val service = gatt.getService(serviceUUID)
                writeCharacteristic = service?.getCharacteristic(characteristicUUID)

                if (writeCharacteristic != null) {
                    Log.d("BLE", "Caractéristique trouvée, prête à écrire")
                } else {
                    Log.e("BLE", "Caractéristique non trouvée")
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (scanning) {
            scanning = false
            handler.removeCallbacksAndMessages(null)
            bluetoothLeScanner.stopScan(scanCallback)
            Log.d("BLE_SCAN", "Scan stopped")
        }
    }

    @SuppressLint("MissingPermission")
    private fun startScan() {
        val scanFilters = listOf(ScanFilter.Builder().build())
        val scanSettings =
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

        if (!scanning) {
            scanning = true
            bluetoothLeScanner.startScan(scanCallback)
            Log.d("BLE_SCAN", "Scan started")

            // Arrêter le scan après SCAN_PERIOD millisecondes
            handler.postDelayed({
                scanning = false
                bluetoothLeScanner.stopScan(scanCallback)
                Log.d("BLE_SCAN", "Scan stopped after timeout")
            }, SCAN_PERIOD)
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







