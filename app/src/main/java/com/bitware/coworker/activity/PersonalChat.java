package com.bitware.coworker.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.adapter.CallsAdapter;
import com.bitware.coworker.adapter.ContactAdapter;
import com.bitware.coworker.baseUtils.AsyncTaskCompleteListener;
import com.bitware.coworker.baseUtils.Const;
import com.bitware.coworker.baseUtils.PostHelper;
import com.bitware.coworker.baseUtils.SharedHelper;
import com.bitware.coworker.baseUtils.Utils;
import com.bitware.coworker.fragment.ContactFragment;
import com.bitware.coworker.models.ContactsModel;
import com.bitware.coworker.models.GetUserFromDBModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PersonalChat extends AppCompatActivity {

    private static final String TAG = ContactFragment.class.getSimpleName();
    public static List<ContactsModel> list_of_contacts;
    public static ContactFragment refresh = null;

    DBHandler dbHandler;
    public static MainActivity mainActivity;
    private RecyclerView recyclerView;
    private List<GetUserFromDBModel> contact_list;
    private ContactAdapter contactAdapter;
    private String reload_value = "";
    private boolean internet;
    private RelativeLayout errorLayout;
    Context context;


    public static int getPrimaryCOlor (Context context) {
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (R.attr.colorPrimary, value, true);
        return value.data;
    }
    public static int getPrimaryDark (Context context) {
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (R.attr.colorPrimaryDark, value, true);
        return value.data;
    }

    public static void customView(View v, int backgroundColor, int borderColor)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[] { 100, 100, 100, 100, 100, 100, 100, 100 });
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        v.setBackgroundDrawable(shape);
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String themevalue=SharedHelper.getKey(this,"theme_value");
        Setheme(themevalue);
        setContentView(R.layout.activity_personal_chat);

        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setBackgroundColor(getPrimaryCOlor(PersonalChat.this));




        toolbar.setTitle("Start a new chat");
        toolbar.setTitleTextColor(Color.WHITE);

 
            String should_contacts_fetch=  SharedHelper.getKey(PersonalChat.this, "should_contacts_fetch");
//        if (should_contacts_fetch.equalsIgnoreCase("no"))
//        {
//
//        }
//        else {
            new readAllContacts().execute();
            SharedHelper.putKey(PersonalChat.this,"should_contacts_fetch","no");
