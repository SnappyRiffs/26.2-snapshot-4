/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.permissions;

public interface PermissionSet {
  public static final PermissionSet NO_PERMISSIONS = permission -> false;
  public static final PermissionSet ALL_PERMISSIONS = permission -> true;

  public boolean hasPermission(Permission var1);

  public default PermissionSet union(PermissionSet other) {
    if (other instanceof PermissionSetUnion) {
      return other.union(this);
    }
    return new PermissionSetUnion(this, other);
  }
}
