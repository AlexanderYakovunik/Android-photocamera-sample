package com.example.nn_yakovunik_av.photocamera;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.HashSet;

public class FileManager extends Activity {


    static HashSet<File> checkedFiles;
    private File[] directory_files, filesToOperate;


    GridView gvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_manager);
        gvMain = findViewById(R.id.gvMain);
        checkedFiles = new HashSet<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        filesUpdate();
    }

    public void onClickDelete(View view) {
        filesToOperate = checkedFiles.toArray(new File[checkedFiles.size()]);
        if(filesToOperate.length!=0) {
            for (File f:filesToOperate) {
                String title = f.toString();
                String[] tokens = title.split("/");
                title = tokens[tokens.length-1];
                String alertMessage = getString(R.string.file_deleted);
                showResult(this,title,alertMessage,f);
            }
        }
        else Toast.makeText(this, R.string.noFiles, Toast.LENGTH_LONG).show();
        checkedFiles.clear();
    }

    public void onClickApply(View view) {
        filesToOperate = checkedFiles.toArray(new File[checkedFiles.size()]);
        if(filesToOperate.length!=0) {
            for (File f:filesToOperate) {
                Toast.makeText(this, "Do something with "+f.toString(), Toast.LENGTH_LONG).show();
            }
        }
        else Toast.makeText(this, R.string.noFiles, Toast.LENGTH_LONG).show();
        filesToOperate = null;
        checkedFiles.clear();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    private File[] getDirectoryFiles(File file) {
        return file.listFiles();
    }

    private void filesUpdate(){
        directory_files = getDirectoryFiles(MainActivity.directory);
        GridViewAdapter gridViewAdapter = new GridViewAdapter(this,directory_files);
        gvMain.setAdapter(gridViewAdapter);
    }

    private void showResult(Context context, String title, String alertMessage, final File file) {
        if(file!=null) {
            FrameLayout frameLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.dialog,null);
            ImageView ivImg = frameLayout.findViewById(R.id.dialogImageView);
            Bitmap image = BitmapFactory.decodeFile(file.getPath());
            image = Bitmap.createScaledBitmap(image,225,225,false);
            ivImg.setImageBitmap(image);
            DialogInterface.OnClickListener alertDialogButtonsClickListener = new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean filesDeleted = file.delete();
                    if(filesDeleted) {
                        directory_files = null;
                        filesUpdate();
                    }
                }
            };
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setView(frameLayout)
                    .setPositiveButton(R.string.yes, alertDialogButtonsClickListener)
                    .setNegativeButton(R.string.no, null)
                    .show();

        }
        else {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(alertMessage)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    }

}
