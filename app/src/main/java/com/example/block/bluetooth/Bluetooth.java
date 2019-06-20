package com.example.block.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Bluetooth{
    static final int REQUEST_ENABLE_BT = 10;
    // Sensor accelerometer;
    // boolean angleFlag = false;
    int data = 0;
    //   int[] intTemp = new int[3];
    BluetoothAdapter mBluetoothAdapter;
    char mCharDelimiter = '\n';
    Set<BluetoothDevice> mDevices;
    // ToggleButton mEditBack;
    //  float[] mGeomagnetic;
    //  float[] mGravity;
    InputStream mInputStream = null;
    OutputStream mOutputStream = null;
    int mPairedDeviceCount = 0;
    BluetoothDevice mRemoteDevice;
    //  private SensorManager mSensorManager;
    BluetoothSocket mSocket = null;
    String mStrDelimiter = "\n";
    Thread mWorkerThread = null;
    //  Sensor magnetometer;
    //  float pitch;
    byte[] readBuffer;
    int readBufferPosition;
    String[] temp;

    Context context;

    public Bluetooth(Context context){
        this.context = context;
    }

    //블루투스 사용가능 여부
    public void checkBluetooth()
    {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.mBluetoothAdapter == null)
        {
            Toast.makeText(context, "기기가 블루투스를 지원하지 않습니다.",Toast.LENGTH_SHORT).show();

            return;
        }
        if (!this.mBluetoothAdapter.isEnabled())
        {
            Toast.makeText(context, "현재 블루투스가 비활성화 상태입니다.",Toast.LENGTH_SHORT).show();
//            getstartActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 10);
            return;
        }
        selectDevice();
    }



    void connectToSelectedDevice(String paramString)
    {
        this.mRemoteDevice = getDeviceFromBondedList(paramString);
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try
        {
            this.mSocket = this.mRemoteDevice.createRfcommSocketToServiceRecord(uuid);
            this.mSocket.connect();
            this.mOutputStream = this.mSocket.getOutputStream();
            this.mInputStream = this.mSocket.getInputStream();
            sendData("t");
            return;
        }
        catch (Exception e)
        {
            Toast.makeText(context,"블루투스 연결 중 오류 발생",Toast.LENGTH_SHORT).show();

        }
    }

    BluetoothDevice getDeviceFromBondedList(String paramString)
    {
        Iterator localIterator = this.mDevices.iterator();
        BluetoothDevice localBluetoothDevice;
        do
        {
            if (!localIterator.hasNext()) {
                return null;
            }
            localBluetoothDevice = (BluetoothDevice)localIterator.next();
        } while (!paramString.equals(localBluetoothDevice.getName()));
        return localBluetoothDevice;
    }

    //블투 연결해주는 퍼미션 나올때 사용자의 선택에 따라서 동작
    /*requstCode => 블투 고유번호
      resultCode => 예/ 아니오 (사용자 선택)
      data => 전송할 데이터 (이 함수에선 사용 안함)
     */
    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
        switch (paramInt1)
        {
        }
        for (;;)
        {
            onActivityResult(paramInt1, paramInt2, paramIntent);
            // return;
            if (paramInt2 == -1)
            {
                selectDevice();
            }
            else if (paramInt2 == 0)
            {
                Toast.makeText(context, "블루투스를 사용할 수 없어 종료합니다.",Toast.LENGTH_SHORT).show();

            }
        }
    }



    public boolean onCreateOptionsMenu(Menu paramMenu)
    {
        //  getMenuInflater().inflate(2131492864, paramMenu);
        return true;
    }

    //앱 종료시 stream들 제거
    protected void onDestroy()
    {
        try
        {
            // this.mWorkerThread.interrupt();
            this.mInputStream.close();
            this.mOutputStream.close();
            this.mSocket.close();
            return;
        }
        catch (Exception localException)
        {
            for (;;) {}
        }
    }

    //디바이스 선택창 띄워주는 함수
    void selectDevice()
    {
        this.mDevices = this.mBluetoothAdapter.getBondedDevices();
        this.mPairedDeviceCount = this.mDevices.size();
        if (this.mPairedDeviceCount == 0)
        {
            Toast.makeText(context, "페어링된 장치가 없습니다.", Toast.LENGTH_SHORT).show();

        }
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
        localBuilder.setTitle("블루투스 장치 선택");
        List<String> listItems = new ArrayList<String>();
        for(BluetoothDevice device : mDevices){
            listItems.add(device.getName());
        }
        listItems.add("취소");

        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        localBuilder.setItems(items, (dialog, which) -> {
            if(which== Bluetooth.this.mPairedDeviceCount){
                Toast.makeText(Bluetooth.this.context, "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            else{
                connectToSelectedDevice(items[which].toString());
            }
        });

        localBuilder.setCancelable(false);
        AlertDialog alert = localBuilder.create();
        alert.show();

    }

    //아두이노로 데이터 전송 (Fragment 에서 쓰는것)
    public void sendData(String paramString)
    {
        paramString = paramString + this.mStrDelimiter;
        try
        {
            this.mOutputStream.write(paramString.getBytes());
            return;
        }
        catch (Exception e) //데이터 전송중 오류났을 때
        {
            Toast.makeText(context, "데이터 전송 중 오류 발생.", Toast.LENGTH_SHORT).show();
        }
    }

    public int recieveData(){
        byte[] buffer = new byte[1024];
        int bytes = 0;
        try
        {
            bytes = this.mInputStream.read(buffer);
            return bytes;
        }
        catch (Exception e) //데이터 전송중 오류났을 때
        {
            Toast.makeText(context, "데이터 수신 중 오류 발생.", Toast.LENGTH_SHORT).show();
        }
        return bytes;
    }

}
