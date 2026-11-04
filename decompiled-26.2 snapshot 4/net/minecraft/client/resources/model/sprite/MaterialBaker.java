/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.resources.model.sprite;

import net.minecraft.client.resources.model.ModelDebugName;

public interface MaterialBaker {
  public Material.Baked get(Material var1, ModelDebugName var2);

  public Material.Baked reportMissingReference(String var1, ModelDebugName var2);

  public default Material.Baked resolveSlot(TextureSlots slots, String id, ModelDebugName name) {
    Material resolvedMaterial = slots.getMaterial(id);
    return resolvedMaterial != null
        ? this.get(resolvedMaterial, name)
        : this.reportMissingReference(id, name);
  }
}
