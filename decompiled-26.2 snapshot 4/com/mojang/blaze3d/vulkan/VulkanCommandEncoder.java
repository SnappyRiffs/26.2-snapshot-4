/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.MathUtil
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.system.MemoryUtil
 *  org.lwjgl.system.Pointer
 *  org.lwjgl.vulkan.KHRDynamicRendering
 *  org.lwjgl.vulkan.KHRSynchronization2
 *  org.lwjgl.vulkan.VK12
 *  org.lwjgl.vulkan.VkBufferCopy
 *  org.lwjgl.vulkan.VkBufferCopy$Buffer
 *  org.lwjgl.vulkan.VkBufferImageCopy
 *  org.lwjgl.vulkan.VkBufferImageCopy$Buffer
 *  org.lwjgl.vulkan.VkClearAttachment
 *  org.lwjgl.vulkan.VkClearAttachment$Buffer
 *  org.lwjgl.vulkan.VkClearColorValue
 *  org.lwjgl.vulkan.VkClearDepthStencilValue
 *  org.lwjgl.vulkan.VkClearRect
 *  org.lwjgl.vulkan.VkClearRect$Buffer
 *  org.lwjgl.vulkan.VkClearValue
 *  org.lwjgl.vulkan.VkCommandBuffer
 *  org.lwjgl.vulkan.VkCommandBufferAllocateInfo
 *  org.lwjgl.vulkan.VkCommandBufferBeginInfo
 *  org.lwjgl.vulkan.VkCommandBufferInheritanceInfo
 *  org.lwjgl.vulkan.VkCommandBufferInheritanceRenderingInfo
 *  org.lwjgl.vulkan.VkCommandPoolCreateInfo
 *  org.lwjgl.vulkan.VkDependencyInfo
 *  org.lwjgl.vulkan.VkDevice
 *  org.lwjgl.vulkan.VkImageCopy
 *  org.lwjgl.vulkan.VkImageCopy$Buffer
 *  org.lwjgl.vulkan.VkImageSubresourceLayers
 *  org.lwjgl.vulkan.VkImageSubresourceRange
 *  org.lwjgl.vulkan.VkMemoryBarrier2
 *  org.lwjgl.vulkan.VkMemoryBarrier2$Buffer
 *  org.lwjgl.vulkan.VkRect2D
 *  org.lwjgl.vulkan.VkRenderingAttachmentInfo
 *  org.lwjgl.vulkan.VkRenderingAttachmentInfo$Buffer
 *  org.lwjgl.vulkan.VkRenderingInfo
 *  org.lwjgl.vulkan.VkSemaphoreCreateInfo
 *  org.lwjgl.vulkan.VkSemaphoreTypeCreateInfo
 *  org.lwjgl.vulkan.VkSemaphoreWaitInfo
 */
package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoderBackend;
import com.mojang.blaze3d.systems.GpuQueryPool;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderPassBackend;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MathUtil;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Pointer;
import org.lwjgl.vulkan.KHRDynamicRendering;
import org.lwjgl.vulkan.KHRSynchronization2;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkBufferCopy;
import org.lwjgl.vulkan.VkBufferImageCopy;
import org.lwjgl.vulkan.VkClearAttachment;
import org.lwjgl.vulkan.VkClearColorValue;
import org.lwjgl.vulkan.VkClearDepthStencilValue;
import org.lwjgl.vulkan.VkClearRect;
import org.lwjgl.vulkan.VkClearValue;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkCommandBufferInheritanceInfo;
import org.lwjgl.vulkan.VkCommandBufferInheritanceRenderingInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDependencyInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkImageCopy;
import org.lwjgl.vulkan.VkImageSubresourceLayers;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkMemoryBarrier2;
import org.lwjgl.vulkan.VkRect2D;
import org.lwjgl.vulkan.VkRenderingAttachmentInfo;
import org.lwjgl.vulkan.VkRenderingInfo;
import org.lwjgl.vulkan.VkSemaphoreCreateInfo;
import org.lwjgl.vulkan.VkSemaphoreTypeCreateInfo;
import org.lwjgl.vulkan.VkSemaphoreWaitInfo;

public class VulkanCommandEncoder implements CommandEncoderBackend, Destroyable {
  public static final int MAX_SUBMITS_IN_FLIGHT = 2;
  private final VulkanDevice device;
  private final long submitSemaphore;
  private long currentSubmitIndex = 2L;
  private long completedSubmitIndex = 0L;
  private VulkanQueue.Submission submissionBuilder;
  private final DestructionQueue<Destroyable> destroyQueue =
      new DestructionQueue<Destroyable>(2, Destroyable::destroy);
  private final DestructionQueue<VkCommandBuffer> commandBufferDestroyQueue;
  private final long commandPool;
  private @Nullable VkCommandBuffer currentCommandBuffer;
  private @Nullable VulkanRenderPass currentRenderPass;

