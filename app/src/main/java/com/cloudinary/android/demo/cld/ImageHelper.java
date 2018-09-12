package com.cloudinary.android.demo.cld;

import android.widget.ImageView;

import com.cloudinary.Transformation;
import com.cloudinary.Url;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.ResponsiveUrl;
import com.cloudinary.transformation.TextLayer;
import com.squareup.picasso.Picasso;

/**
 * This class consolidates all the cloudinary image operations. It contains demonstrations of
 * several effects along with responsive images api, downloaded using Picasso.
 */
public class ImageHelper {

    public static final String DEFAULT_FORMAT = "webp";

    public static void loadCartItemAdapterImages(String publicId, String cloud, ImageView imageView) {
        // Cloudinary APIs used:
        // 1) Responsive url - generates a url with dimensions that automatically fit the ImageView
        // using the auto-fill scaling strategy.
        // 2) Dynamic format, change everything to webp, being the most suitable format for Android.
        MediaManager.get().responsiveUrl(imageView, publicId, ResponsiveUrl.Preset.AUTO_FILL, url -> {
            download(imageView, url.cloudName(cloud).format(DEFAULT_FORMAT));
        });
    }

    public static void loadImageAdapterImages(String publicId, String cloud, ImageView imageView) {
        // Cloudinary APIs used:
        // 1) Responsive url - generates a url with dimensions that automatically fit the ImageView
        // using the auto-fill scaling strategy.
        // 2) Dynamic format, change everything to webp, being the most suitable format for Android.
        MediaManager.get().responsiveUrl(imageView, publicId, ResponsiveUrl.Preset.AUTO_FILL, url -> {
            download(imageView, url.cloudName(cloud).format(DEFAULT_FORMAT));
        });
    }

    public static void loadCategoriesAdapterItem(String publicId, String cloud, ImageView imageView) {
        // Cloudinary APIs used:
        // 1) Crop a 80% sub-rectangle of the image to get a zoom effect, detecting what's
        // interesting in the image and keeping it inside the frame.
        // 2) Responsive url - generates a url with dimensions that automatically fit the ImageView
        // using the auto-fill scaling strategy.
        // 3) Dynamic format, change everything to webp, being the most suitable format for Android.
        Url baseUrl = MediaManager.get().url().transformation(new Transformation().width(0.80).height(0.80).crop("crop").gravity("auto")).publicId(publicId);
        MediaManager.get().responsiveUrl(imageView, baseUrl, ResponsiveUrl.Preset.AUTO_FILL, url -> {
            download(imageView, url.cloudName(cloud).format(DEFAULT_FORMAT));
        });
    }

    public static void loadProductsAdapterImages(String publicId, String cloud, ImageView imageView) {
        // Cloudinary APIs used:
        // 1) Responsive url - generates a url with dimensions that automatically fit the ImageView
        // using the auto-fill scaling strategy.
        // 2) Dynamic format, change everything to webp, being the most suitable format for Android.
        MediaManager.get().responsiveUrl(imageView, publicId, ResponsiveUrl.Preset.AUTO_FILL, url ->
                download(imageView, url.cloudName(cloud).format(DEFAULT_FORMAT)));
    }

    public static void loadProductImage(String publicId, String cloud, ImageView imageView) {
        // Cloudinary APIs used:
        // 1) Responsive url - generates a url with dimensions that automatically fit the ImageView
        // using the FIT scaling strategy.
        // 2) Dynamic format, change everything to webp, being the most suitable format for Android.
        MediaManager.get().responsiveUrl(ResponsiveUrl.Preset.FIT).maxDimension(2000).generate(publicId, imageView,
                url -> download(imageView, url.cloudName(cloud).format(DEFAULT_FORMAT)));
    }

    public static void updateProductToolbarImage(String publicId, String cloud, ImageView imageView) {
        // Cloudinary APIs used:
        // 1) Responsive url - generates a url with dimensions that automatically fit the ImageView
        // using custom strategy - thumb crop mode with automatic gravity, applied to both width
        // and height
        // 2) Dynamic format, change everything to webp, being the most suitable format for Android.
        MediaManager.get().responsiveUrl(true, true, "thumb", "auto")
                .generate(publicId, imageView, url -> download(imageView, url.cloudName(cloud).format(DEFAULT_FORMAT)));

    }

