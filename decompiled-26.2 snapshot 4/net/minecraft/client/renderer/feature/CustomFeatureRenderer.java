/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 */
package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;

public class CustomFeatureRenderer {
  public void renderSolid(SubmitNodeCollection nodeCollection, FeatureFrameContext context) {
    Storage storage = nodeCollection.getCustomGeometrySubmits();
    for (Map.Entry<RenderType, List<Submit>> entry :
        storage.solidCustomGeometrySubmits.entrySet()) {
      for (Submit submit : entry.getValue()) {
        VertexConsumer buffer = context.bufferSource().getBuffer(entry.getKey());
        submit.customGeometryRenderer().render(submit.pose(), buffer);
        this.renderOutline(submit, context.outlineBufferSource());
      }
    }
  }

  public void renderTranslucent(SubmitNodeCollection nodeCollection, FeatureFrameContext context) {
    Storage storage = nodeCollection.getCustomGeometrySubmits();
    for (Map.Entry<RenderType, List<Submit>> entry :
        storage.translucentCustomGeometrySubmits.entrySet()) {
      for (Submit submit : entry.getValue()) {
        VertexConsumer buffer = context.bufferSource().getBuffer(entry.getKey());
        submit.customGeometryRenderer().render(submit.pose(), buffer);
        this.renderOutline(submit, context.outlineBufferSource());
      }
    }
  }

  public void renderOutline(Submit submit, OutlineBufferSource outlineBufferSource) {
    RenderType renderType = submit.renderType();
    int outlineColor = submit.outlineColor();
    if (outlineColor != 0 && (renderType.outline().isPresent() || renderType.isOutline())) {
      outlineBufferSource.setColor(outlineColor);
      VertexConsumer outlineBuffer = outlineBufferSource.getBuffer(renderType);
      submit.customGeometryRenderer().render(submit.pose(), outlineBuffer);
    }
  }

  public static class Storage {
    private final Map<RenderType, List<Submit>> solidCustomGeometrySubmits =
        new HashMap<RenderType, List<Submit>>();
    private final Map<RenderType, List<Submit>> translucentCustomGeometrySubmits =
        new HashMap<RenderType, List<Submit>>();
    private final Set<RenderType> solidCustomGeometrySubmitsUsage = new ObjectOpenHashSet();
    private final Set<RenderType> translucentCustomGeometrySubmitsUsage = new ObjectOpenHashSet();

    public void add(
        PoseStack poseStack,
        RenderType renderType,
        int outlineColor,
        SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {
      Submit submit =
          new Submit(poseStack.last().copy(), renderType, outlineColor, customGeometryRenderer);
      if (!renderType.hasBlending()) {
        this.solidCustomGeometrySubmits
            .computeIfAbsent(renderType, rt -> new ArrayList())
            .add(submit);
      } else {
        this.translucentCustomGeometrySubmits
            .computeIfAbsent(renderType, rt -> new ArrayList())
            .add(submit);
      }
    }

    public void clear() {
      for (Map.Entry<RenderType, List<Submit>> entry : this.solidCustomGeometrySubmits.entrySet()) {
        if (entry.getValue().isEmpty()) continue;
        this.solidCustomGeometrySubmitsUsage.add(entry.getKey());
        entry.getValue().clear();
      }
      for (Map.Entry<RenderType, List<Submit>> entry :
          this.translucentCustomGeometrySubmits.entrySet()) {
        if (entry.getValue().isEmpty()) continue;
        this.translucentCustomGeometrySubmitsUsage.add(entry.getKey());
        entry.getValue().clear();
      }
    }

    public void endFrame() {
      this.solidCustomGeometrySubmits
          .keySet()
          .removeIf(renderType -> !this.solidCustomGeometrySubmitsUsage.contains(renderType));
      this.solidCustomGeometrySubmitsUsage.clear();
      this.translucentCustomGeometrySubmits
          .keySet()
          .removeIf(renderType -> !this.translucentCustomGeometrySubmitsUsage.contains(renderType));
      this.translucentCustomGeometrySubmitsUsage.clear();
    }
  }

  public record Submit(
      PoseStack.Pose pose,
      RenderType renderType,
      int outlineColor,
      SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {}
}
