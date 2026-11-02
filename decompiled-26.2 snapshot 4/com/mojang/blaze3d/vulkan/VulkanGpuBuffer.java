/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.util.vma.Vma
 *  org.lwjgl.util.vma.VmaAllocationCreateInfo
 *  org.lwjgl.vulkan.VkBufferCreateInfo
 */
package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.vulkan.Destroyable;
import com.mojang.blaze3d.vulkan.VulkanConst;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.VulkanUtils;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.Objects;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VkBufferCreateInfo;

public class VulkanGpuBuffer
extends GpuBuffer
implements Destroyable {
    private final VulkanDevice device;
    private boolean closed = false;
    private final long vkBuffer;
    private final long vmaAllocation;

    public VulkanGpuBuffer(VulkanDevice device, @Nullable Supplier<String> label, @GpuBuffer.Usage int usage, long size, boolean forceHostVisibleAllocation) {
        super(usage, size);
        this.device = device;
        try (MemoryStack stack = MemoryStack.stackPush();){
            VkBufferCreateInfo bufferCreateInfo = VkBufferCreateInfo.calloc((MemoryStack)stack).sType$Default();
            bufferCreateInfo.size(size);
            bufferCreateInfo.usage(VulkanConst.bufferUsageToVk(usage));
            bufferCreateInfo.sharingMode(0);
            bufferCreateInfo.pQueueFamilyIndices(null);
            VmaAllocationCreateInfo allocCreateInfo = VmaAllocationCreateInfo.calloc((MemoryStack)stack);
            allocCreateInfo.usage(8);
            if (forceHostVisibleAllocation) {
                allocCreateInfo.requiredFlags(allocCreateInfo.requiredFlags() | 2);
            }
            if (VulkanUtils.hasAnyBit(usage, 3)) {
                allocCreateInfo.requiredFlags(allocCreateInfo.requiredFlags() | 2 | 4);
                if (VulkanUtils.hasAnyBit(usage, 1)) {
                    allocCreateInfo.preferredFlags(allocCreateInfo.preferredFlags() | 8);
                    allocCreateInfo.flags(allocCreateInfo.flags() | 0x800);
                } else {
                    allocCreateInfo.flags(allocCreateInfo.flags() | 0x400);
                }
            }
            if (VulkanUtils.hasAnyBit(usage, 5)) {
                allocCreateInfo.usage(9);
            }
            LongBuffer bufferPtr = stack.callocLong(1);
            PointerBuffer allocPtr = stack.callocPointer(1);
            int result = Vma.vmaCreateBuffer((long)device.vma(), (VkBufferCreateInfo)bufferCreateInfo, (VmaAllocationCreateInfo)allocCreateInfo, (LongBuffer)bufferPtr, (PointerBuffer)allocPtr, null);
            VulkanUtils.crashIfFailure(result, "Failed to allocate VkBuffer");
            this.vkBuffer = bufferPtr.get(0);
            this.vmaAllocation = allocPtr.get(0);
            if (label != null) {
                device.instance().debug().setObjectName(device.vkDevice(), 9, this.vkBuffer, label);
            }
        }
    }

    @Override
    public void destroy() {
        Vma.vmaDestroyBuffer((long)this.device.vma(), (long)this.vkBuffer, (long)this.vmaAllocation);
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.device.createCommandEncoder().queueForDestroy(this);
    }

    public long vkBuffer() {
        return this.vkBuffer;
    }

    public GpuBuffer.MappedView map(long offset, long length) {
        try (MemoryStack stack = MemoryStack.stackPush();){
            PointerBuffer pointer = stack.callocPointer(1);
            VulkanUtils.crashIfFailure(Vma.vmaMapMemory((long)this.device.vma(), (long)this.vmaAllocation, (PointerBuffer)pointer), "Failed to map buffer");
            ByteBuffer byteBuffer = MemoryUtil.memByteBuffer((long)(pointer.get(0) + offset), (int)((int)length));
            VkMappedView vkMappedView = new VkMappedView(this, byteBuffer);
            return vkMappedView;
        }
    }

    private class VkMappedView
    implements GpuBuffer.MappedView {
        private final ByteBuffer data;
        private boolean closed;
        final /* synthetic */ VulkanGpuBuffer this$0;

        private VkMappedView(VulkanGpuBuffer vulkanGpuBuffer, ByteBuffer data) {
            VulkanGpuBuffer vulkanGpuBuffer2 = vulkanGpuBuffer;
            Objects.requireNonNull(vulkanGpuBuffer2);
            this.this$0 = vulkanGpuBuffer2;
            this.closed = false;
            this.data = data;
        }

        @Override
        public ByteBuffer data() {
            return this.data;
        }

        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            Vma.vmaUnmapMemory((long)this.this$0.device.vma(), (long)this.this$0.vmaAllocation);
        }
    }
}

