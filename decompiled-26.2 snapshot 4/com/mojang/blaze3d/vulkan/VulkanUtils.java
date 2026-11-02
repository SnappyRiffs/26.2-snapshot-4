/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.vulkan.VkClearColorValue
 */
package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.systems.BackendCreationException;
import net.minecraft.util.ARGB;
import org.lwjgl.vulkan.VkClearColorValue;

public class VulkanUtils {
    public static void throwIfFailure(int result, String message, BackendCreationException.Reason reason) throws BackendCreationException {
        if (result < 0) {
            throw new BackendCreationException(VulkanUtils.resultToString(result) + ": " + message, reason);
        }
    }

    public static void crashIfFailure(int result, String message) {
        if (result < 0) {
            throw new IllegalStateException(VulkanUtils.resultToString(result) + ": " + message);
        }
    }

    public static String resultToString(int error) {
        return switch (error) {
            case 0 -> "VK_SUCCESS";
            case 1 -> "VK_NOT_READY";
            case 2 -> "VK_TIMEOUT";
            case 3 -> "VK_EVENT_SET";
            case 4 -> "VK_EVENT_RESET";
            case 5 -> "VK_INCOMPLETE";
            case -1 -> "VK_ERROR_OUT_OF_HOST_MEMORY";
            case -2 -> "VK_ERROR_OUT_OF_DEVICE_MEMORY";
            case -3 -> "VK_ERROR_INITIALIZATION_FAILED";
            case -4 -> "VK_ERROR_DEVICE_LOST";
            case -5 -> "VK_ERROR_MEMORY_MAP_FAILED";
            case -6 -> "VK_ERROR_LAYER_NOT_PRESENT";
            case -7 -> "VK_ERROR_EXTENSION_NOT_PRESENT";
            case -8 -> "VK_ERROR_FEATURE_NOT_PRESENT";
            case -9 -> "VK_ERROR_INCOMPATIBLE_DRIVER";
            case -10 -> "VK_ERROR_TOO_MANY_OBJECTS";
            case -11 -> "VK_ERROR_FORMAT_NOT_SUPPORTED";
            case -12 -> "VK_ERROR_FRAGMENTED_POOL";
            case -13 -> "VK_ERROR_UNKNOWN";
            case -1000069000 -> "VK_ERROR_OUT_OF_POOL_MEMORY";
            case -1000072003 -> "VK_ERROR_INVALID_EXTERNAL_HANDLE";
            case -1000257000 -> "VK_ERROR_INVALID_OPAQUE_CAPTURE_ADDRESS";
            case -1000161000 -> "VK_ERROR_FRAGMENTATION";
            case -1000000000 -> "VK_ERROR_SURFACE_LOST_KHR";
            case -1000000001 -> "VK_ERROR_NATIVE_WINDOW_IN_USE_KHR";
            case 1000001003 -> "VK_SUBOPTIMAL_KHR";
            case -1000001004 -> "VK_ERROR_OUT_OF_DATE_KHR";
            default -> "0x" + Integer.toHexString(error);
        };
    }

    public static boolean hasAllBits(int bitfield, int bitmask) {
        return (bitfield & bitmask) == bitmask;
    }

    public static boolean hasAllBits(long bitfield, long bitmask) {
        return (bitfield & bitmask) == bitmask;
    }

    public static boolean hasAnyBit(int bitfield, int bitmask) {
        return (bitfield & bitmask) != 0;
    }

    public static boolean hasAnyBit(long bitfield, long bitmask) {
        return (bitfield & bitmask) != 0L;
    }

    public static boolean hasNoBit(int bitfield, int bitmask) {
        return (bitfield & bitmask) == 0;
    }

    public static boolean hasNoBit(long bitfield, long bitmask) {
        return (bitfield & bitmask) == 0L;
    }

    public static VkClearColorValue putArgb(VkClearColorValue vkClearColor, int argb) {
        vkClearColor.float32(0, ARGB.redFloat(argb));
        vkClearColor.float32(1, ARGB.greenFloat(argb));
        vkClearColor.float32(2, ARGB.blueFloat(argb));
        vkClearColor.float32(3, ARGB.alphaFloat(argb));
        return vkClearColor;
    }
}

