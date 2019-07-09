package quanghuong.nqh.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
public class imgHorizontalAdapter extends PagerAdapter {
    private List<Image> imageList;
    private Context context;
    private int layout;

    public static boolean flag = true;
    private android.widget.Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;


    public imgHorizontalAdapter(Context context, int layout, List<Image> imageList) {
        this.imageList = imageList;
        this.context = context;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layout,container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.imgHorizontal);
        Image image = imageList.get(position);
        Glide.with(context)
                .load(image.getPath())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .into(imageView);
        container.addView(view);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePhoto();
            }
        });

        return view;
    }
    private void handlePhoto() {
        if (flag == true) { 
            showToolbar((Activity) context, ImageHorizontalView.mainNav, ImageHorizontalView.toolbar);
            flag = false;
        } else {
            hideToolbar((Activity) context, ImageHorizontalView.mainNav, ImageHorizontalView.toolbar);
            flag = true;
        }

    }

    private void hideToolbar(Activity context, BottomNavigationView mainNav, Toolbar toolbar) {
        mainNav.animate().translationY(mainNav.getHeight() + 100).setInterpolator(new AccelerateInterpolator()).start();
        toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
        View decorView = context.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void showToolbar(Activity context, BottomNavigationView mainNav, Toolbar toolbar) {
        View decorView = context.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        mainNav.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((LinearLayout) object);
    }
    @Override
    public int getItemPosition(Object object){
        return POSITION_NONE;
    }



}
