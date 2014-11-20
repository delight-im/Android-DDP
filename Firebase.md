# Firebase

This library aims to clone the [Firebase](https://www.firebase.com/) API for Android and intends to become a complete drop-in replacement.

The Firebase replacement library is implemented in the [`firebase`](Android/src/im/delight/android/ddp/firebase) package and based on the [`ddp`](Android/src/im/delight/android/ddp) package.

The server, which is a Meteor instance with custom methods, [can be found here](Server/).

> Don't build your house on someone else's platform.
>
> — Paul Anthony

This lets you connect to your own Meteor server just as you would connect to Firebase with their official SDK.

In the end, you should be able to just replace `com.firebase.client.*` with `im.delight.android.ddp.firebase.*` in your Java imports.

> Don't be a Google Bitch, don't be a Facebook Bitch, and don't be a Twitter Bitch. Be your own Bitch.
>
> — Fred Wilson

Firebase is an awesome service. But if you build your product and infrastructure around a PaaS/BaaS, you usually don't get the amount of reliability and guaranteed support for the future that you'd love to have. Thus it's always good to have a safe replacement up your sleeve.

> Nearly everyone who gets acquired sends an email to customers along the lines of: "Everything will stay the same, we're just going to have more resources to make things better for you."
>
> Everyone who's used apps built by these acquired companies know that sooner or later, that promise almost always turns out to be bullshit.
>
> — Alex Turnbull

## Examples

An example app for Android [can be found here](Examples/Firebase/). It shows some of the Firebase features being implemented with this library and a self-hosted Meteor server using the [server code](Server/) that is also included in this project.
