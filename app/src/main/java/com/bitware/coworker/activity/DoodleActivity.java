package com.bitware.coworker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.SharedHelper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoodleActivity extends AppCompatActivity {
    private static SignaturePad mSignaturePad;
    private static ImageView penColorView;
    private static RecyclerView theme_list;
    Boolean visible = false;
    private Button done_doo;

    public static int getPrimaryCOlor(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
    }

    private static void SetPencolor(int selectedColor) {
        collapse(theme_list);
        switch (selectedColor) {
            case 1:
                penColorView.setColorFilter(Color.parseColor("#FF179d82"));
                mSignaturePad.setPenColor(Color.parseColor("#FF179d82"));
                break;
            case 2:
                penColorView.setColorFilter(Color.parseColor("#FF3498db"));
                mSignaturePad.setPenColor(Color.parseColor("#FF3498db"));
                break;
            case 3:
                penColorView.setColorFilter(Color.parseColor("#FF9b59b6"));
                mSignaturePad.setPenColor(Color.parseColor("#FF9b59b6"));
                break;
            case 4:
                penColorView.setColorFilter(Color.parseColor("#FF34495e"));
                mSignaturePad.setPenColor(Color.parseColor("#FF34495e"));
                break;
            case 5:
                penColorView.setColorFilter(Color.parseColor("#FFf1c40f"));
                mSignaturePad.setPenColor(Color.parseColor("#FFf1c40f"));
                break;
            case 6:
                penColorView.setColorFilter(Color.parseColor("#FFe67e22"));
                mSignaturePad.setPenColor(Color.parseColor("#FFe67e22"));
                break;
            case 7:
                penColorView.setColorFilter(Color.parseColor("#FF897fba"));
                mSignaturePad.setPenColor(Color.parseColor("#FF897fba"));
                break;
            case 8:
                penColorView.setColorFilter(Color.parseColor("#FF7bb0a6"));
                mSignaturePad.setPenColor(Color.parseColor("#FF7bb0a6"));
                break;
            case 9:
                penColorView.setColorFilter(Color.parseColor("#FF67bdff"));
                mSignaturePad.setPenColor(Color.parseColor("#FF67bdff"));
                break;
            case 10:
                penColorView.setColorFilter(Color.parseColor("#FF000000"));
                mSignaturePad.setPenColor(Color.parseColor("#FF000000"));
                break;
            case 11:
                penColorView.setColorFilter(Color.parseColor("#FF4ae2dd"));
                mSignaturePad.setPenColor(Color.parseColor("#FF4ae2dd"));
                break;
            case 12:
                penColorView.setColorFilter(Color.parseColor("#FFe74c3c"));
                mSignaturePad.setPenColor(Color.parseColor("#FFe74c3c"));
                break;
            default:
                penColorView.setColorFilter(Color.parseColor("#FF000000"));
                mSignaturePad.setPenColor(Color.parseColor("#FF000000"));
                break;
        }
    }

    public static void customViews(View v, int backgroundColor, int borderColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{10, 10, 10, 10, 10, 10, 10, 10});
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        v.setBackgroundDrawable(shape);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue = SharedHelper.getKey(this, "theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_doodle);
        done_doo = (Button) findViewById(R.id.done_doodle);
        penColorView = (ImageView) findViewById(R.id.penColorView);
        theme_list = (RecyclerView) findViewById(R.id.theme_list);

        penColorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!visible) {
                    expand(theme_list);
                    visible = true;
                } else {
                    collapse(theme_list);
                    visible = false;

                }

            }
        });
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
        theme_list.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        DoodleActivity.MyRecyclerViewAdapter adapter = new DoodleActivity.MyRecyclerViewAdapter(this, allColors);

        theme_list.setAdapter(adapter);

        customViews(done_doo, getPrimaryCOlor(DoodleActivity.this), getPrimaryCOlor(DoodleActivity.this));
        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getResources().getString(R.string.doodle_title));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setBackgroundColor(getPrimaryCOlor(DoodleActivity.this));
        mSignaturePad = (SignaturePad) findViewById(R.id.signature_pad);


        done_doo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.doodle_bitmap = mSignaturePad.getSignatureBitmap();
                Intent output = new Intent();
                output.putExtra("", "");
                setResult(RESULT_OK, output);
                finish();

            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent output = new Intent();
                output.putExtra("", "");
                setResult(RESULT_CANCELED, output);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.doodle_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                mSignaturePad.clear();
                break;
            default:
                return false;
        }
        return true;
    }

    private List<Integer> getAllMaterialColors() throws IOException, XmlPullParserException {
        XmlResourceParser xrp = DoodleActivity.this.getResources().getXml(R.xml.theme_color);
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

    public static class MyRecyclerViewAdapter extends RecyclerView.Adapter<DoodleActivity.MyRecyclerViewAdapter.ViewHolder> {

        Context context;
        private List<Integer> mData = new ArrayList<>();
        private LayoutInflater mInflater;
        private SettingsActivity.MyRecyclerViewAdapter.ItemClickListener mClickListener;


        // data is passed into the constructor
        public MyRecyclerViewAdapter(Context context, List<Integer> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
            this.context = context;
        }

        // inflates the cell layout from xml when needed
        @Override
        public DoodleActivity.MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.theme_item, parent, false);
            DoodleActivity.MyRecyclerViewAdapter.ViewHolder viewHolder = new DoodleActivity.MyRecyclerViewAdapter.ViewHolder(view);
            return viewHolder;
        }

        // binds the data to the textview in each cell
        @Override
        public void onBindViewHolder(DoodleActivity.MyRecyclerViewAdapter.ViewHolder holder, int position) {

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
                        Log.d("onClick: ", "getpos" + getAdapterPosition());
                        int selectedColor = getAdapterPosition() + 1;
                        SetPencolor(selectedColor);
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
