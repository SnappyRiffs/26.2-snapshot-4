/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util.parsing.packrat;

public record ErrorEntry<S>(int cursor, SuggestionSupplier<S> suggestions, Object reason) {}
