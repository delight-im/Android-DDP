# Firebase

This project aims to clone the [Firebase](https://www.firebase.com/) API for Android and intends to become a complete drop-in replacement.

The Firebase replacement library is implemented in the [`firebase`](Android/Source/src/im/delight/android/ddp/firebase) package and based on the [`ddp`](Android/Source/src/im/delight/android/ddp) package.

The server, which is a Meteor instance with custom methods, [can be found here](Server/).

> Don't build your house on someone else's platform.
>
> — Paul Anthony

## The goal

This library lets you connect to your own Meteor server just as you would connect to Firebase with their official SDK.

In the end, you should be able to just replace `import com.firebase.client.` with `import im.delight.android.ddp.firebase.` in your Java files and be ready to run the same application code with your own Meteor server.

> Don't be a Google Bitch, don't be a Facebook Bitch, and don't be a Twitter Bitch. Be your own Bitch.
>
> — Fred Wilson

## Why this is important

Firebase is an awesome service. But if you build your product and infrastructure around a PaaS/BaaS, you usually don't get the amount of reliability and guaranteed support for the future that you'd love to have. Thus it's always good to have a safe replacement up your sleeve.

> Nearly everyone who gets acquired sends an email to customers along the lines of: "Everything will stay the same, we're just going to have more resources to make things better for you."
>
> Everyone who's used apps built by these acquired companies know that sooner or later, that promise almost always turns out to be bullshit.
>
> — Alex Turnbull

## Examples

### [Basic example](Examples/Firebase/)

The basic example shows how the most important Firebase features are implemented. This library is used as a drop-in replacement and provides a self-hosted solution.

### [`AndroidChat` example](https://github.com/delight-im/AndroidChat)

This is the official `AndroidChat` example from Firebase — implemented with this library as a drop-in replacement on the client and server. See the short list of [required changes](https://github.com/delight-im/AndroidChat/commits/master) that had to be applied to make this work.
