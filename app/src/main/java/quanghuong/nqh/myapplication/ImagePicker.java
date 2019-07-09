package quanghuong.nqh.myapplication;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ImagePicker extends AppCompatActivity {
    Button btnPickPhotos;
    TextView txtInfo;
    String albumName="";
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);
        albumName=getIntent().getStringExtra("ALBUM_NAME");
        getSupportActionBar().setTitle(albumName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        txtInfo=(TextView)findViewById(R.id.pickerinfo);
        btnPickPhotos=(Button)findViewById(R.id.btnPickPhotos);
        btnPickPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Choose Photos"),1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1&&resultCode==RESULT_OK)
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String date = simpleDateFormat.format(Calendar.getInstance().getTime());
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + albumName);
            File path;


            ClipData clipData=data.getClipData();
            String info = "";
            if(clipData!=null)
            {
                for(int i=0;i<clipData.getItemCount();i++)
                {
                    ClipData.Item item=clipData.getItemAt(i);
                    Uri uri=item.getUri();
                    String fileName=albumName+"_img_"+i+".png";

                    if(file.exists()) {
                        path = new File(file, "img"+i +"_"+ date + ".PNG");
                    }else
                    {
                        file.mkdirs();
                        path = new File(file, "img"+i+"_" + date + ".PNG");

                    }
                    String temp = path.getAbsolutePath();
                    try {
                        OutputStream outputStream = new FileOutputStream(path);

                        //Bitmap bitmap = BitmapFactory.decodeFile();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                        outputStream.flush();
                        outputStream.close();

                        MediaScannerConnection.scanFile(ImagePicker.this, new String[]{temp},
                                null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {

                                    }
                                }
                        );

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
            else {
                if (data.getData() != null){
                    Uri uri=data.getData();
                    String fileName=albumName+"_img"+".png";

                    if(file.exists()) {
                        path = new File(file, "img"+"_"+ date + ".PNG");
                    }else
                    {
                        file.mkdirs();
                        path = new File(file, "img"+"_" + date + ".PNG");

                    }
                    String temp = path.getAbsolutePath();
                    try {
                        OutputStream outputStream = new FileOutputStream(path);

                        //Bitmap bitmap = BitmapFactory.decodeFile();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                        outputStream.flush();
                        outputStream.close();

                        MediaScannerConnection.scanFile(ImagePicker.this, new String[]{temp},
                                null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {

                                    }
                                }
                        );

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Intent intent=new Intent(ImagePicker.this,MainActivity.class);
            startActivity(intent);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
