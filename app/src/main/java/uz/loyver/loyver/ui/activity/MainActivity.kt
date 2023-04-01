package uz.loyver.loyver.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import net.posprinter.posprinterface.IMyBinder
import net.posprinter.posprinterface.UiExecute
import net.posprinter.service.PosprinterService
import net.posprinter.utils.DataForSendToPrinterPos80
import uz.loyver.loyver.R
import uz.loyver.loyver.databinding.ActivityMainBinding
import uz.loyver.loyver.manager.SharedPref
import uz.loyver.loyver.model.Cheque
import uz.loyver.loyver.utils.*
import uz.loyver.loyver.viewmodel.main.MainViewModel
import java.io.IOException
import java.io.OutputStream
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Runnable {

    lateinit var binding: ActivityMainBinding

    private lateinit var binder: IMyBinder

    private var IS_CONNECT = false
    private val DISCONNECT = "com.posconsend.net.disconnetct"

    private lateinit var cheque: Cheque

    private val viewModel: MainViewModel by viewModels()

    //bindService connection
    var connection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            //Bind successfully
            binder = iBinder as IMyBinder
            Log.e("IMyBinder", "connected")
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.e("IMyBinder", "disconnected")
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SharedPref(this).saveInt("customer", 0)
        SharedPref(this).saveBoolean("isActiveCategory", false)
        SharedPref(this).saveBoolean("isConnected", false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // bind service, get IMyBinder object
        val intent = Intent(this, PosprinterService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)

        checkPermission()

        printerObservers()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_main, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_ADMIN)) {
                Toast.makeText(this, "Разрешение", Toast.LENGTH_SHORT).show()
            }
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_ADMIN), 7)
        } else {
            toast(binding.root, "Приложение готово")
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            7 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toast(binding.root, "РАЗРЕШЕНИЕ ПОЛУЧЕНО")
                } else {
                    checkPermission()
                }
            }
        }
    }

    private val REQUEST_CONNECT_DEVICE = 1
    private val REQUEST_ENABLE_BT = 2

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var mBluetoothConnectProgressDialog: ProgressDialog? = null
    private var mBluetoothSocket: BluetoothSocket? = null
    private var mBluetoothDevice: BluetoothDevice? = null

    private var outputStream: OutputStream? = null

    @SuppressLint("MissingPermission")
    override fun onActivityResult(mRequestCode: Int, mResultCode: Int, mDataIntent: Intent?) {
        super.onActivityResult(mRequestCode, mResultCode, mDataIntent)

        when (mRequestCode) {
            REQUEST_CONNECT_DEVICE -> if (mResultCode == RESULT_OK) {
                val mExtra = mDataIntent!!.extras
                val mDeviceAddress = mExtra!!.getString("DeviceAddress")
                toast(binding.root, "Приходящий входящий адрес $mDeviceAddress")
                mBluetoothDevice = mBluetoothAdapter!!
                    .getRemoteDevice(mDeviceAddress)

                mBluetoothConnectProgressDialog = ProgressDialog.show(
                    this,
                    "Подключение...", mBluetoothDevice!!.name + " : "
                            + mBluetoothDevice!!.address, true, false
                )
                val bluetoothConnectThread = Thread(this)
                bluetoothConnectThread.start()

                // pairToDevice(mBluetoothDevice); This method is replaced by
                // progress dialog with thread
            }
            REQUEST_ENABLE_BT -> if (mResultCode == RESULT_OK) {
                listPairedDevices()
                val connectIntent = Intent(
                    this@MainActivity,
                    DeviceListActivity::class.java
                )
                startActivityForResult(connectIntent, REQUEST_CONNECT_DEVICE)
            } else {
                Toast.makeText(this@MainActivity, "Не подключен ни к одному устройству", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (mBluetoothSocket != null) {
                mBluetoothSocket!!.close()
            }
        } catch (e: Exception) {
            toast(binding.root, "ERROR: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun listPairedDevices() {
        val mPairedDevices = mBluetoothAdapter!!.bondedDevices
        if (mPairedDevices.size > 0) {
            for (mDevice in mPairedDevices) {
                toast(binding.root, "${mDevice.name} ${mDevice.address}")
            }
        }

    }

    @SuppressLint("HandlerLeak")
    private val mHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            mBluetoothConnectProgressDialog!!.dismiss()
            toast(binding.root, "СВЯЗАНО")
            SharedPref(this@MainActivity).saveBoolean("isConnected", true)
        }
    }

    @SuppressLint("MissingPermission")
    override fun run() {
        try {
            mBluetoothSocket = mBluetoothDevice!!.createRfcommSocketToServiceRecord(applicationUUID)
            mBluetoothAdapter!!.cancelDiscovery()
            mBluetoothSocket!!.connect()
            mHandler.sendEmptyMessage(0)

        } catch (eConnectException: IOException) {
            toast(binding.root, "ERROR ${eConnectException.message}")
            closeSocket(mBluetoothSocket!!)
            return
        }
    }

    private fun closeSocket(nOpenSocket: BluetoothSocket) {
        try {
            nOpenSocket.close()
            mBluetoothConnectProgressDialog!!.dismiss()
            toast(binding.root, "Не удается подключиться к принтеру")
            SharedPref(this@MainActivity).saveBoolean("isConnected", false)
        } catch (ex: IOException) {
            toast(binding.root, "Не удалось закрыть сокет")
        }
    }

    private fun printCustom(msg: String, size: Int, align: Int) {
        //Print config "mode"
        val cc = byteArrayOf(0x1B, 0x21, 0x03) // 0- normal size text
        //byte[] cc1 = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        val bb = byteArrayOf(0x1B, 0x21, 0x08) // 1- only bold text
        val bb2 = byteArrayOf(0x1B, 0x21, 0x20) // 2- bold with medium text
        val bb3 = byteArrayOf(0x1B, 0x21, 0x10) // 3- bold with large text
        try {
            when (size) {
                0 -> outputStream!!.write(cc)
                1 -> outputStream!!.write(bb)
                2 -> outputStream!!.write(bb2)
                3 -> outputStream!!.write(bb3)
            }
            when (align) {
                0 ->                     //left align
                    outputStream!!.write(PrinterCommands.ESC_ALIGN_LEFT)
                1 ->                     //center align
                    outputStream!!.write(PrinterCommands.ESC_ALIGN_CENTER)
                2 ->                     //right align
                    outputStream!!.write(PrinterCommands.ESC_ALIGN_RIGHT)
            }
            outputStream!!.write(msg.toByteArray())
            outputStream!!.write(PrinterCommands.LF.toInt())
            //outputStream.write(cc);
            //printNewLine();
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun printTestBluetooth() {
        if (mBluetoothSocket == null) {
            toast(binding.root, "ПОЖАЛУЙСТА, ПОДКЛЮЧИТЕ ПРИНТЕР")
        } else {
            var opstream: OutputStream? = null
            try {
                opstream = mBluetoothSocket!!.outputStream
            } catch (e: IOException) {
                e.printStackTrace()
            }
            outputStream = opstream

            // Print Command
            try {
                outputStream = mBluetoothSocket!!.outputStream
                val printFormat = byteArrayOf(0x1B, 0x21, 0x03)
                outputStream!!.write(printFormat)

                printCustom("OLLOYOR NAZOKAT OPTOM", 3, 1)

                printCustom("\n", 0, 0)

                printCustom("134 - Dokon orqa tarafi", 0, 1)
                printCustom("+998 88 299 00 08", 0, 1)

                printCustom("------------------------------", 0, 1)

                printCustom("Developer : OgabekDev", 0, 1)
                printCustom("Programmer : Ogabek Matyakubov", 0, 1)
                printCustom("Phone Number : +998 93 203 73 13", 0, 1)
                printCustom("Social Media : @OgabekDev", 0, 1)
                printCustom("Created by : Next Level Group", 0, 1)

                printCustom("------------------------------", 0, 1)

                printCustom("This is text print", 0, 1)

                printCustom("\n", 0, 0)
                printCustom("\n", 0, 0)

                outputStream!!.flush()

            } catch (e: Exception) {
                if (e.message == "Broken pipe") toast(binding.root, "Принтер отключен. Пожалуйста, подключите принтер") else toast(binding.root, "ERROR: ${e.message}")
                SharedPref(this@MainActivity).saveBoolean("isConnected", false)
                e.printStackTrace()
            }

        }
    }

    private fun printChequeBluetooth(cheque: Cheque) {
        if (mBluetoothSocket == null) {
            toast(binding.root, "ПОЖАЛУЙСТА, ПОДКЛЮЧИТЕ ПРИНТЕР")
        } else {
            toast(binding.root, getString(R.string.str_send_to_printer))
            var opstream: OutputStream? = null
            try {
                opstream = mBluetoothSocket!!.outputStream
            } catch (e: IOException) {
                e.printStackTrace()
            }
            outputStream = opstream

            // Print Command
            try {
                outputStream = mBluetoothSocket!!.outputStream
                val printformat = byteArrayOf(0x1B, 0x21, 0x03)
                outputStream!!.write(printformat)

                printCustom("OLLOYOR NAZOKAT OPTOM", 3, 1)

                printCustom("\n", 0, 0)

                printCustom("134 - Dokon orqa tarafi", 0, 1)
                printCustom("+998 88 299 00 08", 0, 1)

                printCustom("------------------------------", 0, 1)

                printCustom("Mijoz: ${cheque.user.name}", 0, 1)
                printCustom("Tel raqam: ${cheque.user.phone_number}", 0, 1)

                printCustom("------------------------------", 0, 1)

                for (i in cheque.items) {
                    printCustom(i.name.toString(), 1, 0)
                    printCustom("${if (i.type == Constants.EACH) i.quantity.toInt() else i.quantity} x ${i.price.toInt().toString().setAsPrice()}          ${(i.quantity * i.price).toInt().toString().setAsPrice()}", 1, 2)
                }

                printCustom("------------------------------", 0, 1)

                printCustom("Umumiy narx", 2, 1)
                printCustom("${cheque.total_summa.toInt()}".setAsPrice(), 2, 1)

                printCustom("------------------------------", 0, 1)

                printCustom("${cheque.create_date}  ${cheque.time.subSequence(0, 5)}     ${cheque.cart_number}", 0, 2)

                printCustom("Xaridingiz uchun raxmat", 0, 1)

                printCustom("\n", 0, 0)

                outputStream!!.flush()

            } catch (e: Exception) {
                if (e.message == "Broken pipe") toast(binding.root, "Принтер отключен. Пожалуйста, подключите принтер") else toast(binding.root, "ERROR: ${e.message}")
                SharedPref(this@MainActivity).saveBoolean("isConnected", false)
                e.printStackTrace()
            }

        }
    }




    // WiFi Printer

    private fun connectWiFi(ipAddress: String) {
        binder.connectNetPort(ipAddress, 9100, object: UiExecute {
            override fun onsucess() {
                IS_CONNECT = true

                binder.acceptdatafromprinter(object: UiExecute {
                    override fun onsucess() {}
                    override fun onfailed() {
                        IS_CONNECT = false
                        val intent = Intent()
                        toast(binding.root, getString(R.string.str_connection_failed))
                        intent.action = DISCONNECT
                        sendBroadcast(intent)
                    }
                })
            }

            override fun onfailed() {
                IS_CONNECT = false
                toast(binding.root, getString(R.string.str_connection_failed))
            }

        })
    }

    private fun printCustom(list: ArrayList<ByteArray>, msg: String, size: Int) {
        //Print config "mode"
        val cc = byteArrayOf(0x1B, 0x21, 0x03) // 0- normal size text
        val bb = byteArrayOf(0x1B, 0x21, 0x08) // 1- only bold text
        val bb2 = byteArrayOf(0x1B, 0x21, 0x08) // 2- bold with medium text
        val bb3 = byteArrayOf(0x1B, 0x21, 0x20) // 3- bold with large text
        try {
            list.add(DataForSendToPrinterPos80.setRelativeHorizontalPrintPosition(0, 0))

            when (size) {
                0 -> list.add(cc)
                1 -> list.add(bb)
                2 -> list.add(bb2)
                3 -> list.add(bb3)
            }

            list.add(msg.toByteArray())

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun printTestWiFi() {
        binder.writeDataByYouself(object: UiExecute {
            override fun onsucess() {}

            override fun onfailed() {
                toast(binding.root, "Потеряно подключение к WiFi, перезапустите приложение и WiFi.")
                IS_CONNECT = false
            }

        }) {

            val list: ArrayList<ByteArray> = ArrayList()
            list.add(DataForSendToPrinterPos80.initializePrinter())

            val space = "          "

            printCustom(list, "  OLLOYOR NAZOKAT OPTOM", 3)

            printCustom(list, "\n", 0)
            printCustom(list, "\n", 0)

            printCustom(list, "$space   134 - Dokon orqa tarafi\n", 2)
            printCustom(list, "$space      +998 88 299 00 08\n", 2)

            printCustom(list, "  --------------------------------------------\n", 2)

            printCustom(list, "${space}Developer : OgabekDev\n", 2)
            printCustom(list, "${space}Programmer : Ogabek Matyakubov\n", 2)
            printCustom(list, "${space}Phone Number : +998 93 203 73 13\n", 2)
            printCustom(list, "${space}Social Media : @OgabekDev\n", 2)
            printCustom(list, "${space}Created by : Next Level Group\n", 2)

            printCustom(list, "  --------------------------------------------\n", 2)

            printCustom(list, "$space     This is text print", 2)

            printCustom(list, "\n", 2)
            printCustom(list, "\n", 2)

            list.add(DataForSendToPrinterPos80.printAndFeedLine())
            list.add(DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(66, 1))

            list
        }
    }

    private fun printChequeWiFi(cheque: Cheque) {
        binder.writeDataByYouself(object: UiExecute {
            override fun onsucess() {}

            override fun onfailed() {
                toast(binding.root, "Потеряно подключение к WiFi, перезапустите приложение и WiFi.")
                IS_CONNECT = false
            }

        }) {

            val list: ArrayList<ByteArray> = ArrayList()
            list.add(DataForSendToPrinterPos80.initializePrinter())

            val space = "          "

            printCustom(list, "  OLLOYOR NAZOKAT OPTOM", 3)

            printCustom(list, "\n", 0)
            printCustom(list, "\n", 0)

            printCustom(list, "$space   134 - Dokon orqa tarafi\n", 2)
            printCustom(list, "$space      +998 88 299 00 08\n", 2)

            printCustom(list, "  --------------------------------------------\n", 2)

            printCustom(list, "$space   Mijoz : ${cheque.user.name}\n", 2)
            printCustom(list, "$space   Tel : ${cheque.user.phone_number}\n", 2)

            printCustom(list, "  --------------------------------------------\n", 2)

            val products = cheque.items

            for (i in products) {
                printCustom(list, "$space${i.name}\n", 2)
                printCustom(list, "$space     ${if (i.type == "EACH") i.quantity.toInt() else i.quantity} x ${i.price}          ${(i.quantity * i.price).toInt().toString().setAsPrice()}\n", 2)
            }

            printCustom(list, "  --------------------------------------------\n", 2)

            printCustom(list, "       Umumiy Narx\n", 3)
            printCustom(list, "         ${cheque.total_summa.toStringAsPrice()?.setAsPrice()}", 3)

            printCustom(list, "\n", 0)

            printCustom(list, "  --------------------------------------------\n", 2)

            printCustom(list, "$space${cheque.create_date} ${cheque.time.substring(0, 5)}     ${cheque.cart_number}\n", 2)
            printCustom(list, "$space   Xaridingiz uchun raxmat\n", 2)

            printCustom(list, "\n", 2)

            list.add(DataForSendToPrinterPos80.printAndFeedLine())
            list.add(DataForSendToPrinterPos80.selectCutPagerModerAndCutPager(66, 1))

            list
        }
    }

    // For All Printers

    fun printTest(isBluetooth: Boolean, ipAddress: String) {
        if (isBluetooth) {
            val printBT = PrintBluetooth()
            PrintBluetooth.printer_id = ipAddress
            printBT.findBT()
            printBT.openBT()
            printBT.printTestBluetooth(binding.root)
            printBT.closeBT()
        } else {
            if (!IS_CONNECT) connectWiFi(ipAddress)
            printTestWiFi()
        }
    }

    private fun printerObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.mainPrinter.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        if (it.data != null) {
                            if (it.data.isBluetooth) {
                                val printBT = PrintBluetooth()
                                PrintBluetooth.printer_id = it.data.address
                                printBT.findBT()
                                printBT.openBT()
                                printBT.printChequeBluetooth(binding.root, cheque)
                                printBT.closeBT()
                            } else {
                                if (!IS_CONNECT) connectWiFi(it.data.address)
                                printChequeWiFi(cheque)
                            }
                        } else {
                            toast(binding.root, "Основной принтер не выбран")
                        }
                    }
                    is UiStateObject.ERROR -> {
                        toast(binding.root, it.message)
                    }
                    else -> Unit
                }
            }
        }
    }

    fun printCheque(cheque: Cheque) {
        this.cheque = cheque
        viewModel.getMainPrinter()
    }
}