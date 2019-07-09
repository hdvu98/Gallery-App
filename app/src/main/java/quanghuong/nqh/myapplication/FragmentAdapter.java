package quanghuong.nqh.myapplication;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.Switch;

public class FragmentAdapter extends FragmentPagerAdapter {
    private String listTab[] = {"PHOTOS", "VIDEOS"};
    private Fragment_Image imageFragment;
    private Fragment_Video videoFragment;

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
        imageFragment = new Fragment_Image();
        videoFragment = new Fragment_Video();
    }

    @Override
    public Fragment getItem(int position) {

        switch(position)
        {
            case 0:
                return imageFragment;
            case 1:
                return videoFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return listTab.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return listTab[position];
    }
}
