package org.miscar;

import org.gradle.api.provider.Property;

public abstract class MisCarExtension {
    abstract Property<Boolean> getUseSubmodules();
}
