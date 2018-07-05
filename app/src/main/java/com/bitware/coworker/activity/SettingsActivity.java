package com.bitware.coworker.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;
import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.SharedHelper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    public static Toolbar toolbar;
    public static RelativeLayout back_layout;
    public ImageView back;
    LinearLayout profilelayout, notificationlaoyut, medialayout, themelayout,chatlayout;
    ImageView noti_icon, priv_icon, med_ico, them_ico, fb_icon, twitter_icon, insta_icon, wohoo_icon,chat_ico;
    LinearLayout privacylayout;
    TextView user_name, user_status;
    public  static CircleImageView pro_image;
    RecyclerView theme_list;
    public  static ProgressBar progress_bar;

    Boolean visible = false;
    private List<Integer> allColors = new ArrayList<>();
    public static TextView textView;
    private Drawable drawable;

    public static int getPrimaryCOlor(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
    }

    public static int getPrimaryDark(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, value, true);
        return value.data;
    }

    public static void expand(final View v) {
        v.measure(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? RecyclerView.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void Setheme(String themevalue) {
        switch (themevalue) {
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

    public void setIconColour(Drawable drawable, int primaryCOlor) {

        drawable.setColorFilter(new PorterDuffColorFilter(getPrimaryCOlor(SettingsActivity.this),PorterDuff.Mode.SRC_IN));

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue = SharedHelper.getKey(this, "theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_settings);
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        back_layout = (RelativeLayout) findViewById(R.id.back_layout);
        progress_bar=(ProgressBar)findViewById(R.id.progress_bar);

        textView = (TextView) findViewById(R.id.text_name_per);


        pro_image = (CircleImageView) findViewById(R.id.profile_image);


        back = (ImageView) back_layout.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();

            }
        });

        noti_icon = (ImageView) findViewById(R.id.noti_icon);

        setIconColour(noti_icon.getDrawable(), getPrimaryCOlor(SettingsActivity.this));
        priv_icon = (ImageView) findViewById(R.id.priv_icon);
        setIconColour(priv_icon.getDrawable(), getPrimaryCOlor(SettingsActivity.this));
        med_ico = (ImageView) findViewById(R.id.med_ico);
        setIconColour(med_ico.getDrawable(), getPrimaryCOlor(SettingsActivity.this));
        chat_ico=(ImageView)findViewById(R.id.chat_ico);
        setIconColour(chat_ico.getDrawable(), getPrimaryCOlor(SettingsActivity.this));

        profilelayout = (LinearLayout) findViewById(R.id.profile_layout);
        privacylayout = (LinearLayout) findViewById(R.id.privacy_settings);
        notificationlaoyut = (LinearLayout) findViewById(R.id.notification_settings);
        chatlayout = (LinearLayout) findViewById(R.id.chat_settings);
        medialayout = (LinearLayout) findViewById(R.id.media_settings);
        user_name = (TextView) findViewById(R.id.user_name);
        user_status = (TextView) findViewById(R.id.user_status);
        theme_list = (RecyclerView) findViewById(R.id.theme_list);
        List<Integer> allColors = null;
        final Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

        final Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        try {
            allColors = getAllMaterialColors();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        int numberOfColumns = 6;
//        theme_list.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, allColors);
//        theme_list.setAdapter(adapter);
//        themelayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!visible) {
////                    theme_list.setVisibility(View.VISIBLE);
//                    expand(theme_list);
////                    theme_list.startAnimation(slide_down);
//                    visible = true;
//                } else {
////                    theme_list.startAnimation(slide_up);
//                    collapse(theme_list);
////                    theme_list.setVisibility(View.GONE);
//                    visible = false;
//
//                }
//
//            }
//        });


        user_name.setText(SharedHelper.getKey(SettingsActivity.this, "name"));
        user_status.setText(SharedHelper.getKey(SettingsActivity.this, "status"));
        Log.d("onCreate: ", "Profile_image:" + SharedHelper.getKey(SettingsActivity.this, "image"));

        if (SharedHelper.getKey(SettingsActivity.this, "image").equalsIgnoreCase("") || SharedHelper.getKey(SettingsActivity.this, "image").equalsIgnoreCase(" "))
        {
            Picasso.with(SettingsActivity.this).load(R.drawable.ic_account_circle).placeholder(getResources().getDrawable(R.drawable.ic_account_circle)).error(getResources().getDrawable(R.drawable.ic_account_circle)).into(pro_image);
            textView.setVisibility(View.GONE);
            progress_bar.setVisibility(View.GONE);

        } else {
            textView.setVisibility(View.GONE);

            Glide.with(SettingsActivity.this)
                    .load(SharedHelper.getKey(SettingsActivity.this, "image"))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progress_bar.setVisibility(View.GONE);
//                    textView.setText(f_let.toUpperCase());
                            textView.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progress_bar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(pro_image);
//            Picasso.with(SettingsActivity.this).load(SharedHelper.getKey(SettingsActivity.this, "image")).placeholder(drawable).error(drawable).into(pro_image, new Callback() {
//                @Override
//                public void onSuccess() {
//                    progress_bar.setVisibility(View.GONE);
//
//
//                }
//
//                @Override
//                public void onError() {
//                    Picasso.with(SettingsActivity.this).load(R.drawable.ic_account_circle).placeholder(getResources().getDrawable(R.drawable.ic_account_circle)).error(getResources().getDrawable(R.drawable.ic_account_circle)).into(pro_image);
//
//                    progress_bar.setVisibility(View.GONE);
////                    textView.setText(f_let.toUpperCase());
//                    textView.setVisibility(View.GONE);
//
//                }
//            });

        }

        profilelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateintent(pro_image);
            }
        });


        privacylayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setti = new Intent(SettingsActivity.this, PrivacyActivity.class);
                setti.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(setti);
            }
        });


        chatlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setti = new Intent(SettingsActivity.this, ChatSettings.class);
                setti.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(setti);
            }
        });


        notificationlaoyut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent noti = new Intent(SettingsActivity.this, NotificationSettings.class);
                noti.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(noti);

            }
        });

        medialayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, AutoDownloadSettings.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private List<Integer> getAllMaterialColors() throws IOException, XmlPullParserException {
        XmlResourceParser xrp = SettingsActivity.this.getResources().getXml(R.xml.theme_color);
        List<Integer> allColors = new ArrayList<>();
        int nextEvent;
        while ((nextEvent = xrp.next()) != XmlResourceParser.END_DOCUMENT) {
            String s = xrp.getName();
            if ("color".equals(s)) {
                String color = xrp.nextText();
                allColors.add(Color.parseColor(color));
            }
        }
        return allColors;
    }

    private List<Integer> getAllMaterialColorss() throws IOException, XmlPullParserException {
        XmlResourceParser xrp = SettingsActivity.this.getResources().getXml(R.xml.select_color);
        List<Integer> allColors = new ArrayList<>();
        int nextEvent;
        while ((nextEvent = xrp.next()) != XmlResourceParser.END_DOCUMENT) {
            String s = xrp.getName();
            if ("color".equals(s)) {
                String color = xrp.nextText();
                allColors.add(Color.parseColor(color));
            }
        }
        return allColors;
    }
