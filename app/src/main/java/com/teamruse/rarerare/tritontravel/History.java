package com.teamruse.rarerare.tritontravel;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
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

@RequiresApi(api = Build.VERSION_CODES.M)
public class History extends Fragment {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.history, container, false);
        defineButtons(myView);
        return myView;
    }

    public void defineButtons(View view) {
        view.findViewById(R.id.back).setOnClickListener(buttonClickListener);
        view.findViewById(R.id.clear).setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    //startActivity(new Intent(login.this, signup.class));
                    getFragmentManager().beginTransaction().replace(R.id.history_frag, new Home())
                            .commit();
                    break;

                case R.id.clear:
                    //startActivity(new Intent(login.this, signup.class));
                    clearHist();
                    break;
            }
        }

    };

    public void clearHist() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage("Clear all history?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(),"Cleared", Toast.LENGTH_SHORT).show();
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(),"Nvm", Toast.LENGTH_SHORT).show();
            }
        });

        //alert.show();

        AlertDialog dialog = alert.create();
        dialog.show();

        Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        if(b != null) {
            b.setTextColor(Color.parseColor("#064264"));

        }

    }

}
