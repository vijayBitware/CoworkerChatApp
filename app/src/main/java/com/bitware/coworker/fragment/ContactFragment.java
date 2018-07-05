package com.bitware.coworker.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.activity.MainActivity;
import com.bitware.coworker.activity.NewGroup_activity;
import com.bitware.coworker.activity.SettingsActivity;
import com.bitware.coworker.adapter.CallsAdapter;
import com.bitware.coworker.adapter.ContactAdapter;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.models.ContactsModel;
import com.bitware.coworker.models.GetUserFromDBModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by KrishnaDev on 12/30/16.
 */
public class ContactFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String TAG = ContactFragment.class.getSimpleName();
    public static List<ContactsModel> list_of_contacts;
    public static ContactFragment refresh = null;
    public static MainActivity mainActivity;
    DBHandler dbHandler;
    Context context;
    Button invite_friends;
    private RecyclerView recyclerView;
    private List<GetUserFromDBModel> contact_list;
    private ContactAdapter contactAdapter;
    private String reload_value = "";
    private boolean internet;
    private RelativeLayout errorLayout;

    public ContactFragment() {
        refresh = this;
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String should_contacts_fetch = SharedHelper.getKey(getActivity(), "should_contacts_fetch");
//        if (should_contacts_fetch.equalsIgnoreCase("no"))
//        {
//
//        }
//        else {
        new readAllContacts().execute();
        SharedHelper.putKey(getActivity(), "should_contacts_fetch", "no");
//        }

    }
    public static void customViews(View v, int backgroundColor, int borderColor) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[]{10, 10, 10, 10, 10, 10, 10, 10});
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        v.setBackgroundDrawable(shape);
    }


    public static int getPrimaryCOlor(Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, value, true);
        return value.data;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        internet = Utils.isNetworkAvailable(getActivity());
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        errorLayout = (RelativeLayout) view.findViewById(R.id.error_layout);
        invite_friends = (Button) view.findViewById(R.id.invite_friends);
        mainActivity.progressBar.setVisibility(View.GONE);
        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) view.findViewById(R.id.contact_list);

        customViews(invite_friends, getPrimaryCOlor(getActivity()), getPrimaryCOlor(getActivity()));


        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(Color.parseColor("#dfdfdf"))
                .sizeResId(R.dimen.divider)
                .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                .build());
        invite_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatFragment.isGroup = "Invite";
                Intent invite = new Intent(getActivity(), NewGroup_activity.class);
                startActivity(invite);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHandler = new DBHandler(getActivity());
        list_of_contacts = dbHandler.GetListOfContacts();
        dbHandler.close();
        if (list_of_contacts.size() > 0) {
            Collections.sort(list_of_contacts, new Comparator<ContactsModel>() {
                @Override
                public int compare(ContactsModel getUserFromDB, ContactsModel t1) {
                    return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                }
            });
            contactAdapter = new ContactAdapter(getActivity(), list_of_contacts);
            recyclerView.setAdapter(contactAdapter);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }

