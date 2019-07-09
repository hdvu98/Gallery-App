package quanghuong.nqh.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class Images_adapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<Image> imageList;

    public Images_adapter(Context context, int layout, List<Image> imageList) {
        this.context = context;
        this.layout = layout;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
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
        ImageView imageView;
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
            holder.imageView = (ImageView) convertView.findViewById(R.id.imgImage);
            holder.imgCheck = (ImageView) convertView.findViewById(R.id.photoCheck);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Image image = imageList.get(position);

        Glide.with(context)
                .load(image.getPath())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                .into(holder.imageView);
        if(image.isCheck())
        {
            holder.imgCheck.setVisibility(View.VISIBLE);
        }else{
            holder.imgCheck.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }
}
