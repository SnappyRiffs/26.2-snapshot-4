/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.network.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import java.util.Optional;

public interface ComponentContents {
  public default <T> Optional<T> visit(
      FormattedText.StyledContentConsumer<T> output, Style currentStyle) {
    return Optional.empty();
  }

  public default <T> Optional<T> visit(FormattedText.ContentConsumer<T> output) {
    return Optional.empty();
  }

  public default MutableComponent resolve(ResolutionContext context, int recursionDepth)
      throws CommandSyntaxException {
    return MutableComponent.create(this);
  }

  public MapCodec<? extends ComponentContents> codec();
}
