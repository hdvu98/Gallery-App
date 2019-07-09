package quanghuong.nqh.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Image_view extends AppCompatActivity {

    ImageView imgView;
    ImageButton btnCrop, btnDelete, btnShare;
    private int vitri;
    Toolbar toolbar;
    LinearLayout linearLayout;
    boolean visible;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    ViewPager viewPager;
    imgHorizontalAdapter adapter;

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();

            if(ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();

                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_image_view);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(0, 0);
        initView();

        Intent intent = getIntent();
        vitri = intent.getIntExtra("imageview", -1);
//        Bitmap bitmap = BitmapFactory.decodeFile(Image_List.imageArrayList.get(vitri).getPath());
//        imgView.setImageBitmap(bitmap);
        Glide.with(getApplicationContext()).load(Image_List.imageArrayList.get(vitri).getPath())
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().placeholder(null).fitCenter())
                .into(imgView);

//        adapter = new imgHorizontalAdapter(Image_view.this,
//                R.layout.custom_image_horizontal_view,
//                Image_List.imageArrayList);
//        viewPager.setAdapter(adapter);
//
//        viewPager.setCurrentItem(vitri);
        toolbar.setTitle(Image_List.imageArrayList.get(vitri).getName());
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!visible)
                {
                    visible = true;
                    toolbar.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);
                }else{
                    visible = false;
                    toolbar.setVisibility(View.INVISIBLE);
                    linearLayout.setVisibility(View.INVISIBLE);
                }
            }
        });

        btnCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String date = simpleDateFormat.format(Calendar.getInstance().getTime());
                File file = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Edit photos");
                File path;
                if(file.exists()) {
                    path = new File(file, "edtPhoto_" + date + ".PNG");
                }else
                {
                    file.mkdirs();
                    path = new File(file, "edtPhoto_" + date + ".PNG");

                }
                String temp = path.getAbsolutePath();
                try {
                    OutputStream outputStream = new FileOutputStream(path);
                    Bitmap bitmap = BitmapFactory.decodeFile(Image_List.imageArrayList.get(vitri).getPath());
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                    outputStream.flush();
                    outputStream.close();

                    MediaScannerConnection.scanFile(Image_view.this, new String[]{temp},
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
                UCrop.of(Uri.fromFile(new File(Image_List.imageArrayList.get(vitri).getPath())), Uri.fromFile(path))
                        .withAspectRatio(16, 9)
                        .start(Image_view.this);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWarning();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(Image_view.this, "Share success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(Image_view.this, "Share cancel", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(Image_view.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                Picasso.with(getBaseContext())
                        .load(new File(Image_List.imageArrayList.get(vitri).getPath()))
                        .into(target);
            }
        });
    }


    private void dialogWarning() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Image_view.this, R.style.Theme_Dialog);
        dialog.setTitle("Warning!");
        dialog.setMessage("Are you sure want to delete this photo ?");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File (Image_List.imageArrayList.get(vitri).getPath());
                file.delete();
                MediaScannerConnection.scanFile(Image_view.this, new String[]{file.getAbsolutePath()},
                        null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        }
                );

                Toast.makeText(Image_view.this, "Deleted success!", Toast.LENGTH_SHORT).show();
                Image_List.imageArrayList.remove(vitri);
                finish();
            }
        });

        dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }




    private void initView()
    {
         imgView = (ImageView) findViewById(R.id.imgView);
        btnCrop = (ImageButton) findViewById(R.id.btnCrop);
        btnDelete = (ImageButton) findViewById(R.id.btnDelete);
        btnShare = (ImageButton) findViewById(R.id.btnShareImg);
        toolbar = (Toolbar) findViewById(R.id.toolbarImageView);
        linearLayout = (LinearLayout) findViewById(R.id.llTool);
        setSupportActionBar(toolbar);
        visible = true;
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

       // viewPager = (ViewPager) findViewById(R.id.vpImage);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if(resultUri != null) {
                Toast.makeText(Image_view.this, "Crop image success!", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
