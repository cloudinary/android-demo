package com.cloudinary.android.demo.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by Nitzan Jaitman on 08/02/2018.
 */

@Scope
@Retention(value = RetentionPolicy.RUNTIME)
public @interface FragmentScope {
}
