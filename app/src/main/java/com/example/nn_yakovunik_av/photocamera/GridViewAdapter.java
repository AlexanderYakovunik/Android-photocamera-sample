package com.example.nn_yakovunik_av.photocamera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.io.File;


class GridViewAdapter extends BaseAdapter {
    private String LOD_TAG = "Adapter";
    private Context context;
    private File[] directory_files;

    GridViewAdapter(Context context, File[] directory_files){
        this.context = context;
        this.directory_files = directory_files;
    }

    @Override
    public int getCount() {
        return directory_files.length;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.grid_item, null);
        }

        ImageView ivImg = convertView.findViewById(R.id.ivImg);
        final CheckBox cbShare = convertView.findViewById(R.id.cbShare);
        cbShare.setChecked(false);

        String filePath = directory_files[position].getPath();
        Bitmap image = BitmapFactory.decodeFile(filePath);
        image = Bitmap.createScaledBitmap(image,225,225,false);

        ivImg.setImageBitmap(image);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cbShare.isChecked()) {
                    cbShare.setChecked(false);
                    FileManager.checkedFiles.remove(directory_files[position]);
                }
                else {
                    cbShare.setChecked(true);
                    FileManager.checkedFiles.add(directory_files[position]);

                }
            }
        });

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        Log.d(LOD_TAG,String.valueOf(position));
        return position;
    }

    @Override
    public Object getItem(int position) {
        Log.d(LOD_TAG,directory_files[position].toString());
        return directory_files[position];
    }
}
