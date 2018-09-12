package com.cloudinary.android.demo.data;

import android.util.Log;

import com.cloudinary.android.demo.data.model.CartItem;
import com.cloudinary.android.demo.data.model.CartItemWithData;
import com.cloudinary.android.demo.data.model.FavoriteProduct;
import com.cloudinary.android.demo.data.model.Product;
import com.cloudinary.android.demo.data.model.ProductPublicId;
import com.cloudinary.android.demo.data.model.ProductTag;
import com.cloudinary.android.demo.data.model.ProductWithData;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

/**
 * Created by Nitzan Jaitman on 07/02/2018.
 */

/**
 * Data access object definition for Android Room. This is the main app database entry point.
 * Room generates the implementation of abstract methods, based on queries and annotations.
 */
@Dao
public abstract class ProductDao {
    private static final String TAG = ProductDao.class.getSimpleName();
    private static final String selectAllProducts = "SELECT * FROM Product";

    // ========
    //   Cart
    // ========
    @Insert
    abstract void insert(CartItem item);

    @Update
    abstract void update(CartItem cartItem);

    @Query("SELECT * FROM CartItem WHERE productId=:productId")
    abstract CartItem getCartItemForProductId(int productId);

    @Query("DELETE FROM CartItem WHERE productId=:productId")
    public abstract void deleteCartForProductId(Integer productId);

    @Query("SELECT * FROM CartItem " +
            "INNER JOIN product ON Product.id = CartItem.productId")
    abstract LiveData<List<CartItemWithData>> getCart();

    // ============
    //   Product
    // ============
    @Insert
    abstract void insert(Product product);

    @Update
    abstract void update(Product product);

    @Insert
    abstract long insert(ProductPublicId productPublicId);

    @Insert
    abstract long insert(ProductTag productTag);

    @Transaction
    @Query(selectAllProducts)
    protected abstract List<ProductWithData> getAllProductsWithDataSync();

    @Transaction
    @Query(selectAllProducts)
    abstract LiveData<List<ProductWithData>> getAllProductsWithData();

    @Transaction
    @Query("SELECT * FROM Product " +
            "WHERE Product.id=:productId")
    public abstract ProductWithData getProductById(int productId);

    /**
     * Syncs the local database to match the remote, remove deleted products, update data, etc.
     * All the observers of the database (e.g. any user of a live-data method) will get notified
     * once the local database is synced.
     * This should be called whenever a new list is fetched from the remote repository.
     *
     * @param remoteProducts The list of products retrieved from the remote repository.
     */
    @Transaction
    public void syncProducts(List<ProductWithData> remoteProducts) {
        // note: Remote products are aggregated  - tags and public ids are already included
        if (remoteProducts.isEmpty()) {
            // nothing in remote (note: we assume that if a remote call fails we will not reach here
            // with an empty list)
            clearDb();
            return;
        }

        Function<ProductWithData, Integer> idComparator = productWithData -> productWithData.getProduct().getId();

        // list local and remotes products ordered by ids:
        List<ProductWithData> localProducts = getAllProductsWithDataSync().stream()
                .sorted(Comparator.comparing(idComparator))
                .collect(Collectors.toList());

        remoteProducts = remoteProducts.stream()
                .sorted(Comparator.comparing(idComparator)).collect(Collectors.toList());

        if (localProducts.isEmpty()) {
            // everything is new, insert everything as is:
            remoteProducts.forEach(this::insertWithAggregates);
            return;
        }

        //  products exists both locally and remotely, compare and synchronize local:
        int localIndex = 0;
        int remoteIndex = 0;

        // note: product Ids are NOT local, they are stable ids coming from remote repository
        while (remoteIndex < remoteProducts.size() || localIndex < localProducts.size()) {
            ProductWithData remote = remoteIndex >= remoteProducts.size() ? null : remoteProducts.get(remoteIndex);
            ProductWithData local = localIndex >= localProducts.size() ? null : localProducts.get(localIndex);

            if (remote == null || (local != null && (remote.getProduct().getId() > local.getProduct().getId()))) {
                // local doesn't exist in remote anymore, delete local:
                //noinspection ConstantConditions Local should NEVER be null in here. if it
                // happens let is crash to investigate
                deleteProductWithAggregate(local);
                localIndex++;
            } else if (local == null || remote.getProduct().getId() < local.getProduct().getId()) {
                // remote doesn't exist locally, insert:
                insertWithAggregates(remote);
                remoteIndex++;
            } else if (remote.getProduct().getId().equals(local.getProduct().getId())) {
                // products exits both locally and remotely, sync it's data:
                updateLocalFromRemote(remote, local);
                remoteIndex++;
                localIndex++;
            }
        }
    }

    private void deleteProductWithAggregate(ProductWithData local) {
        int tags = deleteTagsForProductId(local.getProduct().getId());
        int publicIds = deletePublicIdsForProductId(local.getProduct().getId());
        int products = delete(local.getProduct());
        Log.d(TAG, String.format("Deleted %d products with %d tags and %d public IDs", products, tags, publicIds));
    }

    @Transaction
    public void updateLocalFromRemote(ProductWithData remote, ProductWithData local) {
        update(remote.getProduct());

        // replace tags and public ids for simplicity:
        deleteTagsForProductId(local.getProduct().getId());
        deletePublicIdsForProductId(local.getProduct().getId());

        insertPublicIds(remote.getImages());
        insertTags(remote.getTags());
//        remote.getImages().forEach(this::insert);
//        remote.getTags().forEach(this::insert);
    }

    @Transaction
    public void insertWithAggregates(ProductWithData product) {
        // set (local) creation date
        product.getProduct().setTimestamp(new Date().getTime());
        insert(product.getProduct());
        product.getTags().forEach(this::insert);
        product.getImages().forEach(this::insert);
    }

    @Delete
    public abstract int delete(Product product);

    @Query("DELETE FROM Product")
    abstract void deleteAllProducts();

    // ========
    //   Tags
    // ========
    @Insert
    protected abstract void insertTags(Collection<ProductTag> tags);

    @Query("DELETE FROM ProductTag")
    abstract int deleteAllTags();

    @Query("DELETE FROM ProductTag WHERE ProductTag.productId=:productId")
    abstract int deleteTagsForProductId(int productId);

    // ==============
    //   Public ids
    // ==============
    @Insert
    protected abstract void insertPublicIds(Collection<ProductPublicId> publicIds);

    @Query("DELETE FROM ProductPublicId WHERE ProductPublicId.productId=:productId")
    abstract int deletePublicIdsForProductId(int productId);

    @Query("DELETE FROM ProductPublicId")
    abstract int deleteAllPublicIds();

    // =============
    //   Favorites
    // =============
    @Query("SELECT * FROM FavoriteProduct")
    abstract LiveData<List<FavoriteProduct>> getFavoriteIds();

    @Insert
    abstract void addToFavorite(FavoriteProduct product);

    @Query("DELETE FROM FavoriteProduct WHERE productId=:productId")
    abstract void removeFromFavorites(int productId);

    @Transaction
    @Query("SELECT * FROM Product WHERE id in (SELECT productId FROM FavoriteProduct)")
    abstract LiveData<List<ProductWithData>> getFavoriteProducts();

    // ==================
    //   Helper methods
    // ==================
    @Transaction
    public void clearDb() {
        deleteAllTags();
        deleteAllPublicIds();
        deleteAllProducts();
    }
}
