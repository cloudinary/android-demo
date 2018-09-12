package com.cloudinary.android.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.app.ActivityUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

/**
 * Custom class, represents a 'department widget' - image + label contained in a card view.
 */
public class DepartmentCard extends CardView {
    ImageView image;
    TextView label;

    public DepartmentCard(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public DepartmentCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DepartmentCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // setup all values for card itself:
        int cornerRadius = context.getResources().getDimensionPixelSize(R.dimen.card_corner_radius);
        setCardBackgroundColor(context.getColor(R.color.colorPrimary));
        setClipChildren(true);
        setClickable(true);
        setFocusable(true);
        setForeground(ActivityUtils.getDrawableAttribute(context, R.attr.selectableItemBackground));
        setCardElevation(context.getResources().getDimension(R.dimen.card_elevation));
        setRadius(cornerRadius);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        addView(linearLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        // setup children values:
        image = new ImageView(context);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setOutlineProvider(ActivityUtils.getRoundedRectProvider(cornerRadius));

        linearLayout.addView(image, LinearLayout.LayoutParams.MATCH_PARENT, 0);
        ((LinearLayout.LayoutParams) image.getLayoutParams()).weight = 1;

        label = new TextView(context);
        label.setTextAppearance(R.style.TextAppearance_AppCompat_Large_Inverse);
        label.setTextSize(context.getResources().getDimension(R.dimen.home_department_text_size));
        label.setBackgroundColor(context.getResources().getColor(R.color.colorAccent, null));
        label.setTypeface(ResourcesCompat.getFont(context, R.font.montserrat_medium_600));
        int padding = context.getResources().getDimensionPixelSize(R.dimen.card_label_padding);
        label.setPadding(padding, padding, padding, padding);
        label.setTextAlignment(TEXT_ALIGNMENT_CENTER);

        label.setTextColor(context.getResources().getColor(R.color.colorPrimary, null));
        linearLayout.addView(label, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // set attributes from xml:
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DepartmentCard,
                0, 0);

        image.setImageResource(typedArray.getResourceId(R.styleable.DepartmentCard_image, 0));
        label.setText(typedArray.getString(R.styleable.DepartmentCard_label));
    }

    public ImageView getImage() {
        return image;
    }

    public CharSequence getText() {
        return label.getText();
    }
}
