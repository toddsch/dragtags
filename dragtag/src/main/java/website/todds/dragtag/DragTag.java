package website.todds.dragtag;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DragTag extends RelativeLayout implements View.OnClickListener, View.OnTouchListener {

    private ImageButton mPrimaryAction;
    private TextView mText;
    private ImageButton mSecondaryAction;

    // Event Listeners
    private OnPrimaryClickListener mOnPrimaryClick;
    private OnSecondaryClickListener mOnSecondaryClick;
    protected OnDropListener mOnDropListener;

    private Object mData;

    public interface OnPrimaryClickListener {
        void onPrimaryClick(DragTag dragTag, ImageButton primaryButton);
    }

    public interface OnSecondaryClickListener {
        void onSecondaryClick(DragTag dragTag, ImageButton secondaryButton);
    }

    public interface OnDropListener {
        /**
         * <p>Provides an interface to execute code during a call to {@link #moveTo(ViewGroup)}</p>
         * <p></p>
         * <p>You don't need to handle setting the view's visibility or moving it from ViewGroups --
         * this interface lets you do other work related to moving the tag. This method is called
         * twice -- once <i>before</i> <var>dragTag</var> is moved and once <i>directly after</i>
         * </p>
         *
         * @param dragTag  the {@link DragTag} that invoked this interface
         * @param previous the {@link ViewGroup} the DragTag is leaving
         * @param newGroup the {@link ViewGroup} the DragTag is moving to
         * @param hasMoved  <tt>false</tt> if <var>dragTag</var> hasn't been moved to the new
         *                 container (onMove fired before), <tt>true</tt> if it has (onMove fired
         *                 after)
         */
        void onMove(DragTag dragTag, ViewGroup previous, ViewGroup newGroup, boolean hasMoved);
    }

    public DragTag(Context context) {
        super(context);
        init(null);
    }

    public DragTag(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.drag_item, this);

        mPrimaryAction = findViewById(R.id.drag_item_imagebutton_primary);
        mText = findViewById(R.id.drag_item_textview);
        mSecondaryAction = findViewById(R.id.drag_item_imagebutton_secondary);

        mPrimaryAction.setOnClickListener(this);
        mSecondaryAction.setOnClickListener(this);

        if (attrs != null)
            // Collect and apply XML attributes if available
            applyAttributes(attrs);

        setOnTouchListener(this);
    }

    private void applyAttributes(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DragTag);
        try {
            final int DEF_VALUE = -1;

            int primarySrc = a.getResourceId(R.styleable.DragTag_primaryButtonSrc, DEF_VALUE);
            int secondarySrc = a.getResourceId(R.styleable.DragTag_secondaryButtonSrc, DEF_VALUE);
            int textColor = a.getResourceId(R.styleable.DragTag_textColor, DEF_VALUE);
            int textSize = a.getResourceId(R.styleable.DragTag_textSize, DEF_VALUE);

            // Drawable for the primary action button
            if (primarySrc != DEF_VALUE)
                mPrimaryAction.setImageResource(primarySrc);

            // Drawable for the secondary action button
            if (secondarySrc != DEF_VALUE)
                mSecondaryAction.setImageResource(secondarySrc);

            // Text color for the main label, mText. Calls different methods per API level.
            if (textColor != DEF_VALUE) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                    mText.setTextColor(getResources().getColor(textColor));
                else
                    mText.setTextColor(getResources().getColor(textColor, null));
            }

            if (textSize != DEF_VALUE)
                mText.setTextSize(getResources().getDimension(textSize));

            // Forcibly apply layout params given in XML (layout_margin, etc)
            setLayoutParams(generateLayoutParams(attrs));

        } finally {
            // Finally, recycle the typed array
            a.recycle();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && v instanceof DragTag) {
            DragTag dragTag = (DragTag) v;
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(dragTag);

            ClipData data = ClipData.newPlainText(dragTag.getText(), "");

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                dragTag.startDrag(data, shadow, dragTag, 0);
            else
                dragTag.startDragAndDrop(data, shadow, dragTag, 0);
            return true;
        }
        v.performClick();
        return false;
    }

    @SuppressLint("ClickableViewAccessibility") // performClick() called inside touch listener
    @Override
    public void setOnTouchListener(OnTouchListener touchListener) {
        // Let outsiders override our TouchListener but set it back to our default if they pass null
        super.setOnTouchListener(touchListener == null ? this : touchListener);
    }

    /**
     * <p>Sets the click listener for the primary button.</p>
     * <p>
     * <p>Don't forget to pass {@link View#VISIBLE} to {@link #setButtonsVisibility(int, int)} to
     * actually show the button.</p>
     *
     * @param onPrimaryClick {@link OnPrimaryClickListener}
     */
    public void setOnPrimaryClick(OnPrimaryClickListener onPrimaryClick) {
        mOnPrimaryClick = onPrimaryClick;
    }

    /**
     * <p>Sets the click listener for the secondary button.</p>
     * <p>
     * <p>Don't forget to pass {@link View#VISIBLE} to {@link #setButtonsVisibility(int, int)} to
     * actually show the button.</p>
     *
     * @param onSecondaryClick {@link OnSecondaryClickListener}
     */
    public void setOnSecondaryClick(OnSecondaryClickListener onSecondaryClick) {
        mOnSecondaryClick = onSecondaryClick;
    }

    /**
     * <p>Sets {@link View#setVisibility(int) visibility} for the primary and secondary buttons, using
     * {@link View#VISIBLE}, {@link View#INVISIBLE} or {@link View#GONE}</p>
     * <p>
     * <p>Pass -1 to either parameter to ignore affecting that button.</p>
     *
     * @param primary   one of the {@link View}'s visibility constants or -1 to ignore
     * @param secondary one of the {@link View}'s visibility constants or -1 to ignore
     */
    public void setButtonsVisibility(int primary, int secondary) {
        if (primary != -1)
            mPrimaryAction.setVisibility(primary);

        if (secondary != -1)
            mSecondaryAction.setVisibility(secondary);
    }

    /**
     * <p>Sets {@link ImageButton#setImageResource(int) either image buttons' graphic} or both.</p>
     * <p>Pass -1 to either parameter to ignore affecting that button.</p>
     *
     * @param primaryResId   an integer reference to a drawable's ID
     * @param secondaryResId an integer reference to a drawable's ID
     */
    public void setButtonsDrawables(int primaryResId, int secondaryResId) {
        if (primaryResId != -1)
            mPrimaryAction.setImageResource(primaryResId);

        if (secondaryResId != -1)
            mSecondaryAction.setImageResource(secondaryResId);
    }

    public void setOnDropListener(OnDropListener onDropListener) {
        mOnDropListener = onDropListener;
    }

    public void setText(String text) {
        mText.setText(text);
    }

    public final CharSequence getText() {
        return mText.getText();
    }

    public void setData(Object object) {
        mData = object;
    }

    public final Object getData() {
        return mData;
    }

    @Override
    public void onClick(View v) {
        // Invoke a click listener only if either image buttons fired the event
        if (v.getId() == R.id.drag_item_imagebutton_primary)
            mOnPrimaryClick.onPrimaryClick(this, mPrimaryAction);

        else if (v.getId() == R.id.drag_item_imagebutton_secondary)
            mOnSecondaryClick.onSecondaryClick(this, mSecondaryAction);
    }

    /**
     * <p>Removes the DragTag from its current group and adds it to the provided one.</p>
     * <p>If {@link #setOnDropListener(OnDropListener) given an OnDropListener}, invokes
     * {@link OnDropListener#onMove(DragTag, ViewGroup, ViewGroup, boolean)}.</p>
     *
     * @param newViewGroup {@link ViewGroup}
     */
    public void moveTo(ViewGroup newViewGroup) {
        ViewGroup current = (ViewGroup) getParent();

        // Notify callback if it was provided
        if (mOnDropListener != null)
            mOnDropListener.onMove(this, current, newViewGroup, false);

        // Remove us from the current view group if it exists
        if (current != null)
            current.removeView(this);

        // Add ourselves to the provided view group
        newViewGroup.addView(this);

        // Notify callback if it was provided
        if (mOnDropListener != null)
            mOnDropListener.onMove(this, current, newViewGroup, true);
    }
}
