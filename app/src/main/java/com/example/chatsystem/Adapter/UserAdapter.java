package com.example.chatsystem.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatsystem.MessageActivity;
import com.example.chatsystem.R;
import com.example.chatsystem.User;
import com.example.chatsystem.services.AuthService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    private FirebaseStorage storage;
    private StorageReference storageReference;

    private Context mContext;
    private List<User> mUsers;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public UserAdapter(Context mContext, List<User> mUsers) {
        this.mUsers = mUsers;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        AuthService authService = new AuthService();
        final User user = mUsers.get(position);
        holder.username.setText(user.getUsername());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        StorageReference imageRef = storageReference.child("images");
        imageRef.child(user.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext /* context */)
                        .load(uri)
                        .into(holder.profImage);
            }
        });

        holder.profImage.setImageResource(R.drawable.profile_account);

        if (user.getStatus().equals("Online")) {
            Log.d("ERRROORR", "lkkkljkhfgcf");
            holder.img_on.setVisibility(View.VISIBLE);
            holder.img_off.setVisibility(View.INVISIBLE);
        } else {
            holder.img_on.setVisibility(View.INVISIBLE);
            holder.img_off.setVisibility(View.VISIBLE);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userId", user.getId());
                intent.putExtra("userName", user.getUsername());
                intent.putExtra("image", user.getImageURL());
                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mUsers.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profImage;
        private ImageView img_on;
        private ImageView img_off;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profImage = itemView.findViewById(R.id.prof_image);
            img_on = itemView.findViewById(R.id.img_on);
            img_off = itemView.findViewById(R.id.img_off);

        }


    }

}
