/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.input;

import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonInfo;

public record MouseButtonEvent(double x, double y, MouseButtonInfo buttonInfo) implements InputWithModifiers
{
    @Override
    public int input() {
        return this.button();
    }

    @MouseButtonInfo.MouseButton
    public int button() {
        return this.buttonInfo().button();
    }

    @Override
    @InputWithModifiers.Modifiers
    public int modifiers() {
        return this.buttonInfo().modifiers();
    }
}

