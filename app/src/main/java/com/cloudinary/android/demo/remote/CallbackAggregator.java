package com.cloudinary.android.demo.remote;

import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.Nullable;

/**
 * Aggregate upload callbacks from several upload operations, with a calculated percentage value
 * taking all images into account. This is useful in cases where there's no need for separate
 * callbacks for each uploaded image, but rather a single callback with notification only when
 * all the images are completely uploaded.
 */
public class CallbackAggregator implements UploadCallback {
    private final ProgressAggregator progress;
    private final RemoteProductRepo.AddProductEvents callback;
    private final List<String> images;
    private final List<String> resultPublicIds;
    private final onResultListener resultProcessor;
    private boolean started = false;
    private int success = 0;


    CallbackAggregator(RemoteProductRepo.AddProductEvents callback, List<String> images, @Nullable onResultListener resultProcessor) {
        this.callback = callback;
        this.images = images;
        this.progress = new ProgressAggregator(callback, images.size());
        resultPublicIds = new ArrayList<>(images.size());
        this.resultProcessor = resultProcessor;
    }

    @Override
    public void onStart(String requestId) {
        if (!started) {
            started = true;
            callback.onStart();
        }
    }

    @Override
    public void onProgress(String requestId, long bytes, long totalBytes) {
        progress.progress(requestId, bytes, totalBytes);
    }

    @Override
    public void onSuccess(String requestId, Map resultData) {
        success++;
        resultPublicIds.add(resultData.get("public_id").toString());
        if (success == images.size()) {
            // post process if exists:
            if (resultProcessor != null) {
                resultProcessor.onResult(resultPublicIds);
            }

            // post finish:
            callback.onFinished();
        }
    }

    @Override
    public void onError(String requestId, ErrorInfo error) {
        callback.onError();
    }

    @Override
    public void onReschedule(String requestId, ErrorInfo error) {
        callback.onError();
    }

    interface onResultListener {
        void onResult(List<String> publicIds);
    }

    private static final class ProgressAggregator {
        private final RemoteProductRepo.AddProductEvents callback;
        private final int idsCount;
        private long allImagesTotalBytes = 0;
        private Map<String, Long> progress = new ConcurrentHashMap<>();

        private ProgressAggregator(RemoteProductRepo.AddProductEvents callback, int idsCount) {
            this.callback = callback;
            this.idsCount = idsCount;
        }

        void progress(String id, long bytes, long totalBytes) {
            if (!progress.containsKey(id)) {
                // first time for this id, add to total:
                allImagesTotalBytes += totalBytes;
            }

            // update current bytes for id:
            progress.put(id, bytes);

            if (progress.size() == idsCount) {
                // all requests are here, we can start notifying:
                callback.onProgress((float) progress.values().stream().mapToLong(Long::longValue).sum() / allImagesTotalBytes);
            }
        }
    }
}

