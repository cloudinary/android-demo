package com.cloudinary.android.demo.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.cld.ImageHelper;
import com.cloudinary.android.demo.data.model.CartItem;
import com.cloudinary.android.demo.data.model.CartItemWithData;
import com.cloudinary.android.demo.data.model.Product;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Nitzan Jaitman on 13/03/2018.
 */

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {
    private final CartListener listener;
    private List<CartItemWithData> cartItems = new ArrayList<>();

    CartItemAdapter(CartListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        View add = view.findViewById(R.id.cart_item_button_add);
        View subtract = view.findViewById(R.id.cart_item_button_subtract);

        add.setOnClickListener(v -> listener.onItemAdd((CartItemWithData) v.getTag()));
        subtract.setOnClickListener(v -> listener.onItemSubtract((CartItemWithData) v.getTag()));
        view.setOnClickListener(v -> listener.onProductSelected((Product) v.getTag()));

        return new CartItemViewHolder(view, view.findViewById(R.id.cart_item_text_title), view.findViewById(R.id.cart_item_text_subtitle), view.findViewById(R.id.cart_item_text_count), view.findViewById(R.id.cart_item_text_price),
                view.findViewById(R.id.cart_item_image_main), add, subtract, view.findViewById(R.id.cart_item_text_total_price));
    }

    /**
     * Set the items for the adapter to display
     *
     * @param newItems List of the cart items to display
     */
    public void setItems(List<CartItemWithData> newItems) {
        List<CartItemWithData> old = this.cartItems;
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new IdentifiableDiff<>(old, newItems));
        this.cartItems = newItems;
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItemWithData item = cartItems.get(position);
        Product product = item.getProduct();
        CartItem cartItem = item.getCartItem();

        holder.title.setText(product.getName());
        holder.subtitle.setText(product.getDescription());
        holder.count.setText(String.valueOf(cartItem.getCount()));
        holder.price.setText(holder.price.getContext().getString(R.string.price, product.getPrice()));
        holder.totalPrice.setText(holder.price.getContext().getString(R.string.price, product.getPrice() * cartItem.getCount()));
        holder.add.setTag(item);
        holder.subtract.setTag(item);
        holder.itemView.setTag(product);
        ImageHelper.loadCartItemAdapterImages(product.getMainImage(), product.getCloud(), holder.imageView);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public interface CartListener extends ProductsAdapter.ProductSelectListener {
        void onItemAdd(CartItemWithData item);

        void onItemSubtract(CartItemWithData item);
    }

    static class CartItemViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView subtitle;
        final TextView count;
        final TextView price;
        final ImageView imageView;
        final View add;
        final View subtract;
        final TextView totalPrice;

        CartItemViewHolder(View itemView, TextView title, TextView subtitle, TextView count, TextView price, ImageView imageView, View add, View subtract, TextView totalPrice) {
            super(itemView);
            this.title = title;
            this.subtitle = subtitle;
            this.count = count;
            this.price = price;
            this.imageView = imageView;
            this.add = add;
            this.subtract = subtract;
            this.totalPrice = totalPrice;
        }
    }
}
