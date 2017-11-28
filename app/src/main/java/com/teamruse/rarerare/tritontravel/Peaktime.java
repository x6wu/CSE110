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
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by JingJing on 11/13/17.
 */

public class Peaktime extends Fragment {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.peaktime, container, false);
        defineButtons(myView);

        Button wrong = (Button) myView.findViewById(R.id.wrong_alert);
        wrong.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick (View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("The system has recieved your feedback and will update peak time alert imformation. Thank you!");
                alert.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),"Yeah", Toast.LENGTH_SHORT).show();

                    }
                });
                alert.show();
            }
        });


        Button no = (Button) myView.findViewById(R.id.no_alert);
        no.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick (View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setMessage("The system has recieved your feedback and will update peak time alert imformation. Thank you!");
                alert.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(),"kk", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.show();
            }
        });

        return myView;
    }

    public void defineButtons(View view) {
        view.findViewById(R.id.back).setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.back) {

                //startActivity(new Intent(login.this, signup.class));
                getFragmentManager().beginTransaction().replace(R.id.peak_frag, MapFragment.getInstance())
                        .commit();

            }
        }

    };
}
