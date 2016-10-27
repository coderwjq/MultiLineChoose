package com.ihidea.multilinechooselib;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义flowlayout，显示运营商
 * https://github.com/jackuhan/FlowlayoutTags
 */

public class MultiLineChooseLayout extends ViewGroup {
    
    /**
     * 默认的文字颜色
     */
    private final int default_text_color = Color.rgb(0x49, 0xC1, 0x20);
    
    /**
     * 默认的背景颜色
     */
    private final int default_background_color = Color.WHITE;
    
    /**
     * 默认的内嵌文字颜色
     */
    private final int default_input_hint_color = Color.argb(0x80, 0x00, 0x00, 0x00);
    
    /**
     * 默认的输入文字颜色
     */
    private final int default_input_text_color = Color.argb(0xDE, 0x00, 0x00, 0x00);
    
    /**
     * 默认的选中文字颜色
     */
    private final int default_checked_text_color = Color.WHITE;
    
    /**
     *默认的选中背景颜色
     */
    private final int default_checked_background_color = Color.rgb(0x49, 0xC1, 0x20);
    
    /**
     *默认的点击背景颜色
     */
    private final int default_pressed_background_color = Color.rgb(0xED, 0xED, 0xED);
    
    /**
     *默认的文字大小
     */
    private final float default_text_size;
    
    /**
     *默认的水平间距
     */
    private final float default_horizontal_spacing;
    
    /**
     *默认的竖直间距
     */
    private final float default_vertical_spacing;
    
    /**
     *默认的内部水平间距
     */
    private final float default_horizontal_padding;
    
    /**
     *默认的内部竖直间距
     */
    private final float default_vertical_padding;
    
    private int textColor;
    
    private int backgroundColor;
    
    private int checkedTextColor;
    
    private int checkedBackgroundColor;
    
    private float textSize;
    
    private int horizontalSpacing;
    
    private int verticalSpacing;
    
    private int horizontalPadding;
    
    private int verticalPadding;
    
    private int tagWidth, tagHeight;
    
    private int tagMaxEms;
    
    private boolean multiChooseable, singleLine;
    
    private boolean animUpdateDrawable = false;
    
    //textview的属性
    private float mRadius[] = { 0, 0, 0, 0, 0, 0, 0, 0 };
    
    private ColorStateList mStrokeColor, mCheckedStrokeColor;
    
    private int mStrokeWidth = 0;
    
    private OnTagChangeListener mOnTagChangeListener;
    
    private OnTagClickListener mOnTagClickListener;
    
    private InternalTagClickListener mInternalTagClickListener = new InternalTagClickListener();
    
    public MultiLineChooseLayout(Context context) {
        this(context, null);
    }
    
