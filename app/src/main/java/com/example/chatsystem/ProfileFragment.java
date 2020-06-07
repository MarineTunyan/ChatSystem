package com.example.chatsystem;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chatsystem.R;
import com.example.chatsystem.services.AuthService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class ProfileFragment extends Fragment {
    /////////////////

    private ImageView imageView;
    private CircularImageView circularImageView;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    StorageReference userRef;
    ///////////////////
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView email, fullName, Company, birthday, phone;
    Button signOut;
    private AuthService authService = new AuthService();
    BottomNavigationView bottomNavigation;
    CollectionReference reference;
    FirebaseUser firebaseUser= authService.getAuth().getCurrentUser();


    private String mParam1;
    private String mParam2;
    private StorageReference imageRef;


//    public ProfileFragment() {
//    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readUserData();

        Log.d("ID", LoginPage.getUserId());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Log.d("ID", "email : " + email);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /////////////


        /////////////
        // Inflate the layout for this fragment
        View RootView = inflater.inflate(R.layout.fragment_profile, container, false);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        imageView=RootView.findViewById(R.id.imageView);
        circularImageView= RootView.findViewById(R.id.profile_image);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();

            }
        });



        signOut = RootView.findViewById(R.id.sign_out);
        fullName = RootView.findViewById(R.id.fullName);
        birthday = RootView.findViewById(R.id.birthDay);
        phone = RootView.findViewById(R.id.phone);
        imageRef = storageReference.child("images");
        imageRef.child(authService.getAuth().getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getContext() /* context */)
                        .load(uri)
                        .into(imageView);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (authService.signOut()) {
                    LoginPage.setUserLoggedIn(false);
                    Intent intent = new Intent(getContext(), LoginPage.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    LoginPage.setUserId(null);
                    getActivity().startActivity(intent);
                } else {
                    Log.w("Mash", "signOut:failure");
                }
            }
        });
        return RootView;
    }

    public void readUserData() {
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d("document_id", (String) Objects.requireNonNull(document.getData().get("email")));
                                Log.d("user_id", LoginPage.getUserId());
                                if (document.getId().contains(LoginPage.getUserId())) {
                                    fullName.setText((String) document.getData().get("fullName"));
                                    birthday.setText((String) document.getData().get("birthday"));
                                    phone.setText((String) document.getData().get("phone"));

                                    break;
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void chooseImage() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), PICK_IMAGE_REQUEST);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("/images/" + authService.getAuth().getCurrentUser().getUid());
            Log.d("Path", String.valueOf(filePath));
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

//    private void uploadImage() {
//
//    }



}