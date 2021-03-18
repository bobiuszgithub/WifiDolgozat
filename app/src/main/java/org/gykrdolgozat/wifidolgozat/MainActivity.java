package org.gykrdolgozat.wifidolgozat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textViewWifiInfo;
    private BottomNavigationView bottomNavigationView;
    private MediaPlayer mediaPlayerWifiOn;
    private MediaPlayer mediaPlayerWifiOff;
    private MediaPlayer mediaPlayerWifiInfo;
    private ListView listView;
    WifiManager wifiManager;
    WifiInfo wifiInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.wifiON:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                            textViewWifiInfo.setText("Nincs jogosultság a wifi módosítására.");
                            Intent panel = new Intent(Settings.Panel.ACTION_WIFI);
                            startActivityForResult(panel, 0);
                            break;
                        }

                        textViewWifiInfo.setText("Wifi bekapcsolva");
                        wifiManager.setWifiEnabled(true);
                        mediaPlayerWifiOn.start();
                        break;
                    case R.id.wifiOFF:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                            textViewWifiInfo.setText("Nincs jogosultság a wifi módosítására.");
                            Intent panel = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
                            startActivityForResult(panel, 0);
                            break;
                        }

                        textViewWifiInfo.setText("Wifi kikapcsolva");
                        wifiManager.setWifiEnabled(false);
                        mediaPlayerWifiOff.start();
                        break;
                    case R.id.wifiInfo:
                        ConnectivityManager conManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo netInfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        if (netInfo.isConnected()){
                            int ip_szam = wifiInfo.getIpAddress();
                            String ip = Formatter.formatIpAddress(ip_szam);
                            textViewWifiInfo.setText("Ip: "+ ip);
                        }else{
                            textViewWifiInfo.setText("Nem csatlakoztál wifi hálózatra.");
                        }

                        mediaPlayerWifiInfo.start();
                        break;
                    case R.id.wifiLista:
                        conManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        netInfo= conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        if (netInfo.isConnected()){
                            textViewWifiInfo.setText("Elérhető wifi hálózatok:");
                            wifiManager.startScan();
                            listView.setVisibility(View.VISIBLE);
                            List<ScanResult> scanResults = wifiManager.getScanResults();
                            //listView.setAdapter(scanResults);
                        }else{
                            textViewWifiInfo.setText("Nincs bekapcsolva a wifi");
                        }
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            if (wifiManager.getWifiState() == wifiManager.WIFI_STATE_DISABLING||
                    wifiManager.getWifiState() == wifiManager.WIFI_STATE_DISABLED
            ){
                textViewWifiInfo.setText("Wifi Kikapcsolva");
            }else if (wifiManager.getWifiState() == wifiManager.WIFI_STATE_ENABLING||
                    wifiManager.getWifiState() == wifiManager.WIFI_STATE_ENABLED
            ){
                textViewWifiInfo.setText("Wifi Bekapcsolva");
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
            }
        }
    }



    @Override
    protected void onPause() {
        mediaPlayerWifiInfo.stop();
        mediaPlayerWifiOn.stop();
        mediaPlayerWifiOff.stop();
        super.onPause();
    }

    public void init(){

        textViewWifiInfo = findViewById(R.id.textViewMediaInfo);
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        mediaPlayerWifiOn = MediaPlayer.create(this, R.raw.wifi_on);
        mediaPlayerWifiOff = MediaPlayer.create(this, R.raw.wifi_off);
        mediaPlayerWifiInfo = MediaPlayer.create(this, R.raw.wifi_info);
        listView = findViewById(R.id.ListViewWifiLista);
        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();


    }


}