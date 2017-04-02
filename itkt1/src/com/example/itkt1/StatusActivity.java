package com.example.itkt1;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Admin on 02.04.2017.
 */
public class StatusActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);

        TextView textView = (TextView)findViewById(R.id.textView2);

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();

        textView.setText(
                String.format("SSID=%s\nBSSID=%s\nIP=%s\nMac=%s\nSpeed=%d",
                        info.getSSID(),
                        info.getBSSID(),
                        IPString.intToString(info.getIpAddress()),
                        info.getMacAddress(),
                        info.getLinkSpeed()
                ));

    }
}

class IPString {
    public static String intToString(int ip) {
        String s = String.format("%d.%d.%d.%d",
                ip & 0xFF,
                (ip >> 8) & 0xFF,
                (ip >> 16) & 0xFF,
                (ip >> 24) & 0xFF);
        return s;
    }
}