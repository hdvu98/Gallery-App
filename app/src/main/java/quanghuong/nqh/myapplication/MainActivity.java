package quanghuong.nqh.myapplication;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;

import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.text.Editable;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.facebook.CallbackManager;

import com.facebook.FacebookSdk;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    int REQUEST_CODE_STORAGE = 567;
    int REQUEST_CODE_CAMERA = 123;
    public static ArrayList<Image> imageList;
    FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
    String Sort;
    String Order;
    public static SharedPreferences sharedPreferences;
    public static SharedPreferences sharedPreferencesPass;
    public static String virtualPass;

    public void InitView()
    {
        viewPager = (ViewPager) findViewById(R.id.viewPagerAlbum);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.tabAlbum);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setPageTransformer(true, new DefaultTransformer());
        imageList = new ArrayList<>();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Sort = sharedPreferences.getString("Sort", "Name");
        Order = sharedPreferences.getString("Order", "Ascending");
        virtualPass = sharedPreferencesPass.getString("Password", "");

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        sharedPreferencesPass = getSharedPreferences("passss", MODE_PRIVATE);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            InitView();

        }else if(requestCode == REQUEST_CODE_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {

        } else {
            Toast.makeText(MainActivity.this, "Permission denied !", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // item menu selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menuNewAlbum:

                int pos = viewPager.getCurrentItem();
                switch (pos)
                {
                    case 0:
                        dialogNewAlbum();
                        break;
                    case 1:
                        Toast.makeText(MainActivity.this, "Album is only for images!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
            case R.id.menuSortBy:
                dialogSortby();
                break;
            case R.id.menuCamera:
                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                Intent openCameraApp = new Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
                startActivity(openCameraApp);
                break;
            case R.id.menuShare:
                shareFunction();
                break;
            case R.id.menuAboutUs:
                Intent intent = new Intent(MainActivity.this, AboutUS.class);
                startActivity(intent);
                break;
            case R.id.menuSecu:
                DialogSecurity();
                break;
            default:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void DialogSecurity() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_security);

        final Switch switchLock = (Switch) dialog.findViewById(R.id.switchLock);
        TextView txtChangeLock = (TextView) dialog.findViewById(R.id.txtChangeLock);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnSecCancel);

        Boolean switchchange = sharedPreferencesPass.getBoolean("Switch", false);
        if(switchchange)
        {
            switchLock.setText("Lock on");
            switchLock.setChecked(true);
        }else {
            switchLock.setText("Lock off");
            switchLock.setChecked(false);
        }

        switchLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    switchLock.setText("Lock on");
                    dialogCreatePassword();
                }else{
                    switchLock.setText("Lock off");
                    String pass = sharedPreferencesPass.getString("Password", "");
                    if(!pass.equals(""))
                    {
                        dialogEnterPassword();
                        boolean check = sharedPreferencesPass.getBoolean("Switch", false);
                        if(check)
                        {
                            switchLock.setText("Lock on");
                            switchLock.setChecked(true);
                        }else {
                            switchLock.setText("Lock off");
                            switchLock.setChecked(false);
                        }
                    }
                }

            }
        });

        txtChangeLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchLock.isChecked())
                {
                    dialogChangePassword();
                }else{
                    Toast.makeText(MainActivity.this, "Security lock off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void dialogEnterPassword() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_enter_password);

        final EditText edtEnter = (EditText) dialog.findViewById(R.id.edtPassword);
        Button btnOk = (Button) dialog.findViewById(R.id.btnPassOK);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnPassCancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtEnter.getText().toString().equals(""))
                {
                    Toast.makeText(MainActivity.this, "Password can't be empty", Toast.LENGTH_SHORT).show();
                }else{
                    String pass = sharedPreferencesPass.getString("Password", "");
                    if(edtEnter.getText().toString().equals(pass))
                    {
                        SharedPreferences.Editor editor = sharedPreferencesPass.edit();
                        editor.remove("Password");
                        editor.remove("Switch");
                        editor.putBoolean("Switch", false);
                        editor.apply();
                        dialog.dismiss();
                        virtualPass = "";
                    }else {
                        Toast.makeText(MainActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void dialogChangePassword() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_password);

        final EditText edtOldPass = (EditText) dialog.findViewById(R.id.edtOldPass);
        final EditText edtNewPass = (EditText) dialog.findViewById(R.id.edtEnterNewPass);
        final EditText edtConfirm = (EditText) dialog.findViewById(R.id.edtConfirmNewPass);
        Button btnOK = (Button) dialog.findViewById(R.id.btnChangePassOK);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnChangePassCancel);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = sharedPreferencesPass.getString("Password", "");
                if(!edtOldPass.getText().toString().equals(oldPass))
                {
                    Toast.makeText(MainActivity.this, "Old password incorrect", Toast.LENGTH_SHORT).show();
                }else{
                    if(!edtConfirm.getText().toString().equals(edtNewPass.getText().toString()))
                    {
                        Toast.makeText(MainActivity.this, "confirm password does not match the password", Toast.LENGTH_SHORT).show();
                    }else{
                        SharedPreferences.Editor editor = sharedPreferencesPass.edit();
                        editor.remove("Password");
                        editor.putString("Password", edtNewPass.getText().toString());
                        editor.apply();
                        virtualPass = edtNewPass.getText().toString();
                        Toast.makeText(MainActivity.this, "Change password success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        virtualPass = edtNewPass.getText().toString();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void dialogCreatePassword() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_password);

        final EditText edtEnter = (EditText) dialog.findViewById(R.id.edtEnterPass);
        final EditText edtConfirm = (EditText) dialog.findViewById(R.id.edtConfirmPass);
        Button btnOK = (Button) dialog.findViewById(R.id.btnEnterPassOK);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnEnterPassCancel);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtEnter.getText().toString().equals(""))
                {
                    Toast.makeText(MainActivity.this, "Password can't be empty", Toast.LENGTH_SHORT).show();
                }else{
                    if(!edtConfirm.getText().toString().equals(edtEnter.getText().toString()))
                    {
                        Toast.makeText(MainActivity.this, "confirm password does not match the password", Toast.LENGTH_SHORT).show();
                    }else{
                        SharedPreferences.Editor editor = sharedPreferencesPass.edit();
                        editor.putString("Password", edtEnter.getText().toString());
                        editor.putBoolean("Switch", true);
                        editor.apply();
                        Toast.makeText(MainActivity.this, "Create password success ", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        virtualPass = edtEnter.getText().toString();
                    }
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    //thuc hien chuc nang chia se thong tin ung dung len facebook
    private void shareFunction() {
        CallbackManager callbackManager = CallbackManager.Factory.create();
        ShareDialog shareDialog = new ShareDialog(this);
        // chia se link
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setQuote("this is a great gallery app")
                .setContentUrl(Uri.parse("https://play.google.com/store/apps"))
                .build();
        if(shareDialog.canShow(ShareLinkContent.class))
        {
            shareDialog.show(linkContent);
        }
    }


    // Dialog tao album moi
    private void dialogNewAlbum() {


        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_new_album);

        final EditText edtName = (EditText) dialog.findViewById(R.id.edtAlbumName);
        Button btnOK = (Button) dialog.findViewById(R.id.btnNewAlbumOK);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnNewAlbumCancel);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtName.getText().toString().equals(""))
                {
                    Toast.makeText(MainActivity.this, "Name can be empty !", Toast.LENGTH_SHORT).show();
                }else
                {
                    int pos = viewPager.getCurrentItem();
                    Fragment activeFragment = adapter.getItem(pos);
                    ((Fragment_Image)activeFragment).AddAlbum(edtName.getText().toString());
                    dialog.dismiss();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    // Dialog sap xep album
    private void dialogSortby() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_sortby);

        RadioGroup groupSort = (RadioGroup) dialog.findViewById(R.id.rgSort);
        RadioGroup groupOrder = (RadioGroup) dialog.findViewById(R.id.rgOrder);
        Button btnOk = (Button) dialog.findViewById(R.id.btnSortOK);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnSortCancel);

        switch (Sort)
        {
            case "Name":
                groupSort.check(R.id.rbtnName);
                break;
            case "Date":
                groupSort.check(R.id.rbtnDate);
                break;
            case "Size":
                groupSort.check(R.id.rbtnSize);
                break;
            default:
                break;
        }

        switch (Order)
        {
            case "Ascending":
                groupOrder.check(R.id.rbtnAscending);
                break;
            case "Descending":
                groupOrder.check(R.id.rbtnDescending);
                break;
            default:
                break;
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment image = adapter.getItem(0);
                Fragment video = adapter.getItem(1);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Sort", Sort);
                editor.putString("Order", Order);
                editor.apply();

                ((Fragment_Image)image).SortBy(Sort, Order);
                ((Fragment_Video)video).SortBy(Sort, Order);
                dialog.dismiss();
            }
        });

        groupSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.rbtnName:
                        Sort = "Name";
                        break;
                    case R.id.rbtnDate:
                        Sort = "Date";
                        break;
                    case R.id.rbtnSize:
                        Sort = "Size";
                        break;
                    default:
                        break;

                }
            }
        });

        groupOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.rbtnAscending:
                        Order = "Ascending";
                        break;
                    case R.id.rbtnDescending:
                        Order = "Descending";
                        break;
                    default:
                        break;
                }
            }
        });




        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void DialogAboutUs() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_about_us);

        Button btnOk = (Button) dialog.findViewById(R.id.btnAboutUs);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }
}
