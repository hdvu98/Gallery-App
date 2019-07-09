package quanghuong.nqh.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.view.ActionMode;
import android.util.Log;
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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Fragment_Image extends Fragment {
    GridView gridView;
    Fragment_Image_Adapter adapterImage;
    ArrayList<Album> albumArrayList;
    View view;
    TextView txtNumOfPhoto;
    TextView txtNumOfAlbum;
    String BUCKET_ORDER_BY;
    String Sort;
    String Order;

    public void InitView()
    {

        gridView = (GridView) view.findViewById(R.id.gridviewPhoto);
        txtNumOfAlbum = (TextView) view.findViewById(R.id.txtNumberOfAlbum);
        txtNumOfPhoto = (TextView) view.findViewById(R.id.txtNumberOfPhoto);
        albumArrayList = new ArrayList<>();
        BUCKET_ORDER_BY = null;

        Sort = MainActivity.sharedPreferences.getString("Sort", "Name");
        Order = MainActivity.sharedPreferences.getString("Order", "Ascending");
        BUCKET_ORDER_BY = checkConditionSort(Sort,Order);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_image_layout, container, false);
        InitView();

        adapterImage = new Fragment_Image_Adapter(getActivity(), R.layout.custom_fragment_image_layout, albumArrayList);
        getAllShownImagesPath(BUCKET_ORDER_BY);
        adapterImage.notifyDataSetChanged();

        gridView.setAdapter(adapterImage);
        gridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            private int nr = 0;
            MenuItem itemEdit;
            ArrayList<String> positionList = new ArrayList<>();
            @Override
            public void onItemCheckedStateChanged(android.view.ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    albumArrayList.get(position).setCheckbox(true);
                    adapterImage.notifyDataSetChanged();
                    nr++;
                } else {
                    albumArrayList.get(position).setCheckbox(false);
                    adapterImage.notifyDataSetChanged();
                    nr--;
                }
                mode.setTitle(nr + "");
                if(nr > 1)
                {
                    itemEdit.setVisible(false);
                }else
                {
                    itemEdit.setVisible(true);
                }

            }

            @Override
            public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                MenuInflater menuInflater = getActivity().getMenuInflater();
                menuInflater.inflate(R.menu.action_mode_toolbar, menu);
                itemEdit = menu.findItem(R.id.menuEdit);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.menuDelete:
                        for(int i = 0;i<albumArrayList.size();i++)
                        {
                            if(albumArrayList.get(i).isCheckbox())
                            {
                                positionList.add(albumArrayList.get(i).getNameAlbum());
                            }
                        }
                        dialogWarning(positionList);
                        mode.finish();
                        return true;
                    case R.id.menuEdit:
                        String path = null;
                        for(int i = 0;i<albumArrayList.size();i++)
                        {
                            if(albumArrayList.get(i).isCheckbox())
                            {
                                path = albumArrayList.get(i).getNameAlbum();
                            }
                        }
                        dialogEditName(path);
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(android.view.ActionMode mode) {
                nr = 0;
                for(int i=0; i<albumArrayList.size();i++)
                {
                    if(albumArrayList.get(i).isCheckbox())
                        albumArrayList.get(i).setCheckbox(false);
                }

            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(MainActivity.virtualPass.equals(""))
                {
                    Intent intent = new Intent(getActivity(), Image_List.class);
                    intent.putExtra("album_name", albumArrayList.get(position).getNameAlbum());
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
                        MainActivity.virtualPass = "";
                        Intent intent = new Intent(getActivity(), Image_List.class);
                        intent.putExtra("album_name", albumArrayList.get(position).getNameAlbum());
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
                if(edtRename.toString().equals(""))
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


    public void getAllShownImagesPath(String bucket_oder_by) {

        Uri Image_Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = null;
        Cursor cursorBucket = null;
        int column_index_data;
        int column_index_folder_name;

        String absolutePathOfImage = null;
        ContentResolver contentResolver = getActivity().getContentResolver();
        String BUCKET_GROUP_BY = "1) GROUP BY 1,(2";

        String[] projection = {MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.DATA};

        cursor = contentResolver.query(Image_Uri, projection, BUCKET_GROUP_BY,null,bucket_oder_by);

        if(cursor != null)
        {
            column_index_data = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            column_index_folder_name = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            while(cursor.moveToNext())
            {
                absolutePathOfImage = cursor.getString(column_index_data);

                Log.d("title_apps", "bucket name:" + absolutePathOfImage);

                String[] selectionArg = {"%" + cursor.getString(column_index_folder_name) + "%"};
                String selection = MediaStore.Images.Media.DATA + " LIKE ?";
                String  [] projectionBucket = {MediaStore.MediaColumns.DATA,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                };

                cursorBucket = contentResolver.query(Image_Uri, projectionBucket, selection, selectionArg, null);

                File file = new File(absolutePathOfImage.substring(absolutePathOfImage.lastIndexOf("/") - 1));
                long createMili = file.lastModified();

                if(!absolutePathOfImage.equals("") && absolutePathOfImage != null)
                {
                    albumArrayList.add(new Album(cursor.getString(column_index_folder_name),
                            absolutePathOfImage,cursorBucket.getCount(), createMili, false));
                }
            }


            txtNumOfAlbum.setText(albumArrayList.size() + "");
        }

        Cursor cursorTemp = contentResolver.query(Image_Uri,null,null,null,null);
        if(cursorTemp != null)
        {
            txtNumOfPhoto.setText(cursorTemp.getCount() + "");
        }

    }

    // function tao album moi
    public void AddAlbum(String albumName)
    {
//        Bitmap bitmap;
//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.new_folder);
//        FileOutputStream outputStream;
//        File album = new File(Environment.getExternalStorageDirectory() + File.separator  + Environment.DIRECTORY_DCIM + File.separator + albumName);
//        boolean success;
//        if(!album.exists())
//        {
//            success = album.mkdirs();
//        }else
//        {
//            success = false;
//        }
//
//        if(success)
//        {
//            File path = new File(album, albumName + "_newAlbum.png");
//            String temp = path.getAbsolutePath();
//
//            try {
//                outputStream = new FileOutputStream(path);
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
//
//                Log.d("title_apps", temp);
//
//                outputStream.flush();
//                outputStream.close();
//
//                MediaScannerConnection.scanFile(getContext(), new String[]{temp},
//                        null,
//                        new MediaScannerConnection.OnScanCompletedListener() {
//                            @Override
//                            public void onScanCompleted(String path, Uri uri) {
//
//                            }
//                        }
//                );
//
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//            Toast.makeText(getActivity(), "Create Album "+ albumName +" success !", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(Fragment_Image.this.getContext(),ImagePicker.class);
            intent.putExtra("ALBUM_NAME",albumName);
            startActivity(intent);



//        }else {
//
//            Toast.makeText(getActivity(), "Album is already exist !", Toast.LENGTH_SHORT).show();
//        }
//
//        refresh();
    }

    public void SortBy(String sort, String order)
    {
        albumArrayList.clear();
        getAllShownImagesPath(checkConditionSort(sort,order));
        adapterImage.notifyDataSetChanged();
    }

    public String checkConditionSort(String sort, String order)
    {
        String sortOrder = null;
        if(order.equals("Ascending") )
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
                default:
                    break;
            }
        }else if(order.equals("Descending") )
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
                default:
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
        albumArrayList.clear();
        getAllShownImagesPath(checkConditionSort(sort,order));
        adapterImage.notifyDataSetChanged();
    }
}
