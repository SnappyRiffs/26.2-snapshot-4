/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  org.jspecify.annotations.Nullable
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import org.jspecify.annotations.Nullable;

public record RealmsConfigurationDto(
    @SerializedName(value = "options") RealmsSlotUpdateDto options,
    @SerializedName(value = "settings") List<RealmsSetting> settings,
    @SerializedName(value = "regionSelectionPreference")
        @Nullable RegionSelectionPreferenceDto regionSelectionPreference,
    @SerializedName(value = "description") @Nullable RealmsDescriptionDto description)
    implements ReflectionBasedSerialization {}
