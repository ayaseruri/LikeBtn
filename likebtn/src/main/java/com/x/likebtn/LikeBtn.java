package com.x.likebtn;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by wufeiyang on 16/3/1.
 */
public class LikeBtn extends FrameLayout{

    private static final int HEART_SCALE_SMALL_TIME = 800;

    private boolean islike;
    private AccelerateInterpolator accelerateInterpolator;
    private DecelerateInterpolator decelerateInterpolator;

    private ImageView heartImg;
    private ImageView starImg;
    private RingImg ringImg;

    private ILikeBtnAnimationAdapter likeBtnAnimationAdapter;
    private int likeHeartColor;
    private int starColor;
    private int dislikeHeartColor;
    private boolean isAnimationRunning;

    public LikeBtn(Context context) {
        super(context);
        init(context);
    }

    public LikeBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = null;
        try {
            array = context.obtainStyledAttributes(attrs, R.styleable.LikeBtn);
            likeHeartColor = array.getColor(R.styleable.LikeBtn_like_heart_color, Color.parseColor("#FE0D1D"));
            dislikeHeartColor = array.getColor(R.styleable.LikeBtn_dislike_heart_color, Color.parseColor("#C0C0C0"));
            starColor = array.getColor(R.styleable.LikeBtn_star_color, Color.parseColor("#FFDF01"));
        }finally {
            if(null != array){
                array.recycle();
            }
        }
        init(context);
    }

    private void init(Context mContext){
        setClickable(true);

        accelerateInterpolator = new AccelerateInterpolator();
        decelerateInterpolator = new DecelerateInterpolator();

        LayoutInflater.from(mContext).inflate(R.layout.like_btn, this, true);

        heartImg = (ImageView) findViewById(R.id.heart_img);
        starImg = (ImageView) findViewById(R.id.start_img);
        ringImg = (RingImg) findViewById(R.id.ring_img);

        heartImg.setColorFilter(islike?likeHeartColor:dislikeHeartColor);
        starImg.setColorFilter(starColor);
    }

    private void startAnimation(){
        if(isAnimationRunning){
            return;
        }
        isAnimationRunning = true;
        if(islike){
            ObjectAnimator heartScaleX = ObjectAnimator.ofFloat(heartImg, "scaleX", 0.05f);
            ObjectAnimator heartScaleY = ObjectAnimator.ofFloat(heartImg, "scaleY", 0.05f);
            heartScaleX.setDuration(HEART_SCALE_SMALL_TIME);
            heartScaleY.setDuration(HEART_SCALE_SMALL_TIME);
            heartScaleX.setInterpolator(accelerateInterpolator);
            heartScaleY.setInterpolator(accelerateInterpolator);

            ObjectAnimator heartRotateAnimator = ObjectAnimator.ofFloat(heartImg, "rotation", 0f, 360f);
            heartRotateAnimator.setDuration(HEART_SCALE_SMALL_TIME);
            heartRotateAnimator.setInterpolator(accelerateInterpolator);
            heartRotateAnimator.addListener(new SimpleAnimationListener(){
                @Override
                public void onAnimationStart(Animator animation) {
                    if(null != likeBtnAnimationAdapter){
                        likeBtnAnimationAdapter.onAnimationStart();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    heartImg.setVisibility(GONE);
                    secondPartAnimation(heartImg.getScaleY() * heartImg.getHeight());
                }
            });

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(heartRotateAnimator).with(heartScaleY).with(heartScaleX);
            animatorSet.start();
        }else {
            heartImg.setColorFilter(dislikeHeartColor);
            YoYo.with(Techniques.Tada).duration(HEART_SCALE_SMALL_TIME).withListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if(null != likeBtnAnimationAdapter){
                        likeBtnAnimationAdapter.onAnimationStart();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(null != likeBtnAnimationAdapter){
                        likeBtnAnimationAdapter.onAnimationEnd();
                    }
                    isAnimationRunning = false;
                }
            }).playOn(this);
        }
    }


    private void secondPartAnimation(final float startSize){
        ringImg.setVisibility(VISIBLE);
        ringImg.setmRingColor(likeHeartColor);
        ringImg.setmOvalInner(0);

        float outterMax = Math.min(heartImg.getWidth(), heartImg.getHeight())/2;
        ValueAnimator outterAnimator = ObjectAnimator.ofFloat(startSize, outterMax);
        outterAnimator.setDuration(HEART_SCALE_SMALL_TIME);
        outterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ringImg.setmOvalOutter((float)animation.getAnimatedValue());
                ringImg.invalidate();
            }
        });
        outterAnimator.setInterpolator(decelerateInterpolator);

        final float innerMax = outterMax;
        ValueAnimator innerAnimator = ObjectAnimator.ofFloat(0, innerMax);
        innerAnimator.setDuration(HEART_SCALE_SMALL_TIME);
        innerAnimator.addListener(new SimpleAnimationListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                ringImg.setVisibility(GONE);
            }
        });
        innerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                starImg.setScaleX((float)animation.getAnimatedValue()/innerMax);
                starImg.setScaleY(starImg.getScaleX());

                if(starImg.getVisibility() == GONE){
                    starImg.setVisibility(VISIBLE);
                }

                ringImg.setmOvalInner((float)animation.getAnimatedValue());
                ringImg.invalidate();
            }
        });
        innerAnimator.setInterpolator(accelerateInterpolator);


        ObjectAnimator startRotateAnimator = ObjectAnimator.ofFloat(starImg, "rotation", 0f, 270f);
        startRotateAnimator.setDuration(HEART_SCALE_SMALL_TIME + 250);

        ObjectAnimator heartRotateAnimator = ObjectAnimator.ofFloat(heartImg, "rotation", 0f, 360f);
        heartRotateAnimator.setDuration(HEART_SCALE_SMALL_TIME + 500);
        heartRotateAnimator.addListener(new SimpleAnimationListener(){
            @Override
            public void onAnimationStart(Animator animation) {
                heartImg.setVisibility(VISIBLE);
                heartImg.setColorFilter(likeHeartColor);
            }
        });
        heartRotateAnimator.setInterpolator(accelerateInterpolator);

        ObjectAnimator heartScaleX = ObjectAnimator.ofFloat(heartImg, "scaleX", 1f);
        ObjectAnimator heartScaleY = ObjectAnimator.ofFloat(heartImg, "scaleY", 1f);
        heartScaleY.addListener(new SimpleAnimationListener(){
            @Override
            public void onAnimationEnd(Animator animation) {
                if(null != likeBtnAnimationAdapter){
                    likeBtnAnimationAdapter.onAnimationEnd();
                }
                isAnimationRunning = false;
            }
        });
        heartScaleX.setDuration(HEART_SCALE_SMALL_TIME);
        heartScaleY.setDuration(heartScaleX.getDuration());
        heartScaleX.setInterpolator(accelerateInterpolator);
        heartScaleY.setInterpolator(accelerateInterpolator);

        ObjectAnimator starScaleX = ObjectAnimator.ofFloat(starImg, "scaleX", 0.1f);
        ObjectAnimator startScaleY = ObjectAnimator.ofFloat(starImg, "scaleY", 0.1f);
        starScaleX.setDuration(250);
        startScaleY.setDuration(starScaleX.getDuration());
        starScaleX.setInterpolator(decelerateInterpolator);
        startScaleY.setInterpolator(decelerateInterpolator);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(outterAnimator);
        animatorSet.play(innerAnimator)
                .with(heartRotateAnimator)
                .with(startRotateAnimator)
                .before(starScaleX)
                .before(startScaleY)
                .after(100);
        animatorSet.play(heartScaleX)
                .with(heartScaleY)
                .after(100 * 6);
        animatorSet.start();
    }

    public void setIslike(boolean like, boolean animation) {
        if(isAnimationRunning){
            return;
        }
        islike = like;
        if(animation){
            startAnimation();
        }else {
            if(like){
                heartImg.setColorFilter(likeHeartColor);
            }else {
                heartImg.setColorFilter(dislikeHeartColor);
            }
        }
    }

    public ILikeBtnAnimationAdapter getLikeBtnAnimationAdapter() {
        return likeBtnAnimationAdapter;
    }

    public void setLikeBtnAnimationAdapter(ILikeBtnAnimationAdapter likeBtnAnimationAdapter) {
        this.likeBtnAnimationAdapter = likeBtnAnimationAdapter;
    }

    public int getLikeHeartColor() {
        return likeHeartColor;
    }

    public void setLikeHeartColor(int likeHeartColor) {
        this.likeHeartColor = likeHeartColor;
    }

    public int getStarColor() {
        return starColor;
    }

    public void setStarColor(int starColor) {
        this.starColor = starColor;
    }

    public int getDislikeHeartColor() {
        return dislikeHeartColor;
    }

    public void setDislikeHeartColor(int dislikeHeartColor) {
        this.dislikeHeartColor = dislikeHeartColor;
    }

    public boolean islike() {
        return islike;
    }

    public interface ILikeBtnAnimationAdapter{
        void onAnimationStart();
        void onAnimationEnd();
    }
}
