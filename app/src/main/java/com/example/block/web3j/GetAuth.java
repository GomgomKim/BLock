package com.example.block.web3j;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.block.adapter.DoorList_sub;
import com.example.block.adapter.KeyStoreUtils;
import com.example.block.database.MemberPost;
import com.example.block.service.BusProvider;
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
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class GetAuth {

    private String URL = "https://ropsten.infura.io/v3/73b49c62a5c34d50b9ba5e4c4c2b2ceb";
    private String ADDRESS;
    private String WALLET = "UTC--2019-04-10T20-43-14.985--8c2ae0866c19bd02e2f4ba20050f9600d9dd3dfd.json";
    final String PASSWORD = "@qlqjs2019";
    Context context;
    String filename="";
    Credentials credentials;
    BigInteger GAS_PRICE = BigInteger.valueOf(10000);
    BigInteger GAS_LIMIT = BigInteger.valueOf(3000000);
    Greeter greeter;
    String result_trim = "";

    public GetAuth(Context context, String address) throws ExecutionException, InterruptedException{

        this.context = context;

        ADDRESS = address;

        Web3j web3j = Web3j.build(new HttpService(URL));

        BusProvider.getInstance().register(context);

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
                String result = String.valueOf(greeter.getAuth());
                Log.i("gomgomKim", "get auth : "+result);

                // u_id 분류
                StringTokenizer st = new StringTokenizer(result, "/");
                ArrayList<String> result_arr = new ArrayList<>();
                while(st.hasMoreElements()){
                    result_arr.add(st.nextToken());
                }

                int count = result_arr.size();

                for(int i=0; i<result_arr.size(); i++){
                    count --;
                    // u_id 분류
                    StringTokenizer st_detail = new StringTokenizer(result_arr.get(i), ",");
                    ArrayList<String> result_detail = new ArrayList<>();
                    while(st_detail.hasMoreElements()){
                        result_detail.add(st_detail.nextToken());
                    }
                    if(result_detail !=null){
                        String type = result_detail.get(0);
                        String date = result_detail.get(1);
                        String user_id = result_detail.get(2);
                        String name = result_detail.get(3);
                        findReceiver(user_id, type, date, name, count);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        chain_thread.start();


    }

    public void sendResult(String result){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> DoorList_sub.setTextAuth(result));
    }

    public void findReceiver(String r_id, String type, String date, String name, int count){
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

                    if(r_id.equals(info[0])){
                        sender_name = info[1];
                        sender_phone = key;
                        String receiver = sender_name+"("+sender_phone+")";
                        result_trim = result_trim+"[ "+type+" ]\n";
                        result_trim = result_trim+name+" -> "+receiver+"\n";
                        result_trim = result_trim+date+"\n";
                    }

                }
                if(count == 0) sendResult(result_trim);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("member");
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
}