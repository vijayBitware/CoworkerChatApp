package com.bitware.coworker.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.activity.MainActivity;
import com.bitware.coworker.activity.SettingsActivity;
import com.bitware.coworker.activity.StarredMessages;
import com.bitware.coworker.adapter.CallsAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by KrishnaDev on 12/30/16.
 */
public class CallsFragment extends Fragment implements SearchView.OnQueryTextListener {
    public static CallsFragment mainActivity = null;
    //action mode
    public static int counter = 0;
    public static CallsAdapter callsAdapter;
    public static RecyclerView recyclerView;
    DBHandler dbHandler;
    private MainActivity main_activity;
    private RelativeLayout errorLayout;
    private List<JSONObject> chatsList;
    private List<JSONObject> s_chatslist;


    public CallsFragment() {
        mainActivity = this;
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        View view = inflater.inflate(R.layout.fragment_calls, container, false);


        errorLayout = (RelativeLayout) view.findViewById(R.id.error_layout);
        ImageView errorImage = (ImageView) errorLayout.findViewById(R.id.image);

        errorImage.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_call_color));


        errorLayout.setVisibility(View.GONE);
        DBHandler dbHandler = new DBHandler(getActivity());
        recyclerView = (RecyclerView) view.findViewById(R.id.chats_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(Color.parseColor("#dfdfdf"))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                .build());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        /*chatsList = dbHandler.GetCalls();
        Log.e("callList", chatsList.toString());

        if (chatsList.size() > 0) {
            callsAdapter = new CallsAdapter(getActivity(), chatsList);
            recyclerView.setAdapter(callsAdapter);
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }*/

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

   public void applyTint(Drawable icon){
        icon.setColorFilter(
                new PorterDuffColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN)
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        DBHandler dbHandler = new DBHandler(getActivity());
        chatsList = dbHandler.GetCalls();
        dbHandler.close();
        Log.e("calls", chatsList.toString());

        Collections.sort(chatsList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject jsonObject, JSONObject t1) {
                return jsonObject.optString("sortTime").compareToIgnoreCase(t1.optString("sortTime"));
            }
        });
        Collections.reverse(chatsList);
        if (chatsList.size() > 0) {
            callsAdapter = new CallsAdapter(getActivity(), chatsList);
            recyclerView.setAdapter(callsAdapter);
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }


    }

    public void refresh() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dbHandler = new DBHandler(getActivity());
                chatsList = dbHandler.GetChats();
                dbHandler.close();
                Log.e("chatList", chatsList.toString());

                Collections.sort(chatsList, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject jsonObject, JSONObject t1) {
                        return jsonObject.optString("sortTime").compareToIgnoreCase(t1.optString("sortTime"));
                    }
                });
                Collections.reverse(chatsList);
                if (chatsList.size() > 0) {
                    callsAdapter = new CallsAdapter(getActivity(), chatsList);
                    recyclerView.setAdapter(callsAdapter);
                    errorLayout.setVisibility(View.GONE);
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                }
            }
        });


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

    /*Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e("onConnectError", "onConnectError");
        }
    };

    Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("onDisconnect", "Disconnect");
                    MyCommon.getInstance().isConnected = false;
                }
            });

        }
    };*/

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MainActivity.mainActivity.toolbar.getMenu().clear();
        MainActivity.mainActivity.toolbar.setNavigationIcon(null);
        MainActivity.mainActivity.counterTextView.setText(R.string.app_name);
        inflater.inflate(R.menu.call_main, menu);
        for (int i=0;i<menu.size();i++)
        {
            MenuItem menuItem=menu.getItem(i);
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
//                    callsAdapter.setFilter(chatsList);
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
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.starred_msgs:
                Intent intent1 = new Intent(getActivity(), StarredMessages.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;


            case R.id.action_delete:
                Log.e("Hello", "World");
                new AsyncTask<String, String, String>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected String doInBackground(String... strings) {

                        if (CallsAdapter.selectRemoveItem.size() > 0) {
                            DBHandler dbHandler = new DBHandler(getActivity());
                            for (int i = 0; i < CallsAdapter.selectRemoveItem.size(); i++) {
                                dbHandler.DeleteCalls(CallsAdapter.selectRemoveItem.get(i), CallsAdapter.selectRemoveItemcall_time.get(i));
                                chatsList = dbHandler.GetCalls();
                            }
                            dbHandler.close();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        if (chatsList.size() > 0) {
                            callsAdapter = new CallsAdapter(getActivity(), chatsList);
                            recyclerView.setAdapter(callsAdapter);
                        } else {
                            errorLayout.setVisibility(View.VISIBLE);
                        }
                        CallsFragment.mainActivity.clearActionM();
                    }
                }.execute();


                break;

        }
        return true;

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        try {

            Log.d("onQueryTextChange: ", "new_text_valu:" + newText);
            if (newText.length() > 0) {
                DBHandler dbHandler = new DBHandler(getActivity());

                chatsList = dbHandler.GetCalls();
                s_chatslist = new ArrayList<>();
                List<JSONObject> getUserFromDBs = filter(chatsList, newText);

                for (int i = 0; i < getUserFromDBs.size(); i++) {
                    Log.d("onQueryTextChange: ", "queriesvalue:" + getUserFromDBs.get(i));
                    JSONObject object = getUserFromDBs.get(i);
                    s_chatslist.add(object);
                }


                if (getUserFromDBs.size() > 0) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                    callsAdapter = new CallsAdapter(getActivity(), s_chatslist);
                    recyclerView.setAdapter(callsAdapter);
//                chatsAdapter.setFilter(getUserFromDBs);
                    callsAdapter.notifyDataSetChanged();
                } else {
                    s_chatslist = new ArrayList<>();
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    callsAdapter = new CallsAdapter(getActivity(), s_chatslist);
                    recyclerView.setAdapter(callsAdapter);
//                chatsAdapter.setFilter(getUserFromDBs);
                    callsAdapter.notifyDataSetChanged();
                }
            } else {
                s_chatslist = new ArrayList<>();
                DBHandler dbHandler = new DBHandler(getActivity());
                s_chatslist = dbHandler.GetCalls();
                dbHandler.close();
                Log.e("calls", s_chatslist.toString());

                Collections.sort(s_chatslist, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject jsonObject, JSONObject t1) {
                        return jsonObject.optString("sortTime").compareToIgnoreCase(t1.optString("sortTime"));
                    }
                });
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                callsAdapter = new CallsAdapter(getActivity(), s_chatslist);
                recyclerView.setAdapter(callsAdapter);