    public MultiLineChooseLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.MultiLineChooseLayoutTagsStyle);
    }
    
    public MultiLineChooseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        default_text_size = sp2px(13.0f);
        default_horizontal_spacing = dp2px(8.0f);
        default_vertical_spacing = dp2px(4.0f);
        default_horizontal_padding = dp2px(0.0f);
        default_vertical_padding = dp2px(0.0f);
        
        final TypedArray attrsArray = context.obtainStyledAttributes(attrs,
                R.styleable.MultiLineChooseItemTags,
                defStyleAttr,
                R.style.MultiLineChooseItemTags);
        try {
            textColor = attrsArray.getColor(R.styleable.MultiLineChooseItemTags_tag_textColor, default_text_color);
            backgroundColor = attrsArray.getColor(R.styleable.MultiLineChooseItemTags_tag_backgroundColor,
                    default_background_color);
            checkedTextColor = attrsArray.getColor(R.styleable.MultiLineChooseItemTags_tag_checkedTextColor,
                    default_checked_text_color);
            checkedBackgroundColor = attrsArray.getColor(R.styleable.MultiLineChooseItemTags_tag_checkedBackgroundColor,
                    default_checked_background_color);
            textSize = attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_tag_textSize, default_text_size);
            horizontalSpacing = (int) attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_tag_horizontalSpacing,
                    default_horizontal_spacing);
            verticalSpacing = (int) attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_tag_verticalSpacing,
                    default_vertical_spacing);
            horizontalPadding = (int) attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_tag_horizontalPadding,
                    default_horizontal_padding);
            verticalPadding = (int) attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_tag_verticalPadding,
                    default_vertical_padding);
            multiChooseable = attrsArray.getBoolean(R.styleable.MultiLineChooseItemTags_tag_multiChooseable, true);
            singleLine = attrsArray.getBoolean(R.styleable.MultiLineChooseItemTags_tag_singleLine, false);
            tagWidth = attrsArray.getInt(R.styleable.MultiLineChooseItemTags_tag_width,
                    MultiLineChooseLayout.LayoutParams.WRAP_CONTENT);
            if (tagWidth >= 0) {
                tagWidth = sp2px(tagWidth);
            }
            tagHeight = attrsArray.getInt(R.styleable.MultiLineChooseItemTags_tag_height,
                    MultiLineChooseLayout.LayoutParams.WRAP_CONTENT);
            if (tagHeight >= 0) {
                tagHeight = sp2px(tagHeight);
            }
            
            tagMaxEms = attrsArray.getInt(R.styleable.MultiLineChooseItemTags_tag_maxEms, -1);
            if (tagWidth < 0) {
                tagMaxEms = -1;
            }
            
            float radius = attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_tag_radius, 0);
            float topLeftRadius = attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_tag_topLeftRadius, 0);
            float topRightRadius = attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_tag_topRightRadius, 0);
            float bottomLeftRadius = attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_tag_bottomLeftRadius,
                    0);
            float bottomRightRadius = attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_tag_bottomRightRadius,
                    0);
            
            if (topLeftRadius == 0 && topRightRadius == 0 && bottomLeftRadius == 0 && bottomRightRadius == 0) {
                topLeftRadius = topRightRadius = bottomRightRadius = bottomLeftRadius = radius;
            }
            
            mRadius[0] = mRadius[1] = topLeftRadius;
            mRadius[2] = mRadius[3] = topRightRadius;
            mRadius[4] = mRadius[5] = bottomRightRadius;
            mRadius[6] = mRadius[7] = bottomLeftRadius;
            
            mStrokeColor = attrsArray.getColorStateList(R.styleable.MultiLineChooseItemTags_tag_strokeColor);
            mCheckedStrokeColor = attrsArray
                    .getColorStateList(R.styleable.MultiLineChooseItemTags_tag_checkedStrokeColor);
            mStrokeWidth = (int) attrsArray.getDimension(R.styleable.MultiLineChooseItemTags_tag_strokeWidth,
                    mStrokeWidth);
            
        }
        finally {
            attrsArray.recycle();
        }
        
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        
        int width = 0;
        int height = 0;
        
        int row = 0; // The row counter.
        int rowWidth = 0; // Calc the current row width.
        int rowMaxHeight = 0; // Calc the max tag height, in current row.
        
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();
            
            if (child.getVisibility() != GONE) {
                rowWidth += childWidth;
                if (rowWidth > widthSize) { // Next line.
                    rowWidth = childWidth; // The next row width.
                    height += rowMaxHeight + verticalSpacing;
                    rowMaxHeight = childHeight; // The next row max height.
                    row++;
                }
                else { // This line.
                    rowMaxHeight = Math.max(rowMaxHeight, childHeight);
                }
                rowWidth += horizontalSpacing;
            }
        }
        // Account for the last row height.
        height += rowMaxHeight;
        
        // Account for the padding too.
        height += getPaddingTop() + getPaddingBottom();
        
        // If the tags grouped in one row, set the width to wrap the tags.
        if (row == 0) {
            width = rowWidth;
            width += getPaddingLeft() + getPaddingRight();
        }
        else {// If the tags grouped exceed one line, set the width to match the parent.
            width = widthSize;
        }
        
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width,
                heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int parentLeft = getPaddingLeft();
        final int parentRight = r - l - getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = b - t - getPaddingBottom();
        
        int childLeft = parentLeft;
        int childTop = parentTop;
        
        int rowMaxHeight = 0;
        
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();
            
            if (child.getVisibility() != GONE) {
                if (childLeft + width > parentRight) { // Next line
                    childLeft = parentLeft;
                    childTop += rowMaxHeight + verticalSpacing;
                    rowMaxHeight = height;
                }
                else {
                    rowMaxHeight = Math.max(rowMaxHeight, height);
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
                
                childLeft += width + horizontalSpacing;
            }
        }
    }
    
    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.tags = getTags();
        ss.checkedPosition = getCheckedTagIndex();
        return ss;
    }
    
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        
        setList(ss.tags);
        ItemView checkedTagView = getTagAt(ss.checkedPosition);
        if (checkedTagView != null) {
            checkedTagView.setCheckedWithoutAnimal(true);
        }
    }
    
    /**
     * Return the last NORMAL state tag view in this group.
     *
     * @return the last NORMAL state tag view or null if not exists
     */
    protected ItemView getLastTagView() {
        final int lastNormalTagIndex = getChildCount() - 1;
        return getTagAt(lastNormalTagIndex);
    }
    
    /**
     * Returns the tag array in group, except the INPUT tag.
     *
     * @return the tag array.
     */
    public String[] getTags() {
        final int count = getChildCount();
        final List<String> tagList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            final ItemView tagView = getTagAt(i);
            tagList.add(tagView.getText().toString());
        }
        
        return tagList.toArray(new String[tagList.size()]);
    }
    
    /**
     * @see #setList(String...)
     */
    public void setList(List<String> tagList) {
        setList(tagList.toArray(new String[tagList.size()]));
    }
    
    /**
     * Set the tags. It will remove all previous tags first.
     *
     * @param tags the tag list to set.
     */
    public void setList(String... tags) {
        removeAllViews();
        for (final String tag : tags) {
            appendTag(tag);
        }
        
    }
    
    /**
     * 设置默认的位置上选中
     * @param position
     * @return
     */
    public int setDefaultPostionChecked(int position) {
        
        int index = -1;
        final int count = getChildCount();
        if (position >= count) {
            
            return -1;
        }
        
        ItemView tagView = getTagAt(position);
        tagView.setCheckedWithoutAnimal(true);
        index = position;
        return index;
    }
    
    /**
     * Returns the tag view at the specified position in the group.
     *
     * @param index the position at which to get the tag view from.
     * @return the tag view at the specified position or null if the position
     * does not exists within this group.
     */
    protected ItemView getTagAt(int index) {
        return null == getChildAt(index) ? null : (ItemView) getChildAt(index);
    }
    
    /**
     * Returns the checked tag view in the group.单选时候有用,多选时候返回第一个
     *
     * @return the checked tag view or null if not exists.
     */
    protected ItemView getCheckedTag() {
        final int checkedTagIndex = getCheckedTagIndex();
        if (checkedTagIndex != -1) {
            return getTagAt(checkedTagIndex);
        }
        return null;
    }
    
    /**
     * Returns the checked tag view in the group.单选时候有用,多选时候返回第一个
     *
     * @return the checked tag view or null if not exists.
     */
    protected String getCheckedTagText() {
        if (null != getCheckedTag()) {
            return getCheckedTag().getText().toString();
        }
        return null;
    }
    
    /**
     * 返回选中的tag的文字
     * Returns the tag array in group, except the INPUT tag.
     *
     * @return the tag array.
     */
    public String[] getCheckedTagsText() {
        final int count = getChildCount();
        final List<String> tagList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            final ItemView tagView = getTagAt(i);
            if (tagView.isChecked) {
                tagList.add(tagView.getText().toString());
            }
        }
        
        return tagList.toArray(new String[tagList.size()]);
    }
    
    /**
     * 返回选中的tag的文字
     * Returns the tag array in group, except the INPUT tag.
     *
     * @return the tag array.
     */
    public ArrayList<String> getCheckedTagsTextsArrayList() {
        final int count = getChildCount();
        final ArrayList<String> tagList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            final ItemView tagView = getTagAt(i);
            if (tagView.isChecked) {
                tagList.add(tagView.getText().toString());
            }
        }
        
        return tagList;
    }
    
    /**
     * 单选时候有用,多选时候返回第一个
     * Return the checked tag index.
     *
     * @return the checked tag index, or -1 if not exists.
     */
    public int getCheckedTagIndex() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final ItemView tag = getTagAt(i);
            if (tag.isChecked) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * 返回选中的tag的索引
     * Returns the tag array in group, except the INPUT tag.
     *
     * @return the tag array.
     */
    public ArrayList<Integer> getCheckedTagsIndexArrayList() {
        final int count = getChildCount();
        final ArrayList<Integer> tagList = new ArrayList<Integer>();
        for (int i = 0; i < count; i++) {
            final ItemView tagView = getTagAt(i);
            if (tagView.isChecked) {
                tagList.add(i);
            }
        }
        
        return tagList;
    }
    
    /**
     * 取消选中状态
     */
    public void cancelAllSelectedItem() {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            ItemView tag = getTagAt(i);
            if (null != tag && tag.isChecked) {
                tag.setCheckedWithoutAnimal(false);
            }
        }
    }
    
    /**
     * Register a callback to be invoked when this tag group is changed.
     *
     * @param l the callback that will run
     */
    public void setOnTagChangeListener(OnTagChangeListener l) {
        mOnTagChangeListener = l;
    }
    
    /**
     * Append tag to this group.
     *
     * @param tag the tag to append.
     */
    public void appendTag(CharSequence tag) {
        final ItemView newTag = new ItemView(getContext(), tag);
        //        if (singleLine) {
        //            newTag.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        //            newTag.setMaxEms(2);
        //        }
        //        newTag.setSingleLine(singleLine);
        newTag.setOnClickListener(mInternalTagClickListener);
        addView(newTag);
    }
    
    public float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
    
    public int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
    
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MultiLineChooseLayout.LayoutParams(getContext(), attrs);
    }
    
    /**
     * Register a callback to be invoked when a tag is clicked.
     *
     * @param l the callback that will run.
     */
    public void setOnTagClickListener(OnTagClickListener l) {
        mOnTagClickListener = l;
    }
    
    /**
     * Interface definition for a callback to be invoked when a tag group is changed.
     */
    public interface OnTagChangeListener {
        /**
         * Called when a tag has been appended to the group.
         *
         * @param tag the appended tag.
         */
        void onAppend(MultiLineChooseLayout flowlayoutTags, String tag);
        
        /**
         * Called when a tag has been deleted from the the group.
         *
         * @param tag the deleted tag.
         */
        void onDelete(MultiLineChooseLayout flowlayoutTags, String tag);
    }
    
    /**
     * Interface definition for a callback to be invoked when a tag is clicked.
     */
    public interface OnTagClickListener {
        /**
         * Called when a tag has been clicked.
         *
         * @param tag The tag text of the tag that was clicked.
         */
        void onTagClick(String tag);
    }
    
    /**
     * Per-child layout information for layouts.
     */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }
        
        public LayoutParams(int width, int height) {
            super(width, height);
        }
    }
    
    /**
     * For {@link MultiLineChooseLayout} save and restore state.
     */
    static class SavedState extends BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }
            
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        
        int tagCount;
        
        String[] tags;
        
        int checkedPosition;
        
        public SavedState(Parcel source) {
            super(source);
            tagCount = source.readInt();
            tags = new String[tagCount];
            source.readStringArray(tags);
            checkedPosition = source.readInt();
        }
        
        public SavedState(Parcelable superState) {
            super(superState);
        }
        
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            tagCount = tags.length;
            dest.writeInt(tagCount);
            dest.writeStringArray(tags);
            dest.writeInt(checkedPosition);
        }
    }
    
    /**
     * The tag view click listener for internal use.
     */
    class InternalTagClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            final ItemView tag = (ItemView) v;
            
            final ItemView checkedTag = getCheckedTag();
            if (!multiChooseable) {
                //单选
                if (checkedTag != null) {
                    checkedTag.setCheckedWithoutAnimal(false);
                }
                
                tag.setCheckedWithoutAnimal(true);
            }
            else {
                //多选
                tag.setCheckedWithoutAnimal(!tag.isChecked);
            }
            
            //外部点击事件
            if (mOnTagClickListener != null) {
                mOnTagClickListener.onTagClick(tag.getText().toString());
            }
            
        }
    }
    
    /**
     * The tag view which has two states can be either NORMAL or INPUT.
     */
    class ItemView extends TextView {
        
        private Context mContext;
        
        private boolean isChecked = false;
        
        private boolean isPressed = false;
        
        private Paint mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        /**
         * Used to detect the touch event.
         */
        private Rect mOutRect = new Rect();
        
        {
            mBackgroundPaint.setStyle(Paint.Style.FILL);
        }
        
        public ItemView(Context context, CharSequence text) {
            super(context);
            this.mContext = context;
            setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
            setLayoutParams(new MultiLineChooseLayout.LayoutParams(tagWidth, tagHeight));
            
            setGravity(Gravity.CENTER);
            
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            
            setSingleLine(singleLine);
            if (singleLine) {
                if (tagMaxEms >= 0) {
                    setEllipsize(TextUtils.TruncateAt.valueOf("END"));
                    setMaxEms(tagMaxEms);
                }
            }
            
            setText(text);
            
            setClickable(true);
            invalidatePaint();
            
        }
        
        /**
         * Set whether this tag view is in the checked state.
         *
         * @param checked true is checked, false otherwise
         */
        public void setCheckedWithoutAnimal(boolean checked) {
            isChecked = checked;
            invalidatePaint();
        }
        
        @Override
        protected boolean getDefaultEditable() {
            return false;
        }
        
        /**
         * Indicates whether the input content is available.
         *
         * @return True if the input content is available, false otherwise.
         */
        public boolean isInputAvailable() {
            return getText() != null && getText().length() > 0;
        }
        
        private void invalidatePaint() {
            
            animUpdateDrawable = false;
            
            if (isChecked) {
                mBackgroundPaint.setColor(checkedBackgroundColor);
                setTextColor(checkedTextColor);
            }
            else {
                mBackgroundPaint.setColor(backgroundColor);
                setTextColor(textColor);
            }
            
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            if (!animUpdateDrawable) {
                updateDrawable();
            }
            super.onDraw(canvas);
        }
        
        private void updateDrawable() {
            mStrokeColor = mStrokeColor == null ? ColorStateList.valueOf(Color.TRANSPARENT) : mStrokeColor;
            mCheckedStrokeColor = mCheckedStrokeColor == null ? mStrokeColor : mCheckedStrokeColor;
            updateDrawable(!isChecked ? mStrokeColor.getDefaultColor() : mCheckedStrokeColor.getDefaultColor());
        }
        
        private void updateDrawable(int strokeColor) {
            
            int mbackgroundColor;
            if (isChecked) {
                mbackgroundColor = checkedBackgroundColor;
            }
            else {
                mbackgroundColor = backgroundColor;
            }
            
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadii(mRadius);
            drawable.setColor(mbackgroundColor);
            drawable.setStroke(mStrokeWidth, strokeColor);
            
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                this.setBackgroundDrawable(drawable);
            }
            else {
                this.setBackground(drawable);
            }
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    getDrawingRect(mOutRect);
                    isPressed = true;
                    invalidatePaint();
                    invalidate();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (!mOutRect.contains((int) event.getX(), (int) event.getY())) {
                        isPressed = false;
                        invalidatePaint();
                        invalidate();
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    isPressed = false;
                    invalidatePaint();
                    invalidate();
                    break;
                }
            }
            return super.onTouchEvent(event);
        }
        
        @Override
        public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
            return new ZanyInputConnection(super.onCreateInputConnection(outAttrs), true);
        }
        
        /**
         * Solve edit text delete(backspace) key detect, see<a href="http://stackoverflow.com/a/14561345/3790554">
         * Android: Backspace in WebView/BaseInputConnection</a>
         */
        private class ZanyInputConnection extends InputConnectionWrapper {
            public ZanyInputConnection(android.view.inputmethod.InputConnection target, boolean mutable) {
                super(target, mutable);
            }
            
            @Override
            public boolean deleteSurroundingText(int beforeLength, int afterLength) {
                // magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
                if (beforeLength == 1 && afterLength == 0) {
                    // backspace
                    return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                            && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
                }
                return super.deleteSurroundingText(beforeLength, afterLength);
            }
        }
    }
}