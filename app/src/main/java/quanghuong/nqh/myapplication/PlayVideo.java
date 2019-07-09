package quanghuong.nqh.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

public class PlayVideo extends AppCompatActivity {
    VideoView videoView;
    ImageButton btnPlay;
    MediaController mediaController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Init();

        Intent intent = getIntent();
        int position = intent.getIntExtra("video_position", -1);
        videoView.setVideoPath(Video_List.videoList.get(position).getPath());

        videoView.seekTo(1);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlay.setVisibility(View.INVISIBLE);
                videoView.setMediaController(mediaController);
                mediaController.setAnchorView(videoView);
                videoView.start();

            }
        });
    }

    private void Init() {
        videoView = (VideoView) findViewById(R.id.videoView);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        mediaController = new MediaController(this);
    }
}
