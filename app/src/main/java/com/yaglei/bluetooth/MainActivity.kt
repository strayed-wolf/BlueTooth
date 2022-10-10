package com.yaglei.bluetooth

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.yaglei.bluetooth.adapter.BondAdapter
import com.yaglei.bluetooth.adapter.UnBondAdapter
import com.yaglei.bluetooth.databinding.ActivityMainBinding
import org.json.JSONObject


class MainActivity : AppCompatActivity(), BluetoothUtils.BluetoothInterface,
    BondAdapter.BonAdapterListener, BluetoothChatService.ConnectBluetoothListener {
    private val TAG = MainActivity::class.java.name
    private lateinit var binding: ActivityMainBinding
    private var bondAdapter: BondAdapter? = null
    private var unBondAdapter: UnBondAdapter? = null
    private var bluetoothChatService: BluetoothChatService? = null
    private var connectState = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // 状态栏黑色
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        bondAdapter = BondAdapter(this, this)
        unBondAdapter = UnBondAdapter(this)
        binding.list1.adapter = bondAdapter
        binding.list1.layoutManager = LinearLayoutManager(this)
        binding.list2.adapter = unBondAdapter
        binding.list2.layoutManager = LinearLayoutManager(this)
        BluetoothUtils.instance.setBluetoothListener(this)
        BluetoothUtils.instance.initBluetooth(this)
        if (!BluetoothUtils.instance.isEnabled) { // 没有没有打开蓝牙，请求打开蓝牙
            BluetoothUtils.instance.enable()
        }
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothChatService = BluetoothChatService(bluetoothManager.adapter)
        requestPermission()
        initListener()

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            1
        )
    }

    // 打开蓝牙
    private fun openBlueTooth() {

        if (!BluetoothUtils.instance.isEnabled) { // 没有没有打开蓝牙，请求打开蓝牙
            Log.e(TAG, "openBlueTooth:尝试打开蓝牙 ")
            BluetoothUtils.instance.enable()
            Log.e(TAG, "openBlueTooth:打开完毕，尝试搜索 ")
        }
        if (!BluetoothUtils.instance.isDiscovering) {
            Log.e(TAG, "openBlueTooth:开始搜索 ")
            BluetoothUtils.instance.startDiscovery() // 开始搜索蓝牙
        }
    }

    // 初始化点击事件
    private fun initListener() {
        binding.bluetoothName.setOnClickListener {
            if (connectState != 0) {
                bluetoothChatService?.stop()
                return@setOnClickListener
            }
            openBlueTooth()
            if (binding.bluetoothList.visibility == View.GONE) {
                binding.bluetoothList.visibility = View.VISIBLE
            } else {
                binding.bluetoothList.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        BluetoothUtils.instance.onDestroy()
    }

    // 获取到蓝牙列表
    override fun getBluetoothList(deviceBeans: ArrayList<BluetoothDevice>) {
        Log.e(TAG, "getBluetoothList: 搜索完毕，${deviceBeans}")
        unBondAdapter?.update(deviceBeans)
//        deviceBeans.forEach {
//            if(it.name=="MI 8"){
//                it.createBond()
//            }
//        }
    }

    override fun getBluetoothBondList(deviceBeans: Set<BluetoothDevice>) {
        Log.e(TAG, "getBluetoothBondList: 已绑定列表，${deviceBeans}")
        bondAdapter?.update(deviceBeans.toMutableList())
    }

    // 准备连接蓝牙
    override fun onConnect(bluetoothDevice: BluetoothDevice) {
        Toast.makeText(this, "连接中...", Toast.LENGTH_SHORT).show()
        bluetoothChatService?.connect(bluetoothDevice, this)
    }

    override fun changeStatus(state: Int) {
        Log.e(TAG, "changeStatus: ${state}")
        runOnUiThread {
            connectState = state
            if (state == 1) {
                binding.bluetoothList.visibility = View.GONE
                binding.bluetoothName.text = "已连接设备,点击断开"
                binding.bluetoothName.setBackgroundResource(R.drawable.button2)
            } else if (state == 0) {
                binding.bluetoothName.text = "未连接,点击连接"
                binding.bluetoothName.setBackgroundResource(R.drawable.button1)
            }
        }

    }

    override fun message(msg: String) {
        Log.e(TAG, "message: ${msg}")
        runOnUiThread {
            try {
                val data = JSONObject(msg)
                binding.time.text = data.getString("time")
                binding.name.text = data.getString("name")
                binding.dianya.text = data.getString("dianya")
                binding.yql.text = data.getString("yql")
                binding.kg.isChecked = data.getInt("kg") == 1
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}