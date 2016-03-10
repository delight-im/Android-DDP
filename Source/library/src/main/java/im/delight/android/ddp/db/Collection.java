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

/** A collection has a name and contains any number of documents, identified by their IDs */
public interface Collection extends Query {

	/**
	 * Returns the name of the collection
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Returns the document with the specified ID from the collection
	 *
	 * @param id the ID of the document to return
	 * @return the document object or `null`
	 */
	Document getDocument(String id);

	/**
	 * Lists all documents from the collection by returning a set of their IDs
	 *
	 * @return an array containing the IDs of all documents
	 */
	String[] getDocumentIds();

	/**
	 * Returns the number of documents in the collection
	 *
	 * @return the count
	 */
	int count();

}
