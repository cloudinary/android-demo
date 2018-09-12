package com.cloudinary.android.demo.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.cld.ImageHelper;
import com.cloudinary.android.demo.data.model.Category;
import com.cloudinary.android.demo.data.model.Product;
import com.cloudinary.android.demo.data.model.ProductWithData;
import com.cloudinary.android.demo.viewmodel.ProductsViewModel;
import com.cloudinary.android.demo.widget.DepartmentCard;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerFragment;

/**
 * Created by Nitzan Jaitman on 07/03/2018.
 */

public class HomeFragment extends DaggerFragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private FlexboxLayout tagsContainer;
    private ProductsViewModel productsViewModel;
    private ImageView topBanner;
    private ImageView bottomBanner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        tagsContainer = rootView.findViewById(R.id.home_viewgroup_tags_container);
        topBanner = rootView.findViewById(R.id.home_image_top_banner);
        bottomBanner = rootView.findViewById(R.id.home_image_bottom_banner);

        setupDepartments(rootView);
        setupBanners();

        return rootView;
    }

    private void setupBanners() {
        View.OnClickListener bannerClickListener = this::bannerClicked;

        topBanner.setOnClickListener(bannerClickListener);
        bottomBanner.setOnClickListener(bannerClickListener);
    }

    private void bannerClicked(View v) {
        Activity activity = getActivity();
        if (activity instanceof HomeFragmentListener && v.getTag() instanceof Product) {
            ((HomeFragmentListener) activity).onProductSelected((Product) v.getTag());
        }
    }

    private void setupDepartments(View rootView) {
        DepartmentCard menCard = rootView.findViewById(R.id.home_card_men);
        DepartmentCard womenCard = rootView.findViewById(R.id.home_card_women);

        menCard.setOnClickListener(v -> selectDepartment(menCard));
        womenCard.setOnClickListener(v -> selectDepartment(womenCard));
    }

    private void selectDepartment(DepartmentCard card) {
        String category = card.getText().toString();
        productsViewModel.setFilter(new ProductsViewModel.ProductsFilter(category.toLowerCase(), null));
        Activity activity = getActivity();
        if (activity instanceof HomeFragmentListener) {
            ((HomeFragmentListener) activity).onDepartmentChanged(category, card.getImage());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Retrieve the view model and setup all events and filters:
        productsViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(ProductsViewModel.class);

        tagsClickListener tagsListener = tag -> {
            productsViewModel.setFilter(new ProductsViewModel.ProductsFilter(null, tag));
            FragmentActivity activity = getActivity();
            if (activity instanceof HomeFragmentListener) {
                ((HomeFragmentListener) activity).onCategorySelected(tag, null, null);
            }
        };

        productsViewModel.getCategories().observe(this, categories ->
                ActivityUtils.updateCategories(getActivity(), categories.stream().map(Category::getTag).collect(Collectors.toList()), tagsContainer, tagsListener));

        productsViewModel.getProducts().observe(this, this::updateBanners);
        productsViewModel.setFilter(new ProductsViewModel.ProductsFilter(null, null));
    }

    private void updateBanners(List<ProductWithData> products) {
        if (products.size() <= 0) {
            topBanner.setVisibility(View.GONE);
            bottomBanner.setVisibility(View.GONE);
            topBanner.setTag(null);
            bottomBanner.setTag(null);
        } else {
            topBanner.setVisibility(View.VISIBLE);
            loadInto(true, products.get(0), topBanner);

            if (products.size() > 1) {
                bottomBanner.setVisibility(View.VISIBLE);
                loadInto(false, products.get(1), bottomBanner);
            } else {
                bottomBanner.setVisibility(View.GONE);
                bottomBanner.setTag(null);
            }
        }
    }

    /**
     * Load the product into the given banner
     *
     * @param top             Is this the top banner (to choose the right transformation)
     * @param productWithData The product data
     * @param banner          The banner to update
     */
    @SuppressWarnings("unchecked")
    private void loadInto(boolean top, ProductWithData productWithData, final ImageView banner) {
        banner.setTag(productWithData.getProduct());

        Product product = productWithData.getProduct();

        if (top) {
            String firstProductTag = productWithData.getTags().iterator().next().getTag();
            ImageHelper.loadHomeFragmentTopBanner(product.getMainImage(), product.getCloud(), firstProductTag, banner);
        } else {
            ImageHelper.loadHomeFragmentBottomBanner(product.getMainImage(), product.getCloud(), product.getDepartment(), banner);
        }
    }

    public interface tagsClickListener {
        void onTagClicked(String tag);
    }

    public interface HomeFragmentListener extends CategoriesFragment.CategoriesActivityInterface, ProductsAdapter.ProductSelectListener {
        void onDepartmentChanged(String department, View view);
    }
}
