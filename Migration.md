# Migration

 * `v1.x.x` to `v2.0.0`
   * The minimum API level is now `9` (Android 2.3) instead of `8` (Android 2.2).
   * `MeteorCallback.onDisconnect(int code, String reason)` is now just `MeteorCallback.onDisconnect()` without the arguments.
