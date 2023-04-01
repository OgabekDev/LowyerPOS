package uz.loyver.loyver.utils;

import static uz.loyver.loyver.utils.ExtensionsKt.getAsPrice;
import static uz.loyver.loyver.utils.ExtensionsKt.toast;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import uz.loyver.loyver.manager.SharedPref;
import uz.loyver.loyver.model.Cheque;
import uz.loyver.loyver.model.ChequeProduct;

public class PrintBluetooth extends AppCompatActivity {

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    byte FONT_TYPE;

    public static String printer_id;

    public PrintBluetooth(){}

    @SuppressLint("MissingPermission")
    public void findBT() {
        System.out.println("Printer ID : "+printer_id);
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBluetoothAdapter == null) {
                Toast.makeText(this, "Device Bluetooth tidak Tersedia", Toast.LENGTH_SHORT).show();
            }
            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(printer_id)) {
                        Log.d("TOAST MESSAGE", "findBT: " + device);
                        mmDevice = device;
                        break;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // tries to open a connection to the bluetooth printer device
    @SuppressLint("MissingPermission")
    public void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            beginListenForData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printCustom(String msg, int size, int align) {
        //Print config "mode"
        byte[] cc = new byte[]{0x1B, 0x21, 0x08}; // 0 - normal size text

        byte[] bb = new byte[]{0x1B, 0x21, 0x08}; // 1 - only bold text
        byte[] bb2 = new byte[]{0x1B, 0x21, 0x20}; // 2 - bold with medium text
        byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3 - bold with large text

        try {
            switch (size) {
                case 0: {
                    mmOutputStream.write(cc);
                    break;
                }
                case 1: {
                    mmOutputStream.write(bb);
                    break;
                }
                case 2: {
                    mmOutputStream.write(bb2);
                    break;
                }
                case 3: {
                    mmOutputStream.write(bb3);
                    break;
                }
            }

            switch (align) {
                case 0: {
                    mmOutputStream.write(PrinterCommands.INSTANCE.getESC_ALIGN_LEFT());
                    break;
                }
                case 1: {
                    mmOutputStream.write(PrinterCommands.INSTANCE.getESC_ALIGN_CENTER());
                    break;
                }
                case 2: {
                    mmOutputStream.write(PrinterCommands.INSTANCE.getESC_ALIGN_RIGHT());
                    break;
                }
            }

            mmOutputStream.write(msg.getBytes());
            mmOutputStream.write(PrinterCommands.INSTANCE.getLF());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printTestBluetooth(View view) {
        if (mmSocket == null) {
            toast(view, "ПОЖАЛУЙСТА, ПОДКЛЮЧИТЕ ПРИНТЕР");
        } else {
            try {
                mmOutputStream = mmSocket.getOutputStream();
                byte[] printFormat = new byte[]{0x1B, 0x21, 0x03};
                mmOutputStream.write(printFormat);

                printCustom("OLLOYOR NAZOKAT OPTOM", 3, 1);
                printCustom("\n", 0, 0);

                printCustom("134 - Dokon orqa tarafi", 0, 1);
                printCustom("+998 88 299 00 08", 0, 1);

                printCustom("------------------------------", 0, 1);

                printCustom("Developer : OgabekDev", 0, 1);
                printCustom("Programmer : Ogabek Matyakubov", 0, 1);
                printCustom("Phone Number : +998 93 203 73 13", 0, 1);
                printCustom("Social Media : @OgabekDev", 0, 1);
                printCustom("Created by : Next Level Group", 0, 1);

                printCustom("------------------------------", 0, 1);

                printCustom("This is text print", 0, 1);

                printCustom("\n", 0, 0);
                printCustom("\n", 0, 0);

                mmOutputStream.flush();

            } catch(Exception e) {
                if (Objects.equals(e.getMessage(), "Broken pipe"))
                    toast(view, "Принтер отключен. Пожалуйста, подключите принтер");
                else toast(view, "ERROR: "+ e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void printChequeBluetooth(View view, Cheque cheque) {
        if (mBluetoothAdapter == null) {
            toast(view, "ПОЖАЛУЙСТА, ПОДКЛЮЧИТЕ ПРИНТЕР");
        } else {
            try {
                mmOutputStream = mmSocket.getOutputStream();
                byte[] printFormat = new byte[]{0x1B, 0x21, 0x03};
                mmOutputStream.write(printFormat);

                printCustom("OLLOYOR NAZOKAT OPTOM", 3, 1);
                printCustom("\n", 0, 0);

                printCustom("134 - Dokon orqa tarafi", 0, 1);
                printCustom("+998 88 299 00 08", 0, 1);

                printCustom("------------------------------", 0, 1);

                printCustom("Mijoz: " + cheque.getUser().getName(), 0, 1);
                printCustom("Tel raqam: " + cheque.getUser().getPhone_number(), 0, 1);

                printCustom("------------------------------", 0, 1);

                for (ChequeProduct i: cheque.getItems()) {
                    printCustom(i.getName(), 1, 0);
                    String quantity;
                    if (Objects.equals(i.getName(), Constants.EACH)) {
                        quantity = String.valueOf((int) i.getQuantity());
                    } else {
                        quantity = String.valueOf(i.getQuantity());
                    }
                    String price = getAsPrice(String.valueOf((int) i.getPrice()));

                    String total = getAsPrice(i.getQuantity() * i.getPrice());
                    printCustom(quantity + " x " + price + "          " + total, 1, 2);
                }

                printCustom("------------------------------", 0, 1);

                printCustom("Umumiy narx", 2, 1);
                printCustom(getAsPrice(cheque.getTotal_summa()), 2, 1);

                printCustom("------------------------------", 0, 1);

                printCustom(cheque.getCreate_date() + "  " + cheque.getTime().substring(0, 5) + "     " + cheque.getCart_number(), 0, 2);

                printCustom("Xaridingiz uchun raxmat", 0, 1);

                printCustom("\n", 0, 0);

                mmOutputStream.flush();

            } catch (Exception e) {
                if (Objects.equals(e.getMessage(), "Broken pipe"))
                    toast(view, "Принтер отключен. Пожалуйста, подключите принтер");
                else toast(view, "ERROR: "+ e.getMessage());
                e.printStackTrace();
            }
        }
    }


    /*
     * after opening a connection to bluetooth printer device,
     * we have to listen and check if a data were sent to be printed.
     */
    public void beginListenForData() {
        try {
            final Handler handler = new Handler();
            // this is the ASCII code for a newline character
            final byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];
            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                        try {
                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );
                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;
                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
//                                                myLabel.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }
                        } catch (IOException ex) {
                            stopWorker = true;
                        }
                    }
                }
            });
            workerThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //    this will update data printer name in ModelUser
    // close the connection to bluetooth printer.
    public void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String setAsPrice(String str) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(str.length() - i);
            if (i % 3 == 1 && i > 1) {
                result.insert(0, ",");
            }
            result.insert(0, String.valueOf(ch));
        }

        return result.toString();
    }

}
