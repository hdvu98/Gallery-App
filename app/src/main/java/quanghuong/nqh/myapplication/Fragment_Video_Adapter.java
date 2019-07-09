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

public class Fragment_Video_Adapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Album> folderVideoList;

    public Fragment_Video_Adapter(Context context, int layout, List<Album> folderVideoList) {
        this.context = context;
        this.layout = layout;
        this.folderVideoList = folderVideoList;
    }

    @Override
    public int getCount() {
        return folderVideoList.size();
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
        ImageView avatar;
        TextView txtNameFolder;
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
            holder.avatar = (ImageView) convertView.findViewById(R.id.imgAvatarFolder);
            holder.txtNameFolder = (TextView) convertView.findViewById(R.id.txtNameFolder);
            holder.imgCheck = (ImageView) convertView.findViewById(R.id.vdCheck);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        Album folder = folderVideoList.get(position);
        Glide.with(context)
                .load(folder.getPath())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .into(holder.avatar);
        holder.txtNameFolder.setText(folder.getNameAlbum() + " (" + folder.getImgCount() + ")");
        if(folder.isCheckbox())
        {
            holder.imgCheck.setVisibility(View.VISIBLE);
        }else{
            holder.imgCheck.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
}
