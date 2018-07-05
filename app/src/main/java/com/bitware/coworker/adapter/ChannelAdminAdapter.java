package com.bitware.coworker.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.bitware.coworker.DBHelper.DBHandler;
import com.bitware.coworker.R;
import com.bitware.coworker.activity.AddAdminChannel;
import com.bitware.coworker.activity.ChatActivity;
import com.bitware.coworker.baseUtils.SharedHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by KrishnaDev on 1/5/17.
 */
public class ChannelAdminAdapter extends RecyclerView.Adapter<ChannelAdminAdapter.ViewHolder> {

    public static Dialog dialog;
    Boolean amAdmin = false;
    String group_id;
    String selected_item;
    private JSONArray list;
    private Context myContext;
    private boolean internet;

    public ChannelAdminAdapter(Context activity, JSONArray jsonArray) {
        this.list = jsonArray;
        this.myContext = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.channel_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        JSONObject object = list.optJSONObject(position);
        DBHandler dbHandler = new DBHandler(myContext);

        final String user_image = dbHandler.GetUserImage(object.optString(""));
        final String user_name = dbHandler.GetUserName(object.optString("participantId"));
        String user_status = dbHandler.GetUserStatus(object.optString("participantId"));
        String my_id = SharedHelper.getKey(myContext, "id");


        Log.d("onBindViewHolder: ", "valeus:" + user_image + "," + user_name);

        String from = SharedHelper.getKey(myContext, "id");

        Log.d("onBindViewHolder: ", "values:from:" + from + ",parti_id:" + object.optString("participantId"));

        if (from.equalsIgnoreCase(object.optString("participantId"))) {
            group_id = ChatActivity.groupId_loc;

            if (user_image.equalsIgnoreCase(" ") || user_image.equalsIgnoreCase("")) {
                Picasso.with(myContext).load(R.drawable.ic_account_circle).error(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);
            } else {

                Picasso.with(myContext).load(user_image).error(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);
            }


            holder.user_name.setText(myContext.getResources().getString(R.string.you));
            holder.user_status.setText(user_status);
            holder.user_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else {
            group_id = ChatActivity.groupId_loc;

            if (user_image.equalsIgnoreCase(" ") || user_image.equalsIgnoreCase("")) {
                Picasso.with(myContext).load(R.drawable.ic_account_circle).placeholder(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).error(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);

            } else {
                Picasso.with(myContext).load(user_image).placeholder(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).error(myContext.getResources().getDrawable(R.drawable.ic_account_circle)).into(holder.user_image);
            }
            holder.user_name.setText(user_name);
            holder.user_status.setText(user_status);
            holder.admin.setVisibility(View.GONE);

        }



    }

    @Override
    public int getItemCount() {
        return list.length();
    }
    public static int getPrimaryCOlor (Context context) {
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (R.attr.colorPrimary, value, true);
        return value.data;
    }






    public static void customView(View v, int backgroundColor, int borderColor)
    {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadii(new float[] { 5, 5, 5, 5, 5, 5, 5, 5 });
        shape.setColor(backgroundColor);
        shape.setStroke(3, borderColor);
        v.setBackgroundDrawable(shape);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView user_image;
        TextView user_name, user_status, admin;

        public ViewHolder(View itemView) {
            super(itemView);

            user_image = (CircleImageView) itemView.findViewById(R.id.contact_image);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            user_status = (TextView) itemView.findViewById(R.id.user_status);
            admin = (TextView) itemView.findViewById(R.id.admin);
            customView(admin,myContext.getResources().getColor(R.color.white),getPrimaryCOlor(myContext));
            admin.setTextColor(getPrimaryCOlor(myContext));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DBHandler dbHandler=new DBHandler(myContext);
                    String isAdmin = dbHandler.GetChannelMember(ChatActivity.groupId_loc,list.optJSONObject(getAdapterPosition()).optString("participantId"));
                    final String parti_id = list.optJSONObject(getAdapterPosition()).optString("participantId");
                    final String created_by=list.optJSONObject(getAdapterPosition()).optString("created_by");
                    final String joined_at = list.optJSONObject(getAdapterPosition()).optString("joined_at");
                    String my_id = SharedHelper.getKey(myContext, "id");



                    AlertDialog.Builder builder = new AlertDialog.Builder(myContext,R.style.AlertDialogCustom);
                    builder.setMessage("Are you sure you want to add the person?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    try {
                                        AddAdminChannel.makeadminChannel(parti_id,group_id, (Dialog) dialog,myContext,created_by,joined_at);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();




                }
            });





                }
            }
        }

