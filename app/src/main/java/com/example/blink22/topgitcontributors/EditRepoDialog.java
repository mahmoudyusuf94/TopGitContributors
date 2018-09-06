package com.example.blink22.topgitcontributors;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.Date;

public class EditRepoDialog extends DialogFragment{


    private EditText mRepoOwner;
    private EditText mRepoName;

    public static final String EXTRA_OWNER = "com.example.blink22.topgitcontributors.owner";
    public static final String EXTRA_REPO = "com.example.blink22.topgitcontributors.repo";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.repo_dialog, null, false);

        mRepoName = v.findViewById(R.id.repo_dialog_repo_name);
        mRepoOwner = v.findViewById(R.id.repo_dialog_repo_owner);

        builder.setView(v)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendResult(Activity.RESULT_OK,
                                mRepoOwner.getText().toString(),
                                mRepoName.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }

    private void sendResult(int resultCode, String owner, String repo){

        if(getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_OWNER, owner);
        intent.putExtra(EXTRA_REPO, repo);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
