package groupaltspaces.alternativespacesandroid.dialogs;

import android.app.Dialog;
import android.content.Context;

import groupaltspaces.alternativespacesandroid.R;

/**
 * Created by BrageEkroll on 14.10.2014.
 */
public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context, R.style.CustomDialog);
        setContentView(R.layout.dialog);

    }
}