//        }

        

            internet = Utils.isNetworkAvailable(PersonalChat.this);
           
            errorLayout = (RelativeLayout) findViewById(R.id.error_layout);
            recyclerView = (RecyclerView) findViewById(R.id.contact_list);


            recyclerView.setHasFixedSize(true);
            recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(PersonalChat.this)
                    .color(Color.parseColor("#dfdfdf"))
                    .sizeResId(R.dimen.divider)
                    .marginResId(R.dimen.leftmargin, R.dimen.rightmargin)
                    .build());


            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PersonalChat.this);
            recyclerView.setLayoutManager(linearLayoutManager);

    

 
            dbHandler = new DBHandler(PersonalChat.this);
            list_of_contacts = dbHandler.GetListOfContacts();
            dbHandler.close();
            if (list_of_contacts.size() > 0) {
                Collections.sort(list_of_contacts, new Comparator<ContactsModel>() {
                    @Override
                    public int compare(ContactsModel getUserFromDB, ContactsModel t1) {
                        return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                    }
                });
                contactAdapter = new ContactAdapter(PersonalChat.this, list_of_contacts);
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

        private class readAllContacts extends AsyncTask<String, String, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (reload_value.equalsIgnoreCase("yes")){}
//                    mainActivity.progressBar.setVisibility(View.VISIBLE);
                else {
                    reload_value = "";
                }
            }

            @Override
            protected String doInBackground(String... strings) {
                DBHandler dbHandler = new DBHandler(PersonalChat.this);
                ContentResolver cr = PersonalChat.this.getContentResolver();
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
                                if (phoneNo.contains("+"))
                                {
                                    rp_phone = phoneNo.replaceAll("[\\-\\+\\.\\^:,\\s+\\(\\)\\#\\*]", "");
                                }
                                else
                                {
                                    String number=SharedHelper.getKey(context,"country_code_default");
                                    rp_phone=number+phoneNo;
                                    rp_phone = rp_phone.replaceAll("[\\-\\+\\.\\^:,\\s+\\(\\)\\#\\*]", "");

                                }

                                if (rp_phone.length() < 14) {
                                    if (rp_phone.length() > 3) {
                                        try {

                                            String registerNumber = SharedHelper.getKey(PersonalChat.this, "id");
                                            registerNumber = registerNumber.substring(registerNumber.length() - 10);
                                            if (!rp_phone.equalsIgnoreCase(registerNumber)) {
                                                if (!dbHandler.CheckIsDataAlreadyInDBorNot(rp_phone)) {
                                                    dbHandler.InsertUser(new ContactsModel(rp_phone, name, "", "", "", 0, "", id,false));
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
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                new getContacts().execute();
            }

        }

        private class getContacts extends AsyncTask<String, String, String> implements AsyncTaskCompleteListener {
            String result = "";

            @Override
            protected String doInBackground(String... strings) {
                JSONArray jsonArray = new JSONArray();
                DBHandler dbHandler = new DBHandler(PersonalChat.this);
                try {


                    contact_list = dbHandler.GetAllUserFromDB();


                    //Utils.appLog("jsonArray", contact_list.toString());

                    for (int i = 0; i < contact_list.size(); i++) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("mobileNumber", contact_list.get(i).getMobile());
                            jsonObject.put("contactName", contact_list.get(i).getName());
                            jsonArray.put(jsonObject);
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
                    jsonObject.put("from",SharedHelper.getKey(PersonalChat.this.getApplicationContext() , "id"));
//                    Utils.appLog("Contact_log", jsonArray.toString());
                    if (internet) {
                        new PostHelper(Const.Methods.SYNC_CONTACTS, jsonObject.toString(), Const.ServiceCode.SYNC_CONTACTS, PersonalChat.this, this);
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
//                        Utils.appLog("Contact_Response", response.toString());
                        if (response != null) {
                            if (response.optString("error").equalsIgnoreCase("false")) {
                                JSONArray jsonArray = response.optJSONArray("contacts");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                                    String mobile_id = jsonObject.optString("mobileNumber");
                                    DBHandler dbHandler = new DBHandler(PersonalChat.this);

                                    if (!dbHandler.CheckIsDataAlreadyInDBorNot(mobile_id)) {
                                        if (jsonObject.optString("showInContactsPage").equalsIgnoreCase("true")) {
                                            dbHandler.InsertUser(new ContactsModel(mobile_id, jsonObject.optString("name"), jsonObject.optString("image"), "", jsonObject.optString("status"), 1, "", jsonObject.optString("contactid"),false));
                                        } else {
                                            if (dbHandler.UserUpdate(mobile_id, jsonObject.optString("name"), jsonObject.optString("image"), "1", jsonObject.optString("zoechatid"), jsonObject.optString("status")) > 0) {
                                            } else {
                                                dbHandler.InsertUser(new ContactsModel(mobile_id, jsonObject.optString("name"), "", "", jsonObject.optString("status"), 0, "", jsonObject.optString("contactid"),false));
                                            }
                                        }

                                    }
                                    else if (jsonObject.optString("showInContactsPage").equalsIgnoreCase("true")) {
                                        dbHandler.UserUpdate(mobile_id, jsonObject.optString("name"), jsonObject.optString("image"), "1", jsonObject.optString("zoechatid"), jsonObject.optString("status"));
                                    }
                                    dbHandler.close();
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



    public void clearActionM() {
        CallsAdapter.is_in_action_mode = false;
        MainActivity.mainActivity.toolbar.getMenu().clear();
        MainActivity.mainActivity.toolbar.inflateMenu(R.menu.contact_main);
        MainActivity.mainActivity.toolbar.setNavigationIcon(null);
        MainActivity.mainActivity.counterTextView.setText(R.string.app_name);
        list_of_contacts = new ArrayList<>();
        list_of_contacts = dbHandler.GetListOfContacts();
        recyclerView.setLayoutManager(new LinearLayoutManager(PersonalChat.this));
        Collections.sort(list_of_contacts, new Comparator<ContactsModel>() {
            @Override
            public int compare(ContactsModel getUserFromDB, ContactsModel t1) {
                return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
            }
        });
        contactAdapter = new ContactAdapter(PersonalChat.this, list_of_contacts);
        recyclerView.setAdapter(contactAdapter);
//                contactAdapter.setFilter(getUserFromDBs);
        contactAdapter.notifyDataSetChanged();
        //selectionList.clear();
    }


    private class printListOfContacts extends AsyncTask<String, String, String> {
        DBHandler dbHandler;

        printListOfContacts() {
            dbHandler = new DBHandler(PersonalChat.this);
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
            if (reload_value.equalsIgnoreCase("yes")) {
                if (PersonalChat.this != null)
                    Toast.makeText(PersonalChat.this, "Your contacts list has been updated", Toast.LENGTH_SHORT).show();
                reload_value = "no";
            }
            if (list_of_contacts.size() > 0) {
                Collections.sort(list_of_contacts, new Comparator<ContactsModel>() {
                    @Override
                    public int compare(ContactsModel getUserFromDB, ContactsModel t1) {
                        return getUserFromDB.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                    }
                });

                contactAdapter = new ContactAdapter(PersonalChat.this, list_of_contacts);
                recyclerView.setAdapter(contactAdapter);
            } else {
                errorLayout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

        }
    }
    }

