package com.melvin.quickalert;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RetrieveAdaptor extends RecyclerView.Adapter<RetrieveAdaptor.MyViewHolder> {

    //Variables
    private ArrayList<Model> mList;
    private Context context;

    public RetrieveAdaptor(ArrayList<Model> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.emergency_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Model model = mList.get(position);
        holder.txt_user_name.setText(model.getUserName());
        holder.txt_user_contact.setText(model.getUserNumber());
        holder.txt_emergency_issue.setText(model.getEmergencyMessage());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //Class for fetching the image view in the xml card view layout
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_user_name, txt_user_contact, txt_emergency_issue;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_user_name = itemView.findViewById(R.id.txt_username);
            txt_user_contact = itemView.findViewById(R.id.txt_usercontact);
            txt_emergency_issue = itemView.findViewById(R.id.txt_emergency);
        }
    }

}
