/*
 * Copyright (c) Stephen Orr (stephen@chatorr.ca)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package im.delight.android.ddp;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.SubscribeListener;

/**
 * An object to manage single subscriptions and multiple subscriptions
 * to the Meteor client.
 */
public class MeteorSubscription {
    private String                      mSubscriptionID;

    private MeteorSubscription[]        mSubscriptions  = null;

    private Meteor                      mMeteor         = null;
    private String                      mName           = null;
    private Object[]                    mParams         = null;
    private Boolean                     mIsReady        = false;

    /**
     * Returns a new instance of a singleton MeteorSubscription to be used in
     * constructing an array of subscriptions. This form does NOT subscribe
     * on it's own.
     *
     * @param meteor the instance of Meteor to subscribe over
     * @param subscriptionName the name of the subscription
     * @param params array of object parameters for the subscription.
     */
    public MeteorSubscription(Meteor meteor, String subscriptionName, Object[] params)  {
        mMeteor = meteor;
        mName = subscriptionName;
        mParams = params;
    }
    /**
     * Returns a new instance of a singleton MeteorSubscription to be used in
     * constructing an array of subscriptions. This form does NOT subscribe
     * on it's own.
     *
     * The subscription will be made on the Meteor Singleton instance.
     *
     * @param subscriptionName the name of the subscription
     * @param params array of object parameters for the subscription.
     */
    public MeteorSubscription(String subscriptionName, Object[] params)                 {
        mName = subscriptionName;
        mParams = params;
    }
    /**
     * Returns a new instance of a MeteorSubscription for more than one simultaneous subscription. The
     * subscription has no need to be notified on subscription ready.
     *
     * @param meteor the instance of Meteor to subscribe over
     * @param subscriptions an array of subscription name / paramter objects
     */
    public MeteorSubscription(Meteor meteor, MeteorSubscription[] subscriptions)        {
        mMeteor = meteor;
        mSubscriptions = subscriptions;
    }
    /**
     * Returns a new instance of a MeteorSubscription for more than one simultaneous subscription.
     *
     * The subscription will be made on the Meteor Singleton instance.
     *
     * @param subscriptions an array of subscription name / paramter objects
     */
    public MeteorSubscription(MeteorSubscription[] subscriptions)                       {
        mSubscriptions = subscriptions;
    }
    /**
     * Starts all associated subscriptions, if a listener is provided it's
     * success will be called only once all subscriptions are ready.
     *
     * The subscription will be made on the Meteor Singleton instance.
     *
     * @param listener an array of subscription name / paramter objects
     *
     * @return [this] to enable chaining with a constructor
     */
    public MeteorSubscription start(final SubscribeListener listener)                   {
        //
        // If Meteor is not yet defined, try the singleton.
        if (mMeteor == null) {
            if (MeteorSingleton.hasInstance()) {
                mMeteor = MeteorSingleton.getInstance();
            } else {
                if (listener != null) {
                    listener.onError("Meteor not found", "Attempt to subscribe to the Meteor Singleton before it has been defined.", "");
                }
                return this;
            }
        }
        //
        // Make sure Meteor is connected, otherwise, fail.
        if (!mMeteor.isConnected()) {
            if (listener != null) {
                listener.onError("Meteor not connected", "Attempt to subscribe while Meteor not connected.", "");
            }
            return this;
        }
        //
        // If we have an array of subscriptions, start them all, otherwise start just this one.
        if (mSubscriptions != null) {
            startAll(listener);
        } else {
            mSubscriptionID = mMeteor.subscribe(mName, mParams, new SubscribeListener() {
                @Override public void onSuccess() {
                    mIsReady = true;
                    if (listener != null) {
                        listener.onSuccess();
                    }
                }
                @Override public void onError(String error, String reason, String details) {
                    if (listener != null) {
                        listener.onError(error, reason, details);
                    }
                }
            });
        }
        return this;
    }
    /**
     * Stops all associated subscriptions.
     *
     */
    public void stop()                                                                  {
        if (mSubscriptions != null) {
            for (final MeteorSubscription sub : mSubscriptions) {
                mMeteor.unsubscribe(sub.mSubscriptionID);
            }
        }
    }
    /**
     * Stops all associated subscriptions.
     *
     * @return Boolean true if all related subscriptions are ready.
     */
    public Boolean isReady()                                                            {

        return mIsReady;
    }

    // Internal implementation
    /**
     * Starts the subscription for each subscription object in the
     * subscription list.
     *
     * If the subscriptions in the list already have an assigned Meteor object, then
     * the subscription will happen over that Meteor instance, otherwise the Meteor
     * instance from [this] object is used instead.
     *
     * @param listener the SubscribeListener to call once all subscriptions are ready.
     */
    private void startAll(final SubscribeListener listener)                             {
        //
        // For each item in the subscription list, create a custom listener that will
        // mark the subscription "isReady", and check to see when all subscriptions are
        // ready.
        for (final MeteorSubscription sub : mSubscriptions) {

            SubscribeListener subListener = new SubscribeListener() {
                @Override public void onSuccess() {
                    sub.mIsReady = true;
                    if (listener != null) {
                        for (MeteorSubscription sub : mSubscriptions) {
                            if (!sub.isReady()) {
                                return;
                            }
                        }
                        //
                        // If we get this far, all subscriptions are ready.
                        mIsReady = true;
                        listener.onSuccess();
                    }
                }
                @Override public void onError(String error, String reason, String details) {
                    //
                    // If any fails, the overall subscription fails
                    if (listener != null) {
                        listener.onError(error, reason, details);
                    }
                }
            };
            sub.mMeteor = mMeteor;
            sub.start(subListener);
        }
    }
}
