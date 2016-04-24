package com.example.yesid.json_firebase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

/**
 * Created by Yesid on 23/04/2016.
 */
public class DetailActivity extends AppCompatActivity{
    private DataEntry mDataEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        mDataEntry = (DataEntry) i.getSerializableExtra("data");

        TextView nombre = (TextView) findViewById(R.id.textViewNombre);
        TextView apellido = (TextView) findViewById(R.id.textViewApellido);
        TextView genero = (TextView) findViewById(R.id.textViewGenero);

        nombre.setText(mDataEntry.getFirstName());
        apellido.setText(mDataEntry.getLastName());
        genero.setText(mDataEntry.getGender());

        new DownloadImageTask((ImageView) findViewById(R.id.imageView)).execute(mDataEntry.getPicture());

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
