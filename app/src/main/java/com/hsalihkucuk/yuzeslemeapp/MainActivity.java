package com.hsalihkucuk.yuzeslemeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    Button cameraButton;

    private final static int REQUEST_IMAGE_CAPTURE = 124;
    FirebaseVisionImage image;
    FirebaseVisionFaceDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        cameraButton = findViewById(R.id.camera_button);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) 
                {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
                else Toast.makeText(MainActivity.this,"Bir şeyler ters gitti!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extra = data.getExtras();
            Bitmap bitmap = (Bitmap)extra.get("data");
            detectFace(bitmap);
        }
    }

    private void detectFace(Bitmap bitmap)
    {
        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();
        try {
            image = FirebaseVisionImage.fromBitmap(bitmap);
            detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace> >() 
        {
            @Override
            public void onSuccess(List<FirebaseVisionFace>firebaseVisionFaces)
            {
                String resultText = "";
                int i = 1;
                for (FirebaseVisionFace face : firebaseVisionFaces) {
                    resultText = resultText
                        .concat("\n" + i + ". Yüz:")
                        .concat("\nGülümseme: " + face.getSmilingProbability() * 100 + "%")
                        .concat("\nSol göz açık: " + face.getLeftEyeOpenProbability() * 100 + "%")
                        .concat("\nSağ göz açık: " + face.getRightEyeOpenProbability() * 100 + "%");
                    i++;
                }

                if (firebaseVisionFaces.size() == 0) {
                    Toast.makeText(MainActivity.this,"   Yüz algılanamadı!\nLütfen tekrar deneyin.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putString( YuzAlgilama.RESULT_TEXT, resultText);
                    DialogFragment resultDialog = new sonucDialog();
                    resultDialog.setArguments(bundle);
                    resultDialog.setCancelable(true);
                    resultDialog.show(getSupportFragmentManager(), YuzAlgilama.RESULT_DIALOG);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(MainActivity.this,"Oops, Bir şeyler ters gitti!",Toast.LENGTH_SHORT).show();
            }
        });
    }
}