package com.cloudinary.android.demo.viewmodel;

import com.cloudinary.android.demo.data.ProductRepo;
import com.cloudinary.android.demo.remote.RemoteProductRepo;
import com.cloudinary.android.demo.util.ConfigurationProvider;
import com.cloudinary.utils.StringUtils;

import java.util.List;

import javax.inject.Inject;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableList;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * The model for the upload form + actions
 */
public class UploadViewModel extends ViewModel {
    public static final int IMAGE_COUNT = 3;
    public static final int STATUS_CODE_NO_IMAGES = 10;
    public static final int STATUS_CODE_NO_NAME = 11;
    public static final int STATUS_CODE_NO_DESCRIPTION = 12;
    public static final int STATUS_CODE_NO_PRICE = 13;
    public static final int STATUS_CODE_NO_CATEGORY = 14;
    public static final int STATUS_CODE_STARTING = 15;
    public static final int STATUS_CODE_UPLOAD_ERROR = 16;
    public static final int STATUS_CODE_FINISHED = 17;

    private final ProductRepo productRepo;
    private final ConfigurationProvider configurationProvider;
    private final MutableLiveData<Integer> events = new MutableLiveData<>();
    private ObservableField<String> name = new ObservableField<>();
    private ObservableList<String> images = new ObservableArrayList<>();
    private ObservableField<String> description = new ObservableField<>();
    private ObservableField<String> price = new ObservableField<>();
    private ObservableInt selectedGenderPosition = new ObservableInt();
    private ObservableField<String> category = new ObservableField<>();
    private MutableLiveData<Float> progressValue = new MutableLiveData<>();

    @Inject
    public UploadViewModel(ProductRepo productRepo, ConfigurationProvider configurationProvider) {
        this.productRepo = productRepo;
        this.configurationProvider = configurationProvider;
        for (int i = 0; i < IMAGE_COUNT; i++) {
            images.add(null);
        }
    }

    public ObservableField<String> getName() {
        return name;
    }

    public void setName(ObservableField<String> name) {
        this.name = name;
    }

    public ObservableList<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        for (int i = 0; i < images.size(); i++) {
            this.images.set(i, images.get(i));
        }
    }

    public ObservableField<String> getDescription() {
        return description;
    }

    public void setDescription(ObservableField<String> description) {
        this.description = description;
    }

    public ObservableField<String> getPrice() {
        return price;
    }

    public void setPrice(ObservableField<String> price) {
        this.price = price;
    }

    public ObservableField<String> getCategory() {
        return category;
    }

    public void setCategory(ObservableField<String> category) {
        this.category = category;
    }

    /**
     * Save the model to the repository. All the fields are automatically updated by the data
     * binding framework as soon as the UI is populated.
     */
    public synchronized void saveProduct() {
        // Validation checks:
        if (StringUtils.isBlank(name.get())) {
            events.setValue(STATUS_CODE_NO_NAME);
        } else if (StringUtils.isBlank(images.get(0))) {
            events.setValue(STATUS_CODE_NO_IMAGES);
        } else if (StringUtils.isBlank(description.get())) {
            events.setValue(STATUS_CODE_NO_DESCRIPTION);
        } else if (StringUtils.isBlank(price.get())) {
            events.setValue(STATUS_CODE_NO_PRICE);
        } else if (StringUtils.isBlank(category.get())) {
            events.setValue(STATUS_CODE_NO_CATEGORY);
        } else {
            // Validation successful, update status and start uploading
            events.setValue(STATUS_CODE_STARTING);
            productRepo.addProduct(images, name.get(), positionToString(selectedGenderPosition), description.get(), Integer.parseInt(price.get()), category.get(), new RemoteProductRepo.AddProductEvents() {

                @Override
                public void onStart() {
                    progressValue.setValue(0f);
                }

                @Override
                public void onError() {
                    events.setValue(STATUS_CODE_UPLOAD_ERROR);
                }

                @Override
                public void onFinished() {
                    events.setValue(STATUS_CODE_FINISHED);
                    progressValue.setValue(1f);
                }

                @Override
                public void onProgress(float progress) {
                    progressValue.setValue(progress);
                }
            });
        }
    }

    private String positionToString(ObservableInt selectedGenderPosition) {
        return configurationProvider.getDepartments()[selectedGenderPosition.get()];
    }

    public ObservableInt getSelectedGenderPosition() {
        return selectedGenderPosition;
    }

    public void setSelectedGenderPosition(ObservableInt selectedGenderPosition) {
        this.selectedGenderPosition = selectedGenderPosition;
    }

    public LiveData<Integer> getEvents() {
        return events;
    }

    public LiveData<Float> getProgress() {
        return progressValue;
    }
}