//                chatsAdapter.setFilter(getUserFromDBs);
                callsAdapter.notifyDataSetChanged();
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
            if (type_value.contains(newText)) {
                filter_list.add(getUserFromDB);
            }
        }
        return filter_list;
    }

    public void clearActionM() {
        try {

            if (chatsList.size() > 0) {
                callsAdapter = new CallsAdapter(getActivity(), chatsList);
                recyclerView.setAdapter(callsAdapter);
                errorLayout.setVisibility(View.GONE);
            } else {
                errorLayout.setVisibility(View.VISIBLE);
            }

            CallsAdapter.is_in_action_mode = false;
            MainActivity.mainActivity.toolbar.getMenu().clear();
            MainActivity.mainActivity.toolbar.inflateMenu(R.menu.chat_main);
            MainActivity.mainActivity.toolbar.setNavigationIcon(null);
            MainActivity.mainActivity.counterTextView.setText(R.string.app_name);
            callsAdapter = new CallsAdapter(getActivity(), chatsList);

            if (chatsList.size() > 0) {
                callsAdapter = new CallsAdapter(getActivity(), chatsList);
                recyclerView.setAdapter(callsAdapter);
                errorLayout.setVisibility(View.GONE);
            } else {
                errorLayout.setVisibility(View.VISIBLE);
            }

            recyclerView.setAdapter(callsAdapter);
            CallsAdapter.selectRemoveItem.clear();
            counter = 0;
        } catch (Exception e) {

        }
        //selectionList.clear();
    }

    public  void Refreshfrommain()
    {

        try {

            CallsAdapter.is_in_action_mode = false;
            callsAdapter = new CallsAdapter(getActivity(), chatsList);

            if (chatsList.size() > 0) {
                callsAdapter = new CallsAdapter(getActivity(), chatsList);
                recyclerView.setAdapter(callsAdapter);
                errorLayout.setVisibility(View.GONE);
            } else {
                errorLayout.setVisibility(View.VISIBLE);
            }

            recyclerView.setAdapter(callsAdapter);
            CallsAdapter.selectRemoveItem.clear();
            counter = 0;
        } catch (Exception e) {

        }


    }

}
