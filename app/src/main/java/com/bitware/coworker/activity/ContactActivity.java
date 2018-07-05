package com.bitware.coworker.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.models.GetUserFromDBModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactActivity extends AppCompatActivity implements AsyncTaskCompleteListener, SearchView.OnQueryTextListener {

    public static List<GetUserFromDBModel> list_of_contacts = new ArrayList<>();
    public static List<GetUserFromDBModel> s_chatslist = new ArrayList<>();
    RecyclerView recyclerView;
    DBHandler dbHandler = new DBHandler(ContactActivity.this);
    private List<JSONObject> participantsList=new ArrayList<>();
    private RelativeLayout errorLayout;
    private SelectContact selectContact;
    private Toolbar toolbar;

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

    public static void customView(View v, int backgroundColor, int borderColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{100, 100, 100, 100, 100, 100, 100, 100});
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        v.setBackgroundDrawable(shape);
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
        setContentView(R.layout.activity_contact);

        toolbar = (Toolbar) findViewById(R.id.contact_toolbar);
        toolbar.setTitle("Contacts to send");
        toolbar.setSubtitle("0 Selected");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setBackgroundColor(getPrimaryCOlor(ContactActivity.this));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);


        errorLayout = (RelativeLayout) findViewById(R.id.error_layout);
        recyclerView = (RecyclerView) findViewById(R.id.contact_list);
        recyclerView.setHasFixedSize(true);

        list_of_contacts = dbHandler.GetAllUserFromDB();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ContactActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        if (list_of_contacts.size() > 0) {
            Collections.sort(list_of_contacts, new Comparator<GetUserFromDBModel>() {
                @Override
                public int compare(GetUserFromDBModel getUserFromDB, GetUserFromDBModel t1) {
                    return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                }
            });
            selectContact = new SelectContact(ContactActivity.this, list_of_contacts, toolbar);
            recyclerView.setAdapter(selectContact);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }

        ImageView send_contact = (ImageView) findViewById(R.id.send_contact);
        customView(send_contact, getPrimaryCOlor(ContactActivity.this), getPrimaryCOlor(ContactActivity.this));

        send_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (participantsList.size() != 0) {
                        Intent intent = new Intent();
                        intent.putExtra("selected_contact", participantsList.toString());
                        setResult(103, intent);
                        finish();
                    } else {
                        Toast.makeText(ContactActivity.this, getResources().getString(R.string.select_one), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                }


            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_only, menu);
        MenuItem menuItem = menu.findItem(R.id.contact_search);
        try {
            SearchView view = (SearchView) MenuItemCompat.getActionView(menuItem);
            EditText searchEditText = (EditText) view.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchEditText.setHintTextColor(getResources().getColor(R.color.white));
            searchEditText.setTextColor(getResources().getColor(R.color.white));

            view.setOnQueryTextListener(this);
            MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    return true;
                }
            });

        } catch (Exception e) {

        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onTaskCompleted(JSONObject response, int serviceCode) {
        Log.e("res", response.toString());
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<GetUserFromDBModel> filter(List<GetUserFromDBModel> list, String query) {
        query = query.toLowerCase();
        List<GetUserFromDBModel> filter_list = new ArrayList<>();
        for (GetUserFromDBModel getUserFromDB : list) {
            String type_value = getUserFromDB.getName().toLowerCase();
            if (type_value.contains(query)) {
                filter_list.add(getUserFromDB);
            }
        }
        return filter_list;

    }


    @Override
    public boolean onQueryTextChange(String newText) {
        List<GetUserFromDBModel> getUserFromDBs = filter(list_of_contacts, newText);

        try {
            if (newText.length() > 0) {

                if (getUserFromDBs.size() > 0) {

                    if (list_of_contacts.size() > 0) {
//                        Collections.sort(list_of_contacts, new Comparator<GetUserFromDBModel>() {
//                            @Override
//                            public int compare(GetUserFromDBModel getUserFromDB, GetUserFromDBModel t1) {
//                                return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
//                            }
//                        });

                        selectContact = new SelectContact(ContactActivity.this, getUserFromDBs, toolbar);
                        recyclerView.setAdapter(selectContact);
                        selectContact.notifyDataSetChanged();

                    } else {
                        errorLayout.setVisibility(View.VISIBLE);
                    }

                } else {
                    getUserFromDBs = new ArrayList<>();
//                    Collections.sort(getUserFromDBs, new Comparator<GetUserFromDBModel>() {
//                        @Override
//                        public int compare(GetUserFromDBModel getUserFromDB, GetUserFromDBModel t1) {
//                            return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
//                        }
//                    });
                    selectContact = new SelectContact(ContactActivity.this, getUserFromDBs, toolbar);
                    recyclerView.setAdapter(selectContact);
                    selectContact.notifyDataSetChanged();

                }


            } else {
                list_of_contacts = new ArrayList<>();
                list_of_contacts = dbHandler.GetAllUserFromDB();
                recyclerView.setLayoutManager(new LinearLayoutManager(ContactActivity.this));
                Collections.sort(list_of_contacts, new Comparator<GetUserFromDBModel>() {
                    @Override
                    public int compare(GetUserFromDBModel getUserFromDB, GetUserFromDBModel t1) {
                        return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                    }
                });
                selectContact = new SelectContact(ContactActivity.this, list_of_contacts, toolbar);
                recyclerView.setAdapter(selectContact);
                selectContact.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

    }

    private class SelectContact extends RecyclerView.Adapter<SelectContact.MyViewHolder> {
        private List<GetUserFromDBModel> list;
        private Context mContext;

        private Toolbar sub;

        public SelectContact(ContactActivity contactActivity, List<GetUserFromDBModel> list_of_contacts, Toolbar toolbar) {
            this.list = list_of_contacts;
            this.mContext = contactActivity;
            this.sub = toolbar;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.contact_list_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {


            if (list.get(position).getImage().equalsIgnoreCase("")) {
                Picasso.with(mContext).load(R.drawable.ic_account_circle)
                        .error(mContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);
            } else {
                Picasso.with(mContext).load(list.get(position).getImage()).into(holder.user_image);
            }
            holder.user_name.setText(list.get(position).getName());
            holder.user_status.setText(list.get(position).getStatus());
            if (participantsList.size()>0) {
                for (int i =0;i<participantsList.size();i++) {
                    String id = participantsList.get(i).optString("mobile");
                    String clickId = list.get(position).getMobile();
                    Log.d("onBindViewHolder: ","id:"+id+",click:"+clickId);
                    if (id.equalsIgnoreCase(clickId)) {
                        holder.click.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        holder.click.setVisibility(View.GONE);

                    }
                }
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.click.getVisibility() == View.VISIBLE) {
                        String clickId = list.get(position).getZoeChatId();
                        if (participantsList.size() == 1) {
                            participantsList.clear();
                            sub.setSubtitle("0 Selected");

                        } else {
                            for (int i = 0; i < participantsList.size(); i++) {
                                String id = participantsList.get(i).optString("zoeChatId");
                                if (id.equalsIgnoreCase(clickId)) {
                                    participantsList.remove(i);
                                    sub.setSubtitle(String.valueOf(participantsList.size() + " Selected"));
                                    break;
                                }

                            }

                        }

                        holder.click.setVisibility(View.GONE);
                        Log.d("addedList", "" + participantsList);
                        //addedAdapter.notifyDataSetChanged();

                    } else if (holder.click.getVisibility() == View.GONE) {

                        JSONObject jsonObject = new JSONObject();
                        JSONObject pat_Obj = new JSONObject();
                        try {
                            jsonObject.put("name", list.get(position).getName());
                            jsonObject.put("image", list.get(position).getImage());
                            jsonObject.put("zoeChatId", list.get(position).getZoeChatId());
                            jsonObject.put("mobile", list.get(position).getMobile());
                            pat_Obj.put("participantId", list.get(position).getZoeChatId());
                            jsonObject.put("status", list.get(position).getStatus());
                            participantsList.add(jsonObject);
                            holder.click.setVisibility(View.VISIBLE);
                            sub.setSubtitle(String.valueOf(participantsList.size() + " Selected"));
                            Log.d("addedList", "" + participantsList);
                            //addedAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            CircleImageView user_image;
            TextView user_name, user_status;
            ImageView click;

            public MyViewHolder(View itemView) {
                super(itemView);
                user_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
                user_name = (TextView) itemView.findViewById(R.id.user_name);
                user_status = (TextView) itemView.findViewById(R.id.user_status);
                click = (ImageView) itemView.findViewById(R.id.remove_view_check);
                customView(click, getPrimaryCOlor(ContactActivity.this), getPrimaryCOlor(ContactActivity.this));

            }
        }
    }
}
