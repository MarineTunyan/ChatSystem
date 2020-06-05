package com.example.chat_system.services;

import com.google.firebase.auth.FirebaseAuth;


public class AuthService{
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public FirebaseAuth getAuth() {
        return auth;
    }

    public String getUId(){
       return auth.getCurrentUser().getUid();
    }

    public boolean signOut() {
        try{
        auth.signOut();
        return true;
        }
        catch (Exception e){
            return false;
        }
    }

}
