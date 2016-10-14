package com.seng480b.bumerang;


import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;


public class BorrowDialogFragment extends DialogFragment {
    //private Button cancelButton, acceptButton;
    public BorrowDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_borrow_dialog, container, false);
        getDialog().setTitle("-MORE INFORMATION- DIALOG");

        ImageButton cancelButton = (ImageButton)rootView.findViewById(R.id.buttonBorrowDismiss);
        Button acceptButton = (Button)rootView.findViewById(R.id.buttonBorrowAccept);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss(v);
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //alert box -> change strings 'yes' and 'undo' to res/values/strings
                new AlertDialog.Builder(getContext())
                        .setTitle("Accept Lend Offer")
                        .setMessage("Your phone number will be shared with the user.")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setNegativeButton("undo", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();


                dismiss(v);
            }
        });

        return rootView;
    }


    /** cancel (x) button **/
    public void dismiss(View view){
        getDialog().dismiss();
    }

}