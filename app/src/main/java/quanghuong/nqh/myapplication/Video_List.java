package quanghuong.nqh.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Video_List extends AppCompatActivity {
    GridView gridView;
    public static ArrayList<Image> videoList;
    Videos_adapter adapter;
    private SharedPreferences sharedPreferences;
    String Sort;
    String Order;
    String folder_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video__list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init();

        Intent intent = getIntent();
        folder_name = intent.getStringExtra("folder_name");
        getSupportActionBar().setTitle(folder_name);

        getVideosFromFolder(folder_name, checkConditionSort(Sort, Order));

        adapter = new Videos_adapter(Video_List.this, R.layout.custom_videos_layout, videoList);
        gridView.setAdapter(adapter);
        gridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            int nr;
            ArrayList<String> positionList = new ArrayList<>();
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    videoList.get(position).setCheck(true);
                    adapter.notifyDataSetChanged();
                    nr++;
                } else {
                    videoList.get(position).setCheck(false);
                    adapter.notifyDataSetChanged();
                    nr--;
                }
                mode.setTitle(nr + "");
            }

            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                MenuInflater menuInflater = getMenuInflater();
                menuInflater.inflate(R.menu.action_mode_toolbar, menu);
                MenuItem item = menu.findItem(R.id.menuEdit);
                item.setVisible(false);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menuDelete:
                        for(int i = 0;i<videoList.size();i++)
                        {
                            if(videoList.get(i).isCheck())
                            {
                                positionList.add(videoList.get(i).getPath());
                            }
                        }
                        dialogWarning(positionList);
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                nr = 0;
                for(int i=0; i<videoList.size();i++)
                {
                    if(videoList.get(i).isCheck())
                        videoList.get(i).setCheck(false);
                }

            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(Video_List.this, PlayVideo.class);
                intent1.putExtra("video_position", position);
                startActivity(intent1);
            }
        });
    }

    private void Init() {
        gridView = (GridView) findViewById(R.id.gridVideo);
        videoList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("Sort", MODE_PRIVATE);
        Sort = sharedPreferences.getString("Sort", "Name");
        Order = sharedPreferences.getString("Order", "Ascending");
    }

    private void dialogWarning(final List<String> list) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Theme_Dialog);
        dialog.setTitle("Warning!");
        dialog.setMessage("Are you sure want to delete this album ?");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file;
                for(int i=0;i<list.size();i++)
                {
                    file = new File(list.get(i));
                    file.delete();

                    MediaScannerConnection.scanFile(Video_List.this, new String[]{file.toString()},
                            null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                }
                            }
                    );
                }

                Toast.makeText(Video_List.this, "Deleted success!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                for(int i=0;i<list.size();i++)
                {
                    for(int j=0;j<videoList.size();j++)
                    {
                        if(videoList.get(j).getPath().equals(list.get(i)))
                        {
                            videoList.remove(j);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.image_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
            case R.id.listSort:
                dialogSort();
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogSort() {
        final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_sort_image);

        RadioGroup groupSort = (RadioGroup) dialog.findViewById(R.id.rgSortImg);
        RadioGroup groupOrder = (RadioGroup) dialog.findViewById(R.id.rgOrderImg);
        Button btnOk = (Button) dialog.findViewById(R.id.btnSortOKImg);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnSortCancelImg);

        switch (Sort)
        {
            case "Name":
                groupSort.check(R.id.rbtnNameImg);
                break;
            case "Date":
                groupSort.check(R.id.rbtnDateImg);
                break;
            case "Size":
                groupSort.check(R.id.rbtnSizeImg);
                break;
            default:
                break;
        }

        switch (Order)
        {
            case "Ascending":
                groupOrder.check(R.id.rbtnAscendingImg);
                break;
            case "Descending":
                groupOrder.check(R.id.rbtnDescendingImg);
                break;
            default:
                break;
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Sort", Sort);
                editor.putString("Order", Order);
                editor.apply();

                sortImage(Sort, Order);
                dialog.dismiss();
            }
        });
        groupSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.rbtnNameImg:
                        Sort = "Name";
                        break;
                    case R.id.rbtnDateImg:
                        Sort = "Date";
                        break;
                    case R.id.rbtnSizeImg:
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
                    case R.id.rbtnAscendingImg:
                        Order = "Ascending";
                        break;
                    case R.id.rbtnDescendingImg:
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


    private void sortImage(String sort, String order)
    {
        videoList.clear();
        getVideosFromFolder(folder_name, checkConditionSort(sort,order));
        adapter.notifyDataSetChanged();
    }

    public String checkConditionSort(String sort, String order)
    {
        String sortOrder = null;
        if(order.equals("Ascending") )
        {
            switch (sort)
            {
                case "Name":
                    sortOrder = "_display_name COLLATE NOCASE ASC";
                    break;
                case "Date":
                    sortOrder = "datetaken ASC";
                    break;
                case "Size":
                    sortOrder = "_size ASC";
                    break;
                default:
                    break;
            }
        }else if(order.equals("Descending") )
        {
            switch (sort)
            {
                case "Name":
                    sortOrder = "_display_name COLLATE NOCASE DESC";
                    break;
                case "Date":
                    sortOrder = "datetaken DESC";
                    break;
                case "Size":
                    sortOrder = "_size DESC";
                    break;
                default:
                    break;
            }
        }
        return sortOrder;
    }
    private void getVideosFromFolder(String album, String sortby)
    {
        Uri Video_Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        String[] selectionArg = {"%" + album + "%"};
        String selection = MediaStore.Video.Media.DATA + " LIKE ?";
        String  [] projectionBucket = {MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Media.SIZE};

        Cursor cursor = contentResolver.query(Video_Uri, projectionBucket, selection, selectionArg, sortby);
        while (cursor.moveToNext())
        {
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            File filepath = new File(path);
            long fileSize = filepath.length();

            videoList.add(new Image(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_TAKEN)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)),
                    fileSize));
        }

    }
}
