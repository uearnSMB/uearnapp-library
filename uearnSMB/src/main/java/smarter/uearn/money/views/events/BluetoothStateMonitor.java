package smarter.uearn.money.views.events;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import smarter.uearn.money.R;
import smarter.uearn.money.utils.SmarterSMBApplication;

public class BluetoothStateMonitor extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            //Do something if connected
            //  Toast.makeText(getApplicationContext(), "BT Connected", Toast.LENGTH_SHORT).show();
         //   SmarterSMBApplication.isHeadPhoneConnected = true;

        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            //   Toast.makeText(getApplicationContext(), "BT Disconnected", Toast.LENGTH_SHORT).show();
          //  SmarterSMBApplication.isHeadPhoneConnected = false;
        }

    }

}


