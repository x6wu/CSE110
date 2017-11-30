package com.teamruse.rarerare.tritontravel;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class login extends Fragment {
    View myView;
    SignInButton googleButton;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = inflater.inflate(R.layout.login, container, false);

        defineButtons(myView);

        return myView;
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
                    getFragmentManager().beginTransaction().replace(R.id.login_view, new MapFragment())
                            .commit();
                }


        }
    };




}
