package com.cloudinary.android.demo.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.data.model.CartItemWithData;
import com.cloudinary.android.demo.data.model.Product;
import com.cloudinary.android.demo.viewmodel.CartViewModel;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import dagger.android.support.DaggerFragment;

/**
 * Created by Nitzan Jaitman on 13/03/2018.
 */

public class CartFragment extends DaggerFragment {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private CartViewModel cartViewModel;
    private RecyclerView recyclerView;
    private TextView totalPrice;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        CartItemAdapter.CartListener listener = new CartItemAdapter.CartListener() {

            @Override
            public void onProductSelected(Product product) {
                if (getActivity() instanceof ProductsAdapter.ProductSelectListener) {
                    ((ProductsAdapter.ProductSelectListener) getActivity()).onProductSelected(product);
                }
            }

            @Override
            public void onItemAdd(CartItemWithData item) {
                cartViewModel.add(item.getProduct(), 1);
            }

            @Override
            public void onItemSubtract(CartItemWithData item) {
                cartViewModel.subtract(item.getProduct(), 1);
            }
        };

        // setup recycler view
        recyclerView = rootView.findViewById(R.id.cart_list_main);
        recyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new CartItemAdapter(listener));

        totalPrice = rootView.findViewById(R.id.cart_text_total_price);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the view model and register for updates on the cart state.
        // noinspection ConstantConditions activity cannot be null in 'onActivityCreated'
        cartViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(CartViewModel.class);
        cartViewModel.getCartItems().observe(this, this::updateUI);
    }

    /**
     * This method is called automatically when the LiveData object updates (registered above).
     *
     * @param cartItems The updated list of cart items.
     */
    private void updateUI(List<CartItemWithData> cartItems) {
        ((CartItemAdapter) recyclerView.getAdapter()).setItems(cartItems);
        int total = cartItems.stream().mapToInt(item -> item.getCartItem().getCount() * item.getProduct().getPrice()).sum();
        totalPrice.setText(getString(R.string.price, total));
    }
}
