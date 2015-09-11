package com.tatbigy.in_appbilling;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tatbigy.in_appbilling.util.IabHelper;
import com.tatbigy.in_appbilling.util.IabResult;
import com.tatbigy.in_appbilling.util.Inventory;
import com.tatbigy.in_appbilling.util.Purchase;

public class MainActivity extends AppCompatActivity {
    ImageView content;
    FloatingActionButton fab;
    //
    private static final String TAG =
            "tatbigy.inappbilling";
    IabHelper mHelper;
    //
      String SKU = "android.test.purchased";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        content = (ImageView) findViewById(R.id.content);
        content.setEnabled(false);
        // copy YOUR LICENSE KEY FOR THIS APPLICATION from
        //https://play.google.com/apps/publish
        String base64EncodedPublicKey =
                "key ,,, ";

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result) {
                                           if (!result.isSuccess()) {
                                               Log.d(TAG, "In-app Billing setup failed: " +
                                                       result);
                                           } else {
                                               Log.d(TAG, "In-app Billing is set up OK");
                                           }
                                       }
                                   });

         fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHelper.launchPurchaseFlow(MainActivity.this, SKU, 10001,
                        mPurchaseFinishedListener, "ahmed");
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (!mHelper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result,
                                          Purchase purchase)
        {
            if (result.isFailure()) {
                // Handle error
                Toast.makeText(getApplicationContext(),result.toString(),Toast.LENGTH_LONG).show();
                return;
            }
            else if (purchase.getSku().equals(SKU)) {
                consumeItem();
                fab.setImageResource(R.drawable.ic_done_white_24dp);

            }



        }
    };

    public void consumeItem() {
        mHelper.queryInventoryAsync(mReceivedInventoryListener);
    }

    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            if (result.isFailure()) {
                // Handle failure
            } else {
                mHelper.consumeAsync(inventory.getPurchase(SKU),
                        mConsumeFinishedListener);
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase,
                                              IabResult result) {

                    if (result.isSuccess()) {
                        content.setVisibility(View.VISIBLE);
                    } else {
                        // handle error
                    }
                }
            };


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }
}
