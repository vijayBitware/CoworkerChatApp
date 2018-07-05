package com.bitware.coworker.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.bitware.coworker.activity.NewBroadCast;
import com.bitware.coworker.activity.QRCodeActivity;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.activity.MainActivity;
import com.bitware.coworker.activity.NewGroup_activity;
import com.bitware.coworker.activity.PersonalChat;
import com.bitware.coworker.activity.SettingsActivity;
import com.bitware.coworker.activity.StarredMessages;
import com.bitware.coworker.adapter.ChatsAdapter;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;

/**
 * Created by KrishnaDev on 12/30/16.
 */
public class ChatFragment extends Fragment implements SearchView.OnQueryTextListener {
    public static ChatFragment mainActivity = null;
    public static Boolean channel_enabled = false;
    //action mode
    public static int counter = 0;
    public static ChatsAdapter chatsAdapter;
    public static String isGroup = "true";
    public static RecyclerView recyclerView;
    public static RelativeLayout errorLayout;
    public static List<JSONObject> chatsList;
    public static DBHandler dbHandler;
    FloatingActionMenu actionButton;
    private List<FloatingActionMenu> menus = new ArrayList<>();
    private FloatingActionButton new_group;
    private FloatingActionButton new_chat;
    private boolean internet;
    private MainActivity main_activity;
    private List<JSONObject> s_chatslist = new ArrayList<>();
    private Handler handler = new Handler();


    public ChatFragment() {
        mainActivity = this;
        setHasOptionsMenu(true);
    }

