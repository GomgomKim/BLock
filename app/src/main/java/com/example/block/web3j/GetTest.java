package com.example.block.web3j;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.block.adapter.KeyStoreUtils;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class GetTest {

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

    public GetTest(Context context) throws ExecutionException, InterruptedException, IOException, TransactionException {

        this.context = context;

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
                String result = String.valueOf(greeter.getHost());
                Log.i("gomgomKim", "get : "+result);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        chain_thread.start();


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


    public void  private_connect(){
         /*// 계좌정보 불러오기
        EthAccounts ethAccounts = web3j.ethAccounts().sendAsync().get(); // 등록계좌정보
        String accounts[] = ethAccounts.getAccounts().toArray(new String[0]);*/

        // 첫번째 계좌 락 해제 id, pwr
     /*   if(accounts.length > 1){
            PersonalUnlockAccount personalUnlockAccount
                    = admin.personalUnlockAccount(accounts[0], "rlarl123").sendAsync().get();
            // 기연 같은 경우엔 accounts[0] == 0x2cad275fb41068a1cc0076a4cf9b69bd9c87070e

            if (personalUnlockAccount.accountUnlocked()) {
                // send a transaction
                Log.i("gomgomKim", "unlock account clear");
            } else{
                Log.i("gomgomKim", "unlock account defeat");
            }
        } else{
            Log.i("gomgomKim", "no account ! ");
        }*/
    }
}