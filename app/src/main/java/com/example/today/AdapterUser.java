package com.example.today;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class AdapterUser  extends RecyclerView.Adapter<AdapterUser.MyHolder>{
    public AdapterUser(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    Context context;
    List<ModelUser> userList;


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.row_users, viewGroup,false );


        return new MyHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myholder, int i) {

        String userImage = userList.get(i).getImage();
        String userName = userList.get(i).getName();
        String userEmail = userList.get(i).getEmail();

        myholder.mNameTv.setText(userName);
        myholder.mEmailTv.setText(userEmail);
        try{
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.default_profile_pic_white)
                    .into(myholder.mAvatarIv);
        }
        catch (Exception e)
        {
        }

        myholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,userEmail,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatarIv;
        TextView mNameTv,mEmailTv;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

        mAvatarIv = itemView.findViewById(R.id.avatarIv);
        mNameTv = itemView.findViewById(R.id.nameTv);
        mEmailTv = itemView.findViewById(R.id.emailTv);


        }
    }
}
