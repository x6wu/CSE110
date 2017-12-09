package com.teamruse.rarerare.tritontravel;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by JingJing on 12/8/17.
 */

public class TagDialog extends AppCompatDialogFragment {
    private EditText editTextTag;
    private TagDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.tag_dialog,null);
        builder.setView(view)
                .setTitle("Tag")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("save location", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tag = editTextTag.getText().toString();
                        listener.applyTexts(tag);

                        //FirebaseUser user = MapFragment.mAuth.getCurrentUser();
                    //TODO
                        /*if(this.q.equals(mDestPlace)) {
                            //minor fix to save id
                            mDatabase.child("stops")
                                    .child("stop_id_" + user.getUid())
                                    .push()
                                    .setValue(new StopHistory(mDestPlace.getName().toString(),mDestPlace.getId(),tag));
                        }
                        else if(this.q.equals(mOriginPlace)){
                            mDatabase.child("stops")
                                    .child("stop_id_" + user.getUid())
                                    .push()
                                    .setValue(new StopHistory(mOriginPlace.getName().toString(),mOriginPlace.getId(),tag));
                        }
                        Toast.makeText(getContext(),"Location saved", Toast.LENGTH_SHORT).show();
                        tag = "";

                        Toast.makeText(getContext(),"saved", Toast.LENGTH_SHORT).show();*/
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
