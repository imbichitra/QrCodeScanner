package com.example.bpradhan.qrcodescanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission_group.CAMERA;

public class MainActivity extends AppCompatActivity{

    private ZXingScannerView scannerView;
    private static final int REQUEST_CAMERA =1;
    String TAG="MainActivity";
    String ss=null;
    boolean isDialogOpen=false;
    static int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void scann(View v){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(checkPermission()){
                showScanner();
                //Toast.makeText(this, "Permission is granted!", Toast.LENGTH_SHORT).show();
            }else {
                requestPermission();
            }
        }else {
            showScanner();
        }
    }
    private void showScanner(){
        scannerView = new ZXingScannerView(this);
        scannerView.setResultHandler(new ZXingScannerResultHandler());

        setContentView(scannerView);
        scannerView.startCamera();
    }
    private boolean checkPermission(){
        return (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CAMERA:
                if(grantResults.length >0){
                    boolean cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        showScanner();
                        //Toast.makeText(this, "permissin granted!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, "permissin denied!", Toast.LENGTH_SHORT).show();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            if(shouldShowRequestPermissionRationale(CAMERA)){
                                displayAlertMessage("You need to all access for both permission",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                requestPermission();
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(message)
                .setPositiveButton("OK",listener)
                .setNegativeButton("Cancel",listener)
                .create()
                .show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(scannerView!=null) {
            scannerView.stopCamera();
        }
    }

    public void gen(View v){
        Intent i=new Intent(this,GeneratorActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ss=null;
    }

    class ZXingScannerResultHandler implements ZXingScannerView.ResultHandler{

        @Override
        public void handleResult(Result result) {
            String text=result.getText();
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_main);
            scannerView.stopCamera();
        }
    }
}
