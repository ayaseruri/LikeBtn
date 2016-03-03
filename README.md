# LikeBtn
a colorful like btn,inspired by [danmu.fm](http://danmu.fm)

screen:
![gif]()

### add to your project
```java
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}

  dependencies {
  	        compile 'com.github.ayaseruri:LikeBtn:-SNAPSHOT'
  	}
```

### how
1、first
```xml
<com.x.likebtn.LikeBtn
    android:id="@+id/like_btn"
    android:layout_width="200dp"
    android:layout_height="200dp"
    android:layout_centerInParent="true"
    app:dislike_heart_color="@android:color/white"
    app:like_heart_color="@color/colorPrimary"
    app:star_color="@color/colorAccent"/>
```

2、then
```java
mLikeBtn = (LikeBtn) findViewById(R.id.like_btn);
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
```
3、tips
```java
likeBtn.setIslike(boolean like, boolean animation) the second paramter controls if the animation would start.
```

### thanks
[daimajia](http://github.com/daimajia/)

[itorr](https://github.com/itorr/)
