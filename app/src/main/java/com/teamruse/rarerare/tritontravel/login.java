package com.teamruse.rarerare.tritontravel;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class login extends Fragment {
    View myView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = inflater.inflate(R.layout.login, container, false);
        defineButtons(myView);
        return myView;
    }

    public void defineButtons(View view) {
        view.findViewById(R.id.signup).setOnClickListener(buttonClickListener);
        view.findViewById(R.id.forget).setOnClickListener(buttonClickListener);
        view.findViewById(R.id.back).setOnClickListener(buttonClickListener);
        view.findViewById(R.id.login).setOnClickListener(buttonClickListener);
    }


    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v){
                switch (v.getId()) {
                    case R.id.signup:
                        //startActivity(new Intent(login.this, signup.class));
                        getFragmentManager().beginTransaction().replace(R.id.login_view, new signup())
                                .commit();
                        break;

                    case R.id.forget:
                        getFragmentManager().beginTransaction().replace(R.id.login_view, new forget())
                                .commit();

                        break;

                    case R.id.back:
                        getFragmentManager().beginTransaction().replace(R.id.login_view, MapFragment.getInstance())
                                .commit();

                        break;

                    case R.id.login:
                        send_click(v);
                        break;
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void send_click (View v) {

        EditText msg = (EditText) myView.findViewById(R.id.email);
        EditText psw = (EditText) myView.findViewById(R.id.password);

        if (msg.getText().toString().equals("")) {
            msg.setError("Required");
        }

        else if (psw.getText().toString().equals("")) {
            psw.setError("Required");
        }


        else if (!isEmailValid(msg.getText().toString())) {
            msg.setError("Invalid email");
        }

        //data base check

        else {
            getFragmentManager().beginTransaction().replace(R.id.login_view, MapFragment.getInstance())
                    .commit();
        }


    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
