package com.example.tomoki.timemanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by tomoki on 2017/05/10.
 */

public class ContactUsDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CharSequence[] items = {"削除", "閉じる"};

        Activity activity = getActivity();
        final MainActivity mainActivity=new MainActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Toast.makeText(getActivity(), "使い方が押された", Toast.LENGTH_LONG).show();
                        mainActivity.refresh_list();
                        break;
                    case 1:
                        Toast.makeText(getActivity(), "よくある質問が押された", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        });
        return builder.create();
    }
}