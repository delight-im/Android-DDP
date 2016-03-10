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

import im.delight.android.ddp.Fields;
import im.delight.android.ddp.db.Document;

/** Document that is stored in memory */
public final class InMemoryDocument implements Document {

	/** The ID of the document */
	private final String mId;
	/** The fields of the document */
	private final Fields mFields;

	/**
	 * Creates a new document that is stored in memory
	 *
	 * @param id the ID of the document to create
	 */
	protected InMemoryDocument(final String id) {
		this(id, new Fields());
	}

	/**
	 * Creates a new document that is stored in memory
	 *
	 * @param id the ID of the document to create
	 * @param fields the initial fields for the document to create
	 */
	protected InMemoryDocument(final String id, final Fields fields) {
		mId = id;
		mFields = fields;
	}

	@Override
	public String getId() {
		return mId;
	}

	@Override
	public Object getField(final String name) {
		return mFields.get(name);
	}

	@Override
	public String[] getFieldNames() {
		return mFields.keySet().toArray(new String[mFields.size()]);
	}

	@Override
	public int count() {
		return mFields.size();
	}

	/**
	 * Returns the raw map of fields backing this document
	 *
	 * @return the raw map of fields
	 */
	protected Fields getFields() {
		return mFields;
	}

	@Override
	public String toString() {
		return mFields.toString();
	}

}
