package com.cloudinary.android.demo.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nitzan Jaitman on 11/04/2018.
 */

/**
 * A representation of the response from Cloudinary's list by tag request
 */
public class CloudinaryJsonResponse {

    @SerializedName("resources")
    @Expose
    private List<RemoteResource> resources = null;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public List<RemoteResource> getResources() {
        return resources;
    }

    public void setResources(List<RemoteResource> resources) {
        this.resources = resources;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public class RemoteResource {

        @SerializedName("public_id")
        @Expose
        private String publicId;
        @SerializedName("version")
        @Expose
        private Integer version;
        @SerializedName("format")
        @Expose
        private String format;
        @SerializedName("width")
        @Expose
        private Integer width;
        @SerializedName("height")
        @Expose
        private Integer height;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("context")
        @Expose
        private ResourceContext context;
        @SerializedName("tags")
        @Expose
        private List<String> tags;

        public String getPublicId() {
            return publicId;
        }

        public void setPublicId(String publicId) {
            this.publicId = publicId;
        }

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public Integer getWidth() {
            return width;
        }

        public void setWidth(Integer width) {
            this.width = width;
        }

        public Integer getHeight() {
            return height;
        }

        public void setHeight(Integer height) {
            this.height = height;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public ResourceContext getContext() {
            return context;
        }

        public void setContext(ResourceContext context) {
            this.context = context;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }
    }

    public class ResourceContext {

        @SerializedName("custom")
        @Expose
        private Custom custom;

        public Custom getCustom() {
            return custom;
        }

        public void setCustom(Custom custom) {
            this.custom = custom;
        }
    }

    public class Custom {

        @SerializedName("department")
        @Expose
        private String department;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("isMain")
        @Expose
        private boolean isMain;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("price")
        @Expose
        private int price;
        @SerializedName("productId")
        @Expose
        private int productId;
        @SerializedName("sizes")
        @Expose
        private String sizes;

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean getIsMain() {
            return isMain;
        }

        public void setIsMain(boolean isMain) {
            this.isMain = isMain;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(int price) {
            this.price = price;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getSizes() {
            return sizes;
        }

        public void setSizes(String sizes) {
            this.sizes = sizes;
        }
    }
}
