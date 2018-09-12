package com.cloudinary.android.demo.app;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.data.model.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity implements HomeFragment.HomeFragmentListener {

    public static final String INTENT_EXTRA_TAB = "tab_choice";
    public static final String INTENT_EXTRA_TAB_CART = "cart";

    private static final String TAG_HOME = "fragment_tag_home";
    private static final String TAG_ABOUT = "fragment_tag_about";
    private static final String TAG_CART = "fragment_tag_cart";
    private static final String TAG_FAVORITES = "fragment_tag_favorites";

    private TextView title;
    private BottomNavigationView navigation;
    private String currentTag;
    private final BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
            = buildSelectListener();

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // init toolbar:
        title = findViewById(R.id.main_text_title);
        toolbar = findViewById(R.id.main_toolbar);
        title.setText(R.string.home_activity_title);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        // setup navigation
        navigation = findViewById(R.id.main_navigation);
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

        if (getIntent() != null) {
            onNewIntent(getIntent());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.mnu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_menu_add_product) {
            startUpload();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void startUpload() {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, toolbar, "transition");

        // reveal from top right corner:
        Intent intent = new Intent(this, UploadActivity.class);
        intent.putExtra(UploadActivity.EXTRA_CIRCULAR_REVEAL_X, ActivityUtils.getScreenWidth(this));
        intent.putExtra(UploadActivity.EXTRA_CIRCULAR_REVEAL_Y, 0);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String tab = intent.getStringExtra(INTENT_EXTRA_TAB);
        if (tab != null) {
            // go to tab:
            if (INTENT_EXTRA_TAB_CART.equals(tab)) {
                navigation.setSelectedItemId(R.id.navigation_cart);
            }
        }
    }

    @Override
    public void onCategorySelected(String tag, View imageView, View view) {
        startActivity(new Intent(this, ProductsActivity.class));
    }

    @Override
    public void onProductSelected(Product product) {
        Intent intent = new Intent(this, ProductActivity.class).putExtra(ProductActivity.PRODUCT_ID_INTENT_EXTRA, product.getId());
        startActivity(intent);
    }

    @Override
    public void onDepartmentChanged(String department, View view) {
        Intent intent = new Intent(this, CategoriesActivity.class);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, view,
                CategoriesActivity.DEPARTMENT_IMAGE_TRANSITION_NAME);
        startActivity(intent, options.toBundle());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener buildSelectListener() {
        return item -> {

            getSupportActionBar().setDisplayShowHomeEnabled(false);

            switch (item.getItemId()) {
                case R.id.navigation_about:
                    switchTo(TAG_ABOUT, AboutFragment::new);
                    title.setText(R.string.title_about);
                    break;
                case R.id.navigation_home:
                    switchTo(TAG_HOME, HomeFragment::new);
                    title.setText(R.string.title_home);
                    break;
                case R.id.navigation_favorites:
                    switchTo(TAG_FAVORITES, FavoritesFragment::new);
                    title.setText(R.string.title_favorites);
                    break;
                case R.id.navigation_cart:
                    switchTo(TAG_CART, CartFragment::new);
                    title.setText(R.string.title_cart);
                    break;
                default:
                    return false;
            }

            return true;
        };
    }

    private void switchTo(String tag, FragmentCreator creator) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment newFragment = fragmentManager.findFragmentByTag(tag);
        boolean add = false;
        if (newFragment == null) {
            add = true;
            newFragment = creator.create();
        }

        if (!newFragment.isVisible()) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment previousFragment = fragmentManager.findFragmentByTag(currentTag);
            if (previousFragment != null) {
                fragmentTransaction.hide(previousFragment);
            }

            if (add) {
                fragmentTransaction
                        .add(R.id.category_viewgroup_container, newFragment, tag);
            } else {
                fragmentTransaction.show(newFragment);
            }

            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commitNow();
        }

        currentTag = tag;
    }

    private interface FragmentCreator {
        Fragment create();
    }
}