  public VulkanCommandEncoder(final VulkanDevice device) {
    this.device = device;
    MemoryStack baseStack = MemoryStack.stackGet();
    try (MemoryStack stack = baseStack.push(); ) {
      VkSemaphoreTypeCreateInfo semaphoreTypeCreateInfo =
          VkSemaphoreTypeCreateInfo.calloc((MemoryStack) stack).sType$Default();
      semaphoreTypeCreateInfo.semaphoreType(1);
      semaphoreTypeCreateInfo.initialValue(this.currentSubmitIndex - 1L);
      VkSemaphoreCreateInfo semaphoreCreateInfo =
          VkSemaphoreCreateInfo.calloc((MemoryStack) stack).sType$Default();
      semaphoreCreateInfo.pNext(semaphoreTypeCreateInfo);
      LongBuffer semaphoreHandlePtr = stack.callocLong(1);
      VulkanUtils.crashIfFailure(
          VK12.vkCreateSemaphore(
              (VkDevice) device.vkDevice(),
              (VkSemaphoreCreateInfo) semaphoreCreateInfo,
              null,
              (LongBuffer) semaphoreHandlePtr),
          "Failed to create submit VkSemaphore");
      this.submitSemaphore = semaphoreHandlePtr.get(0);
    }
    stack = baseStack.push();
    try {
      VkCommandPoolCreateInfo commandPoolCreateInfo =
          VkCommandPoolCreateInfo.calloc((MemoryStack) stack).sType$Default();
      commandPoolCreateInfo.flags(1);
      commandPoolCreateInfo.queueFamilyIndex(device.graphicsQueue().queueFamilyIndex());
      LongBuffer commandPoolHandlePtr = stack.callocLong(1);
      VulkanUtils.crashIfFailure(
          VK12.vkCreateCommandPool(
              (VkDevice) device.vkDevice(),
              (VkCommandPoolCreateInfo) commandPoolCreateInfo,
              null,
              (LongBuffer) commandPoolHandlePtr),
          "Failed to create VkCommandPool");
      this.commandPool = commandPoolHandlePtr.get(0);
    } finally {
      if (stack != null) {
        stack.close();
      }
    }
    this.submissionBuilder = device.graphicsQueue().beginSubmit();
    this.commandBufferDestroyQueue =
        new DestructionQueue<VkCommandBuffer>(
            2,
            new DestructionQueue.Destroyer<VkCommandBuffer>(this) {
              PointerBuffer commandBuffers;
              final /* synthetic */ VulkanCommandEncoder this$0;

              {
                VulkanCommandEncoder vulkanCommandEncoder = this$0;
                Objects.requireNonNull(vulkanCommandEncoder);
                this.this$0 = vulkanCommandEncoder;
                this.commandBuffers = PointerBuffer.allocateDirect((int) 64);
              }

              @Override
              public void begin(int count) {
                if (this.commandBuffers.capacity() < count) {
                  this.commandBuffers =
                      PointerBuffer.allocateDirect((int) MathUtil.mathRoundPoT((int) count));
                }
                this.commandBuffers.clear();
              }

              @Override
              public void destroy(VkCommandBuffer commandBuffer) {
                this.commandBuffers.put((Pointer) commandBuffer);
              }

              @Override
              public void end() {
                this.commandBuffers.flip();
                if (this.commandBuffers.limit() != 0) {
                  VK12.vkFreeCommandBuffers(
                      (VkDevice) device.vkDevice(),
                      (long) this.this$0.commandPool,
                      (PointerBuffer) this.commandBuffers);
                }
              }
            });
  }

  @Override
  public void destroy() {
    this.submissionBuilder.close();
    this.device.graphicsQueue().waitIdle();
    this.commandBufferDestroyQueue.close();
    this.destroyQueue.close();
    VK12.vkDestroyCommandPool((VkDevice) this.device.vkDevice(), (long) this.commandPool, null);
    VK12.vkDestroySemaphore((VkDevice) this.device.vkDevice(), (long) this.submitSemaphore, null);
  }

  public void queueForDestroy(Destroyable destroyable) {
    this.destroyQueue.add(destroyable);
  }

  public void queueForDestroy(VkCommandBuffer commandBuffer) {
    this.commandBufferDestroyQueue.add(commandBuffer);
  }

  private VulkanGpuBuffer createStagingBuffer(ByteBuffer data) {
    int size = data.remaining();
    VulkanGpuBuffer stagingBuffer =
        new VulkanGpuBuffer(this.device, () -> "Staging buffer", 22, size, false);
    try (GpuBuffer.MappedView mappedMemory = stagingBuffer.map(0L, data.remaining()); ) {
      MemoryUtil.memCopy((ByteBuffer) data, (ByteBuffer) mappedMemory.data());
    }
    return stagingBuffer;
  }

