package com.cloudinary.android.demo.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloudinary.android.demo.databinding.FragmentUploadPhotoBinding;
import com.cloudinary.android.demo.viewmodel.UploadViewModel;

import java.util.Locale;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerFragment;

public class UploadPhotoFragment extends DaggerFragment {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private UploadViewModel uploadViewModel;

    private FragmentUploadPhotoBinding binding;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        uploadViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(UploadViewModel.class);
        uploadViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(UploadViewModel.class);
        uploadViewModel.getProgress().observe(this, this::presentProgress);
        binding.setProduct(uploadViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUploadPhotoBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    private void presentProgress(Float progress) {
        float progressPercentage = progress * 100;
        binding.uploadProgress.setProgress(Math.round(progressPercentage));
        binding.uploadTextProgress.setText(String.format(Locale.getDefault(), "%.0f %%", progressPercentage));
    }
}