//        getAllContacts();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mainActivity = (MainActivity) activity;
        context = activity;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MainActivity.mainActivity.toolbar.getMenu().clear();
        MainActivity.mainActivity.toolbar.setNavigationIcon(null);
        MainActivity.mainActivity.counterTextView.setText(R.string.app_name);
        inflater.inflate(R.menu.contact_main, menu);
        MenuItem menuItem = menu.findItem(R.id.contact_search);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItems = menu.getItem(i);
            Drawable drawable = menuItems.getIcon();
            if (drawable != null) {
                // If we don't mutate the drawable, then all drawable's with this id will have a color
                // filter applied to it.
                drawable.mutate();
                drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

            }

        }
        Refresgfrommain();
        try {
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
            EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchEditText.setHintTextColor(getActivity().getResources().getColor(R.color.white));

            searchView.setOnQueryTextListener(this);
            MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
//                if (list_of_contacts.size() > 0) {
//                    contactAdapter = new ContactAdapter(getActivity(), list_of_contacts);
//                    contactAdapter.setFilter(list_of_contacts);
//                }

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
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {

            case R.id.refresh:
                mainActivity.progressBar.setVisibility(View.VISIBLE);
                SharedHelper.putKey(getActivity(), "reload", reload_value);
                reload_value = "yes";

                new readAllContacts().execute();
                break;

            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.add_contact:
                Intent add_contact = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                startActivity(add_contact);
                break;

            case R.id.invite_friends:
                ChatFragment.isGroup = "Invite";
                Intent invite = new Intent(getActivity(), NewGroup_activity.class);
                startActivity(invite);
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
        List<ContactsModel> getUserFromDBs = filter(list_of_contacts, newText);
        Log.d("onTextChangecontact: ", "new_text_val:" + newText);

        Log.d("onTextChangecontact: ", "jsonobjects:" + getUserFromDBs);

        try {
            if (newText.length() > 0) {

                if (getUserFromDBs.size() > 0) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    Collections.sort(getUserFromDBs, new Comparator<ContactsModel>() {
                        @Override
                        public int compare(ContactsModel getUserFromDB, ContactsModel t1) {
                            return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                        }
                    });

                    contactAdapter = new ContactAdapter(getActivity(), getUserFromDBs);
                    recyclerView.setAdapter(contactAdapter);
//                contactAdapter.setFilter(getUserFromDBs);
                    contactAdapter.notifyDataSetChanged();
                } else {
                    getUserFromDBs = new ArrayList<>();
                    Collections.sort(getUserFromDBs, new Comparator<ContactsModel>() {
                        @Override
                        public int compare(ContactsModel getUserFromDB, ContactsModel t1) {
                            return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                        }
                    });
                    contactAdapter = new ContactAdapter(getActivity(), getUserFromDBs);
                    recyclerView.setAdapter(contactAdapter);
//                contactAdapter.setFilter(getUserFromDBs);
                    contactAdapter.notifyDataSetChanged();

                }


            } else {
                list_of_contacts = new ArrayList<>();
                list_of_contacts = dbHandler.GetListOfContacts();
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                Collections.sort(list_of_contacts, new Comparator<ContactsModel>() {
                    @Override
                    public int compare(ContactsModel getUserFromDB, ContactsModel t1) {
                        return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                    }
                });
                contactAdapter = new ContactAdapter(getActivity(), list_of_contacts);
                recyclerView.setAdapter(contactAdapter);
//                contactAdapter.setFilter(getUserFromDBs);
                contactAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {

        }

        return true;
    }


    private List<ContactsModel> filter(List<ContactsModel> list, String query) {
        query = query.toLowerCase();
        List<ContactsModel> filter_list = new ArrayList<>();
        for (ContactsModel getUserFromDB : list) {
            String type_value = getUserFromDB.getName().toLowerCase();
            if (type_value.contains(query)) {
                filter_list.add(getUserFromDB);
            }
        }
        return filter_list;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void clearActionM() {
        try {
            CallsAdapter.is_in_action_mode = false;
            MainActivity.mainActivity.toolbar.getMenu().clear();
            MainActivity.mainActivity.toolbar.inflateMenu(R.menu.contact_main);
            MainActivity.mainActivity.toolbar.setNavigationIcon(null);
            MainActivity.mainActivity.counterTextView.setText(R.string.app_name);
            if (list_of_contacts.size() > 0) {
            } else {
                errorLayout.setVisibility(View.VISIBLE);
            }

            list_of_contacts = new ArrayList<>();
            list_of_contacts = dbHandler.GetListOfContacts();
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            Collections.sort(list_of_contacts, new Comparator<ContactsModel>() {
                @Override
                public int compare(ContactsModel getUserFromDB, ContactsModel t1) {
                    return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                }
            });

            if (list_of_contacts.size() > 0) {
            } else {
                errorLayout.setVisibility(View.VISIBLE);
            }
            contactAdapter = new ContactAdapter(getActivity(), list_of_contacts);
            recyclerView.setAdapter(contactAdapter);
            contactAdapter.notifyDataSetChanged();
        } catch (Exception e) {

        }

    }

    public void Refresgfrommain() {
        try {

            list_of_contacts = new ArrayList<>();
            list_of_contacts = dbHandler.GetListOfContacts();
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            Collections.sort(list_of_contacts, new Comparator<ContactsModel>() {
                @Override
                public int compare(ContactsModel getUserFromDB, ContactsModel t1) {
                    return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                }
            });

            if (list_of_contacts.size() > 0) {
                contactAdapter = new ContactAdapter(getActivity(), list_of_contacts);
                recyclerView.setAdapter(contactAdapter);
                contactAdapter.notifyDataSetChanged();
            } else {
                errorLayout.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {

        }
    }


    private class readAllContacts extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (reload_value.equalsIgnoreCase("yes"))
                mainActivity.progressBar.setVisibility(View.VISIBLE);
            else {
                try {


                    mainActivity.progressBar.setVisibility(View.GONE);
                } catch (NullPointerException e) {
                }
                reload_value = "";
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                DBHandler dbHandler = new DBHandler(getActivity());
                ContentResolver cr = getActivity().getContentResolver();
                Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                        null, null, null, null);

                if (cur.getCount() > 0) {
                    while (cur.moveToNext()) {
                        String id = cur.getString(
                                cur.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cur.getString(cur.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME));

                        if (cur.getInt(cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                            Cursor pCur = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id}, null);

                            while (pCur.moveToNext()) {
                                String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                String log = "Name: " + name + ", Phone No: " + phoneNo;
                                //Log.e("log", "" + log);
                                String rp_phone;
                                if (phoneNo.contains("+")) {
                                    rp_phone = phoneNo.replaceAll("[\\-\\+\\.\\^:,\\s+\\(\\)\\#\\*]", "");
                                } else {
                                    String number = MainActivity.my_country_code;
                                    rp_phone = number + phoneNo;
                                    rp_phone = rp_phone.replaceAll("[\\-\\+\\.\\^:,\\s+\\(\\)\\#\\*]", "");

                                }

                                if (rp_phone.length() < 14) {
                                    if (rp_phone.length() > 3) {
                                        try {

                                            String registerNumber = MainActivity.my_id;
                                            registerNumber = registerNumber.substring(registerNumber.length() - 10);
                                            if (!rp_phone.equalsIgnoreCase(registerNumber)) {
                                                if (!dbHandler.CheckIsDataAlreadyInDBorNot(rp_phone)) {
                                                    dbHandler.InsertUser(new ContactsModel(rp_phone, name, "", "", "", 0, "", id, false));
                                                } else {
                                                    dbHandler.UserNameUpdate(name, rp_phone);
                                                }
                                            }
                                        } catch (NullPointerException e) {

                                        }
                                    }
                                }

                            }
                            pCur.close();
                        }
                    }
                }
                cur.close();
                dbHandler.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new getContacts().execute();
            mainActivity.progressBar.setVisibility(View.GONE);
        }

    }

    private class getContacts extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {
        String result = "";

        @Override
        protected String doInBackground(String... strings) {
            JSONArray jsonArray = new JSONArray();
            DBHandler dbHandler = new DBHandler(getActivity());
            try {


                contact_list = dbHandler.GetAllUserFromDB();

//                Log.d(TAG, "doInBackground: " + contact_list);


                //Utils.appLog("jsonArray", contact_list.toString());

                for (int i = 0; i < contact_list.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        if (!contact_list.get(i).getMobile().equalsIgnoreCase(MainActivity.my_id)) {
                            jsonObject.put("mobileNumber", contact_list.get(i).getMobile());
                            jsonObject.put("contactName", contact_list.get(i).getName());
//                            Log.d(TAG, "doInBackground: "+jsonObject);
                            jsonArray.put(jsonObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Collections.sort(contact_list, new Comparator<GetUserFromDBModel>() {
                    @Override
                    public int compare(GetUserFromDBModel getUserFromDB, GetUserFromDBModel t1) {
                        return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            //Utils.appLog("get_contact_FromDB", jsonArray.toString());
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("contacts", jsonArray.toString());
                jsonObject.put("from", MainActivity.my_id);
//                Utils.appLog("Contact_log", jsonArray.toString());
                if (internet) {
                    new PostHelper(Const.Methods.SYNC_CONTACTS, jsonObject.toString(), Const.ServiceCode.SYNC_CONTACTS, getActivity(), this);
                } else {
                    result = "yes";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new printListOfContacts().execute();
        }

        @Override
        public void onTaskCompleted(final JSONObject response, int serviceCode) {
            switch (serviceCode) {
                case Const.ServiceCode.SYNC_CONTACTS:
//                    Utils.appLog("Contact_Response", response.toString());
                    if (response != null) {
                        if (response.optString("error").equalsIgnoreCase("false")) {
                            JSONArray jsonArray = response.optJSONArray("contacts");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.optJSONObject(i);
                                String mobile_id = jsonObject.optString("mobileNumber");
                                DBHandler dbHandler = new DBHandler(getActivity());
                                try {
                                    if (!dbHandler.CheckIsDataAlreadyInDBorNot(mobile_id)) {
                                        if (jsonObject.optString("showInContactsPage").equalsIgnoreCase("true")) {
                                            dbHandler.InsertUser(new ContactsModel(mobile_id, jsonObject.optString("name"), jsonObject.optString("image"), "", jsonObject.optString("status"), 1, "", jsonObject.optString("contactid"), false));
                                        } else {
                                            if (dbHandler.UserUpdate(mobile_id, jsonObject.optString("name"), jsonObject.optString("image"), "1", jsonObject.optString("zoechatid"), jsonObject.optString("status")) > 0) {
                                            } else {
                                                dbHandler.InsertUser(new ContactsModel(mobile_id, jsonObject.optString("name"), "", "", jsonObject.optString("status"), 0, "", jsonObject.optString("contactid"), false));
                                            }
                                        }

                                    } else if (jsonObject.optString("showInContactsPage").equalsIgnoreCase("true")) {
                                        dbHandler.UserUpdate(mobile_id, jsonObject.optString("name"), jsonObject.optString("image"), "1", jsonObject.optString("zoechatid"), jsonObject.optString("status"));
                                    }
                                    dbHandler.close();
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }

                            new printListOfContacts().execute();
                        } else {
                            try {

                                mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mainActivity, response.optString("message"), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (NullPointerException e) {

                            }
                            //new printListOfContacts().execute();
                        }
                    } else {
                        try {
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mainActivity, response.optString("message"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (NullPointerException e) {

                        }
                        //new printListOfContacts().execute();
                    }

                    break;
            }

        }
    }

    private class printListOfContacts extends AsyncTask<String, String, String> {
        DBHandler dbHandler;

        printListOfContacts() {
            dbHandler = new DBHandler(getActivity());
        }

        @Override
        protected String doInBackground(String... strings) {

            list_of_contacts = dbHandler.GetListOfContacts();
            dbHandler.close();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mainActivity.progressBar.setVisibility(View.GONE);
            if (reload_value.equalsIgnoreCase("yes")) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), "Your contacts list has been updated", Toast.LENGTH_SHORT).show();
                reload_value = "no";
            }
            if (list_of_contacts.size() > 0) {
                Collections.sort(list_of_contacts, new Comparator<ContactsModel>() {
                    @Override
                    public int compare(ContactsModel getUserFromDB, ContactsModel t1) {
                        return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                    }
                });

                contactAdapter = new ContactAdapter(getActivity(), list_of_contacts);
                recyclerView.setAdapter(contactAdapter);
            } else {
                errorLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

        }
    }
}
