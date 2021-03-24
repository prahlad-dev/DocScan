package info.hannes.liveedgedetection.activity;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import info.hannes.liveedgedetection.R;
import info.hannes.liveedgedetection.ScanConstants;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class data extends AppCompatActivity {

    TextView fname, blockconf, symconf, reqid, vertical, vehicleid, make, model, variant, fuel, cc, previnsu, mfgyear, regdate, expdate,wait;
    String resStr, imagename;
    Button done;
    String filename, block_conf, sym_conf, request_id, vertical_id,make_id, model_id, variant_id, fuel_id, prev_insurer,registration_date,expiry_date;
    String vehicle_id,cc_id,mfg_yr;
    ProgressDialog progressDoalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        fname = findViewById(R.id.fname);
        blockconf= findViewById(R.id.blockconf);
        symconf = findViewById(R.id.symcon);
        reqid = findViewById(R.id.reqid);
        vertical = findViewById(R.id.vertical);
        vehicleid = findViewById(R.id.vehicleid);
        make = findViewById(R.id.make);
        model = findViewById(R.id.model);
        variant = findViewById(R.id.variant);
        fuel = findViewById(R.id.fuel);
        cc = findViewById(R.id.cc);
        previnsu = findViewById(R.id.previnsu);
        mfgyear = findViewById(R.id.mfg);
        regdate = findViewById(R.id.regdate);
        expdate = findViewById(R.id.expdate);
        done = findViewById(R.id.done);
        wait = findViewById(R.id.wait);

        imagename = getIntent().getExtras().getString("fname");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        new MyAsyncTask().execute();
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

    }
    private class MyAsyncTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                connectToServer(ScanConstants.croppedBitmap);
            }catch (Exception e){
                Toast.makeText(data.this, "Error occured in fetching data", Toast.LENGTH_SHORT).show();
            }

            // Waiting for results to be fetched.
            try {

                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            fname.setText(filename);
            blockconf.setText(block_conf);
            symconf.setText(sym_conf);
            reqid.setText(request_id);
            vertical.setText(vertical_id);
            vehicleid.setText(vehicle_id);
            make.setText(make_id);
            model.setText(model_id);
            variant.setText(variant_id);
            fuel.setText(fuel_id);
            cc.setText(cc_id);
            previnsu.setText(prev_insurer);
            mfgyear.setText(mfg_yr);
            regdate.setText(registration_date);
            expdate.setText(expiry_date);
            done.setVisibility(View.VISIBLE);
            wait.setVisibility(View.GONE);
        }
    }

    public void connectToServer(Bitmap image){
        String postUrl= "http://192.180.1.169:7000/api/qis/android_generate_quote/";
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        multipartBodyBuilder.addFormDataPart("image", imagename, RequestBody.create(MediaType.parse("image/*jpg"), imageByte));
        RequestBody postBodyImage = multipartBodyBuilder.build();
        postRequest(postUrl, postBodyImage);
    }

    void postRequest(String postUrl, RequestBody postBody) {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                call.cancel();
                Log.d("Error", "onFailure: "+e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(data.this,"Failed to connect to Server.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            resStr = response.body().string();
                            JSONObject obj = new JSONObject(resStr);
                            filename = (String) obj.get("filename");
                            block_conf = (String) obj.get("block_conf");
                            sym_conf = (String) obj.get("sym_conf");
                            request_id = (String) obj.get("request_id");
                            vertical_id = (String) obj.get("vertical");
                            vehicle_id = String.valueOf((Integer) obj.get("vehicle_id"));
                            make_id = (String) obj.get("make");
                            model_id = (String) obj.get("model");
                            variant_id = (String) obj.get("variant");
                            fuel_id = (String) obj.get("fuel");
                            cc_id = String.valueOf((Integer) obj.get("cc"));
                            prev_insurer = (String) obj.get("prev_insurer");
                            mfg_yr = String.valueOf((Integer) obj.get("mfg_yr"));
                            registration_date = (String) obj.get("registration_date");
                            expiry_date = (String) obj.get("expiry_date");
                            Log.d("mainAPI", "resStr: "+resStr);
                        } catch (Exception e) {
                            System.out.println("++++++INSIDE CATCH______");
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

}