package com.example.administrator.wifisensor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by yejiarui on 2018/2/28.
 */

class DialogUitl {
    public static ProgressDialog createProgressDialog(Context context){
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("saving");
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


    public static AlertDialog createAlertDialog(Context context, String content){
        AlertDialog dialog = new AlertDialog.Builder(context).setMessage(content)
                .setPositiveButton("saving", null).create();
        return dialog;
    }


    public static AlertDialog createAlertDialog(Context context, String content,
                                                DialogInterface.OnClickListener clickListener){
        AlertDialog dialog = new AlertDialog.Builder(context).setMessage(content)
                .setPositiveButton("saving", clickListener).create();
        return dialog;
    }

}
