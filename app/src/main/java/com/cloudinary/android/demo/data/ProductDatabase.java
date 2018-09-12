package com.cloudinary.android.demo.data;

import com.cloudinary.android.demo.data.model.CartItem;
import com.cloudinary.android.demo.data.model.FavoriteProduct;
import com.cloudinary.android.demo.data.model.Product;
import com.cloudinary.android.demo.data.model.ProductPublicId;
import com.cloudinary.android.demo.data.model.ProductTag;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * Created by Nitzan Jaitman on 07/02/2018.
 */

/**
 * Database definition for Android Room.
 */
@Database(entities = {Product.class, ProductTag.class, ProductPublicId.class, CartItem.class, FavoriteProduct.class}, version = 1)
public abstract class ProductDatabase extends RoomDatabase {
    public abstract ProductDao getProductDao();
}
