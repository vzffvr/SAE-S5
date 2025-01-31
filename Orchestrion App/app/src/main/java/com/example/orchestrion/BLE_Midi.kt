import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.UUID

class BleManager(private val context: Context) {

    val requiredPermissions = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private var bluetoothGatt: BluetoothGatt? = null
    private var writeCharacteristic: BluetoothGattCharacteristic? = null
    private var scanning = false
    private val handler = Handler()
    private val SCAN_PERIOD: Long = 10000 // 10 secondes

    private val REQUEST_ENABLE_BT = 1

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bluetoothLeScanner by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }

    // Vérifier si les permissions sont accordées
    fun hasPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Demander les permissions si elles ne sont pas accordées
    fun requestPermissions(activity: Activity) {
        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(activity, requiredPermissions, 1)
        }
    }

    @SuppressLint("MissingPermission")
    fun askBluetoothActivation(activity: Activity) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled ?: false
    }

    // Callback pour le scan BLE
    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device

            if (device.name != null && device.name == "ESP32 BLE Instrument") {
                Toast.makeText(
                    context,
                    "Device found: ${device.name}",
                    Toast.LENGTH_SHORT
                ).show()

                Log.d("BLE_SCAN", "Device found: ${device.name} - ${device.address}")
                stopScan()

                device.connectGatt(context, false, gattCallback)
            }
        }
    }

    // Callback pour la connexion GATT
    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                bluetoothGatt = gatt
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BLE", "Déconnecté")
                bluetoothGatt?.close()
                bluetoothGatt = null
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE", "Services découverts")
                val serviceUUID = UUID.fromString("03B80E5A-EDE8-4B33-A751-6CE34EC4C700")
                val characteristicUUID = UUID.fromString("7772E5DB-3868-4112-A1A9-F2669D106BF3")

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
    fun isOrchestrionConnected(): Boolean {
        return !bluetoothGatt?.device?.name.equals("ESP32 BLE Instrument")

    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        if (!scanning) {
            scanning = true
            bluetoothLeScanner?.startScan(scanCallback)
            Log.d("BLE_SCAN", "Scan started")

            handler.postDelayed({
                stopScan()
            }, SCAN_PERIOD)
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (scanning) {
            scanning = false
            handler.removeCallbacksAndMessages(null)
            bluetoothLeScanner?.stopScan(scanCallback)
            Log.d("BLE_SCAN", "Scan stopped")
        }
    }

    @SuppressLint("MissingPermission")
    fun sendMidiMessage(channel: Int, note: Int, velocity: Int) {
        if (channel < 1 || channel > 16) {
            Log.e("BLE", "Canal MIDI invalide. Doit être entre 1 et 16.")
            return
        }

        val statusByte = (0x90 + (channel - 1)).toByte()
        val noteByte = note.toByte()
        val velocityByte = velocity.toByte()

        val midiPacket = byteArrayOf(0x80.toByte(), statusByte, noteByte, velocityByte)

        writeCharacteristic?.value = midiPacket
        bluetoothGatt?.writeCharacteristic(writeCharacteristic)
        Log.d("BLE", "Message MIDI envoyé: Canal=$channel, Note=$note, Velocity=$velocity")
    }
}