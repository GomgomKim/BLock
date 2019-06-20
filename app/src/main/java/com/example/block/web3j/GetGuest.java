package com.example.block.web3j;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.block.adapter.KeyStoreUtils;
import com.example.block.database.MemberPost;
import com.example.block.main.MainActivity;
import com.example.block.service.TransactionService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class GetGuest {

    private String URL = "https://ropsten.infura.io/v3/73b49c62a5c34d50b9ba5e4c4c2b2ceb";
    private String ADDRESS = "0xd9c1c60ba106fca0a920056545dfe47a5b465a04";
    private String WALLET = "UTC--2019-04-10T20-43-14.985--8c2ae0866c19bd02e2f4ba20050f9600d9dd3dfd.json";
    final String PASSWORD = "@qlqjs2019";
    Context context;
    String filename="";
    Credentials credentials;
    BigInteger GAS_PRICE = BigInteger.valueOf(10000);
    BigInteger GAS_LIMIT = BigInteger.valueOf(3000000);
    Greeter greeter;
    String user_id, address;
    public GetGuest(Context context, String address, String user_id, String cur_time) throws ExecutionException, InterruptedException{

        this.context = context;
        this.user_id = user_id;
        this.address = address;

        ADDRESS = address;

        Web3j web3j = Web3j.build(new HttpService(URL));

        Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().sendAsync().get();;
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();
        Log.i("gomgomKim", "client version : "+clientVersion);

        createWallet("@qlqjs2019");

        try {
            credentials = WalletUtils.loadCredentials(PASSWORD, Environment.getExternalStorageDirectory().getPath() + "/LightWallet/"+WALLET);
//           Log.i("gomgomKim", "file path : "+Environment.getExternalStorageDirectory().getPath() + "/LightWallet/"+filename);

//           credentials = Credentials.create(web3j.ethAccounts().send().getAccounts().get(0));
            Log.i("gomgomKim", "Credentials loaded");
            Log.i("gomgomKim", "credentials : "+credentials);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }

        ContractGasProvider contractGasProvider = new ContractGasProvider() {
            @Override
            public BigInteger getGasPrice(String contractFunc) {
                return GAS_PRICE;
            }

            @Override
            public BigInteger getGasPrice() {
                return GAS_PRICE;
            }

            @Override
            public BigInteger getGasLimit(String contractFunc) {
                return GAS_LIMIT;
            }

            @Override
            public BigInteger getGasLimit() {
                return GAS_LIMIT;
            }
        };

        greeter = Greeter.load(ADDRESS, web3j, credentials, contractGasProvider);
        Log.i("gomgomKim", "Greeter loaded : "+greeter);

        Thread chain_thread = new Thread(() -> {
//            new Uint256(BigInteger.valueOf(123));
            try {
                String result = String.valueOf(greeter.getGuest());
                Log.i("gomgomKim", "get guest : "+result);

                // 사람 분류
                StringTokenizer st = new StringTokenizer(result, "/");
                ArrayList<String> u_id_arr = new ArrayList<>();
                while(st.hasMoreElements()){
                    u_id_arr.add(st.nextToken());
                }

                boolean is_guest = false;

                for(int i=0; i<u_id_arr.size(); i++){
                    StringTokenizer st2 = new StringTokenizer(u_id_arr.get(i), ",");
                    ArrayList<String> guest_arr = new ArrayList<>();
                    while(st2.hasMoreElements()){
                        guest_arr.add(st2.nextToken());
                    }
                    String u_id = guest_arr.get(0);
                    String start_auth = guest_arr.get(1);
                    String end_auth = guest_arr.get(2);

                    Log.i("gomgomKim", "user : "+u_id);
                    Log.i("gomgomKim", "start_auth : "+start_auth);
                    Log.i("gomgomKim", "end_auth : "+end_auth);

                    long start_date_long = cropDateToLong(start_auth);
                    long end_date_long = cropDateToLong(end_auth);
                    long cur_time_long = cropDateToLong(cur_time);
                    Log.i("gomgomKim", "start_long : "+start_date_long);
                    Log.i("gomgomKim", "end_long : "+end_date_long);
                    Log.i("gomgomKim", "now : "+cur_time_long);

                    // u_id 일치하고 현재시간이 기간안쪽이라면
                    if(user_id.equals(u_id) && cur_time_long > start_date_long && cur_time_long < end_date_long){
                        is_guest = true;
                    }
                }

                if(is_guest){
                    openDoor();
                }
                else {
                    notOpenDoor();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        chain_thread.start();


    }

    public void openDoor(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context, "open Door !", Toast.LENGTH_SHORT).show());
        handler.post(() -> MainActivity.sendData("1"));
        handler.post(() -> findSender());
    }

    public void notOpenDoor(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(context, "You don't have auth !", Toast.LENGTH_SHORT).show());
    }


    public void createWallet(final String password) {
        try {
            File fileDir = new File(Environment.getExternalStorageDirectory().getPath() + "/LightWallet");
            if (!fileDir.exists()) {
                fileDir.mkdirs();

                ECKeyPair ecKeyPair = Keys.createEcKeyPair();

                //외장카드생성
                filename = WalletUtils.generateWalletFile(password, ecKeyPair, fileDir, false);

                KeyStoreUtils.genKeyStore2Files(ecKeyPair);

                String msg = "fileName:\n" + filename
                        + "\nprivateKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPrivateKey())
                        + "\nPublicKey:\n" + Numeric.encodeQuantity(ecKeyPair.getPublicKey());
                Log.i("gomgomKim", msg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long cropDateToLong(String date){
        String crop1 = date.replaceAll("-", "");
        String crop2 = crop1.replaceAll(" ", "");
        String crop3 = crop2.replaceAll(":", "");
        long long_date = Long.parseLong(crop3);
        return long_date;
    }

    public String getNow(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String getTime = sdf.format(date);
        return getTime;
    }

    public void findSender(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String sender_name="";
                String sender_phone="";

                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
                    String[] info = {get.user_id, get.user_name};

                    if(user_id.equals(info[0])){
                        sender_name = info[1];
                        sender_phone = key;
                        String sender = sender_name+"("+sender_phone+")";
                        Intent intent = new Intent(context, TransactionService.class);
                        intent.putExtra("sender", sender);
                        intent.putExtra("address", address);
                        intent.putExtra("type", "guest");
                        intent.putExtra("cur_time", getNow());
                        intent.putExtra("state", "setHistory");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent);
                        } else{
                            context.startService(intent);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("member");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }
}