//    private void showlthemeaert() {
//        final Dialog dialog = new Dialog(SettingsActivity.this);
//        dialog.setContentView(R.layout.theme_picker);
//
//        dialog.setTitle("Select Theme");
//        RecyclerView theme_list=(RecyclerView)dialog.findViewById(R.id.theme_list) ;
//        List<Integer> allColors=null;
//        try {
//       allColors = getAllMaterialColors();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (XmlPullParserException e) {
//            e.printStackTrace();
//        }
//        int numberOfColumns = 4;
//        theme_list.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
//        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, allColors);
//
//        theme_list.setAdapter(adapter);
//
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(dialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
//
//        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        dialog.show();
//        dialog.getWindow().setAttributes(lp);
//    }

    public void animateintent(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pro_image.setTransitionName("DetailsTransition");
            Pair<View, String> pair1 = Pair.create((View) pro_image, pro_image.getTransitionName());
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) SettingsActivity.this, pair1);
            Intent intent = new Intent(SettingsActivity.this, Profile_activity.class);
            startActivity(intent, optionsCompat.toBundle());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        user_name.setText(SharedHelper.getKey(SettingsActivity.this, "name"));
        user_status.setText(SharedHelper.getKey(SettingsActivity.this, "status"));
        Log.d("onCreate: ", "Profile_image:" + SharedHelper.getKey(SettingsActivity.this, "image"));

        if (SharedHelper.getKey(SettingsActivity.this, "image").equalsIgnoreCase("") || SharedHelper.getKey(SettingsActivity.this, "image").equalsIgnoreCase(" "))
        {
            Picasso.with(SettingsActivity.this).load(R.drawable.ic_account_circle).placeholder(getResources().getDrawable(R.drawable.ic_account_circle)).error(getResources().getDrawable(R.drawable.ic_account_circle)).into(pro_image);
            textView.setVisibility(View.GONE);
            progress_bar.setVisibility(View.GONE);

        } else {
            textView.setVisibility(View.GONE);

            Glide.with(SettingsActivity.this)
                    .load(SharedHelper.getKey(SettingsActivity.this, "image"))
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progress_bar.setVisibility(View.GONE);
//                    textView.setText(f_let.toUpperCase());
                            textView.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progress_bar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(pro_image);

        }
    }

    public static class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        Context context;
        private List<Integer> mData = new ArrayList<>();
        private LayoutInflater mInflater;
        private ItemClickListener mClickListener;

        // data is passed into the constructor
        public MyRecyclerViewAdapter(Context context, List<Integer> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.context = context;
        }

        // inflates the cell layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.theme_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        // binds the data to the textview in each cell
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.myTextView.setImageDrawable(new ColorDrawable(mData.get(position)));

        }

        // total number of cells
        @Override
        public int getItemCount() {
            return mData.size();
        }

        // convenience method for getting data at click position
        public String getItem(int id) {
            return String.valueOf(mData.get(id));
        }

        // allows clicks events to be caught
        public void setClickListener(ItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, int position);
        }

        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public CircleImageView myTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                myTextView = (CircleImageView) itemView.findViewById(R.id.info_text);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
                        builder.setMessage("Are you sure you want to change the theme?")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        int i = getAdapterPosition() + 1;
                                        SharedHelper.putKey(context, "theme_value", "" + i);
                                        Intent intent = new Intent(context, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        context.startActivity(intent);

                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        Log.d("onClick: ", "getpos" + getAdapterPosition());

                    }
                });
            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }
}
