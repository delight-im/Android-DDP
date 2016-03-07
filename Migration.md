# Migration

 * From `v2.x.x` to `v3.x.x`
   * `Meteor#setCallback` is now `Meteor#addCallback`.
   * `Meteor#unsetCallback` is now `Meteor#removeCallback` or `Meteor#removeCallbacks`.
   * You now have to establish the connection to the server manually. Before, this was done automatically in the `Meteor` constructor. To update your code, call `Meteor#connect` somewhere after calling the `Meteor` constructor. It is recommended to place the call after registering the callback via `Meteor#addCallback`.
   * You now have to unregister all callbacks manually by calling either `Meteor#removeCallback` or `Meteor#removeCallbacks`. Before, this was done automatically when disconnecting. It is recommended to remove the callback(s) just after calling `Meteor#disconnect`, whenever you do that.
   * Exceptions triggered while connecting and disconnecting are now correctly delivered in `MeteorCallback#onException`.
 * From `v1.x.x` to `v2.x.x`
   * The minimum API level is now `9` (Android 2.3) instead of `8` (Android 2.2).
   * `MeteorCallback.onDisconnect(int code, String reason)` is now just `MeteorCallback.onDisconnect()` without the arguments.
