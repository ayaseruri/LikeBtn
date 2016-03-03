package com.x.likebtndemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.x.likebtn.LikeBtn;

public class MainActivity extends AppCompatActivity {

    private LikeBtn mLikeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLikeBtn = (LikeBtn) findViewById(R.id.like_btn);
//        mLikeBtn.setLikeHeartColor(getResources().getColor(R.color.colorAccent));
//        mLikeBtn.setDislikeHeartColor(getResources().getColor(R.color.colorPrimary));
//        mLikeBtn.setStarColor(getResources().getColor(android.R.color.white));

        mLikeBtn.setLikeBtnAnimationAdapter(new LikeBtn.ILikeBtnAnimationAdapter() {
            @Override
            public void onAnimationStart() {
                Toast.makeText(MainActivity.this, "Animation Start", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAnimationEnd() {
                Toast.makeText(MainActivity.this, "Animation End", Toast.LENGTH_LONG).show();
            }
        });

        mLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLikeBtn.setIslike(!mLikeBtn.islike(), true);
            }
        });
    }
}
