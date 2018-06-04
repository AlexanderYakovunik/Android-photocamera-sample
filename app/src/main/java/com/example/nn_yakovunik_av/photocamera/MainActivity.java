package com.example.nn_yakovunik_av.photocamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button photoButton, managementButton;

    final String LOG_TAG = "MainActivity";

    static File directory;
    static final String FOLDER_NAME = "PhotoFolder";

    private boolean permitted = false;
    String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    final int MY_PERMISSIONS_REQUEST = 1,REQUEST_CODE_PHOTO=2;
    Uri uri;

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Camera camera;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoButton=findViewById(R.id.photoButton);
        photoButton.setOnClickListener(this);

        managementButton=findViewById(R.id.managementButton);
        managementButton.setOnClickListener(this);

        int permissionsMarker = PackageManager.PERMISSION_GRANTED;  //permissionsMarker=0
        for(String string:permissions){permissionsMarker+= ContextCompat.checkSelfPermission(this,string);} //checkSelfPermission() выдает 0 или -1
        if(permissionsMarker==PackageManager.PERMISSION_GRANTED){permitted = true;}
        else {
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);
        }
        camera=Camera.open(0);
        Log.d(LOG_TAG,"onCreate");

    }

    @Override
    protected void onStart() {
        super.onStart();
        surfaceView=findViewById(R.id.surfaceView);
        surfaceHolder=surfaceView.getHolder();
        Log.d(LOG_TAG,surfaceHolder.toString());
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try{
                    camera.setPreviewDisplay(holder);
                    camera.setDisplayOrientation(90);   //correct orientation in my phone
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        camera.startPreview();
        Log.d(LOG_TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            createDirectory();
        }catch (Exception e){
            Toast.makeText(this,R.string.no_permitted+e.toString(),Toast.LENGTH_LONG).show();
        }
        Log.d(LOG_TAG,"onResume");
/*Отладка*/
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(0,cameraInfo);
        Log.d(LOG_TAG,Integer.toString(cameraInfo.orientation));
/**/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_PHOTO){
            if(resultCode==RESULT_OK){
                Toast.makeText(this, uri.toString()+" created", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permitted = false;
        int marker = PackageManager.PERMISSION_GRANTED;
        for(int i:grantResults){marker+=i;}
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST:
                if(grantResults.length>0&&marker==PackageManager.PERMISSION_GRANTED){permitted=true;}
        }
    }

    @Override
    public void onClick(View view){
        if(permitted){
            if(view.getId()==R.id.photoButton){
                camera.takePicture(null,null,new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        try {
                            Matrix matrix=new Matrix();
                            Bitmap bitmap= BitmapFactory.decodeByteArray(data,0,data.length);
                            matrix.postRotate(90);
                            Bitmap rotatedBitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
                            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG,99,byteArrayOutputStream);
                            byte[] rotatedData=byteArrayOutputStream.toByteArray();
                            FileOutputStream fileOutputStream = new FileOutputStream(new File(directory.getPath() + "/" + "photo_" + System.currentTimeMillis() + ".jpg"));
                            //fileOutputStream.write(data);
                            fileOutputStream.write(rotatedData);
                            fileOutputStream.close();
                            camera.startPreview();
                        }catch (FileNotFoundException e){Log.d(LOG_TAG,"Directory not found");}
                        catch (IOException e){Log.d(LOG_TAG,"OnPictureTaken IOException");}
                    }
                });   //this stop preview, shutter=null - silent photo
            }
            else{
                Intent intent = new Intent(this, FileManager.class);
                startActivity(intent);
            }
        }else ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);
    }

    private void createDirectory() {
        directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), FOLDER_NAME);
        if (!directory.exists())
            if (directory.mkdirs()) Toast.makeText(this, R.string.directory_created, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        camera.stopPreview();
        Log.d(LOG_TAG,"onStop");
    }
}
