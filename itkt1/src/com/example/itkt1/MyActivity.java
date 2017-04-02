package com.example.itkt1;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {

    private BroadcastReceiver br;
    private TextView textView;
    private EditText editText;
    private ListView listView;
    private ArrayAdapter<String> adapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textView = (TextView)findViewById(R.id.textView);
        listView = (ListView)findViewById(R.id.listView);
        editText = (EditText)findViewById(R.id.editText);

        textView.setText("Wifi manager");

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, new ArrayList<String>(20));
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String ssid = (String)listView.getItemAtPosition(position);

                Toast.makeText(MyActivity.this, "Connect to " + ssid, Toast.LENGTH_SHORT).show();


                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                // setup a wifi configuration
                WifiConfiguration wc = new WifiConfiguration();
                wc.SSID = String.format("\"%s\"", ssid);
                if (editText.getText().length() != 0) {
                    wc.preSharedKey = String.format("\"%s\"", editText.getText().toString());
                    wc.status = WifiConfiguration.Status.ENABLED;
                    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                }
                else {
                    // open wi-fi
                    wc.status = WifiConfiguration.Status.ENABLED;
                    wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                }
                // connect to and enable the connection
                int netId = wifiManager.addNetwork(wc);
                wifiManager.enableNetwork(netId, true);
                wifiManager.setWifiEnabled(true);


                return false;
            }
        });

        br = new BroadcastReceiver() {
            private int notifyId = 1;
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction().equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)){
                    boolean connected = intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false);
                    if(!connected) {
                        //Start service for disconnected state here
                        notifyMessage("Изменено состояние WiFi", "WiFi", "Отключено");
                    }
                }

                else if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                    NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if( netInfo.isConnected() )
                    {
                        //Start service for connected state here.
                        notifyMessage("Изменено состояние WiFi", "WiFi", "Включено");
                    }
                }
            }
            public void notifyMessage(String tiker, String title, String text) {
                Notification.Builder builder = new Notification.Builder(getApplicationContext());

                builder.setSmallIcon(R.drawable.ic_launcher)
                        .setTicker(tiker)
                        .setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentText(text);
                Notification notification = builder.build();

                NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(notifyId, notification);
                notifyId++;
            }
        };

        final IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.wifi.STATE_CHANGE");
        filters.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        super.registerReceiver(br, filters);

    }

    public void onClickBtn(View view) {
        switch (view.getId()) {
            case R.id.button: {
                //super.unregisterReceiver(br);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                        mWifiManager.startScan();
                        List<ScanResult> mScanResults = mWifiManager.getScanResults();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.clear();
                                for (ScanResult res : mScanResults) {
                                    adapter.add(res.SSID);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();



                break;
            }

            case R.id.button2: {
                Intent intent = new Intent(this, StatusActivity.class);
                startActivity(intent);
            }

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        super.unregisterReceiver(br);
    }
}
