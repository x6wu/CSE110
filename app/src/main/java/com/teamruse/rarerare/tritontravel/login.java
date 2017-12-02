package com.teamruse.rarerare.tritontravel;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.android.gms.common.SignInButton;

//import java.util.concurrent.Executor;


public class login extends Fragment {
    View myView;
    SignInButton googleButton;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final String TAG = "Login_Fragment";
    private static final int RC_SIGN_IN = 9001;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private Fragment thisFrag=this;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = inflater.inflate(R.layout.login, container, false);
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
<<<<<<< HEAD
                .requestIdToken(getString(R.string.default_web_client_id))
=======
                .requestIdToken("745738811309-dssq9ki8f3sekq4d4s2s9e902pqbvhgm.apps.googleusercontent.com")
>>>>>>> c64969566fb307567f594260a52be34f9ae945bc
                .requestEmail()
                .build();
        defineButtons(myView);
        mGoogleSignInClient = GoogleSignIn.getClient(this.getActivity(), gso);
        mAuth = FirebaseAuth.getInstance();

        return myView;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this.getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }



    public void defineButtons(View view) {
        view.findViewById(R.id.back).setOnClickListener(buttonClickListener);

        googleButton = view.findViewById(R.id.login);
        googleButton.setOnClickListener(buttonClickListener);

        //googleButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
    }


    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v){
                if (v.getId() == R.id.back) {
                    ((MainActivity)getActivity()).switchFrag(R.id.back);
                }

                else if (v.getId() == R.id.login) {
                    Log.d("loginFrag", "login()");
                    signIn();
                }


        }
    };
    public void onResume(){
        super.onResume();
        Log.d(TAG, "resume login frag");
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }





}
