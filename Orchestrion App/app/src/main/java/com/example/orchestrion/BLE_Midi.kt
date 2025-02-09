package com.example.orchestrion

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

class BleManager(){
    private lateinit var context: Context

    constructor(_context: Context) : this() {
        context = _context
    }

    val requiredPermissions = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_ADVERTISE
    )

    private var bluetoothGatt: BluetoothGatt? = null
    private var midiWriteCharacteristic: BluetoothGattCharacteristic? = null
    private var colorWriteCharacteristic: BluetoothGattCharacteristic? = null
    private var genericWriteCharacteristic: BluetoothGattCharacteristic? = null
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

    fun hasPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

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
                //Log.d("BLE_SCAN", "Device found: ${device.name} - ${device.address}")
                stopScan()
                Toast.makeText(context, "Connexion réussie", Toast.LENGTH_SHORT).show()
                device.connectGatt(context, true, gattCallback)
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
                Log.e("BLE", "Déconnecté")
                bluetoothGatt?.close()
                bluetoothGatt = null
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {

                Log.d("BLE", "Services découverts")
                val serviceUUID = UUID.fromString("03B80E5A-EDE8-4B33-A751-6CE34EC4C700")
                val midiCharacteristicUUID =
                    UUID.fromString("7772E5DB-3868-4112-A1A9-F2669D106BF3")
                val colorCharacteristicUUID =
                    UUID.fromString("12345678-1234-5678-1234-56789ABCDEF0")
                val genericCharacteristicUUID =
                    UUID.fromString("12345678-5678-9012-3456-56789ABCDEF0")

                val service = gatt.getService(serviceUUID)
                midiWriteCharacteristic = service?.getCharacteristic(midiCharacteristicUUID)
                colorWriteCharacteristic = service?.getCharacteristic(colorCharacteristicUUID)
                genericWriteCharacteristic = service?.getCharacteristic(genericCharacteristicUUID)

                if (midiWriteCharacteristic != null) {
                    Log.d("BLE", "Caractéristique midi trouvée, prête à écrire")
                } else {
                    Log.e("BLE", "Caractéristique midi non trouvée")
                }

                if (colorWriteCharacteristic != null) {
                    Log.d("BLE", "Caractéristique couleur trouvée, prête à écrire")
                } else {
                    Log.e("BLE", "Caractéristique couleur non trouvée")
                }

                if (genericWriteCharacteristic != null) {
                    Log.d("BLE", "Caractéristique generic trouvée, prête à écrire")
                } else {
                    Log.e("BLE", "Caractéristique generic non trouvée")
                }
            }
        }
    }


    @SuppressLint("MissingPermission")
    fun isSymphonieConnected(): Boolean {
        return bluetoothGatt?.device?.name.equals("ESP32 BLE Instrument")
    }

    fun reconnectToESP32() {
        if (!isBluetoothEnabled()) {
            Toast.makeText(context, "Bluetooth non activé", Toast.LENGTH_SHORT).show()
            return
        }

        if (isSymphonieConnected()) {
            Toast.makeText(context, "Déjà connecté", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Tentative de Reconnexion", Toast.LENGTH_SHORT).show()
            stopScan()
            startScan()
        }
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
    fun sendMidiMessage(channel: Int, note: Int?, velocity: Int, NoteON: Boolean = true) {
        if (channel < 1 || channel > 16) {
            Log.e("BLE", "Canal MIDI invalide. Doit être entre 1 et 16.")
            return
        }
        var statusByte = (0x80 + (channel - 1)).toByte() //Channel
        //98 ->Note ON canal 7
        //99 ->Note ON canal 8
        //83 ->Note OFF canal 2
        var noteByte: Byte = note?.toByte() ?: 9999.toByte()
        val velocityByte = velocity.toByte() //Velocity

        if(NoteON)
            statusByte = (0x90 + (channel - 1)).toByte()

        val midiPacket = byteArrayOf(0x90.toByte(), statusByte, noteByte, velocityByte)
        midiWriteCharacteristic?.value = midiPacket

        bluetoothGatt?.writeCharacteristic(midiWriteCharacteristic)

        Log.d(
            "BLE",
            "Message envoyé: 0x${statusByte.toUByte().toString(16).uppercase()}, " +
                    "\t 0x${noteByte.toUByte().toString(16).uppercase()}, " +
                    "\t 0x${velocityByte.toUByte().toString(16).uppercase()}"
        )
    }

    @SuppressLint("MissingPermission")
    fun sendColorOrder(red: Int, green: Int, blue: Int, animation: Int) {

        val Red = red.toByte() //Rouge
        val Blue = green.toByte() //Vert
        val Green = blue.toByte() //Blue
        val Animation = animation.toByte() //Blue

        val colorPacket = byteArrayOf(0xFF.toByte(), Red, Green, Blue, Animation)
        colorWriteCharacteristic?.value = colorPacket

        bluetoothGatt?.writeCharacteristic(colorWriteCharacteristic)

        Log.d("BLE", "Message envoyé: $Red,\t $green, \t $blue, \t $animation")
    }

    @SuppressLint("MissingPermission")
    fun sendGenericOrder(content1: Int, content2: Int, content3: Int, content4: Int) {

        val Content1 = content1.toByte()
        val Content2 = content2.toByte()
        val Content3 = content3.toByte()
        val Content4 = content4.toByte()

        val genericPacket = byteArrayOf(0x00.toByte(), Content1, Content2, Content3, Content4)
        genericWriteCharacteristic?.value = genericPacket

        bluetoothGatt?.writeCharacteristic(genericWriteCharacteristic)

        Log.d("BLE", "Message envoyé: $Content1,\t $Content2, \t $Content3, \t $Content4")
    }
}