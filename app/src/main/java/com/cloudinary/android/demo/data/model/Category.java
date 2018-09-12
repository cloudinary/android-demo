package com.cloudinary.android.demo.data.model;

import com.cloudinary.android.demo.app.Identifiable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Nitzan Jaitman on 26/02/2018.
 */

public class Category implements Identifiable {
    private final String tag;
    private final Set<String> departments = new HashSet<>();
    private String publicIdMen;
    private String publicIdWomen;
    private int countMen;
    private int countWomen;
    private String cloud;

    public Category(String tag) {
        this.tag = tag;
    }

    public String getPublicIdMen() {
        return publicIdMen;
    }

    public void setPublicIdMen(String publicIdMen) {
        this.publicIdMen = publicIdMen;
    }

    public String getPublicIdWomen() {
        return publicIdWomen;
    }

    public void setPublicIdWomen(String publicIdWomen) {
        this.publicIdWomen = publicIdWomen;
    }

    public String getTag() {
        return tag;
    }

    public void incMen() {
        countMen++;
    }

    public void incWomen() {
        countWomen++;
    }

    public Set<String> getDepartments() {
        return departments;
    }

    public void addDepartment(String department) {
        if (department.equals("women")) {
            incWomen();
        } else {
            incMen();
        }

        departments.add(department);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        Category category = (Category) o;

        if (countMen != category.countMen) return false;
        if (countWomen != category.countWomen) return false;
        if (!tag.equals(category.tag)) return false;
        if (publicIdMen != null ? !publicIdMen.equals(category.publicIdMen) : category.publicIdMen != null)
            return false;
        if (publicIdWomen != null ? !publicIdWomen.equals(category.publicIdWomen) : category.publicIdWomen != null)
            return false;
        return departments.equals(category.departments);
    }

    @Override
    public int hashCode() {
        int result = tag.hashCode();
        result = 31 * result + (publicIdMen != null ? publicIdMen.hashCode() : 0);
        result = 31 * result + (publicIdWomen != null ? publicIdWomen.hashCode() : 0);
        result = 31 * result + departments.hashCode();
        result = 31 * result + countMen;
        result = 31 * result + countWomen;
        return result;
    }

    public int getCountMen() {
        return countMen;
    }

    public int getCountWomen() {
        return countWomen;
    }

    @Override
    public String getId() {
        return tag;
    }

    public String getCloud() {
        return cloud;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }
}
