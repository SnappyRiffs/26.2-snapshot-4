/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 */
package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;

public class TreeDecoratorType<P extends TreeDecorator> {
  public static final TreeDecoratorType<TrunkVineDecorator> TRUNK_VINE =
      TreeDecoratorType.register("trunk_vine", TrunkVineDecorator.CODEC);
  public static final TreeDecoratorType<LeaveVineDecorator> LEAVE_VINE =
      TreeDecoratorType.register("leave_vine", LeaveVineDecorator.CODEC);
  public static final TreeDecoratorType<PaleMossDecorator> PALE_MOSS =
      TreeDecoratorType.register("pale_moss", PaleMossDecorator.CODEC);
  public static final TreeDecoratorType<CreakingHeartDecorator> CREAKING_HEART =
      TreeDecoratorType.register("creaking_heart", CreakingHeartDecorator.CODEC);
  public static final TreeDecoratorType<CocoaDecorator> COCOA =
      TreeDecoratorType.register("cocoa", CocoaDecorator.CODEC);
  public static final TreeDecoratorType<BeehiveDecorator> BEEHIVE =
      TreeDecoratorType.register("beehive", BeehiveDecorator.CODEC);
  public static final TreeDecoratorType<AlterGroundDecorator> ALTER_GROUND =
      TreeDecoratorType.register("alter_ground", AlterGroundDecorator.CODEC);
  public static final TreeDecoratorType<AttachedToLeavesDecorator> ATTACHED_TO_LEAVES =
      TreeDecoratorType.register("attached_to_leaves", AttachedToLeavesDecorator.CODEC);
  public static final TreeDecoratorType<PlaceOnGroundDecorator> PLACE_ON_GROUND =
      TreeDecoratorType.register("place_on_ground", PlaceOnGroundDecorator.CODEC);
  public static final TreeDecoratorType<AttachedToLogsDecorator> ATTACHED_TO_LOGS =
      TreeDecoratorType.register("attached_to_logs", AttachedToLogsDecorator.CODEC);
  private final MapCodec<P> codec;

  private static <P extends TreeDecorator> TreeDecoratorType<P> register(
      String name, MapCodec<P> codec) {
    return Registry.register(
        BuiltInRegistries.TREE_DECORATOR_TYPE, name, new TreeDecoratorType<P>(codec));
  }

  private TreeDecoratorType(MapCodec<P> codec) {
    this.codec = codec;
  }

  public MapCodec<P> codec() {
    return this.codec;
  }
}
