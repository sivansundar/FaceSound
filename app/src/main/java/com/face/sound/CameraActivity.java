package com.face.sound;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CameraActivity extends AppCompatActivity {

    byte[] imageByteArray;
    @BindView(R.id.imageView)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);

        imageByteArray = getIntent().getByteArrayExtra("img_byte");

        Drawable image = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length));

        imageView.setImageDrawable(image);

    }
}
