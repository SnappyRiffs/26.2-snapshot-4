/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public record PingResult(
    @SerializedName(value = "pingResults") List<RegionPingResult> pingResults,
    @SerializedName(value = "worldIds") List<Long> realmIds)
    implements ReflectionBasedSerialization {}
