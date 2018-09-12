package com.cloudinary.android.demo.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Toast;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.databinding.ActivityUploadBinding;
import com.cloudinary.android.demo.viewmodel.UploadViewModel;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerAppCompatActivity;

public class UploadActivity extends DaggerAppCompatActivity {
    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    @Inject
    Executor executor;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @SuppressWarnings("FieldCanBeLocal") // for clarity, view models are fields
    private UploadViewModel uploadViewModel;
    private int revealX;
    private int revealY;
    private ActivityUploadBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload);
        binding.setLifecycleOwner(this);

        // setup action bar:
        setSupportActionBar(binding.uploadToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.upload_viewgroup_container, new UploadDetailsFragment())
                .commit();

        final Intent intent = getIntent();

        if (savedInstanceState == null &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            // handle animation (circular reveal):
            binding.uploadViewgroupRoot.setVisibility(View.INVISIBLE);

            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);

            ViewTreeObserver viewTreeObserver = binding.uploadViewgroupRoot.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        binding.uploadViewgroupRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            binding.uploadViewgroupRoot.setVisibility(View.VISIBLE);
        }

        uploadViewModel = ViewModelProviders.of(this, viewModelFactory).get(UploadViewModel.class);
        uploadViewModel.getEvents().observe(this, this::presentEvents);

    }

    private void presentEvents(Integer status) {
        switch (status) {
            case UploadViewModel.STATUS_CODE_STARTING:
                getSupportFragmentManager().beginTransaction().replace(R.id.upload_viewgroup_container, new UploadPhotoFragment()).commit();
                break;
            case UploadViewModel.STATUS_CODE_UPLOAD_ERROR:
                Toast.makeText(this, R.string.upload_error, Toast.LENGTH_LONG).show();
                finish();
            case UploadViewModel.STATUS_CODE_FINISHED:
                Toast.makeText(this, R.string.upload_finished, Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    protected void revealActivity(int x, int y) {
        float finalRadius = (float) (Math.max(binding.uploadViewgroupRoot.getWidth(), binding.uploadViewgroupRoot.getHeight()) * 1.1);

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(binding.uploadViewgroupRoot, x, y, 0, finalRadius);
        circularReveal.setDuration(400);
        circularReveal.setInterpolator(new AccelerateInterpolator());

        // make the view visible and start the animation
        binding.uploadViewgroupRoot.setVisibility(View.VISIBLE);
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        circularReveal.start();
    }

    protected void unRevealActivity() {

        float finalRadius = (float) (Math.max(binding.uploadViewgroupRoot.getWidth(), binding.uploadViewgroupRoot.getHeight()) * 1.1);
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                binding.uploadViewgroupRoot, revealX, revealY, finalRadius, 0);

        circularReveal.setDuration(400);
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                binding.uploadViewgroupRoot.setVisibility(View.INVISIBLE);
                finish();
            }
        });

        circularReveal.start();
    }

    @Override
    public void finishAfterTransition() {
        unRevealActivity();
        super.finishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
