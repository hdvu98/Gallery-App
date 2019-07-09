package quanghuong.nqh.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class Fragment_Image_Adapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Album> albumList;

    public Fragment_Image_Adapter(Context context, int layout, List<Album> albumList) {
        this.context = context;
        this.layout = layout;
        this.albumList = albumList;
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    public class ViewHolder{
        ImageView imgAvatar;
        TextView txtNameAlbum;
        ImageView imgCheck;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder.imgAvatar = (ImageView) convertView.findViewById(R.id.imgAvatarAlbum);
            holder.txtNameAlbum = (TextView) convertView.findViewById(R.id.txtNameAlbum);
            holder.imgCheck = (ImageView) convertView.findViewById(R.id.imgCheck);
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Album album = albumList.get(position);
        Glide.with(context)
                .load(album.getPath())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .into(holder.imgAvatar);

        holder.txtNameAlbum.setText(album.getNameAlbum() + " (" + album.getImgCount() + ")");
        if(album.isCheckbox())
        {
            holder.imgCheck.setVisibility(View.VISIBLE);
        }else{
            holder.imgCheck.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }
}
