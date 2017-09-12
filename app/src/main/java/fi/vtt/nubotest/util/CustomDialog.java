package fi.vtt.nubotest.util;

/**
 * Created by elesah on 8.1.2016.
 */

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import fi.vtt.nubotest.R;

public class CustomDialog {


    private Context mContext;
    private int type;
    private Dialog dialog;
    private String title, message;
    private ProgressBar dialogProgressBar;
    public static final int INFORMATION = 1;
    public static final int JOIN_ROOM = 2;
    public static final int SEND_MESSAGE = 3;
    private String TAG = "CustomDialog";
    public Button button_dialog_ok, button_dialog_cancel;
    public TextView dialog_title;
    public TextView dialog_message;
    public EditText editText1, editText2, editText3;
    public NumberPicker numberPicker;

    public CustomDialog(Context context, int type, String title, String message) {

        this.mContext = context;
        this.type = type;
        this.title = title;
        this.message = message;

        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        int i = 0;

        dialog.setContentView(R.layout.dialog_custom);
        dialog.setCancelable(false);

        dialog_title = (TextView)dialog.findViewById(R.id.textView_dialog_title);
        dialog_message = (TextView)dialog.findViewById(R.id.textView_dialog_message);
        numberPicker = (NumberPicker)dialog.findViewById(R.id.numberPicker);
        button_dialog_ok=(Button)dialog.findViewById(R.id.button_dialog_ok);
        button_dialog_cancel=(Button)dialog.findViewById(R.id.button_dialog_cancel);
        dialogProgressBar = (ProgressBar)dialog.findViewById(R.id.progressBar);

        editText1 = (EditText)dialog.findViewById(R.id.editText1);
        editText2 = (EditText)dialog.findViewById(R.id.editText2);
        editText3 = (EditText)dialog.findViewById(R.id.editText3);


        if(message.length()==0 || message==null)
            dialog_message.setVisibility(View.GONE);

    }

    public void closeDialog() {
        CustomDialog.this.dialog.dismiss();
    }

    public Dialog showDialog() {


        switch(type) {


            // Dialog containing information
            case CustomDialog.INFORMATION:

                dialog_title.setText(this.title);
                dialog_message.setText(this.message);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

                params.gravity= Gravity.RIGHT;

                button_dialog_ok.setLayoutParams(params);
                button_dialog_cancel.setVisibility(View.INVISIBLE);

                break;


            // Dialog join room
            case CustomDialog.JOIN_ROOM:

                dialog_title.setText(this.title);
                dialog_message.setText(this.message);

                editText1.setVisibility(View.VISIBLE);
                editText2.setVisibility(View.VISIBLE);

                break;

            // Dialog send message
            case CustomDialog.SEND_MESSAGE:

                dialog_title.setText(this.title);
                dialog_message.setText(this.message);

                //editText1.setVisibility(View.VISIBLE);
                //editText2.setVisibility(View.VISIBLE);
                editText3.setVisibility(View.VISIBLE);

                break;


        }


        return dialog;
    }

}
