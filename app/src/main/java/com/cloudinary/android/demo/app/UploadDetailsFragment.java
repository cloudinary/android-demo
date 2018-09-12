package com.cloudinary.android.demo.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.databinding.FragmentUploadDetailsBinding;
import com.cloudinary.android.demo.viewmodel.UploadViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerFragment;

import static android.app.Activity.RESULT_OK;
import static com.cloudinary.android.demo.viewmodel.UploadViewModel.IMAGE_COUNT;
import static com.cloudinary.android.demo.viewmodel.UploadViewModel.STATUS_CODE_NO_CATEGORY;
import static com.cloudinary.android.demo.viewmodel.UploadViewModel.STATUS_CODE_NO_DESCRIPTION;
import static com.cloudinary.android.demo.viewmodel.UploadViewModel.STATUS_CODE_NO_IMAGES;
import static com.cloudinary.android.demo.viewmodel.UploadViewModel.STATUS_CODE_NO_NAME;
import static com.cloudinary.android.demo.viewmodel.UploadViewModel.STATUS_CODE_NO_PRICE;

public class UploadDetailsFragment extends DaggerFragment {
    private static final int CHOOSE_IMAGE_REQUEST_CODE = 1000;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private UploadViewModel uploadViewModel;
    private FragmentUploadDetailsBinding binding;

    private List<ImageView> images = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        uploadViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(UploadViewModel.class);
        uploadViewModel.getEvents().observe(this, this::presentEvents);
        binding.setProduct(uploadViewModel);
    }

    private void presentEvents(Integer status) {
        if (status < UploadViewModel.STATUS_CODE_STARTING) {
            // show error message:
            Snackbar.make(binding.getRoot(), descriptionFromCode(status), Snackbar.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private String descriptionFromCode(Integer status) {
        int resId;
        switch (status) {
            case STATUS_CODE_NO_IMAGES:
                resId = R.string.upload_error_no_images;
                break;
            case STATUS_CODE_NO_CATEGORY:
                resId = R.string.upload_error_no_category;
                break;
            case STATUS_CODE_NO_DESCRIPTION:
                resId = R.string.upload_error_no_description;
                break;
            case STATUS_CODE_NO_NAME:
                resId = R.string.upload_error_no_name;
                break;
            case STATUS_CODE_NO_PRICE:
                resId = R.string.upload_error_no_price;
                break;
            default:
                return getString(R.string.error_uploading_with_code, status);
        }

        return getString(resId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode >= CHOOSE_IMAGE_REQUEST_CODE && requestCode < CHOOSE_IMAGE_REQUEST_CODE + IMAGE_COUNT) {
            final int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            Uri uri = data.getData();
            if (uri != null) {
                Context context = getContext();
                if (context == null) {
                    // activity is gone, nothing to do anymore
                    return;
                }

                if (DocumentsContract.isDocumentUri(context, uri)) {
                    context.getContentResolver().takePersistableUriPermission(uri, takeFlags);
                }

                updateImage(uri, requestCode - CHOOSE_IMAGE_REQUEST_CODE);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUploadDetailsBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);

        Spinner spinner = binding.uploadSpinnerGender;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(spinner.getContext(),
                R.array.gender_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        images.add(binding.uploadImageMain);
        images.add(binding.uploadImage2);
        images.add(binding.uploadImage3);

        for (int i = 0; i < images.size(); i++) {
            int finalI = i;
            images.get(i).setOnClickListener(v -> ActivityUtils.openMediaChooser(this, CHOOSE_IMAGE_REQUEST_CODE + finalI));
        }

        binding.uploadButtonSave.setOnClickListener(v -> uploadViewModel.saveProduct());
        binding.uploadButtonSave.setOutlineProvider(ActivityUtils.getRoundedRectProvider(inflater.getContext().getResources().getDimensionPixelSize(R.dimen.upload_button_corners)));
        return binding.getRoot();
    }

    private void updateImage(Uri uri, int i) {
        images.get(i).setTag(uri);
        uploadViewModel.setImages(images.stream().map(imageView -> imageView.getTag() == null ? null : imageView.getTag().toString()).collect(Collectors.toList()));
    }
}
