/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.world.entity;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public interface OwnableEntity {
  public @Nullable EntityReference<LivingEntity> getOwnerReference();

  public Level level();

  public default @Nullable LivingEntity getOwner() {
    return EntityReference.getLivingEntity(this.getOwnerReference(), this.level());
  }

  public default @Nullable LivingEntity getRootOwner() {
    ObjectArraySet seen = new ObjectArraySet();
    LivingEntity owner = this.getOwner();
    seen.add(this);
    while (owner instanceof OwnableEntity) {
      OwnableEntity ownableOwner = (OwnableEntity) ((Object) owner);
      LivingEntity ownersOwner = ownableOwner.getOwner();
      if (seen.contains(ownersOwner)) {
        return null;
      }
      seen.add(owner);
      owner = ownableOwner.getOwner();
    }
    return owner;
  }
}
