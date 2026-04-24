package com.example.questify.sync;

import android.content.Context;
import android.util.Log;

import com.example.questify.UserSession;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthenticationManager {

    private static final String TAG = "AuthenticationManager";
    private FirebaseAuth auth;
    private final UserSession userSession;
    private final Context context;
    private boolean initialized = false;

    @Inject
    public AuthenticationManager(UserSession userSession, Context context) {
        this.userSession = userSession;
        this.context = context;
        initFirebase();
    }

    private void initFirebase() {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                try {
                    FirebaseApp.initializeApp(context);
                    Log.d(TAG, "Firebase initialized successfully");
                } catch (Exception e) {
                    Log.e(TAG, "Failed to initialize Firebase", e);
                    return;
                }
            }

            this.auth = FirebaseAuth.getInstance();
            initialized = true;

            if (auth.getCurrentUser() != null) {
                updateSessionFromFirebase(auth.getCurrentUser());
            }
            Log.d(TAG, "Firebase Auth initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Firebase Auth", e);
        }
    }

    private FirebaseAuth getAuth() {
        if (!initialized || auth == null) {
            initFirebase();
        }
        return auth;
    }

    public FirebaseUser getCurrentUser() {
        return getAuth() != null ? getAuth().getCurrentUser() : null;
    }

    public String getCurrentUserId() {
        return getCurrentUser() != null ? getCurrentUser().getUid() : null;
    }

    public Task<AuthResult> signInAnonymously() {
        if (getAuth() == null) {
            throw new IllegalStateException("Firebase not initialized");
        }
        return getAuth().signInAnonymously()
                .addOnSuccessListener(result -> {
                    updateSessionFromFirebase(result.getUser());
                    userSession.setAnonymous(true);
                });
    }

    public Task<AuthResult> signInWithEmail(String email, String password) {
        if (getAuth() == null) {
            throw new IllegalStateException("Firebase not initialized");
        }
        return getAuth().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    updateSessionFromFirebase(result.getUser());
                    userSession.setAnonymous(false);
                });
    }

    public Task<AuthResult> signUpWithEmail(String email, String password) {
        if (getAuth() == null) {
            throw new IllegalStateException("Firebase not initialized");
        }
        return getAuth().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    updateSessionFromFirebase(result.getUser());
                    userSession.setAnonymous(false);
                });
    }

    private void updateSessionFromFirebase(FirebaseUser firebaseUser) {
        if (firebaseUser != null) {
            userSession.setFirebaseUserId(firebaseUser.getUid());
            if (userSession.getUserGlobalId() == null) {
                userSession.setUserGlobalId(java.util.UUID.randomUUID().toString());
            }
        }
    }

    public void signOut() {
        if (getAuth() != null) {
            getAuth().signOut();
        }
        userSession.setFirebaseUserId(null);
        userSession.setAnonymous(false);
    }

    public boolean isAuthenticated() {
        return getAuth() != null && getAuth().getCurrentUser() != null;
    }
}