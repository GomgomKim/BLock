package com.example.block.web3j;

import android.content.Context;
import android.util.Log;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.util.concurrent.ExecutionException;

public class ContactBlockchain{

    private String URL = "https://ropsten.infura.io/v3/73b49c62a5c34d50b9ba5e4c4c2b2ceb";
    final String PASSWORD = "@qlqjs2019";
    Context context;

    public ContactBlockchain(Context context) throws ExecutionException, InterruptedException {

        this.context = context;

        Web3j web3j = Web3j.build(new HttpService(URL));
        Admin admin = Admin.build(new HttpService(URL));

        Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().sendAsync().get();
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();
        Log.i("gomgomKim", "client version : "+clientVersion);

        PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount("0x7E40fBce16dfC95066977b1939Fe2e2db65B7D4B", PASSWORD).sendAsync().get();
        if (personalUnlockAccount.accountUnlocked() ==  null) {
            Log.i("gomgomKim", "unlock failed");
        } else{
            Log.i("gomgomKim", "unlock successed");
        }


    }
/*
    public String[] createWallet(final String password) {
        String[] result = new String[2];
        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //다운로드 path 가져오기
            Log.i("gomgomKim", "download path :  : "+path);
            if (!path.exists()) {
                path.mkdir();
                Log.i("gomgomKim", "make dir");
            }
            String fileName = WalletUtils.generateNewWalletFile(password, new File(String.valueOf(path))); //지갑생성
            Log.i("gomgomKim", "made wallet");
            result[0] = path+"/"+fileName;

//            Credentials credentials = WalletUtils.loadCredentials(password,result[0]);

//            result[1] = credentials.getAddress();

            Log.i("gomgomKim", "path : "+result[0]);
//            Log.i("gomgomKim", "credential address : "+result[1]);

            return result;
        } catch (NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | IOException
                | CipherException e) {
            Log.i("gomgomKim", "wallet failed");
            e.printStackTrace();
            return null;
        }
    }*/


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