  public VkCommandBuffer allocateTransientCommandBuffer(boolean primary) {
    try (MemoryStack stack = MemoryStack.stackPush(); ) {
      VkCommandBufferAllocateInfo allocateInfo =
          VkCommandBufferAllocateInfo.calloc((MemoryStack) stack).sType$Default();
      allocateInfo.commandPool(this.commandPool);
      allocateInfo.level(primary ? 0 : 1);
      allocateInfo.commandBufferCount(1);
      PointerBuffer bufferPtr = stack.callocPointer(1);
      VulkanUtils.crashIfFailure(
          VK12.vkAllocateCommandBuffers(
              (VkDevice) this.device.vkDevice(),
              (VkCommandBufferAllocateInfo) allocateInfo,
              (PointerBuffer) bufferPtr),
          "Failed to allocated VkCommandBuffer");
      VkCommandBuffer commandBuffer = new VkCommandBuffer(bufferPtr.get(0), this.device.vkDevice());
      this.queueForDestroy(commandBuffer);
      VkCommandBuffer vkCommandBuffer = commandBuffer;
      return vkCommandBuffer;
    }
  }

  private VkCommandBuffer commandBuffer() {
    if (this.currentCommandBuffer != null) {
      return this.currentCommandBuffer;
    }
    try (MemoryStack stack = MemoryStack.stackPush(); ) {
      this.currentCommandBuffer = this.allocateTransientCommandBuffer(true);
      VkCommandBufferBeginInfo beginInfo =
          VkCommandBufferBeginInfo.calloc((MemoryStack) stack).sType$Default();
      beginInfo.flags(1);
      VulkanUtils.crashIfFailure(
          VK12.vkBeginCommandBuffer(
              (VkCommandBuffer) this.currentCommandBuffer, (VkCommandBufferBeginInfo) beginInfo),
          "Failed to begin VkCommandBuffer");
      this.submissionBuilder.executeCommands(this.currentCommandBuffer);
      VkCommandBuffer vkCommandBuffer = this.currentCommandBuffer;
      return vkCommandBuffer;
    }
  }

  private VkCommandBuffer renderpassCommandBuffer(
      IntBuffer colorAttachmentFormats, int depthFormat) {
    try (MemoryStack stack = MemoryStack.stackPush(); ) {
      VkCommandBuffer renderpassCommandBuffer = this.allocateTransientCommandBuffer(false);
      VkCommandBufferInheritanceRenderingInfo dynamicRenderinginheritanceInfo =
          VkCommandBufferInheritanceRenderingInfo.calloc((MemoryStack) stack).sType$Default();
      dynamicRenderinginheritanceInfo.pColorAttachmentFormats(colorAttachmentFormats);
      dynamicRenderinginheritanceInfo.depthAttachmentFormat(depthFormat);
      dynamicRenderinginheritanceInfo.rasterizationSamples(1);
      VkCommandBufferInheritanceInfo inheritanceInfo =
          VkCommandBufferInheritanceInfo.calloc((MemoryStack) stack).sType$Default();
      inheritanceInfo.pNext(dynamicRenderinginheritanceInfo);
      VkCommandBufferBeginInfo beginInfo =
          VkCommandBufferBeginInfo.calloc((MemoryStack) stack).sType$Default();
      beginInfo.flags(3);
      beginInfo.pInheritanceInfo(inheritanceInfo);
      VulkanUtils.crashIfFailure(
          VK12.vkBeginCommandBuffer(
              (VkCommandBuffer) renderpassCommandBuffer, (VkCommandBufferBeginInfo) beginInfo),
          "Failed to begin VkCommandBuffer");
      VkCommandBuffer vkCommandBuffer = renderpassCommandBuffer;
      return vkCommandBuffer;
    }
  }

  VkCommandBuffer textureInitCommandBuffer() {
    return this.commandBuffer();
  }

  private void endCommandBuffer() {
    if (this.currentCommandBuffer == null) {
      return;
    }
    VulkanUtils.crashIfFailure(
        VK12.vkEndCommandBuffer((VkCommandBuffer) this.currentCommandBuffer),
        "Failed to end VkCommandBuffer");
    this.currentCommandBuffer = null;
  }

  public void waitSemaphore(long vkSemaphore, long value, long stageMask) {
    this.endCommandBuffer();
    this.submissionBuilder.waitSemaphore(vkSemaphore, value, stageMask);
  }

  public void execute(VkCommandBuffer commandBuffer) {
    this.endCommandBuffer();
    this.submissionBuilder.executeCommands(commandBuffer);
  }

  public void signalSemaphore(long vkSemaphore, long value, long stageMask) {
    this.endCommandBuffer();
    this.submissionBuilder.signalSemaphore(vkSemaphore, value, stageMask);
  }

