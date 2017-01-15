package im.delight.android.ddp.db.memory;

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

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import im.delight.android.ddp.db.Collection;
import im.delight.android.ddp.db.Document;
import im.delight.android.ddp.db.Query;

/** Collection that is stored in memory */
public final class InMemoryCollection implements Collection {

	/** The name of the collection */
	private final String mName;
	/** The map of documents backing the collection */
	private final DocumentsMap mDocuments;

	/**
	 * Creates a new collection that is stored in memory
	 *
	 * @param name the name of the collection to create
	 */
	protected InMemoryCollection(final String name) {
		mName = name;
		mDocuments = new DocumentsMap();
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public Document getDocument(final String id) {
		return mDocuments.get(id);
	}

	@Override
	public String[] getDocumentIds() {
		return mDocuments.keySet().toArray(new String[mDocuments.size()]);
	}

	@Override
	public int count() {
		return mDocuments.size();
	}

	@Override
	public Query whereEqual(final String fieldName, final Object fieldValue) {
		return new InMemoryQuery(mDocuments).whereEqual(fieldName, fieldValue);
	}

	@Override
	public Query whereNotEqual(final String fieldName, final Object fieldValue) {
		return new InMemoryQuery(mDocuments).whereNotEqual(fieldName, fieldValue);
	}

	@Override
	public Query whereLessThan(final String fieldName, final double fieldValue) {
		return new InMemoryQuery(mDocuments).whereLessThan(fieldName, fieldValue);
	}

	@Override
	public Query whereLessThanOrEqual(final String fieldName, final double fieldValue) {
		return new InMemoryQuery(mDocuments).whereLessThanOrEqual(fieldName, fieldValue);
	}

	@Override
	public Query whereGreaterThan(final String fieldName, final double fieldValue) {
		return new InMemoryQuery(mDocuments).whereGreaterThan(fieldName, fieldValue);
	}

	@Override
	public Query whereGreaterThanOrEqual(final String fieldName, final double fieldValue) {
		return new InMemoryQuery(mDocuments).whereGreaterThanOrEqual(fieldName, fieldValue);
	}

	@Override
	public Query whereNull(final String fieldName) {
		return new InMemoryQuery(mDocuments).whereNull(fieldName);
	}

	@Override
	public Query whereNotNull(final String fieldName) {
		return new InMemoryQuery(mDocuments).whereNotNull(fieldName);
	}

	@Override
	public Query whereIn(String fieldName, Object[] fieldValues) {
		return new InMemoryQuery(mDocuments).whereIn(fieldName, fieldValues);
	}

	@Override
	public Query whereNotIn(String fieldName, Object[] fieldValues) {
		return new InMemoryQuery(mDocuments).whereNotIn(fieldName, fieldValues);
	}

	@Override
	public Document[] find() {
		return new InMemoryQuery(mDocuments).find();
	}

	@Override
	public Document[] find(int limit) {
		return new InMemoryQuery(mDocuments).find(limit);
	}

	@Override
	public Document[] find(int limit, int offset) {
		return new InMemoryQuery(mDocuments).find(limit, offset);
	}

	@Override
	public Document findOne() {
		return new InMemoryQuery(mDocuments).findOne();
	}

	/**
	 * Returns the raw map of documents backing this collection
	 *
	 * @return the raw map of documents
	 */
	protected DocumentsMap getDocumentsMap() {
		return mDocuments;
	}

	@Override
	public String toString() {
		return mDocuments.toString();
	}

	/** Data type for the map backing the collection */
	protected static class DocumentsMap extends ConcurrentHashMap<String, InMemoryDocument> {

		public DocumentsMap() {
			super();
		}

		public DocumentsMap(final Map<? extends String, ? extends InMemoryDocument> map) {
			super(map);
		}

	}

}
