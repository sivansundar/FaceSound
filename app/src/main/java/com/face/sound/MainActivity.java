package com.face.sound;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.camerakit.CameraKitView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.cameraView)
    CameraKitView cameraView;
    @BindView(R.id.capture_button)
    FloatingActionButton captureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FirebaseApp.initializeApp(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        cameraView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        cameraView.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @OnClick(R.id.capture_button)
    public void onClick() {

        cameraView.captureImage(new CameraKitView.ImageCallback() {
            @Override
            public void onImage(CameraKitView cameraKitView, byte[] bytes) {
                /*Intent intent = new Intent(MainActivity.this, CameraActivity.class );
                intent.putExtra("img_byte", bytes);
                startActivity(intent);*/

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);


                FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                        .setWidth(1000)   // 480x360 is typically sufficient for
                        .setHeight(1400)  // image recognition
                        .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                        .build();

                FirebaseVisionFaceDetectorOptions options =
                        new FirebaseVisionFaceDetectorOptions.Builder()
                                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                                .enableTracking()
                                .build();

                FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(options);

                FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);

                Task<List<FirebaseVisionFace>> result = detector.detectInImage(firebaseVisionImage)
                        .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                                for (FirebaseVisionFace face : firebaseVisionFaces) {
                                    Log.d(TAG, "****************************");
                                    Log.d(TAG, "face ["+face+"]");
                                    Log.d(TAG, "Smiling Prob ["+face.getSmilingProbability()+"]");
                                    Log.d(TAG, "Left eye open ["+face.getLeftEyeOpenProbability()+"]");
                                    Log.d(TAG, "Right eye open ["+face.getRightEyeOpenProbability()+"]");
                                }

                                Log.d(TAG, "onSuccess: SIZE : " + firebaseVisionFaces.size());
                            }
                        })
                        
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }
}