  private void memoryBarrier(MemoryStack stack) {
    VkMemoryBarrier2.Buffer memoryBarrier =
        VkMemoryBarrier2.calloc((int) 1, (MemoryStack) stack).sType$Default();
    memoryBarrier.srcStageMask(65536L);
    memoryBarrier.srcAccessMask(98304L);
    memoryBarrier.dstStageMask(65536L);
    memoryBarrier.dstAccessMask(98304L);
    VkDependencyInfo depInfo = VkDependencyInfo.calloc((MemoryStack) stack).sType$Default();
    depInfo.pMemoryBarriers(memoryBarrier);
    KHRSynchronization2.vkCmdPipelineBarrier2KHR(
        (VkCommandBuffer) this.commandBuffer(), (VkDependencyInfo) depInfo);
  }

  @Override
  public void submit() {
    this.endCommandBuffer();
    this.signalSemaphore(this.submitSemaphore, this.currentSubmitIndex, 65536L);
    this.submissionBuilder.close();
    this.submissionBuilder = this.device.graphicsQueue().beginSubmit();
    ++this.currentSubmitIndex;
    if (!this.awaitSubmitCompletion(this.currentSubmitIndex - 2L, 5000L)) {
      throw new IllegalStateException("5s timeout reached when waiting for VK semaphore");
    }
    this.commandBufferDestroyQueue.rotate();
    this.destroyQueue.rotate();
  }

  @Override
  public RenderPassBackend createRenderPass(
      Supplier<String> label,
      GpuTextureView colorTexture,
      OptionalInt clearColor,
      @Nullable GpuTextureView depthTexture,
      OptionalDouble clearDepth,
      RenderPass.RenderArea renderArea) {
    VulkanGpuTextureView vkColor = (VulkanGpuTextureView) colorTexture;
    VulkanGpuTextureView vulkanDepthAttachment = (VulkanGpuTextureView) depthTexture;
    this.device.instance().debug().beginDebugGroup(this.commandBuffer(), label);
    try (MemoryStack stack = MemoryStack.stackPush(); ) {
      VkRect2D vkRenderArea = VkRect2D.calloc((MemoryStack) stack);
      vkRenderArea.extent().set(renderArea.width(), renderArea.height());
      vkRenderArea.offset().set(renderArea.x(), renderArea.y());
      VkRenderingAttachmentInfo.Buffer colorAttachmentInfo =
          VkRenderingAttachmentInfo.calloc((int) 1, (MemoryStack) stack);
      colorAttachmentInfo.sType$Default();
      colorAttachmentInfo.imageView(vkColor.vkImageView());
      colorAttachmentInfo.imageLayout(1);
      colorAttachmentInfo.storeOp(0);
      if (clearColor.isPresent()) {
        int color = clearColor.getAsInt();
        VkClearColorValue vkClearColor =
            VulkanUtils.putArgb(VkClearColorValue.calloc((MemoryStack) stack), color);
        colorAttachmentInfo.loadOp(1);
        colorAttachmentInfo.clearValue(
            VkClearValue.calloc((MemoryStack) stack).color(vkClearColor));
      } else {
        colorAttachmentInfo.loadOp(0);
      }
      VkRenderingInfo renderingInfo = VkRenderingInfo.calloc((MemoryStack) stack).sType$Default();
      renderingInfo.renderArea(vkRenderArea);
      renderingInfo.flags(1);
      renderingInfo.layerCount(1);
      renderingInfo.viewMask(0);
      renderingInfo.pColorAttachments(colorAttachmentInfo);
      if (vulkanDepthAttachment != null) {
        VkRenderingAttachmentInfo depthAttachmentInfo =
            VkRenderingAttachmentInfo.calloc((MemoryStack) stack).sType$Default();
        depthAttachmentInfo.imageView(vulkanDepthAttachment.vkImageView());
        depthAttachmentInfo.imageLayout(1);
        depthAttachmentInfo.storeOp(0);
        if (clearDepth.isPresent()) {
          double color = clearDepth.getAsDouble();
          VkClearDepthStencilValue vkClearColor =
              VkClearDepthStencilValue.calloc((MemoryStack) stack).depth((float) color);
          depthAttachmentInfo.loadOp(1);
          depthAttachmentInfo.clearValue(
              VkClearValue.calloc((MemoryStack) stack).depthStencil(vkClearColor));
        } else {
          depthAttachmentInfo.loadOp(0);
        }
        renderingInfo.pDepthAttachment(depthAttachmentInfo);
      }
      KHRDynamicRendering.vkCmdBeginRenderingKHR(
          (VkCommandBuffer) this.commandBuffer(), (VkRenderingInfo) renderingInfo);
      Supplier<VkCommandBuffer> secondaryCommandBufferSupplier =
          () -> {
            try (MemoryStack memStack = MemoryStack.stackPush(); ) {
              VkCommandBuffer vkCommandBuffer =
                  this.renderpassCommandBuffer(
                      memStack.ints(VulkanConst.toVk(colorTexture.texture().getFormat())),
                      depthTexture == null
                          ? 0
                          : VulkanConst.toVk(depthTexture.texture().getFormat()));
              return vkCommandBuffer;
            }
          };
      this.currentRenderPass =
          new VulkanRenderPass(
              this.device,
              this::queueForDestroy,
              this.commandBuffer(),
              secondaryCommandBufferSupplier,
              renderArea,
              colorTexture.getWidth(0),
              colorTexture.getHeight(0),
              depthTexture != null);
    }
    return this.currentRenderPass;
  }

