package quanghuong.nqh.myapplication;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class ImageHorizontalView extends AppCompatActivity {
    ViewPager viewPager;
    imgHorizontalAdapter adapter;
    RelativeLayout relativeLayout;
    ImageButton btnCrop, btnDelete, btnShare;
    public static Toolbar toolbar;
    boolean visible;
    int position;
    LinearLayout linearLayout;
    public static BottomNavigationView mainNav;
    View view;
    TextView txtModifiedDate;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy HH:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getWindow().getDecorView();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_horizontal_view);


        Init();
        final Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        adapter = new imgHorizontalAdapter(ImageHorizontalView.this,
                R.layout.custom_image_horizontal_view,
                Image_List.imageArrayList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);


        mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_crop:
                        crop();
                        break;
                    case R.id.nav_delete:
                        dialogWarning();
                        break;
                    case R.id.nav_share:
                        sharePhoto();
                        break;
                }
                return false;
            }

        });
    }

    private void Init() {
        viewPager = (ViewPager) findViewById(R.id.vpImage);
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        toolbar = (Toolbar) findViewById(R.id.toolbarImageView);
        mainNav = findViewById(R.id.nav_bottom);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.full_screen_photo_option_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        switch (id){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_Details:
                String path=Image_List.imageArrayList.get(viewPager.getCurrentItem()).getPath();
                File file = new File(path);
                final DecimalFormat dmf = new DecimalFormat("#.##"); // Tạo format cho size
                final double length = file.length();    // Lấy độ dài file
                String sLength;

                if (length > 1024 * 1024) {
                    sLength = dmf.format(length / (1024 * 1024)) + " MB";
                } else {
                    if (length > 1024) {
                        sLength = dmf.format(length / 1024) + " KB";
                    } else {
                        sLength = dmf.format(length) + " B";
                    }
                }
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(path);
                    String Details = getExif(exif);

                    Details = "Date: " + sdf.format(file.lastModified()) +
                            "\n\nSize: " + sLength +
                            "\n\nFile path: " + path +
                            Details;

                    TextView title = new TextView(getApplicationContext());
                    title.setPadding(60,50,50,50);
                    title.setText("Photo Details");
                    title.setTextSize(23);
                    title.setTypeface(null, Typeface.BOLD);
                    AlertDialog.Builder dialog= new AlertDialog.Builder(ImageHorizontalView.this,R.style.Theme_AppCompat_Light_Dialog);

                    dialog.setCustomTitle(title);
                    dialog.setMessage(Details);
                    dialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();


                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_slideshow:
                Intent newIntentForSlideShowActivity = new Intent(ImageHorizontalView.this, SlideShowActivity.class);
                newIntentForSlideShowActivity.putExtra("id", position);
                startActivity(newIntentForSlideShowActivity);
                break;
            case R.id.action_setBackground:
                WallpaperManager bg = WallpaperManager.getInstance(getApplicationContext());
                Bitmap bitmap = BitmapFactory.decodeFile(Image_List.imageArrayList.get(viewPager.getCurrentItem()).getPath());

                if(bitmap!=null)
                {
                    try {
                        bg.setBitmap(bitmap, null, false,
                                WallpaperManager.FLAG_SYSTEM | WallpaperManager.FLAG_LOCK);
                        Toast.makeText(getApplicationContext(), "Settings have been successfully applied!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;

        }
        return true;

    }

    private String getExif(ExifInterface exif) {
        String details = "";

        if (exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0) == 0) {
            return details;
        } else {
            details += "\n\nResolution: " + exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH) +
                    "x" + exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);

            if (exif.getAttribute(ExifInterface.TAG_MODEL) == null) {
                return details;
            }
        }

        final DecimalFormat apertureFormat = new DecimalFormat("#.#");
        String aperture = exif.getAttribute(ExifInterface.TAG_F_NUMBER);
        if (aperture != null) {
            Double aperture_double = Double.parseDouble(aperture);
            apertureFormat.format(aperture_double);
            details += "\n\nAperture: f/" + aperture_double + "\n\n";
        } else {
            details += "\n\nAperture: unknown\n\n";
        }

        details += "Model: " + exif.getAttribute(ExifInterface.TAG_MODEL)+"\n\n";

        String ExposureTime = exif.getAttribute(ExifInterface.TAG_EXPOSURE_TIME);
        Double ExposureTime_double = Double.parseDouble(ExposureTime);
        Double Denominator = 1 / ExposureTime_double;

        ExposureTime = 1 + "/" + String.format("%.0f", Denominator);

        details += "Exposure Time: " + ExposureTime + "s\n\n";

        if (exif.getAttributeInt(ExifInterface.TAG_FLASH,0) ==0 ) {
            details += "Flash: Off\n\n";
        } else {
            details += "Flash: On\n\n";
        }
        details += "Focal Length: " + exif.getAttributeDouble(ExifInterface.TAG_FOCAL_LENGTH, 0) + "mm\n\n";
        details += "ISO: " + exif.getAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS) + "\n\n";


        return details;
    }


    private void dialogWarning() {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ImageHorizontalView.this, R.style.Theme_Dialog);
        dialog.setTitle("Warning!");
        dialog.setMessage("Are you sure want to delete this photo ?");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File (Image_List.imageArrayList.get(viewPager.getCurrentItem()).getPath());
                file.delete();
                MediaScannerConnection.scanFile(ImageHorizontalView.this, new String[]{file.getAbsolutePath()},
                        null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        }
                );

                Toast.makeText(ImageHorizontalView.this, "Deleted success!", Toast.LENGTH_SHORT).show();
                Image_List.imageArrayList.remove(viewPager.getCurrentItem());
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


    void crop()
    {
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
                    String t=Image_List.imageArrayList.get(position).getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(Image_List.imageArrayList.get(position).getPath());
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                    outputStream.flush();
                    outputStream.close();

                    MediaScannerConnection.scanFile(ImageHorizontalView.this, new String[]{temp},
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
                UCrop.of(Uri.fromFile(new File(Image_List.imageArrayList.get(position).getPath())), Uri.fromFile(path))
                        .withAspectRatio(16, 9)
                        .start(ImageHorizontalView.this);
    }

    private void sharePhoto() {
        File file = new File(Image_List.imageArrayList.get(viewPager.getCurrentItem()).getPath());
        Uri uri;
        try {
            if (Build.VERSION.SDK_INT >= 24)
                uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".my.package.name.provider", file);
            else uri = Uri.fromFile(file);
        } catch (Exception e) {
            uri = Uri.fromFile(file);
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Share"));
    }
}
