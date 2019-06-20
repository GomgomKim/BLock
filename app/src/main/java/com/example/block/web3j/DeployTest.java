package com.example.block.web3j;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.example.block.adapter.KeyStoreUtils;
import com.example.block.database.DoorPost;
import com.example.block.database.HostPost;
import com.example.block.database.MemberPost;
import com.example.block.service.TransactionService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DeployTest {

    private String URL = "https://ropsten.infura.io/v3/73b49c62a5c34d50b9ba5e4c4c2b2ceb";
    private String ADDRESS = "0xd9c1c60ba106fca0a920056545dfe47a5b465a04";
    private String WALLET = "UTC--2019-04-10T20-43-14.985--8c2ae0866c19bd02e2f4ba20050f9600d9dd3dfd.json";
    final String PASSWORD = "@qlqjs2019";
    Context context;
    String filename="";
    Credentials credentials;
    BigInteger GAS_PRICE = BigInteger.valueOf(10000);
    BigInteger GAS_LIMIT = BigInteger.valueOf(3000000);
    BigInteger INITIALWEIVALUE = BigInteger.valueOf(0);
    Utf8String DEPLOYSTRING = new Utf8String("sol test");
    Greeter greeter;

    private DatabaseReference mPostReference;

    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAmgpQZrg:APA91bHs4Bw8PHI_RXxWpxQGFidTm5QJ0Cy8o8dO0GUS5Ua48Oq6Jc0J1dyuLsMBmODmV0zYyL0IMs1diPbSO2tt0qtnF1C1ybLsWRFvhQztO2lqgmVkyTP0yrlcnOuq2ogq-ZqT-QQg";
    private void sendPostToFCM(final String to_fcm, final String message) {
        new Thread(() -> {
            try {
                // FMC 메시지 생성 start
                JSONObject root = new JSONObject();
                JSONObject notification = new JSONObject();
                notification.put("body", message);
                notification.put("title","Block");
                root.put("notification", notification);
                root.put("to", to_fcm);
                // FMC 메시지 생성 end

                java.net.URL Url = new URL(FCM_MESSAGE_URL);
                HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-type", "application/json");
                OutputStream os = conn.getOutputStream();
                os.write(root.toString().getBytes("utf-8"));
                os.flush();
                conn.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public DeployTest(Context context, String door_id, String user_token, String user_id, String user_name) throws ExecutionException, InterruptedException{

        this.context = context;

        Web3j web3j = Web3j.build(new HttpService(URL));
        Admin admin = Admin.build(new HttpService(URL));

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
            try {
                greeter = Greeter.deploy(web3j, credentials, contractGasProvider, INITIALWEIVALUE, DEPLOYSTRING);
                Log.i("gomgomKim", "depoly : "+greeter);
                Log.i("gomgomKim", "depoly : "+greeter.getContractAddress());
                Log.i("gomgomKim", "Greeter deployed");
            }  catch (TransactionException e) {
                e.printStackTrace();
                Log.i("gomgomKim", "deploy error");
            }

            // 잔액확인
            EthGetBalance ethGetBalance = null;
            try {
                ethGetBalance = web3j.ethGetBalance("0x8c2ae0866c19bd02e2f4ba20050f9600d9dd3dfd", DefaultBlockParameterName.LATEST).sendAsync().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            BigInteger wei = ethGetBalance.getBalance();
            String result2 = Convert.fromWei(wei.toString() , Convert.Unit.ETHER).toString();

            Log.i("gomgomKim", "얼마있나요? : "+result2);

            // DB insert
            String deploy_address = greeter.getContractAddress();
            postDoorFirebase(door_id, deploy_address);
            sendPostToFCM(user_token,"deploy is successed!");
            postHome(user_id, user_name, door_id);

            getMemberInfo(user_id, deploy_address);
        });

        chain_thread.start();


    }

    public void postDoorFirebase(String door_id, String deploy_address){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        DoorPost post = new DoorPost(door_id, deploy_address);
        postValues = post.toMap();

        getDoorRow(childUpdates, postValues);
    }

    public void getDoorRow(Map<String, Object> childUpdates, Map<String, Object> postValues){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long door_row = dataSnapshot.getChildrenCount();
                childUpdates.put("/door/" + String.valueOf(door_row+1), postValues);
                mPostReference.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("door");
        sortbyAge.addListenerForSingleValueEvent(postListener);
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

    public void postHome(String user_id, String user_name, String door_id){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        HostPost post = new HostPost(user_id, user_name, door_id);
        postValues = post.toMap();

        getHostRow(childUpdates, postValues);
    }


    public void getHostRow(Map<String, Object> childUpdates, Map<String, Object> postValues){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long host_row = dataSnapshot.getChildrenCount();
                childUpdates.put("/host/" + String.valueOf(host_row+1), postValues);
                mPostReference.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("host");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }

    public void getMemberInfo(String u_id, String address){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
                    String[] info = {get.user_id, get.user_name};

                    if(u_id.equals(info[0])){
                        String sender = info[1]+"("+key+")";
                        String cur_time = getNow();

                        Intent intent = new Intent(context, TransactionService.class);
                        intent.putExtra("type", "host");
                        intent.putExtra("sender", sender);
                        intent.putExtra("user_id", u_id);
                        intent.putExtra("cur_time", cur_time);
                        intent.putExtra("address", address);
                        intent.putExtra("state", "set");

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

    public String getNow(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String getTime = sdf.format(date);
        return getTime;
    }



}