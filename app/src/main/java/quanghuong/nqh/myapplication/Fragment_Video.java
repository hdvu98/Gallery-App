package quanghuong.nqh.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.INotificationSideChannel;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ForwardingListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Fragment_Video extends Fragment {
    private Toolbar toolbarVideo;
    GridView gridView;
    Fragment_Video_Adapter adapterVideo;
    ArrayList<Album> folderArrayList;
    View view;
    TextView txtNumOfFolder;
    TextView txtNumOfVideo;
    String BUCKET_ORDER_BY;
    String Sort;
    String Order;

    public void Init()
    {

        gridView = (GridView) view.findViewById(R.id.gridviewVideo);
        txtNumOfFolder = (TextView) view.findViewById(R.id.txtNumberOfFolders);
        txtNumOfVideo = (TextView) view.findViewById(R.id.txtNumberOfVideos);
        folderArrayList = new ArrayList<>();

        Sort = MainActivity.sharedPreferences.getString("Sort", "Name");
        Order = MainActivity.sharedPreferences.getString("Order", "Ascending");
        BUCKET_ORDER_BY = checkConditionSort(Sort, Order);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_video_layout, container, false);
        Init();

        getAllShownVideosPath(BUCKET_ORDER_BY);
        adapterVideo = new Fragment_Video_Adapter(getActivity(),R.layout.custom_fragment_video_layout, folderArrayList);

        gridView.setAdapter(adapterVideo);
        gridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            private int nr = 0;
            MenuItem item;
            ArrayList<String> positionList = new ArrayList<>();
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    folderArrayList.get(position).setCheckbox(true);
                    adapterVideo.notifyDataSetChanged();
                    nr++;
                } else {
                    folderArrayList.get(position).setCheckbox(false);
                    adapterVideo.notifyDataSetChanged();
                    nr--;
                }
                mode.setTitle(nr + "");
                if(nr > 1)
                {
                    item.setVisible(false);
                }else
                {
                    item.setVisible(true);
                }

            }

            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                MenuInflater menuInflater = getActivity().getMenuInflater();
                menuInflater.inflate(R.menu.action_mode_toolbar, menu);
                item = menu.findItem(R.id.menuEdit);
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
                        for(int i = 0;i<folderArrayList.size();i++)
                        {
                            if(folderArrayList.get(i).isCheckbox())
                            {
                                positionList.add(folderArrayList.get(i).getNameAlbum());
                            }
                        }
                        dialogWarning(positionList);
                        mode.finish();
                        return true;
                    case R.id.menuEdit:
                        String path = null;
                        for(int i = 0;i<folderArrayList.size();i++)
                        {
                            if(folderArrayList.get(i).isCheckbox())
                            {
                                path = folderArrayList.get(i).getNameAlbum();
                            }
                        }
                        dialogEditName(path);
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                nr = 0;
                for(int i=0; i<folderArrayList.size();i++)
                {
                    if(folderArrayList.get(i).isCheckbox())
                        folderArrayList.get(i).setCheckbox(false);
                }

            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pass = MainActivity.sharedPreferencesPass.getString("Password", "");
                if(pass.equals(""))
                {
                    Intent intent = new Intent(getActivity(), Video_List.class);
                    intent.putExtra("folder_name", folderArrayList.get(position).getNameAlbum());
                    startActivity(intent);
                }else{
                    dialogEnterPass(position);
                }
            }
        });

        return view;
    }

    private void dialogEnterPass(final int position) {
        final Dialog dialog = new Dialog(getActivity(), R.style.Theme_Dialog);
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
                    Toast.makeText(getActivity(), "Password can't be empty", Toast.LENGTH_SHORT).show();
                }else{
                    String pass = MainActivity.sharedPreferencesPass.getString("Password", "");
                    if(edtEnter.getText().toString().equals(pass))
                    {
                        Intent intent = new Intent(getActivity(), Video_List.class);
                        intent.putExtra("folder_name", folderArrayList.get(position).getNameAlbum());
                        startActivity(intent);
                        dialog.dismiss();
                    }else {
                        Toast.makeText(getActivity(), "Password is incorrect", Toast.LENGTH_SHORT).show();
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

    private void dialogEditName(final String name) {
        final Dialog dialog = new Dialog(getContext(), R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_rename);

        final EditText edtRename = (EditText) dialog.findViewById(R.id.edtEditAlbum);
        Button btnOk = (Button) dialog.findViewById(R.id.btnEditAlbumOK);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnEditAlbumCancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtRename.equals(""))
                {
                    Toast.makeText(getActivity(), "Name Album can't be empty", Toast.LENGTH_SHORT).show();
                }else{
                    boolean success;
                    File file = new File(Environment.getExternalStorageDirectory() + File.separator + name);
                    File fileNew;
                    File file1 = null;
                    if(file.exists())
                    {
                        fileNew = new File(file.getParent(), edtRename.getText().toString());
                        refreshFile(file);
                        success = file.renameTo(fileNew);
                        deleteFile(file);

                    }else{
                        file1 = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + name);
                        fileNew = new File(file1.getParent(), edtRename.getText().toString());
                        refreshFile(file1);
                        success = file1.renameTo(fileNew);
                        deleteFile(file1);
                    }
                    if(success)
                    {
                        refreshFile(fileNew);
                        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        scanIntent.setData(Uri.fromFile(fileNew));
                        getContext().sendBroadcast(scanIntent);
                        refreshFile(file1);
                        refresh();
                        refresh();
                        Toast.makeText(getActivity(), "Rename success", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        Toast.makeText(getActivity(), "Rename failed", Toast.LENGTH_SHORT).show();
                    }
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

    private void refreshFile(File file)
    {

        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                refreshFile(child);
            }
        }
        MediaScannerConnection.scanFile( getContext(),
                new String[] {file.toString()},
                null,  new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
    }

    private void dialogWarning(final List<String> list) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.Theme_Dialog);
        dialog.setTitle("Warning!");
        dialog.setMessage("Are you sure want to delete this album ?");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String path;
                for(int i=0;i<list.size();i++)
                {

                    File file = new File(Environment.getExternalStorageDirectory() + File.separator + list.get(i));
                    if(file.exists())
                    {
                        path = file.getAbsolutePath();
                        deleteFile(file);
                    }else
                    {
                        File file1 = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + list.get(i));
                        path = file1.getAbsolutePath();
                        deleteFile(file1);
                    }

                    MediaScannerConnection.scanFile(getContext(), new String[]{path},
                            null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                }
                            }
                    );


                }
                refresh();
                refresh();
                refresh();
                Toast.makeText(getActivity(), "Deleted success!", Toast.LENGTH_SHORT).show();
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

    private void refresh()
    {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setReorderingAllowed(false);
        ft.detach(this).attach(this).commit();

    }
    // xoa file
    public void deleteFile(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteFile(child);
            }
        }
        fileOrDirectory.delete();
        MediaScannerConnection.scanFile( getContext(),
                new String[] {fileOrDirectory.getAbsolutePath()},
                null,  new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
    }

    public void getAllShownVideosPath(String bucket_order_by) {
        Uri video_Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getActivity().getContentResolver();

        Cursor cursor = null;
        Cursor cursorBucket = null;
        int column_index_data;
        int column_index_folder_name;

        String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";

        String[] projection = {MediaStore.Video.VideoColumns.BUCKET_ID,
                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DATE_TAKEN,
                MediaStore.Video.VideoColumns.DATA};

        cursor = contentResolver.query(video_Uri, projection, BUCKET_GROUP_BY,null,bucket_order_by);

        if(cursor != null)
        {

            column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);


            while(cursor.moveToNext())
            {
                String[] selectionArg = {"%" + cursor.getString(column_index_folder_name) + "%"};
                String temp = cursor.getString(column_index_data);
                Log.d("title_video", "bucket name:" + cursor.getString(column_index_data));
                String selection = MediaStore.Video.Media.DATA + " LIKE ?";
                String  [] projectionBucket = {MediaStore.Video.Media.DATA,
                        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DATE_TAKEN,
                        MediaStore.Video.Thumbnails.DATA};

                cursorBucket = contentResolver.query(video_Uri, projectionBucket, selection, selectionArg, null);

                File file = new File(temp.substring(temp.lastIndexOf("/") - 1));
                long createMili = file.lastModified();

                folderArrayList.add(new Album(cursor.getString(column_index_folder_name),
                        cursor.getString(column_index_data),cursorBucket.getCount(), createMili, false));
            }

            txtNumOfFolder.setText(folderArrayList.size() + "");
        }

        Cursor cursorTemp = contentResolver.query(video_Uri,null,null,null,null);
        if(cursorTemp != null)
        {
            txtNumOfVideo.setText(cursorTemp.getCount() + "");
        }
    }


    public void SortBy(String sort, String order)
    {
        folderArrayList.clear();
        getAllShownVideosPath(checkConditionSort(sort,order));
        adapterVideo.notifyDataSetChanged();
    }

    public String checkConditionSort(String sort, String order)
    {
        String sortOrder = null;
        if(order == "Ascending")
        {
            switch (sort)
            {
                case "Name":
                    sortOrder = "bucket_display_name COLLATE NOCASE ASC";
                    break;
                case "Date":
                    sortOrder = "MAX(datetaken) ASC";
                    break;
                case "Size":
                    sortOrder = "COUNT(*) ASC";
                    break;
            }
        }else
        {
            switch (sort)
            {
                case "Name":
                    sortOrder = "bucket_display_name COLLATE NOCASE DESC";
                    break;
                case "Date":
                    sortOrder = "MAX(datetaken) DESC";
                    break;
                case "Size":
                    sortOrder = "COUNT(*) DESC";
                    break;
            }
        }
        return sortOrder;
    }

    @Override
    public void onResume() {
        super.onResume();
        String sort = MainActivity.sharedPreferences.getString("Sort", "Name");
        String order = MainActivity.sharedPreferences.getString("Order", "Ascending");
        folderArrayList.clear();
        getAllShownVideosPath(checkConditionSort(sort,order));
        adapterVideo.notifyDataSetChanged();
    }
}
