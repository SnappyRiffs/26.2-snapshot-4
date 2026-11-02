/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.model;

public record AdultAndBabyModelPair<T extends Model<?>>(T adultModel, T babyModel) {
  public T getModel(boolean isBaby) {
    return isBaby ? this.babyModel : this.adultModel;
  }
}