  @Override
  public void submitRenderPass() {
    if (this.currentRenderPass == null) {
      throw new IllegalStateException("Cannot submit a renderpass if one hasn't been started!");
    }
    this.currentRenderPass.end();
    KHRDynamicRendering.vkCmdEndRenderingKHR((VkCommandBuffer) this.commandBuffer());
    this.device.instance().debug().endDebugGroup(this.commandBuffer());
    try (MemoryStack stack = MemoryStack.stackPush(); ) {
      this.memoryBarrier(stack);
    }
  }

  private void clearColorTextureUnsynced(
      MemoryStack stack, GpuTexture colorTexture, int clearColor) {
    VkClearColorValue vkClearColor =
        VulkanUtils.putArgb(VkClearColorValue.calloc((MemoryStack) stack), clearColor);
    VkImageSubresourceRange subresourceRange = VkImageSubresourceRange.calloc((MemoryStack) stack);
    subresourceRange.baseMipLevel(0);
    subresourceRange.levelCount(colorTexture.getMipLevels());
    subresourceRange.baseArrayLayer(0);
    subresourceRange.layerCount(1);
    subresourceRange.aspectMask(1);
    VK12.vkCmdClearColorImage(
        (VkCommandBuffer) this.commandBuffer(),
        (long) ((VulkanGpuTexture) colorTexture).vkImage(),
        (int) 1,
        (VkClearColorValue) vkClearColor,
        (VkImageSubresourceRange) subresourceRange);
  }

  public void clearDepthTextureUnsynced(
      MemoryStack stack, GpuTexture depthTexture, double clearDepth) {
    VkClearDepthStencilValue vkClearDepth =
        VkClearDepthStencilValue.calloc((MemoryStack) stack).depth((float) clearDepth);
    VkImageSubresourceRange subresourceRange = VkImageSubresourceRange.calloc((MemoryStack) stack);
    subresourceRange.baseMipLevel(0);
    subresourceRange.levelCount(depthTexture.getMipLevels());
    subresourceRange.baseArrayLayer(0);
    subresourceRange.layerCount(1);
    subresourceRange.aspectMask(2);
    VK12.vkCmdClearDepthStencilImage(
        (VkCommandBuffer) this.commandBuffer(),
        (long) ((VulkanGpuTexture) depthTexture).vkImage(),
        (int) 1,
        (VkClearDepthStencilValue) vkClearDepth,
        (VkImageSubresourceRange) subresourceRange);
  }

  @Override
  public void clearColorTexture(GpuTexture colorTexture, int clearColor) {
    try (MemoryStack stack = MemoryStack.stackPush(); ) {
      this.clearColorTextureUnsynced(stack, colorTexture, clearColor);
      this.memoryBarrier(stack);
    }
  }

  @Override
  public void clearColorAndDepthTextures(
      GpuTexture colorTexture, int clearColor, GpuTexture depthTexture, double clearDepth) {
    try (MemoryStack stack = MemoryStack.stackPush(); ) {
      this.clearColorTextureUnsynced(stack, colorTexture, clearColor);
      this.clearDepthTextureUnsynced(stack, depthTexture, clearDepth);
      this.memoryBarrier(stack);
    }
  }

