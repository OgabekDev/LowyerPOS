package uz.loyver.loyver.ui.activity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import dagger.hilt.android.AndroidEntryPoint
import uz.loyver.loyver.R
import uz.loyver.loyver.databinding.ActivityDeviceListBinding
import uz.loyver.loyver.utils.toast

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class DeviceListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceListBinding

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mPairedDevicesArrayAdapter: ArrayAdapter<String>? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setResult(RESULT_CANCELED)
        mPairedDevicesArrayAdapter = ArrayAdapter(this, R.layout.device_name)

        val mPairedListView = findViewById<ListView>(R.id.paired_devices)
        mPairedListView.adapter = mPairedDevicesArrayAdapter
        mPairedListView.onItemClickListener = mDeviceClickListener

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val mPairedDevices = mBluetoothAdapter!!.bondedDevices

        if (mPairedDevices.size > 0) {
            /* List of all paired devices */
            findViewById<View>(R.id.title_paired_devices).visibility = View.VISIBLE
            for (mDevice in mPairedDevices) {
                mPairedDevicesArrayAdapter!!.add(
                    """
                ${mDevice.name}
                ${mDevice.address}
                """.trimIndent()
                )
            }
        } else {
            /* No paired device */
            val mNoDevices = "None Paired"
            mPairedDevicesArrayAdapter!!.add(mNoDevices)
        }

    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()

        mBluetoothAdapter?.cancelDiscovery()

    }

    @SuppressLint("MissingPermission")
    private val mDeviceClickListener =
        AdapterView.OnItemClickListener { mAdapterView, mView, mPosition, mLong ->
            try {
                /* Attempt to connect to bluetooth device */
                mBluetoothAdapter!!.cancelDiscovery()
                val mDeviceInfo = (mView as TextView).text.toString()
                val mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length - 17)
                toast(binding.root, "Адрес устройства $mDeviceAddress")
                val mBundle = Bundle()
                mBundle.putString("DeviceAddress", mDeviceAddress)
                val mBackIntent = Intent()
                mBackIntent.putExtras(mBundle)
                setResult(RESULT_OK, mBackIntent)
                finish()
            } catch (ex: Exception) {
                toast(binding.root, "Error: $ex")
            }
        }

}