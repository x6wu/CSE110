package com.teamruse.rarerare.tritontravel;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by JingJing on 11/13/17.
 */

public class Faq extends Fragment {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        myView = inflater.inflate(R.layout.faq, container, false);


        defineButtons(myView);
        return myView;
    }

    public void defineButtons(View view) {
        view.findViewById(R.id.back).setOnClickListener(buttonClickListener);
        view.findViewById(R.id.ask).setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    getFragmentManager().beginTransaction().replace(R.id.faq_frag, new MapFragment())
                            .commit();
                    break;

                case R.id.ask:
                    getFragmentManager().beginTransaction().replace(R.id.faq_frag, new Feedback())
                            .commit();
                    break;
            }
        }

    };
}
