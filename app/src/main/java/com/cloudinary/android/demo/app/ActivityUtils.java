package com.cloudinary.android.demo.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Outline;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudinary.android.demo.R;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.picasso.Picasso;

import java.util.Collection;

import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.BindingAdapter;
import androidx.fragment.app.Fragment;

/**
 * Created by Nitzan Jaitman on 08/03/2018.
 */

public class ActivityUtils {

    private static final String TAG = ActivityUtils.class.getSimpleName();
    private static SparseArray<ViewOutlineProvider> outlineProviders = new SparseArray<>();

    /**
     * Get an instance of outline providers to make views with rounded corners
     *
     * @param radius The corner radius
     * @return The provider instance to use on the view
     */
    public static synchronized ViewOutlineProvider getRoundedRectProvider(final int radius) {
        return outlineProviders.get(radius, buildAndStoreProvider(radius));
    }

    /**
     * This method is used to bind url to an ImageView. It's called automatically by the data
     * binding framework wherever 'app:imageUrl' appears in an xml file, see
     * fragment_upload_details.xml for a usage example.
     * see
     *
     * @param imageView The image view to load the url into
     * @param url       The url to load into the image view
     */
    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView imageView, String url) {
        imageView.setTag(url);
        int width = ActivityUtils.getScreenWidth(imageView.getContext());
        Picasso.get().load(url).resize(width, width).onlyScaleDown().centerCrop().into(imageView);
    }

    private static ViewOutlineProvider buildAndStoreProvider(int radius) {
        ViewOutlineProvider provider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                if (view.getWidth() < 0) {
                    return;
                }

                Rect rect = new Rect();
                view.getDrawingRect(rect);
                outline.setRoundRect(rect, radius);
                view.setClipToOutline(true);
            }
        };

        outlineProviders.append(radius, provider);

        return provider;
    }

    /**
     * Configure app bar layout with varying alpha based on offset
     *
     * @param appBarLayout The layout to config
     * @param scrim        The scrim view
     * @param limit        The upper alpha limit
     */
    public static void setupCustomScrimView(AppBarLayout appBarLayout, View scrim, float limit) {
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) -> {
            float fraction = (1 - (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange()) * limit;
            scrim.setAlpha(fraction);
        });
    }

    /**
     * Helper method to convert DPs to pixels
     *
     * @param context Android context
     * @param dp      Value in DP
     * @return The value in pixels
     */
    public static int dpToPx(Context context, float dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));

    }

    /**
     * Setup toolbar fonts to app custom fonts
     *
     * @param context                 Android context
     * @param collapsingToolbarLayout The appbar to setup
     */
    public static void setupToolbarFonts(Context context, CollapsingToolbarLayout collapsingToolbarLayout) {
        Typeface font = ResourcesCompat.getFont(context, R.font.montserrat_alternates_medium_600);
        collapsingToolbarLayout.setExpandedTitleTypeface(font);
        collapsingToolbarLayout.setCollapsedTitleTypeface(font);
    }

    /**
     * Helper method to get a drawable attribute for context's theme
     *
     * @param context   Android context
     * @param attribute The drawable attribute id
     * @return The drawable attribute.
     */
    public static Drawable getDrawableAttribute(Context context, int attribute) {
        int[] attrs = new int[]{attribute /* index 0 */};
        TypedArray ta = context.obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0 /* index */);
        ta.recycle();

        return drawableFromTheme;
    }

    /**
     * Setup the categories text views
     *
     * @param activity      Activity context
     * @param tags          The list of strings to display
     * @param tagsContainer The flexbox container of the tags
     * @param listener      Click listener to setup oin the views
     */
    public static void updateCategories(Activity activity, Collection<String> tags, FlexboxLayout tagsContainer, HomeFragment.tagsClickListener listener) {
        tagsContainer.removeAllViews();

        for (String tag : tags) {
            TextView textView = (TextView) activity.getLayoutInflater().inflate(R.layout.item_category_compact, tagsContainer, false);
            textView.setText(tag);
            textView.setTag(tag);
            int radius = activity.getResources().getDimensionPixelSize(R.dimen.category_compact_corner_radius);
            textView.setOutlineProvider(ActivityUtils.getRoundedRectProvider(radius));

            tagsContainer.addView(textView);

            textView.setOnClickListener(v -> {
                String tagClicked = (String) v.getTag();
                listener.onTagClicked(tagClicked);
            });
        }
    }

    /**
     * Helper method to get the width of the primary display
     *
     * @param context Android context
     * @return Width in pixels
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = context.getSystemService(WindowManager.class);
        if (windowManager == null) {
            Log.e(TAG, "Cannot retrieve window manager for screen size, unexpected behaviour may occur.");
            return context.getResources().getDimensionPixelSize(R.dimen.default_screen_width);
        }

        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     * Helper method to open the gallery media chooser to pick an image
     *
     * @param fragment    Fragment context
     * @param requestCode The request code to use
     */
    public static void openMediaChooser(Fragment fragment, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/jpg", "image/png"});
        intent.setType("(*/*");
        fragment.startActivityForResult(intent, requestCode);
    }
}
