package com.duvitech.bledemo;

import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private Button scanButton;
    private ListView bleDeviceListView;
    private BLEDeviceListAdapter listViewAdapter;

    private BLEHandler bluetoothHandler;
    private boolean isConnected;
    private static final boolean INPUT = false;
    private static final boolean OUTPUT = true;
    private static final boolean LOW = false;
    private static final boolean HIGH = true;

    private boolean digitalVal[];
    private int analogVal[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            Toast.makeText(this, R.string.ble_supported, Toast.LENGTH_SHORT).show();
        }


        scanButton = (Button) findViewById(R.id.scanButton);
        bleDeviceListView = (ListView) findViewById(R.id.bleDeviceListView);
        listViewAdapter = new BLEDeviceListAdapter(this);
        digitalVal = new boolean[14];
        analogVal = new int[14];

        bluetoothHandler = new BLEHandler(this);

    }

    public void scanOnClick(final View v){
        if(!isConnected){
            bleDeviceListView.setAdapter(bluetoothHandler.getDeviceListAdapter());
            bleDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    String buttonText = (String) ((Button)v).getText();
                    if(buttonText.equals("scanning")){
                        showMessage("scanning...");
                        return ;
                    }
                    BluetoothDevice device = bluetoothHandler.getDeviceListAdapter().getItem(position).device;
                    // connect
                    bluetoothHandler.connect(device.getAddress());
                }
            });
            bluetoothHandler.setOnScanListener(new BLEHandler.OnScanListener() {
                @Override
                public void onScanFinished() {
                    // TODO Auto-generated method stub
                    ((Button)v).setText("scan");
                    ((Button)v).setEnabled(true);
                }
                @Override
                public void onScan(BluetoothDevice device, int rssi, byte[] scanRecord) {}
            });
            ((Button)v).setText("scanning");
            ((Button)v).setEnabled(false);
            bluetoothHandler.scanLeDevice(true);
        }else{
            setConnectStatus(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setConnectStatus(boolean isConnected){
        this.isConnected = isConnected;
        if(isConnected){
            showMessage("Connection successful");
            scanButton.setText("break");
        }else{
            bluetoothHandler.onPause();
            bluetoothHandler.onDestroy();
            scanButton.setText("scan");
        }
    }

    private void showMessage(String str){
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
    }

}