    @SuppressWarnings("unchecked")
    public static void loadHomeFragmentTopBanner(String publicId, String cloud, String firstProductTag, ImageView imageView) {
        // This transformation scales the images and overlays it on a background using
        // custom coordinates. Then a dynamic text layer is baked into the image.
        Transformation transformationTop = new Transformation()
                .gravity("east").height(800).width(1500).background("#FCD7A9").crop("fill").chain()
                .overlay(new TextLayer().fontFamily("montserrat").fontSize(60).fontStyle("bold").textAlign("left").text("%20" + firstProductTag + "%20up%20%20to%20"))
                .color("black").flags("text_no_trim").y(-160).background("#FCD7A9").gravity("west").x(50).border("2px_solid_rgb:FCD7A9").chain()
                .overlay(new TextLayer().fontFamily("montserrat").fontSize(120).fontStyle("bold").textAlign("left").text("%2070%20%25%20"))
                .color("black").flags("text_no_trim").background("#FCD7A9").gravity("west").x(50).chain()
                .overlay(new TextLayer().fontFamily("montserrat").fontSize(50).fontStyle("light").textAlign("center").text("%20%20%20Shop%20Now%20%20%20"))
                .color("white").y(160).background("black").border("40px_solid_rgb:000000").gravity("west").x(50);

        Url baseUrl = MediaManager.get().url().transformation(transformationTop).publicId(publicId);
        MediaManager.get().responsiveUrl(true, false, "scale", "center")
                .maxDimension(1500)
                .generate(baseUrl, imageView, url -> download(imageView, url.format(DEFAULT_FORMAT).cloudName(cloud)));
    }

    @SuppressWarnings("unchecked")
    public static void loadHomeFragmentBottomBanner(String publicId, String cloud, String department, ImageView imageView) {
        // This transformation scales the images and overlays it on a background using
        // custom coordinates. Then a dynamic text layer is baked into the image.
        Transformation transformationBottom =
                new Transformation()
                        .height(800).width(1500).background("grey").crop("fill").gravity("auto").chain()
                        .overlay(new TextLayer().fontFamily("montserrat").fontSize(60).fontStyle("medium").text("%20" + department + "%20"))
                        .color("black").flags("text_no_trim").y(-160).background("#FCD7A9").border("6px_solid_rgb:FCD7A9").chain()
                        .overlay(new TextLayer().fontFamily("montserrat").fontSize(120).fontStyle("bold").textAlign("left").text("%2030%25%20off"))
                        .color("black").flags("text_no_trim").background("#FCD7A9").border("10px_solid_rgb:FCD7A9").chain()
                        .overlay(new TextLayer().fontFamily("montserrat").fontSize(50).fontStyle("light").textAlign("center").text("%20%20%20Shop%20Now%20%20%20"))
                        .color("white").y(160).background("black").border("40px_solid_black");

        Url baseUrl = MediaManager.get().url().transformation(transformationBottom).publicId(publicId);
        MediaManager.get().responsiveUrl(true, false, "scale", "center")
                .maxDimension(1500)
                .generate(baseUrl, imageView, url -> download(imageView, url.format(DEFAULT_FORMAT).cloudName(cloud)));
    }

    /**
     * Download the given url into the given image view. This method uses Picasso but any other
     * image download library can be used just as easily (glide, fresco, etc).
     *
     * @param imageView The image view to hold the downloaded image.
     * @param url       The Url to load.
     */
    private static void download(ImageView imageView, Url url) {
        download(imageView, url.generate());
    }

    /**
     * Download the given url into the given image view. This method uses Picasso but any other
     * image download library can be used just as easily (glide, fresco, etc).
     *
     * @param imageView The image view to hold the downloaded image.
     * @param url       The Url to load.
     */
    private static void download(ImageView imageView, String url) {
        Picasso.get().load(url).into(imageView);
    }
}