  @Override
  public void clearColorAndDepthTextures(
      GpuTexture colorTexture,
      int clearColor,
      GpuTexture depthTexture,
      double clearDepth,
      int regionX,
      int regionY,
      int regionWidth,
      int regionHeight) {
    try (GpuTextureView colorTextureView = this.device.createTextureView(colorTexture);
        GpuTextureView depthTextureView = this.device.createTextureView(depthTexture);
        MemoryStack stack = MemoryStack.stackPush(); ) {
      this.createRenderPass(
          () -> "ClearColorDepthTextures",
          colorTextureView,
          OptionalInt.empty(),
          depthTextureView,
          OptionalDouble.empty(),
          new RenderPass.RenderArea(0, 0, colorTexture.getWidth(0), colorTexture.getHeight(0)));
      assert (this.currentRenderPass != null);
      VkClearRect.Buffer rects = VkClearRect.calloc((int) 1, (MemoryStack) stack);
      rects.baseArrayLayer(0);
      rects.layerCount(1);
      rects.rect().offset().set(regionX, regionY);
      rects.rect().extent().set(regionWidth, regionHeight);
      VkClearAttachment.Buffer attachments = VkClearAttachment.calloc((int) 2, (MemoryStack) stack);
      VkClearValue colorClearValue = VkClearValue.calloc((MemoryStack) stack);
      VulkanUtils.putArgb(colorClearValue.color(), clearColor);
      attachments.aspectMask(1);
      attachments.clearValue(colorClearValue);
      VkClearValue depthClearValue = VkClearValue.calloc((MemoryStack) stack);
      VkClearDepthStencilValue clearValue = depthClearValue.depthStencil();
      clearValue.depth((float) clearDepth);
      attachments.position(1);
      attachments.aspectMask(2);
      attachments.clearValue(depthClearValue);
      attachments.position(0);
      VkCommandBuffer secondaryCommandBuffer =
          this.currentRenderPass.allocateTransientRenderpassCommandBuffer();
      VK12.vkCmdClearAttachments(
          (VkCommandBuffer) secondaryCommandBuffer,
          (VkClearAttachment.Buffer) attachments,
          (VkClearRect.Buffer) rects);
      VulkanUtils.crashIfFailure(
          VK12.vkEndCommandBuffer((VkCommandBuffer) secondaryCommandBuffer),
          "Failed to end VkCommandBuffer");
      this.currentRenderPass.executeCommandBuffer(secondaryCommandBuffer);
      this.submitRenderPass();
    }
  }

  @Override
  public void clearDepthTexture(GpuTexture depthTexture, double clearDepth) {
    try (MemoryStack stack = MemoryStack.stackPush(); ) {
      this.clearDepthTextureUnsynced(stack, depthTexture, clearDepth);
      this.memoryBarrier(stack);
    }
  }

  @Override
  public void writeToBuffer(GpuBufferSlice destination, ByteBuffer data) {
    VulkanGpuBuffer destBuffer = (VulkanGpuBuffer) destination.buffer();
    try (VulkanGpuBuffer stagingBuffer = this.createStagingBuffer(data);
        MemoryStack stack = MemoryStack.stackPush(); ) {
      VkBufferCopy.Buffer regions =
          VkBufferCopy.calloc((int) 1, (MemoryStack) stack)
              .srcOffset(0L)
              .dstOffset(destination.offset())
              .size((long) data.remaining());
      VK12.vkCmdCopyBuffer(
          (VkCommandBuffer) this.commandBuffer(),
          (long) stagingBuffer.vkBuffer(),
          (long) destBuffer.vkBuffer(),
          (VkBufferCopy.Buffer) regions);
      this.memoryBarrier(stack);
    }
  }

  @Override
  public GpuBuffer.MappedView mapBuffer(GpuBufferSlice buffer, boolean read, boolean write) {
    VulkanGpuBuffer vulkanBuffer = (VulkanGpuBuffer) buffer.buffer();
    return vulkanBuffer.map(buffer.offset(), buffer.length());
  }

  @Override
  public void copyToBuffer(GpuBufferSlice source, GpuBufferSlice target) {
    try (MemoryStack stack = MemoryStack.stackPush(); ) {
      VkBufferCopy.Buffer copyInfo = VkBufferCopy.calloc((int) 1, (MemoryStack) stack);
      copyInfo.srcOffset(source.offset());
      copyInfo.dstOffset(target.offset());
      copyInfo.size(source.length());
      VK12.vkCmdCopyBuffer(
          (VkCommandBuffer) this.commandBuffer(),
          (long) ((VulkanGpuBuffer) source.buffer()).vkBuffer(),
          (long) ((VulkanGpuBuffer) target.buffer()).vkBuffer(),
          (VkBufferCopy.Buffer) copyInfo);
      this.memoryBarrier(stack);
    }
  }

  @Override
  public void writeToTexture(
      GpuTexture destination,
      NativeImage source,
      int mipLevel,
      int depthOrLayer,
      int destX,
      int destY,
      int width,
      int height,
      int sourceX,
      int sourceY) {
    int stagingBufferSize =
        source.getWidth() * source.getHeight() * destination.getFormat().pixelSize();
    int texelSize = destination.getFormat().pixelSize();
    int skipTexels = sourceX + sourceY * source.getWidth();
    long skipBytes = (long) skipTexels * (long) texelSize;
    try (VulkanGpuBuffer stagingBuffer =
        this.createStagingBuffer(
            MemoryUtil.memByteBuffer((long) source.getPointer(), (int) stagingBufferSize)); ) {
      this.writeToTexture(
          (VulkanGpuTexture) destination,
          stagingBuffer,
          skipBytes,
          mipLevel,
          depthOrLayer,
          destX,
          destY,
          width,
          height,
          source.getWidth(),
          source.getHeight());
    }
  }

