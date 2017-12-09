package com.teamruse.rarerare.tritontravel;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

/**
 * Created by JingJing on 12/8/17.
 */

public class TagDialog extends AppCompatDialogFragment {
    private EditText editTextTag;
    private TagDialogListener listener;
    private final static String TAG = "TagDialog";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.tag_dialog,null);
        String btnText="save location";
        if (getArguments().getString("type")!=null
                &&getArguments().getString("type").equals("route")){
            btnText="save route";
            ((TextView)(view.findViewById(R.id.edit_tag))).setHint("tag this route (optional)");
        }
        builder.setView(view)
                .setTitle("Tag")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tag = editTextTag.getText().toString();
                        listener.applyTexts(tag);


                        try {
                            //Ruoyu Xu save stop with tag. Austin Moss-Ennis check for duplicates before adding
                            String destOrOrigin = getArguments().getString("destOrOrigin");
                            if (destOrOrigin.equals("dest")) {
                                ((MapFragment) getParentFragment()).writeDestToDB(tag);
                                Toast.makeText(getContext(), "Location saved", Toast.LENGTH_SHORT).show();
                            } else if (destOrOrigin.equals("origin")) {
                                ((MapFragment) getParentFragment()).writeOriginToDB(tag);
                                Toast.makeText(getContext(), "Location saved", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.i(TAG, "Saving Route, no dest or ori is available.");
                            Log.i(TAG, "The new tag for route is:" +tag);
                            ((MapFragment) getParentFragment()).writeRouteToDB();
                            Toast.makeText(getContext(), "Route saved", Toast.LENGTH_LONG).show();
                        }

                    }
                });

        editTextTag = view.findViewById(R.id.edit_tag);
        AlertDialog dialog = builder.create();
        dialog.show();

        Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

        if(b != null) {
            b.setTextColor(Color.parseColor("#064264"));

        }

        return dialog;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (TagDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement TagDialogListener");
        }


    }

    public interface TagDialogListener{
        void applyTexts(String tag);
    }
}
