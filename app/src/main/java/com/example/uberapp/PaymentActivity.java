package com.example.uberapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.VolleyLog.TAG;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {
    private static final String TAG = MainActivity.class.getSimpleName();
/*
    String checkout;
*/
    Button pay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Checkout.preload(getApplicationContext());
        pay = (Button)findViewById(R.id.payment);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startPayment();
            }
        });
    }
    public void startPayment() {

        Checkout checkout = new Checkout();
        final Activity activity = this;
        try{
            JSONObject object = new JSONObject();
            object.put("name","AddyCab");
            object.put("email","sk@gmail.com");
            object.put("contact","7757840368");
            checkout.open(activity,object);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Successfully done"+s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Error"+s, Toast.LENGTH_SHORT).show();
    }
}
