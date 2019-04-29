package com.example.block.web3j;

import android.content.Context;
import android.util.Log;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ContactPrivateChain {
    private String URL = "http://127.0.0.1:8545";
    final String PASSWORD = "rlarl123";
    Context context;

    public ContactPrivateChain(Context context) throws ExecutionException, InterruptedException {

        this.context = context;

        Web3j web3j = Web3j.build(new HttpService(URL));
        Admin admin = Admin.build(new HttpService(URL));

        try {
            Credentials credentials = WalletUtils.loadCredentials("rlarl123", "ether\\data\\keystore/UTC--2019-03-21T09-04-34.957056900Z--2cad275fb41068a1cc0076a4cf9b69bd9c87070e");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }

        Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().sendAsync().get();
        String clientVersion = web3ClientVersion.getWeb3ClientVersion();
        Log.i("gomgomKim", "client version : " + clientVersion);

        PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount("0x2cad275FB41068A1cc0076A4cf9b69Bd9C87070e", PASSWORD).sendAsync().get();
        if (personalUnlockAccount.accountUnlocked() == null) {
            Log.i("gomgomKim", "unlock failed");
        } else {
            Log.i("gomgomKim", "unlock successed");
        }
    }
}
