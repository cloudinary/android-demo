package com.cloudinary.android.demo.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.cld.ImageHelper;
import com.cloudinary.android.demo.data.model.Category;
import com.cloudinary.android.demo.viewmodel.ProductsViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {
    private static final String TAG = CategoriesAdapter.class.getSimpleName();
    private final CategorySelectedListener listener;
    private final int cornerRadius;
    private final int itemHeight;
    private List<Category> categories = new ArrayList<>();
    private String department;

    CategoriesAdapter(Context context, int itemHeight, CategorySelectedListener listener) {
        this.listener = listener;
        this.itemHeight = itemHeight;
        this.cornerRadius = context.getResources().getDimensionPixelSize(R.dimen.category_corner_radius);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        view.setOutlineProvider(ActivityUtils.getRoundedRectProvider(cornerRadius));
        view.setOnClickListener(v -> listener.onItemSelected((Category) v.getTag(), v, v.findViewById(R.id.category_item_text_title)));

        return new CategoryViewHolder(view, view.findViewById(R.id.category_item_text_title), view.findViewById(R.id.category_item_text_subtitle), view.findViewById(R.id.category_item_image));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category item = categories.get(position);
        boolean isWoman = department.equals("women");
        final int count = isWoman ? item.getCountWomen() : item.getCountMen();
        final String publicId = isWoman ? item.getPublicIdWomen() : item.getPublicIdMen();

        boolean fullSpan = position == 0;
        ((StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams()).setFullSpan(fullSpan);

        holder.itemView.setTag(item);
        holder.title.setText(item.getTag());
        holder.subtitle.setText(holder.subtitle.getContext().getResources().getQuantityString(R.plurals.items_count, count, count));

        // some items are square and some are tall rectangles, depending on amount of items:
        holder.itemView.getLayoutParams().height = count > 1 && position > 0 ? itemHeight * 2 : itemHeight;

        if (publicId != null) {
            ImageHelper.loadCategoriesAdapterItem(publicId, item.getCloud(), holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setItems(ProductsViewModel.CategoriesWrapper categoriesWrapper) {
        this.department = categoriesWrapper.department;
        List<Category> old = this.categories;
        List<Category> newItems = categoriesWrapper.categories;
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new IdentifiableDiff<>(old, newItems));
        this.categories = newItems;
        diffResult.dispatchUpdatesTo(this);
    }

    public interface CategorySelectedListener {
        void onItemSelected(Category category, View imageView, View textView);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;
        final ImageView imageView;

        CategoryViewHolder(View itemView, TextView title, TextView subtitle, ImageView imageView) {
            super(itemView);
            this.title = title;
            this.subtitle = subtitle;
            this.imageView = imageView;
        }
    }
}