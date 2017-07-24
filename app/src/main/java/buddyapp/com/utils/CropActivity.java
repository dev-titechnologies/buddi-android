package buddyapp.com.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import com.naver.android.helloyako.imagecrop.view.ImageCropView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import buddyapp.com.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CropActivity extends AppCompatActivity {
    @BindView(R.id.imagecrop)
    ImageCropView imageCropView;


    @BindView(R.id.Save)
    Button save;

    @BindView(R.id.Cancel)
    Button cancel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        ButterKnife.bind(this);
String uri = getIntent().getStringExtra("url");
//        cropImageView.setMinFrameSizeInDp(100);
        imageCropView.setImageFilePath(uri);
        imageCropView.setAspectRatio(1,1);
        imageCropView.setGridInnerMode(ImageCropView.GRID_ON);
        imageCropView.setGridOuterMode(ImageCropView.GRID_ON);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!imageCropView.isChangingScale()) {
                    Bitmap b = imageCropView.getCroppedImage();
                    bitmapConvertToFile(b);
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            finish();
            }
        });

    }


    public File bitmapConvertToFile(Bitmap bitmap) {
        FileOutputStream fileOutputStream = null;
        File bitmapFile = null;
        try {
            File file =getOutputMediaFile();
            if (!file.exists()) {
                file.mkdir();
            }

            bitmapFile = new File(file, "IMG_" + (new SimpleDateFormat("yyyyMMddHHmmss")).format(Calendar.getInstance().getTime()) + ".jpg");
            fileOutputStream = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fileOutputStream);
            MediaScannerConnection.scanFile(this, new String[]{bitmapFile.getAbsolutePath()}, null, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {

                }

                @Override
                public void onScanCompleted(final String path, Uri uri) {



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(CropActivity.this,"Saving Image", Toast.LENGTH_LONG).show();

                            Intent i = new Intent();

                            i.putExtra("url",path);

                            setResult(222,i);


                            finish();
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                }
            }
        }

        return bitmapFile;
    }
    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Dither/Profile");


        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
//                //Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "TITECH_" + timeStamp + ".jpg");

        return mediaStorageDir;
    }



}
