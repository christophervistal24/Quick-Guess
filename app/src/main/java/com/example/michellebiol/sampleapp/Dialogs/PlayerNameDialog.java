package com.example.michellebiol.sampleapp.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.michellebiol.sampleapp.AccountsActivity;
import com.example.michellebiol.sampleapp.CategoriesActivity;
import com.example.michellebiol.sampleapp.R;

public class PlayerNameDialog extends AppCompatDialogFragment {
    private EditText editUsername;
    private PlayerNameDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog,null);
        builder.setView(view)
                .setTitle("Player name")
                .setNegativeButton("Accounts", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent  = new Intent(getContext(),AccountsActivity.class);
                        startActivity(intent);
                    }
                })
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String playerName = editUsername.getText().toString();
                        listener.applyText(playerName);
                    }
                });
        editUsername = view.findViewById(R.id.editUsername);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (PlayerNameDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must implement PlayernameDialogListener");
        }

    }

    public interface PlayerNameDialogListener{
        void applyText(String playerName);
    }
}
