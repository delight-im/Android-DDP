package im.delight.android.ddp.db;

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

import im.delight.android.ddp.Fields;

/** Storage for data that exposes write access */
public interface DataStore {

	/**
	 * Receives data whenever a new document is added to a collection
	 *
	 * @param collectionName the name of the collection that the document is added to
	 * @param documentID the ID of the document that is being added
	 * @param newValues the new fields of the document
	 */
	void onDataAdded(String collectionName, String documentID, Fields newValues);

	/**
	 * Receives data whenever an existing document is changed in a collection
	 *
	 * @param collectionName the name of the collection that the document is changed in
	 * @param documentID the ID of the document that is being changed
	 * @param updatedValues the modified fields of the document
	 * @param removedValues the deleted fields of the document
	 */
	void onDataChanged(String collectionName, String documentID, Fields updatedValues, String[] removedValues);

	/**
	 * Receives data whenever an existing document is removed from a collection
	 *
	 * @param collectionName the name of the collection that the document is removed from
	 * @param documentID the ID of the document that is being removed
	 */
	void onDataRemoved(String collectionName, String documentID);

}
