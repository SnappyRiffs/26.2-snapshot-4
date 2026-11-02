/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.client.gui.narration;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.network.chat.Component;

public interface NarrationElementOutput {
  public default void add(NarratedElementType type, Component contents) {
    this.add(type, NarrationThunk.from(contents.getString()));
  }

  public default void add(NarratedElementType type, String contents) {
    this.add(type, NarrationThunk.from(contents));
  }

  public default void add(NarratedElementType type, Component... contents) {
    this.add(
        type, NarrationThunk.from((List<Component>) ImmutableList.copyOf((Object[]) contents)));
  }

  public void add(NarratedElementType var1, NarrationThunk<?> var2);

  public NarrationElementOutput nest();
}
