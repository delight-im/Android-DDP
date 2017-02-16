package im.delight.android.ddp;

/*
 * Copyright (c) delight.im <info@delight.im>
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

/** Callbacks for all database-related events received from a DDP server */
public interface DdpCallback {

	/**
	 * Callback that is executed whenever a new document is added to a collection
	 *
	 * @param collectionName the name of the collection that the document is added to
	 * @param documentID the ID of the document that is being added
	 * @param newValuesJson the new fields of the document as a JSON string
	 */
	void onDataAdded(String collectionName, String documentID, String newValuesJson);

	/**
	 * Callback that is executed whenever an existing document is changed in a collection
	 *
	 * @param collectionName the name of the collection that the document is changed in
	 * @param documentID the ID of the document that is being changed
	 * @param updatedValuesJson the modified fields of the document as a JSON string
	 * @param removedValuesJson the deleted fields of the document as a JSON string
	 */
	void onDataChanged(String collectionName, String documentID, String updatedValuesJson, String removedValuesJson);

	/**
	 * Callback that is executed whenever an existing document is removed from a collection
	 *
	 * @param collectionName the name of the collection that the document is removed from
	 * @param documentID the ID of the document that is being removed
	 */
	void onDataRemoved(String collectionName, String documentID);
	
	/**
	 * Callback that is executed whenever a subscription stopped and there are no listeners waiting for it.
	 * This can happen when the server decides to stop the subscription or when
	 * subscribing or unsubscribing without a listener.
	 *  
	 * @param subscriptionId The subscription ID returned by subscribe.
	 */
	void onNoSub(String subscriptionId);
}
