package com.teamruse.rarerare.tritontravel;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by JingJing on 11/13/17.
 */

public class Feedback extends Fragment {

    View myView;
    private Fragment currFrag=this;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.feedback, container, false);
        defineButtons(myView);
        return myView;
    }

    public void defineButtons(View view) {
        view.findViewById(R.id.back).setOnClickListener(buttonClickListener);
        view.findViewById(R.id.send_fb).setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    ((MainActivity)getActivity()).switchFrag(R.id.back);
                    break;

                case R.id.send_fb:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        send_click(myView);
                    } else {
                        // 1. Instantiate an AlertDialog.Builder with its constructor
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        // 2. Chain together various setter methods to set the dialog characteristics
                        builder.setMessage("This feature requires API lv.23.")
                                .setTitle("API level is too low!");

                        // 3. Get the AlertDialog from create()
                        AlertDialog dialog = builder.create();
                    }
                    break;
            }
        }

    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void send_click(View v) {

        EditText msg = (EditText) myView.findViewById(R.id.msg);
        EditText subject = (EditText) myView.findViewById(R.id.sub);


       if (subject.getText().toString().equals("")) {
            subject.setError("Requested field");
        }
        else if (msg.getText().toString().equals("")) msg.setError("Requested field");

        else {
            Intent i = new Intent(Intent.ACTION_SENDTO);
            i.setData(Uri.parse("mailto:"));
            i.putExtra(Intent.EXTRA_EMAIL, new String[] {"ruse_ucsd@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
            i.putExtra(Intent.EXTRA_TEXT, msg.getText().toString());

            try {
                startActivity(Intent.createChooser(i,"send mail"));
            }
            catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getContext(), "No mail app found", Toast.LENGTH_SHORT).show();
            }
            catch (Exception ex) {
                Toast.makeText(getContext(), "Unexpected Error" + ex,  Toast.LENGTH_SHORT).show();
            }

        }

    }
}
