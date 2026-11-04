/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package net.minecraft.server.network;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TextFilter {
  public static final TextFilter DUMMY =
      new TextFilter() {

        @Override
        public CompletableFuture<FilteredText> processStreamMessage(String message) {
          return CompletableFuture.completedFuture(FilteredText.passThrough(message));
        }

        @Override
        public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> messages) {
          return CompletableFuture.completedFuture(
              (List)
                  messages.stream()
                      .map(FilteredText::passThrough)
                      .collect(ImmutableList.toImmutableList()));
        }
      };

  public default void join() {}

  public default void leave() {}

  public CompletableFuture<FilteredText> processStreamMessage(String var1);

  public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> var1);
}