  @Override
  public void writeToTexture(
      GpuTexture destination,
      ByteBuffer source,
      NativeImage.Format format,
      int mipLevel,
      int depthOrLayer,
      int destX,
      int destY,
      int width,
      int height) {
    try (VulkanGpuBuffer stagingBuffer = this.createStagingBuffer(source); ) {
      this.writeToTexture(
          (VulkanGpuTexture) destination,
          stagingBuffer,
          0L,
          mipLevel,
          depthOrLayer,
          destX,
          destY,
          width,
          height,
          width,
          height);
    }
  }

  private void writeToTexture(
      VulkanGpuTexture destination,
      VulkanGpuBuffer stagingBuffer,
      long bufferOffset,
      int mipLevel,
      int depthOrLayer,
      int destX,
      int destY,
      int width,
      int height,
      int srcWidth,
      int srcHeight) {
    try (MemoryStack stack = MemoryStack.stackPush(); ) {
      VkBufferImageCopy.Buffer region = VkBufferImageCopy.calloc((int) 1, (MemoryStack) stack);
      region.bufferOffset(bufferOffset);
      region.bufferRowLength(srcWidth);
      region.bufferImageHeight(srcHeight);
      VkImageSubresourceLayers imageSubresource = region.imageSubresource();
      imageSubresource.aspectMask(1);
      imageSubresource.mipLevel(mipLevel);
      imageSubresource.baseArrayLayer(depthOrLayer);
      imageSubresource.layerCount(1);
      region.imageOffset().set(destX, destY, 0);
      region.imageExtent().set(width, height, 1);
      VK12.vkCmdCopyBufferToImage(
          (VkCommandBuffer) this.commandBuffer(),
          (long) stagingBuffer.vkBuffer(),
          (long) destination.vkImage(),
          (int) 1,
          (VkBufferImageCopy.Buffer) region);
      this.memoryBarrier(stack);
    }
  }

  @Override
  public void copyTextureToBuffer(
      GpuTexture source, GpuBuffer destination, long offset, Runnable callback, int mipLevel) {
    this.copyTextureToBuffer(
        source,
        destination,
        offset,
        callback,
        mipLevel,
        0,
        0,
        source.getWidth(mipLevel),
        source.getHeight(mipLevel));
  }

  @Override
  public void copyTextureToBuffer(
      GpuTexture source,
      GpuBuffer destination,
      long offset,
      Runnable callback,
      int mipLevel,
      int x,
      int y,
      int width,
      int height) {
    try (MemoryStack stack = MemoryStack.stackPush(); ) {
      VkBufferImageCopy.Buffer copy = VkBufferImageCopy.calloc((int) 1, (MemoryStack) stack);
      copy.bufferOffset(offset);
      VkImageSubresourceLayers subresource = copy.imageSubresource();
      subresource.aspectMask(VulkanConst.formatAspectMask(source.getFormat()));
      subresource.mipLevel(mipLevel);
      subresource.baseArrayLayer(0);
      subresource.layerCount(1);
      copy.imageOffset().set(x, y, 0);
      copy.imageExtent().set(width, height, 1);
      copy.bufferRowLength(width);
      copy.bufferImageHeight(height);
      VK12.vkCmdCopyImageToBuffer(
          (VkCommandBuffer) this.commandBuffer(),
          (long) ((VulkanGpuTexture) source).vkImage(),
          (int) 1,
          (long) ((VulkanGpuBuffer) destination).vkBuffer(),
          (VkBufferImageCopy.Buffer) copy);
      this.memoryBarrier(stack);
    }
    this.queueForDestroy(callback::run);
  }

  @Override
  public void copyTextureToTexture(
      GpuTexture source,
      GpuTexture destination,
      int mipLevel,
      int destX,
      int destY,
      int sourceX,
      int sourceY,
      int width,
      int height) {
    VulkanGpuTexture vulkanSrc = (VulkanGpuTexture) source;
    VulkanGpuTexture vulkanDst = (VulkanGpuTexture) destination;
    try (MemoryStack stack = MemoryStack.stackPush(); ) {
      VkImageSubresourceLayers subresourceLayers =
          VkImageSubresourceLayers.calloc((MemoryStack) stack);
      subresourceLayers.mipLevel(mipLevel);
      subresourceLayers.baseArrayLayer(0);
      subresourceLayers.layerCount(1);
      subresourceLayers.aspectMask(VulkanConst.formatAspectMask(source.getFormat()));
      VkImageCopy.Buffer regions = VkImageCopy.calloc((int) 1, (MemoryStack) stack);
      regions.srcOffset().set(sourceX, sourceY, 0);
      regions.dstOffset().set(destX, destY, 0);
      regions.extent().set(width, height, 1);
      regions.srcSubresource(subresourceLayers);
      regions.dstSubresource(subresourceLayers);
      VK12.vkCmdCopyImage(
          (VkCommandBuffer) this.commandBuffer(),
          (long) vulkanSrc.vkImage(),
          (int) 1,
          (long) vulkanDst.vkImage(),
          (int) 1,
          (VkImageCopy.Buffer) regions);
      this.memoryBarrier(stack);
    }
  }

