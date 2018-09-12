package com.cloudinary.android.demo.app;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.cld.ImageHelper;
import com.cloudinary.android.demo.data.model.Product;
import com.cloudinary.android.demo.data.model.ProductPublicId;
import com.cloudinary.android.demo.data.model.ProductWithData;
import com.cloudinary.android.demo.viewmodel.CartViewModel;
import com.cloudinary.android.demo.viewmodel.ProductViewModel;
import com.cloudinary.android.demo.widget.CustomNumberPicker;
import com.google.android.material.snackbar.Snackbar;

import java.util.stream.Collectors;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.DaggerFragment;

/**
 * Created by Nitzan Jaitman on 22/03/2018.
 */

public class ProductFragment extends DaggerFragment {
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ProductViewModel productViewModel;
    private ImageView mainImage;
    private TextView description;
    private CustomNumberPicker picker;
    private TextView price;
    private CartViewModel cartViewModel;
    private RecyclerView imagesRecyclerView;
    private Product product = null;
    private ViewGroup sizesContainer;
    private View sizesDivider;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product, container, false);
        mainImage = rootView.findViewById(R.id.product_image_main);
        description = rootView.findViewById(R.id.product_text_description);
        price = rootView.findViewById(R.id.product_text_price);
        picker = rootView.findViewById(R.id.product_picker_item_count);
        imagesRecyclerView = rootView.findViewById(R.id.product_list_images);
        sizesDivider = rootView.findViewById(R.id.product_divider_sizes);
        sizesContainer = rootView.findViewById(R.id.product_viewgroup_sizes_container);

        // setup picker
        picker.setOnValueChangedListener(newValue -> updatePrice());

        // setup recycler view:
        ImagesAdapter adapter = new ImagesAdapter();
        adapter.setListener(publicId -> ImageHelper.loadProductImage(publicId, product.getCloud(), mainImage));
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.HORIZONTAL, false));
        imagesRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, getResources().getDimensionPixelSize(R.dimen.product_page_list_divider), 0);
            }
        });
        imagesRecyclerView.setAdapter(adapter);

        rootView.findViewById(R.id.product_button_add_to_cart).setOnClickListener(v -> {
            if (product != null) {

                int value = picker.getValue();
                final String message = getResources().getQuantityString(R.plurals.items_added_to_cart, value, value);
                int actionTextColor = ContextCompat.getColor(rootView.getContext(), R.color.colorPrimary);
                Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                        .setAction(R.string.go_to_cart, getSnackBarClickListener())
                        .setActionTextColor(actionTextColor)
                        .show();

                cartViewModel.add(product, value);
            }
        });

        int radius = inflater.getContext().getResources().getDimensionPixelSize(R.dimen.count_picker_radius);
        rootView.findViewById(R.id.product_picker_item_count).setOutlineProvider(ActivityUtils.getRoundedRectProvider(radius));

        return rootView;
    }

    private View.OnClickListener getSnackBarClickListener() {
        return v -> {
            if (getActivity() instanceof GoToCartListener) {
                ((GoToCartListener) getActivity()).onGoToCart();
            }
        };
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        productViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(ProductViewModel.class);
        productViewModel.getProduct().observe(this, this::updateState);
        cartViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(CartViewModel.class);
    }

    private void updateSizeButtons() {
        // cleanup old state
        sizesContainer.removeAllViews();

        // update new state
        if (product.getSizes() == null) {
            sizesContainer.setVisibility(View.GONE);
            sizesDivider.setVisibility(View.GONE);
        } else {
            sizesContainer.setVisibility(View.VISIBLE);
            sizesDivider.setVisibility(View.VISIBLE);
            final Typeface regular = ResourcesCompat.getFont(sizesContainer.getContext(), R.font.montserrat_light_400);
            final Typeface selected = ResourcesCompat.getFont(sizesContainer.getContext(), R.font.montserrat_medium_600);
            View.OnClickListener clickListener = v -> {
                // reset state for all buttons:
                for (int i = 0; i < sizesContainer.getChildCount(); i++) {
                    TextView child = (TextView) sizesContainer.getChildAt(i);
                    child.setTypeface(regular);
                    child.setSelected(false);
                }

                // set selected state on the selected button
                ((TextView) v).setTypeface(selected);
                v.setSelected(true);
            };

            int radius = sizesContainer.getContext().getResources().getDimensionPixelSize(R.dimen.size_button_radius);

            for (String size : product.getSizes().split(getString(R.string.sizes_string_separator))) {
                TextView view = (TextView) getLayoutInflater().inflate(R.layout.item_size_button, sizesContainer, false);
                view.setText(size);
                view.setEnabled(true);
                view.setOnClickListener(clickListener);
                view.setOutlineProvider(ActivityUtils.getRoundedRectProvider(radius));
                sizesContainer.addView(view);
            }

            sizesContainer.getChildAt(sizesContainer.getChildCount() / 2).performClick();
        }
    }

    private void updateState(ProductWithData productWithData) {
        this.product = productWithData.getProduct();
        ((ImagesAdapter) imagesRecyclerView.getAdapter()).setCloud(product.getCloud());
        ImageHelper.loadProductImage(product.getMainImage(), product.getCloud(), mainImage);
        description.setText(productWithData.getProduct().getDescription());
        updatePrice();
        updateSizeButtons();
        ((ImagesAdapter) imagesRecyclerView.getAdapter()).setImages(productWithData.getImages().stream().map(ProductPublicId::getPublicId).collect(Collectors.toList()));
    }

    private void updatePrice() {
        price.setText(getString(R.string.price, product.getPrice() * picker.getValue()));
    }

    public interface GoToCartListener {
        void onGoToCart();
    }
}