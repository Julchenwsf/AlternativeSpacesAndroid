package groupaltspaces.alternativespacesandroid.util;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import groupaltspaces.alternativespacesandroid.R;

public class InterestCompleteTextView extends TokenCompleteTextView {

    public InterestCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View getViewForObject(Object o) {
        Interest interest = (Interest)o;

        LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        LinearLayout view = (LinearLayout)l.inflate(R.layout.interest_token, (ViewGroup)InterestCompleteTextView.this.getParent(), false);
        ((TextView)view.findViewById(R.id.name)).setText(interest.getName());
        return view;
    }
}