  private boolean awaitSubmitCompletion(long submitIndex, long timeoutMs) {
    if (this.completedSubmitIndex >= submitIndex) {
      return true;
    }
    if (submitIndex == this.currentSubmitIndex) {
      throw new IllegalStateException("Cannot wait on a fence for the current submit");
    }
    try (MemoryStack stack = MemoryStack.stackPush(); ) {
      boolean completed;
      VkSemaphoreWaitInfo waitInfo =
          VkSemaphoreWaitInfo.calloc((MemoryStack) stack).sType$Default();
      waitInfo.pSemaphores(stack.longs(this.submitSemaphore));
      waitInfo.pValues(stack.longs(submitIndex));
      waitInfo.semaphoreCount(1);
      int result =
          VK12.vkWaitSemaphores(
              (VkDevice) this.device.vkDevice(),
              (VkSemaphoreWaitInfo) waitInfo,
              (long) (timeoutMs * 1000000L));
      VulkanUtils.crashIfFailure(result, "Failed to wait for semaphore");
      boolean bl = completed = result == 0;
      if (completed) {
        this.completedSubmitIndex = submitIndex;
      }
      boolean bl2 = completed;
      return bl2;
    }
  }

  @Override
  public GpuFence createFence() {
    return new GpuFence(this) {
      private final long submitIndex;
      private boolean completed;
      final /* synthetic */ VulkanCommandEncoder this$0;

      {
        VulkanCommandEncoder vulkanCommandEncoder = this$0;
        Objects.requireNonNull(vulkanCommandEncoder);
        this.this$0 = vulkanCommandEncoder;
        this.submitIndex = this.this$0.currentSubmitIndex;
        this.completed = false;
      }

      @Override
      public boolean awaitCompletion(long timeoutMs) {
        if (!this.completed) {
          this.completed = this.this$0.awaitSubmitCompletion(this.submitIndex, timeoutMs);
        }
        return this.completed;
      }

      @Override
      public void close() {
        this.completed = true;
      }
    };
  }

  @Override
  public void writeTimestamp(GpuQueryPool pool, int index) {
    long queryPool = ((VulkanQueryPool) pool).vkQueryPool();
    VK12.vkResetQueryPool(
        (VkDevice) this.device.vkDevice(), (long) queryPool, (int) index, (int) 1);
    KHRSynchronization2.vkCmdWriteTimestamp2KHR(
        (VkCommandBuffer) this.commandBuffer(), (long) 65536L, (long) queryPool, (int) index);
  }

  public long getTimestampNow() {
    try (MemoryStack stack = MemoryStack.stackPush(); ) {
      VulkanQueryPool queryPool = (VulkanQueryPool) this.device.createTimestampQueryPool(1);
      try {
        VkCommandBuffer commandBuffer = this.allocateTransientCommandBuffer(true);
        VkCommandBufferBeginInfo beginInfo =
            VkCommandBufferBeginInfo.calloc((MemoryStack) stack).sType$Default();
        beginInfo.flags(1);
        VulkanUtils.crashIfFailure(
            VK12.vkBeginCommandBuffer(
                (VkCommandBuffer) commandBuffer, (VkCommandBufferBeginInfo) beginInfo),
            "Failed to begin VkCommandBuffer");
        KHRSynchronization2.vkCmdWriteTimestamp2KHR(
            (VkCommandBuffer) commandBuffer, (long) 0L, (long) queryPool.vkQueryPool(), (int) 0);
        VulkanUtils.crashIfFailure(
            VK12.vkEndCommandBuffer((VkCommandBuffer) commandBuffer),
            "Failed to end VkCommandBuffer");
        try (VulkanQueue.Submission submit = this.device.graphicsQueue().beginSubmit(); ) {
          submit.executeCommands(commandBuffer);
        }
        LongBuffer timestampPtr = stack.callocLong(1);
        VulkanUtils.crashIfFailure(
            VK12.vkGetQueryPoolResults(
                (VkDevice) this.device.vkDevice(),
                (long) queryPool.vkQueryPool(),
                (int) 0,
                (int) 1,
                (LongBuffer) timestampPtr,
                (long) 0L,
                (int) 3),
            "Cannot fetch current timestamp");
        long l = timestampPtr.get(0);
        if (queryPool != null) {
          queryPool.close();
        }
        return l;
      } catch (Throwable throwable) {
        if (queryPool != null) {
          try {
            queryPool.close();
          } catch (Throwable throwable2) {
            throwable.addSuppressed(throwable2);
          }
        }
        throw throwable;
      }
    }
  }
}
