package com.teamruse.rarerare.tritontravel;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class forget extends Fragment {

    View myView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        myView = inflater.inflate(R.layout.forget, container, false);
        defineButtons(myView);
        return myView;
    }

    public void defineButtons(View view) {
        view.findViewById(R.id.back).setOnClickListener(buttonClickListener);
        view.findViewById(R.id.reset).setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.back:
                    getFragmentManager().beginTransaction().replace(R.id.forget_frag, new login())
                            .commit();
                    break;

                case R.id.reset:
                    send_click(v);
                    break;
            }

        }

    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void send_click (View v) {

        EditText msg = (EditText) myView.findViewById(R.id.email);
        if (msg.getText().toString().equals("")) {
            msg.setError("Requested field");
        }
        else if (!isEmailValid(msg.getText().toString())) {
            msg.setError("Invalid email");
        }

        else {
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setMessage("Please check your email for further instructions");
            alert.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getContext(),"Lol", Toast.LENGTH_SHORT).show();
                }
            });
                    alert.show();
        }


    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


}
