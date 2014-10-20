package groupaltspaces.alternativespacesandroid.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.QwertyKeyListener;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Filter;
import android.widget.MultiAutoCompleteTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Gmail style auto complete view with easy token customization
 * override getViewForObject to provide your token view
 *
 * @author mgod
 */

public abstract class TokenCompleteTextView extends MultiAutoCompleteTextView {
    private Tokenizer tokenizer;
    private Object selectedObject;
    private ArrayList<Object> objects;
    private Layout lastLayout = null;
    private boolean initialized = false;


    private void init() {
        setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        objects = new ArrayList<Object>();
        Editable text = getText();
        assert null != text;

        setTextIsSelectable(false);
        setLongClickable(false);

        //In theory, get the soft keyboard to not supply suggestions.
        setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);

        setFilters(new InputFilter[] {new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.length() == 1 && !Character.isLetterOrDigit(source.charAt(0)))  return "";

                return null;
            }
        }});

        initialized = true;
    }


    public TokenCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    @Override
    protected void performFiltering(CharSequence text, int start, int end, int keyCode) {
        Filter filter = getFilter();
        if (filter != null)  filter.filter(text.subSequence(start, end), this);
    }


    @Override
    public void setTokenizer(Tokenizer t) {
        super.setTokenizer(t);
        tokenizer = t;
    }

    public List<Object> getObjects() {
        return objects;
    }



    /**
     * A token view for the object
     *
     * @param object the object selected by the user from the list
     * @return a view to display a token in the text field for the object
     */
    abstract protected View getViewForObject(Object object);

    private float maxTextWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    boolean inInvalidate = false;
    @Override
    public void invalidate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (initialized && !inInvalidate) {
                inInvalidate = true;
                setShadowLayer(getShadowRadius(), getShadowDx(), getShadowDy(), getShadowColor());
                inInvalidate = false;
            }
        }

        //Need to force the TextView private mEditor variable to reset as well on API 16 and up
        super.invalidate();
    }

    @Override
    public boolean enoughToFilter() {
        Editable text = getText();

        int end = getSelectionEnd();
        if (end < 0 || tokenizer == null) return false;

        int start = tokenizer.findTokenStart(text, end);

        return end - start >= getThreshold();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        Editable text = getText();
        boolean handled = false;

        if (isFocused() && text != null && lastLayout != null && action == MotionEvent.ACTION_UP) {
            int offset = getOffsetForPosition(event.getX(), event.getY());

            if (offset != -1) {
                TokenImageSpan[] links = text.getSpans(offset, offset, TokenImageSpan.class);

                if (links.length > 0) {
                    links[0].onClick();
                    handled = true;
                }
            }
        }

        if (!handled) {
            handled = super.onTouchEvent(event);
        }
        return handled;

    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        selEnd = selStart;

        Editable text = getText();
        if (text != null) {
            //Make sure if we are in a span, we select the spot 1 space after the span end
            TokenImageSpan[] spans = text.getSpans(selStart, selEnd, TokenImageSpan.class);
            for (TokenImageSpan span: spans) {
                int spanEnd = text.getSpanEnd(span);
                if (selStart <= spanEnd && text.getSpanStart(span) < selStart) {
                    if(spanEnd==text.length())
                        setSelection(spanEnd);
                    else
                        setSelection(spanEnd+1);
                    return;
                }
            }

            super.onSelectionChanged(selStart, selEnd);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        lastLayout = getLayout(); //Used for checking text positions
    }

    @Override
    public void onFocusChanged(boolean hasFocus, int direction, Rect previous) {
        super.onFocusChanged(hasFocus, direction, previous);
    }

    @Override
    protected CharSequence convertSelectionToString(Object object) {
        selectedObject = object;
        return "";
    }

    private SpannableStringBuilder buildSpannableForText(CharSequence text) {
        //Add a sentinel , at the beginning so the user can remove an inner token and keep auto-completing
        //This is a hack to work around the fact that the tokenizer cannot directly detect spans
        return new SpannableStringBuilder("," + tokenizer.terminateToken(text));
    }

    protected TokenImageSpan buildSpanForObject(Object obj) {
        if (obj == null) {
            return null;
        }
        View tokenView = getViewForObject(obj);
        return new TokenImageSpan(tokenView, obj);
    }

    @Override
    protected void replaceText(CharSequence text) {
        clearComposingText();
        SpannableStringBuilder ssb = buildSpannableForText(text);
        TokenImageSpan tokenSpan = buildSpanForObject(selectedObject);

        Editable editable = getText();
        int end = getSelectionEnd();
        int start = tokenizer.findTokenStart(editable, end);
        String original = TextUtils.substring(editable, start, end);

        if (editable != null) {
            if (tokenSpan == null) {
                editable.replace(start, end, "");
            } else if (objects.contains(tokenSpan.getToken())) {
                editable.replace(start, end, "");
            } else {
                QwertyKeyListener.markAsReplaced(editable, start, end, original);
                editable.replace(start, end, ssb);
                editable.setSpan(tokenSpan, start, start + ssb.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
    }


    public void addObject(final Object object) {
        if (object == null) return;
        if (objects.contains(object)) return;

        objects.add(object);

        TokenImageSpan tokenSpan = buildSpanForObject(object);
        Editable editable = getText();

        if (editable != null) {
            int offset = editable.length();
            editable.setSpan(tokenSpan, offset, offset, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            setSelection(editable.length());
        }
    }


    private void removeSpan(TokenImageSpan span) {
        Editable text = getText();
        if (text == null) return;
        objects.remove(span.getToken());

        text.delete(text.getSpanStart(span), text.getSpanEnd(span));
    }


    private class ViewSpan extends ReplacementSpan {
        protected View view;

        public ViewSpan(View v) {
            view = v;
        }

        private void prepView() {
            int widthSpec = MeasureSpec.makeMeasureSpec((int)maxTextWidth(), MeasureSpec.AT_MOST);
            int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

            view.measure(widthSpec, heightSpec);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            prepView();

            canvas.save();
            //Centering the token looks like a better strategy that aligning the bottom
            int padding = (bottom - top - view.getBottom()) / 2;
            canvas.translate(x, bottom - view.getBottom() - padding);
            view.draw(canvas);
            canvas.restore();
        }

        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fm) {
            prepView();

            if (fm != null) {
                //We need to make sure the layout allots enough space for the view
                int height = view.getMeasuredHeight();
                int need = height - (fm.descent - fm.ascent);
                if (need > 0) {
                    int ascent = need / 2;
                    //This makes sure the text drawing area will be tall enough for the view
                    fm.descent += need - ascent;
                    fm.ascent -= ascent;
                    fm.bottom += need - ascent;
                    fm.top -= need / 2;
                }
            }

            return view.getRight();
        }
    }


    protected class TokenImageSpan extends ViewSpan {
        private Object token;

        public TokenImageSpan(View d, Object token) {
            super(d);
            this.token = token;
        }

        public Object getToken() {
            return this.token;
        }

        public void onClick() {
            removeSpan(this);
        }
    }


    protected ArrayList<Serializable> getSerializableObjects() {
        ArrayList<Serializable> serializables = new ArrayList<Serializable>();
        for (Object obj: getObjects()) {
            if (obj instanceof Serializable) {
                serializables.add((Serializable)obj);
            }
        }

        return serializables;
    }

    @SuppressWarnings("unchecked")
    protected ArrayList<Object> convertSerializableArrayToObjectArray(ArrayList<Serializable> s) {
        return (ArrayList<Object>)(ArrayList)s;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        ArrayList<Serializable> baseObjects = getSerializableObjects();

        Parcelable superState = super.onSaveInstanceState();
        SavedState state = new SavedState(superState);

        state.baseObjects = baseObjects;

        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());

        setText(ss.prefix);

        for (Object obj: convertSerializableArrayToObjectArray(ss.baseObjects)) {
            addObject(obj);
        }
    }

    /**
     * Handle saving the token state
     */
    private static class SavedState extends BaseSavedState {
        String prefix;
        boolean allowDuplicates;
        ArrayList<Serializable> baseObjects;

        @SuppressWarnings("unchecked")
        SavedState(Parcel in) {
            super(in);
            prefix = in.readString();
            allowDuplicates = in.readInt() != 0;
            baseObjects = (ArrayList<Serializable>)in.readSerializable();
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(prefix);
            out.writeInt(allowDuplicates ? 1 : 0);
            out.writeSerializable(baseObjects);
        }

        @Override
        public String toString() {
            String str = "TokenCompleteTextView.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " tokens=" + baseObjects;
            return str + "}";
        }

        @SuppressWarnings("hiding")
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}