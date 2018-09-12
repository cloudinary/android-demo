package com.cloudinary.android.demo.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.cld.ImageHelper;
import com.cloudinary.android.demo.data.model.Product;
import com.cloudinary.android.demo.data.model.ProductWithData;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Nitzan Jaitman on 08/02/2018.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder> {
    private final Drawable favoriteSelected;
    private final Drawable favoriteUnselected;
    private final Integer itemWidth;
    private final Integer itemMargin;
    private ProductSelectListener productSelectListener;
    private FavoriteStateChangedListener favoriteStateChangedListener;
    private List<ProductWithData> products = new ArrayList<>();

    ProductsAdapter(Context context, Integer itemWidth, Integer itemMargin) {
        favoriteSelected = context.getDrawable(R.drawable.ic_heart_selected);
        favoriteUnselected = context.getDrawable(R.drawable.ic_heart_black);
        this.itemWidth = itemWidth;
        this.itemMargin = itemMargin;
    }

    void setSelectListener(ProductSelectListener listener) {
        this.productSelectListener = listener;
    }

    void setFavoriteListener(FavoriteStateChangedListener listener) {
        this.favoriteStateChangedListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        int radius = view.getContext().getResources().getDimensionPixelSize(R.dimen.product_price_corner_radius);
        TextView price = view.findViewById(R.id.product_item_text_price);
        price.setOutlineProvider(ActivityUtils.getRoundedRectProvider(radius));


        if (itemWidth != null) {
            view.getLayoutParams().width = itemWidth;
        }

        if (itemMargin != null) {
            ((RecyclerView.LayoutParams) view.getLayoutParams()).setMargins(itemMargin, itemMargin, itemMargin, itemMargin);
        }

        view.setOnClickListener(v -> {
            if (productSelectListener != null) {
                productSelectListener.onProductSelected((Product) v.getTag());
            }
        });

        ImageView favoriteMarker = view.findViewById(R.id.product_item_image_favorite_marker);

        favoriteMarker.setOnClickListener(v -> {
            if (favoriteStateChangedListener != null) {
                ProductWithData productWithData = (ProductWithData) v.getTag();
                favoriteStateChangedListener.onFavoriteStateChanged(productWithData, !productWithData.isFavorite());
            }
        });

        return new ProductViewHolder(view, view.findViewById(R.id.product_item_Image), view.findViewById(R.id.product_item_text_name), view.findViewById(R.id.product_item_text_description), price, favoriteMarker);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductWithData productWithData = products.get(position);
        Product item = productWithData.getProduct();
        holder.itemView.setTag(productWithData.getProduct());
        holder.name.setText(item.getName());
        holder.description.setText(item.getDescription());
        holder.price.setText(holder.price.getContext().getString(R.string.price, item.getPrice()));
        holder.favoriteMarker.setTag(productWithData);
        holder.favoriteMarker.setImageDrawable(productWithData.isFavorite() ? favoriteSelected : favoriteUnselected);
        ImageHelper.loadProductsAdapterImages(item.getMainImage(), item.getCloud(), holder.image);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setItems(List<ProductWithData> products) {
        ArrayList<ProductWithData> newProducts = new ArrayList<>(products.size());
        products.forEach(p -> newProducts.add(p.copy()));
        List<ProductWithData> old = this.products;
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new IdentifiableDiff<>(old, newProducts));
        this.products = newProducts;
        diffResult.dispatchUpdatesTo(this);
    }

    public interface ProductSelectListener {
        void onProductSelected(Product product);
    }

    public interface FavoriteStateChangedListener {
        void onFavoriteStateChanged(ProductWithData productWithData, boolean isFavorite);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        final ImageView image;
        final TextView name;
        final TextView description;
        private final TextView price;
        private final ImageView favoriteMarker;

        ProductViewHolder(View itemView, ImageView image, TextView name, TextView description, TextView price, ImageView favoriteMarker) {
            super(itemView);
            this.image = image;
            this.name = name;
            this.description = description;
            this.price = price;
            this.favoriteMarker = favoriteMarker;
        }
    }
}
