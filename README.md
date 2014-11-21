# Android-DDP

This library implements the [Distributed Data Protocol](https://www.meteor.com/ddp) (DDP) from Meteor for clients on Android.

Connect your native Android apps, written in Java, to apps built with the [Meteor](https://www.meteor.com/) framework and build real-time features.

## Firebase

Besides providing the DDP client for Android/Java, this project also aims to build a complete [drop-in replacement for Firebase](Firebase.md) on Android.

## Installation

 1. Reference this library as a library project in your app
 2. Make sure all [dependencies](#dependencies) of this library are included
 3. Add the Internet permission to your app's `AndroidManifest.xml`:

    `<uses-permission android:name="android.permission.INTERNET" />`

 4. Look at our [basic example](Examples/DDP/) for a quick introduction to the usage of the DDP client

## Contributing

All contributions are welcome! If you wish to contribute, please create an issue first so that your feature, problem or question can be discussed.

## Dependencies

 * [Autobahn|Android](http://autobahn.ws/android/gettingstarted.html#add-jars-to-your-project) — [Tavendo](https://github.com/tavendo/AutobahnAndroid) — [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
 * [Jackson Core](http://autobahn.ws/android/gettingstarted.html#add-jars-to-your-project) — [FasterXML](https://github.com/FasterXML/jackson-core) — [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
 * [Jackson ObjectMapper](http://autobahn.ws/android/gettingstarted.html#add-jars-to-your-project) — [FasterXML](https://github.com/FasterXML/jackson-core) — [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Further reading

 * [DDP — Specification](https://github.com/meteor/meteor/blob/devel/packages/ddp/DDP.md)
 * [Jackson — Documentation](http://wiki.fasterxml.com/JacksonDocumentation)
 * [Autobahn|Android — API documentation](http://autobahn.ws/android/_gen/packages.html)
 * [Firebase — Tutorial](https://www.firebase.com/docs/android/guide/)
 * [Firebase — API documentation](https://www.firebase.com/docs/java-api/javadoc/index.html)

## Disclaimer

This project is neither affiliated with nor endorsed by Meteor or Firebase.

## License

```
Copyright 2014 www.delight.im <info@delight.im>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
