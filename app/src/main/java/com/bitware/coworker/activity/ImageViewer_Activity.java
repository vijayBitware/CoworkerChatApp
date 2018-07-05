package com.bitware.coworker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.models.ChatMessages;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ImageViewer_Activity extends AppCompatActivity {

    private ArrayList<String> imageArray;
    private DBHandler dbHandler;
    private JSONArray jsonArray;
    private String file;

    private void Setheme(String themevalue) {
        switch (themevalue)
        {
            case "1":
                setTheme(R.style.AppThemeGreen);
                break;
            case "2":
                setTheme(R.style.AppThemeBlue);
                break;
            case "3":
                setTheme(R.style.AppThemeIndigo);
                break;
            case "4":
                setTheme(R.style.AppThemeGrey);
                break;
            case "5":
                setTheme(R.style.AppThemeYellow);
                break;
            case "6":
                setTheme(R.style.AppThemeOrange);
                break;
            case "7":
                setTheme(R.style.AppThemePurple);
                break;
            case "8":
                setTheme(R.style.AppThemePaleGreen);
                break;
            case "9":
                setTheme(R.style.AppThemelightBlue);
                break;
            case "10":
                setTheme(R.style.AppThemePink);
                break;
            case "11":
                setTheme(R.style.AppThemelightGreen);
                break;
            case "12":
                setTheme(R.style.AppThemelightRed);
                break;
            default:
                setTheme(R.style.AppThemeGreen);
                break;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue= SharedHelper.getKey(this,"theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_image_viewer);
        getWindow().setStatusBarColor(Color.BLACK);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.chat_toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setTitleTextColor(Color.WHITE);
        dbHandler = new DBHandler(ImageViewer_Activity.this);
        Intent intent = getIntent();
        if (intent != null) {
            file = intent.getStringExtra("select_file");
            String chatRoomType = intent.getStringExtra("chatRoomType");

            int position = 0;
            imageArray = new ArrayList<>();
            ViewPager viewPager = (ViewPager) findViewById(R.id.image_viewer);
            if (chatRoomType.equalsIgnoreCase("0")) {
                jsonArray = dbHandler.GetChatsMessages(ChatActivity.zoeChatID);
            } else {
                jsonArray = dbHandler.GetGroupChatsMessages(ChatActivity.groupId_loc);
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                String type = jsonArray.optJSONObject(i).optString("userType");
                if (type.equalsIgnoreCase(ChatMessages.SENDER_IMAGE)) {
                    String msg = jsonArray.optJSONObject(i).optString("msg");
                    imageArray.add(msg);
                } else if (type.equalsIgnoreCase(ChatMessages.RECEIVER_IMAGE)) {
                    if (jsonArray.optJSONObject(i).optString("download").equalsIgnoreCase("1")) {
                        String msg = jsonArray.optJSONObject(i).optString("msg");
                        imageArray.add(msg);
                    }
                }
            }
            for (int j = 0; j < imageArray.size(); j++) {
                String file_name = new File(imageArray.get(j)).getName();
                if (file.equalsIgnoreCase(file_name)) {
                    position = j;
                    break;
                }
            }

            viewPager.setAdapter(new ImageViewerAdapter(ImageViewer_Activity.this, imageArray));
            viewPager.setCurrentItem(position);
            viewPager.setPageTransformer(true, new DepthPageTransformer());
            int i = viewPager.getCurrentItem();
            toolbar.setTitle((i + 1) + " of " + imageArray.size());
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {
                    // TODO Auto-generated method stub
                    toolbar.setTitle((arg0 + 1) + " of " + imageArray.size());
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onPageScrollStateChanged(int num) {
                    // TODO Auto-generated method stub


                }
            });
        }
    }




    private class ImageViewerAdapter extends PagerAdapter {
        private ArrayList<String> imageList;
        private Context mContext;

        public ImageViewerAdapter(ImageViewer_Activity imageViewer_activity, ArrayList<String> imageArray) {
            this.imageList = imageArray;
            this.mContext = imageViewer_activity;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.sliding_image_layout, container, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.sliding_image);
//            Glide.with(mContext).load(imageList.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
            Bitmap myBitmap = BitmapFactory.decodeFile(imageList.get(position));
            imageView.setImageBitmap(myBitmap);


            container.addView(view, 0);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        imageArray = new ArrayList<>();

    }

    private class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}