    public static void refreshLay() {
        try {
            mainActivity.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    chatsList = dbHandler.GetChats();
                    chatsAdapter = new ChatsAdapter(mainActivity.getActivity(), chatsList);
                    recyclerView.setAdapter(chatsAdapter);
                    chatsAdapter.notifyDataSetChanged();

                }
            });
        } catch (Exception e) {
        }
    }

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

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void changecolour(MenuItem menuItem) {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        Log.d("onCreateView: ", "came_home");
        errorLayout = (RelativeLayout) view.findViewById(R.id.error_layout);
        errorLayout.setVisibility(View.GONE);
        actionButton = (FloatingActionMenu) view.findViewById(R.id.fab_action);
        dbHandler = new DBHandler(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.chats_list);
        recyclerView.setHasFixedSize(false);


        actionButton.setMenuButtonColorNormal(getPrimaryCOlor(getActivity()));
        actionButton.setMenuButtonColorPressed(getPrimaryDark(getActivity()));

        new_chat = (FloatingActionButton) view.findViewById(R.id.new_chat);
        new_group = (FloatingActionButton) view.findViewById(R.id.new_group);
        new_chat.setColorNormal(getPrimaryDark(getActivity()));
        new_chat.setColorPressed(getPrimaryDark(getActivity()));
        new_group.setColorNormal(getPrimaryDark(getActivity()));
        new_group.setColorPressed(getPrimaryDark(getActivity()));

        try {

            new_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ad = new Intent(getActivity(), PersonalChat.class);
                    startActivity(ad);

                }
            });
            new_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent group = new Intent(getActivity(), NewGroup_activity.class);
                    isGroup = "false";
                    group.putExtra("forward_msg", "false");
                    group.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(group);
                }
            });
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                    .color(Color.parseColor("#dfdfdf"))
                    .sizeResId(R.dimen.divider)
                    .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                    .build());
            recyclerView.setLayoutManager(linearLayoutManager);
            chatsList = dbHandler.GetChats();
            Log.e("chatList", chatsList.toString());

            if (chatsList.size() > 0) {
                chatsAdapter = new ChatsAdapter(getActivity(), chatsList);
                recyclerView.setAdapter(chatsAdapter);
                errorLayout.setVisibility(View.GONE);
            } else {
                errorLayout.setVisibility(View.VISIBLE);
            }
            dbHandler.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        DBHandler dbHandler = new DBHandler(getActivity());
        chatsList = dbHandler.GetChats();
        dbHandler.close();
        Log.e("chatList", chatsList.toString());

        Collections.sort(chatsList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject jsonObject, JSONObject t1) {
                Log.d("compare:", " values:" + jsonObject.optString("sortTime") + "," + t1.optString("sortTime"));
                return jsonObject.optString("sortTime").compareToIgnoreCase(t1.optString("sortTime"));
            }
        });
        Collections.reverse(chatsList);
        if (chatsList.size() > 0) {
            chatsAdapter = new ChatsAdapter(getActivity(), chatsList);
            recyclerView.setAdapter(chatsAdapter);
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }


    }

    public void refresh() {

        try {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DBHandler dbHandler = new DBHandler(main_activity);
                    chatsList = dbHandler.GetChats();
                    dbHandler.close();
                    Log.e("chatList", chatsList.toString());

                    Collections.sort(chatsList, new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject jsonObject, JSONObject t1) {

                            Log.d("compare: ", "sortvalues:" + jsonObject.optString("sorttime") + "," + t1.optString("sortTime"));
                            return jsonObject.optString("sortTime").compareToIgnoreCase(t1.optString("sortTime"));
                        }
                    });

                    Collections.reverse(chatsList);
                    if (chatsList.size() > 0) {
                        chatsAdapter = new ChatsAdapter(getActivity(), chatsList);
                        recyclerView.setAdapter(chatsAdapter);
                        errorLayout.setVisibility(View.GONE);
                    } else {
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                    MainActivity.unreadCount++;

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        main_activity = (MainActivity) activity;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MainActivity.mainActivity.toolbar.getMenu().clear();
        MainActivity.mainActivity.toolbar.setNavigationIcon(null);
        MainActivity.mainActivity.counterTextView.setText(R.string.app_name);
        inflater.inflate(R.menu.chat_main, menu);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            Drawable drawable = menuItem.getIcon();
            if (drawable != null) {
                // If we don't mutate the drawable, then all drawable's with this id will have a color
                // filter applied to it.
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

            }

        }
        MenuItem menuItem = menu.findItem(R.id.contact_search);
        Refreshfrommain();
        try {
            SearchView view = (SearchView) MenuItemCompat.getActionView(menuItem);
            EditText searchEditText = (EditText) view.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchEditText.setHintTextColor(getActivity().getResources().getColor(R.color.white));

            view.setOnQueryTextListener(this);
            MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
//                if (chatsList.size() > 0)
//
//                    chatsAdapter.setFilter(chatsList);

                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    return true;
                }
            });

        } catch (Exception e) {

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.settings:
                Intent setti_full = new Intent(getActivity(), SettingsActivity.class);
                setti_full.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(setti_full);
                break;

            case R.id.new_channel:
                Intent channel = new Intent(getActivity(), NewGroup_activity.class);
                isGroup = "Channel";
                channel.putExtra("forward_msg", "false");

                channel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(channel);
                break;

            case R.id.web:
                Intent web = new Intent(getActivity(), QRCodeActivity.class);
                web.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(web);
                break;


            case R.id.starred_msgs:
                Intent star = new Intent(getActivity(), StarredMessages.class);
                star.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(star);
                break;

            case R.id.action_lock:
                Utils.showSimpleProgressDialog(getActivity(), "Please wait...", false);
                checkstatus();
                break;

            case R.id.action_un_lock:
                showalertpassunlock();
                break;


            case R.id.new_group:
                Intent group = new Intent(getActivity(), NewGroup_activity.class);
                isGroup = "false";
                group.putExtra("forward_msg", "false");
                group.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(group, 1);
                break;

            case R.id.new_broadcast:
                Intent broad = new Intent(getActivity(), NewBroadCast.class);
                isGroup = "false";
                broad.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(broad);
                break;

            case R.id.action_delete:
                Log.e("Hello", "World");

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);

                builder.setTitle("");

                builder.setMessage("Are you sure you want to delete chats?");

                builder.setPositiveButton(Html.fromHtml("<font color='#FFFFFF'>OK</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletechat();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(Html.fromHtml("<font color='#FFFFFF'>Cancel</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
        }
        return true;
    }

    private void deletechat() {
        new AsyncTask<String, String, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Utils.showSimpleProgressDialog(getActivity(), "Please wait...", false);
            }

            @Override
            protected String doInBackground(String... strings) {
                DBHandler dbHandler = new DBHandler(getActivity());
                if (ChatsAdapter.selectRemoveItem.size() > 0) {
                    for (int i = 0; i < ChatsAdapter.selectRemoveItem.size(); i++) {
                        dbHandler.GroupDeleteUpdate("1", ChatsAdapter.selectRemoveItem.get(i));
                        dbHandler.DeleteChats(ChatsAdapter.selectRemoveItem.get(i));
                        dbHandler.DeleteGrpMsg(ChatsAdapter.selectRemoveItem.get(i));
                        dbHandler.DeleteChatsComplete(ChatsAdapter.selectRemoveItem.get(i));
                    }
                }
                chatsList = dbHandler.GetChats();
                dbHandler.close();
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Utils.removeProgressDialog();
                if (chatsList.size() > 0) {
                    chatsAdapter = new ChatsAdapter(getActivity(), chatsList);
                    recyclerView.setAdapter(chatsAdapter);
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                }
                ChatFragment.mainActivity.clearActionM();
            }
        }.execute();

    }

    private void showalertpassunlock() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.password_layout_forgot);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


        final EditText password, confirm_password;
        Button submit;
        password = (EditText) dialog.findViewById(R.id.password);
        confirm_password = (EditText) dialog.findViewById(R.id.confirm_password);


        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++)
                    if (Character.isSpaceChar(source.charAt(i)))
                        return "";

                return null;
            }
        };

        password.setFilters(new InputFilter[]{filter});
        confirm_password.setFilters(new InputFilter[]{filter});


        submit = (Button) dialog.findViewById(R.id.password_submit);

        TextView forgot_pass = (TextView) dialog.findViewById(R.id.pass_forgot);
        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new hitapiforotp("unlock").execute();
                Utils.showSimpleProgressDialog(main_activity, "Please Wait . . . .", false);
                dialog.dismiss();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!password.getText().toString().equalsIgnoreCase("")) {

                    if (password.getText().toString().equalsIgnoreCase(confirm_password.getText().toString())) {
                        String confirm_password = dbHandler.GetPassword(ChatsAdapter.selectRemoveItem.get(0));

                        if (confirm_password.equalsIgnoreCase(password.getText().toString())) {
                            dbHandler.Updatelock(ChatsAdapter.selectRemoveItem.get(0), "0");
                            dbHandler.UpdatePassword(ChatsAdapter.selectRemoveItem.get(0), "");
                            ChatFragment.refreshLay();
                            refresh();
                            clearActionM();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();

                        }


                    } else {
                        Toast.makeText(getActivity(), "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(getActivity(), "Password Should not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void checkstatus() {
        internet = Utils.isNetworkAvailable(getActivity());
        if (internet) {
            new checkmail().execute();
        } else {
            Toast.makeText(getContext(), "Check your connection", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        chatsList = dbHandler.GetChats();

        try {

            if (newText.length() > 1) {
                s_chatslist = new ArrayList<>();
                Log.d("onQueryTextChange: ", "new_text_val:" + newText);
                List<JSONObject> getUserFromDBs = filter(chatsList, newText);
                Log.d("onQueryTextChange: ", "jsonobjects:" + getUserFromDBs);

                for (int i = 0; i < getUserFromDBs.size(); i++) {
                    Log.d("onQueryTextChange: ", "queriesvalue:" + getUserFromDBs.get(i));
                    JSONObject object = getUserFromDBs.get(i);
                    s_chatslist.add(object);
                }

                if (getUserFromDBs.size() > 0) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    chatsAdapter = new ChatsAdapter(getActivity(), s_chatslist);
                    recyclerView.setAdapter(chatsAdapter);
//                chatsAdapter.setFilter(getUserFromDBs);
                    chatsAdapter.notifyDataSetChanged();
                } else {
                    s_chatslist = new ArrayList<>();
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    chatsAdapter = new ChatsAdapter(getActivity(), s_chatslist);
                    recyclerView.setAdapter(chatsAdapter);
//                chatsAdapter.setFilter(getUserFromDBs);
                    chatsAdapter.notifyDataSetChanged();
                }
            } else {
                s_chatslist = new ArrayList<>();
                s_chatslist = dbHandler.GetChats();
                Collections.sort(s_chatslist, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject jsonObject, JSONObject t1) {
                        return jsonObject.optString("sortTime").compareToIgnoreCase(t1.optString("sortTime"));


                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                chatsAdapter = new ChatsAdapter(getActivity(), s_chatslist);
                recyclerView.setAdapter(chatsAdapter);
//                chatsAdapter.setFilter(getUserFromDBs);
                chatsAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {

        }
        return true;
    }

    private List<JSONObject> filter(List<JSONObject> chatsList, String newText) {
        newText = newText.toLowerCase();
        List<JSONObject> filter_list = new ArrayList<>();
        for (JSONObject getUserFromDB : chatsList) {
            String type_value = getUserFromDB.optString("Name").toLowerCase();
            Log.d("filter: ", "values:" + type_value + "," + newText);
            if (type_value.contains(newText)) {
                Log.d("filter: ", "addedvalues:" + getUserFromDB);
                filter_list.add(getUserFromDB);
            }
        }
        return filter_list;
    }

    public void clearActionM() {

        try {
            ChatsAdapter.is_in_action_mode = false;
            MainActivity.mainActivity.toolbar.getMenu().clear();
            MainActivity.mainActivity.toolbar.inflateMenu(R.menu.chat_main);
            MainActivity.mainActivity.toolbar.setNavigationIcon(null);
            MainActivity.mainActivity.counterTextView.setText(R.string.app_name);
            if (chatsList.size() > 0) {

            } else {
                errorLayout.setVisibility(View.VISIBLE);
            }

            chatsAdapter = new ChatsAdapter(getActivity(), chatsList);
            recyclerView.setAdapter(chatsAdapter);
            ChatsAdapter.selectRemoveItem.clear();
            try {
                ChatsAdapter.clearall();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chatsAdapter.notifyDataSetChanged();
            counter = 0;
        } catch (Exception e) {

        }
        //selectionList.clear();
    }

    public void Refreshfrommain() {

        try {
            ChatsAdapter.is_in_action_mode = false;

            chatsAdapter = new ChatsAdapter(getActivity(), chatsList);
            recyclerView.setAdapter(chatsAdapter);
            ChatsAdapter.selectRemoveItem.clear();
            try {
                ChatsAdapter.clearall();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            chatsAdapter.notifyDataSetChanged();
            counter = 0;
        } catch (Exception e) {

        }
    }

    private void showalert() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.recovery_email);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


        final EditText recovery_email, receovery_phone;
        Button submit;

        recovery_email = (EditText) dialog.findViewById(R.id.recovery_email);
        receovery_phone = (EditText) dialog.findViewById(R.id.recovery_phone);
        submit = (Button) dialog.findViewById(R.id.recovery_submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidEmail(recovery_email.getText().toString())) {
                    if (recovery_email.getText().toString().trim().equalsIgnoreCase("") || receovery_phone.getText().toString().trim().equalsIgnoreCase("")) {
                        Toast.makeText(getActivity(), "Required fields are empty", Toast.LENGTH_SHORT).show();
                    } else {
                        Utils.showSimpleProgressDialog(main_activity, "Please wait. . .", false);
                        new updateemail(recovery_email.getText().toString(), receovery_phone.getText().toString(), dialog).execute();
                    }
                } else {
                    Toast.makeText(main_activity, "Enter Valid email addresses.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void showalertpass(final int i) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        if (i == 1) {
            dialog.setContentView(R.layout.password_layout_forgot);
        } else {
            dialog.setContentView(R.layout.password_layout);

        }
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


        final EditText password, confirm_password;
        Button submit;
        password = (EditText) dialog.findViewById(R.id.password);
        confirm_password = (EditText) dialog.findViewById(R.id.confirm_password);
        submit = (Button) dialog.findViewById(R.id.password_submit);


        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isSpaceChar(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        password.setFilters(new InputFilter[]{filter});
        confirm_password.setFilters(new InputFilter[]{filter});

        if (i == 1) {
            TextView forgot_pass = (TextView) dialog.findViewById(R.id.pass_forgot);
            forgot_pass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new hitapiforotp("normal").execute();
                    Utils.showSimpleProgressDialog(main_activity, "Please wait. . .", false);

                    dialog.dismiss();
                }
            });
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!password.getText().toString().equalsIgnoreCase("")) {
                    if (password.getText().toString().equalsIgnoreCase(confirm_password.getText().toString())) {
                        if (i == 0) {
                            dbHandler.Updatelock(ChatsAdapter.selectRemoveItem.get(0), "1");
                            dbHandler.UpdatePassword(ChatsAdapter.selectRemoveItem.get(0), password.getText().toString());
                            refresh();
                            clearActionM();
                            dialog.dismiss();
                        } else if (i == 3) {
                            dbHandler.Updatelock(ChatsAdapter.selectRemoveItem.get(0), "0");
                            dbHandler.UpdatePassword(ChatsAdapter.selectRemoveItem.get(0), "");
                            ChatFragment.refreshLay();
                            refresh();
                            clearActionM();
                            dialog.dismiss();
                        } else {
                            dbHandler.Updatelock(ChatsAdapter.selectRemoveItem.get(0), "0");
                            dbHandler.UpdatePassword(ChatsAdapter.selectRemoveItem.get(0), "");
                            ChatFragment.refreshLay();
                            refresh();
                            clearActionM();
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Password Should not be empty. Kindly try again", Toast.LENGTH_SHORT).show();

                }

            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    private void showOTP(final String otp_val) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.otp_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


        final EditText otp;
        Button submit;
        otp = (EditText) dialog.findViewById(R.id.otp_edit);

        submit = (Button) dialog.findViewById(R.id.otp_submit);
        otp.setText(otp_val);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp_value = otp.getText().toString();

                if (otp_val.equalsIgnoreCase(otp_value)) {
                    showalertpass(3);
                }
                dialog.dismiss();

            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }


    }

    private void showunlockotp(final String otp_val) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.otp_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


        final EditText otp;
        Button submit;
        otp = (EditText) dialog.findViewById(R.id.otp_edit);

        submit = (Button) dialog.findViewById(R.id.otp_submit);
        otp.setText(otp_val);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp_value = otp.getText().toString();
                Log.d("onClick: ", "otp:" + otp_value + "," + otp_val);
                if (otp_val.equalsIgnoreCase(otp_value)) {
                    dbHandler.Updatelock(ChatsAdapter.selectRemoveItem.get(0), "0");
                    dbHandler.UpdatePassword(ChatsAdapter.selectRemoveItem.get(0), "");
                    dialog.dismiss();
                }


            }
        });

        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    private void showalertunlock() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.setContentView(R.layout.password_layout);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);


        final EditText password, confirm_password;
        Button submit;
        password = (EditText) dialog.findViewById(R.id.password);
        confirm_password = (EditText) dialog.findViewById(R.id.confirm_password);
        submit = (Button) dialog.findViewById(R.id.password_submit);


        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isSpaceChar(source.charAt(i))) {
                        return "";
                    }
                }
                return null;


            }
        };

        password.setFilters(new InputFilter[]{filter});
        confirm_password.setFilters(new InputFilter[]{filter});

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!password.getText().toString().equalsIgnoreCase("")) {
                    if (password.getText().toString().equalsIgnoreCase(confirm_password.getText().toString())) {
                        dbHandler.Updatelock(ChatsAdapter.selectRemoveItem.get(0), "0");
                        dbHandler.UpdatePassword(ChatsAdapter.selectRemoveItem.get(0), "");

                    } else {
                        Toast.makeText(getActivity(), "Password Mismatch. Kindly try again", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Password Should not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }


    private class checkmail extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {


        String result = "";

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", SharedHelper.getKey(getActivity(), "id"));
//                Log.d("doInBackground: ", "posting_values:" + jsonObject);

                if (internet) {
                    new PostHelper(Const.Methods.MAIL_CHECK, jsonObject.toString(), Const.ServiceCode.PARTICIPANTS_DETAILS, getActivity(), this);
                } else {
                    result = "yes";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onTaskCompleted(JSONObject response, int serviceCode) {

            Log.d("onTaskCompleted: ", "" + response);


            if (response.optString("error").equalsIgnoreCase("true")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.removeProgressDialog();

                        showalert();

                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.removeProgressDialog();

                        showalertpass(0);
                    }
                });
            }

        }
    }


    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    private class hitapiforotp extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {


        String result = "";
        String unlock1;

        public hitapiforotp(String unlock) {
            unlock1 = unlock;
        }

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", SharedHelper.getKey(getActivity(), "id"));
//                Log.d("doInBackground: ", "posting_values:" + jsonObject);
                internet = Utils.isNetworkAvailable(getActivity());
                if (internet) {
                    new PostHelper(Const.Methods.OTP_REQUEST, jsonObject.toString(), Const.ServiceCode.PARTICIPANTS_DETAILS, getActivity(), this);
                } else {
                    result = "yes";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onTaskCompleted(final JSONObject response, int serviceCode) {

            Log.d("onTaskCompleted: ", "" + response);
            if (response.optString("error").equalsIgnoreCase("true")) {

            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (unlock1.equalsIgnoreCase("unlock")) {
                            Utils.removeProgressDialog();
                            showunlockotp(response.optString("otp"));
                        } else {
                            Utils.removeProgressDialog();

                            showOTP(response.optString("otp"));
                        }
                    }
                });
            }

        }
    }

    private class updateemail extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {


        String result = "";
        String email = "";
        String password = "";
        Dialog dialogView;

        public updateemail(String email_val, String password_val, Dialog dialog) {
            email = email_val;
            password = password_val;
            dialogView = dialog;
        }

        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("id", SharedHelper.getKey(getActivity(), "id"));
                jsonObject.put("email", email);
                jsonObject.put("phone", password);
//                Log.d("doInBackground: ", "posting_values:" + jsonObject);

                if (internet) {
                    new PostHelper(Const.Methods.UPDATE_MAIL, jsonObject.toString(), Const.ServiceCode.PARTICIPANTS_DETAILS, getActivity(), this);
                } else {
                    result = "yes";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override


        public void onTaskCompleted(JSONObject response, int serviceCode) {

            Log.d("onTaskCompleted: ", "" + response);


            if (response.optString("error").equalsIgnoreCase("false")) {
                dialogView.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.removeProgressDialog();
                        showalertpass(0);
                    }
                });
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_CANCELED) {
                ChatsAdapter.is_in_action_mode = false;
                MainActivity.mainActivity.toolbar.getMenu().clear();
                MainActivity.mainActivity.toolbar.inflateMenu(R.menu.chat_main);
                MainActivity.mainActivity.toolbar.setNavigationIcon(null);
                MainActivity.mainActivity.counterTextView.setText(R.string.app_name);
            }

        }
    }

}
