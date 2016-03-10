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

import im.delight.android.ddp.db.Document;
import im.delight.android.ddp.db.Query;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** Query that operates on a collection stored in memory */
public final class InMemoryQuery implements Query {

	private static final double PICO_DOUBLE = 0.000000000001;
	private final InMemoryCollection.DocumentsMap mDocuments;

	/**
	 * Creates a new query that operates on a collection stored in memory
	 *
	 * @param documents the map of documents that this new query should operate on
	 */
	protected InMemoryQuery(final InMemoryCollection.DocumentsMap documents) {
		// create a shallow copy of the map containing the documents
		mDocuments = new InMemoryCollection.DocumentsMap(documents);
	}

	@Override
	public Query whereEqual(final String fieldName, final Object fieldValue) {
		if (fieldValue == null) {
			return whereNull(fieldName);
		}

		final Iterator<InMemoryDocument> iterator = mDocuments.values().iterator();

		InMemoryDocument entry;
		while (iterator.hasNext()) {
			entry = iterator.next();

			if (entry.getField(fieldName) == null || !entry.getField(fieldName).equals(fieldValue)) {
				iterator.remove();
			}
		}

		return this;
	}

	@Override
	public Query whereNotEqual(final String fieldName, final Object fieldValue) {
		if (fieldValue == null) {
			return whereNotNull(fieldName);
		}

		final Iterator<InMemoryDocument> iterator = mDocuments.values().iterator();

		InMemoryDocument entry;
		while (iterator.hasNext()) {
			entry = iterator.next();

			if (entry.getField(fieldName) != null && entry.getField(fieldName).equals(fieldValue)) {
				iterator.remove();
			}
		}

		return this;
	}

	@Override
	public Query whereLessThan(final String fieldName, final double fieldValue) {
		final Iterator<InMemoryDocument> iterator = mDocuments.values().iterator();

		InMemoryDocument entry;
		while (iterator.hasNext()) {
			entry = iterator.next();

			if (coerceNumber(entry.getField(fieldName)) >= fieldValue) {
				iterator.remove();
			}
		}

		return this;
	}

	@Override
	public Query whereLessThanOrEqual(final String fieldName, final double fieldValue) {
		return whereLessThan(fieldName, fieldValue + PICO_DOUBLE);
	}

	@Override
	public Query whereGreaterThan(final String fieldName, final double fieldValue) {
		final Iterator<InMemoryDocument> iterator = mDocuments.values().iterator();

		InMemoryDocument entry;
		while (iterator.hasNext()) {
			entry = iterator.next();

			if (coerceNumber(entry.getField(fieldName)) <= fieldValue) {
				iterator.remove();
			}
		}

		return this;
	}

	@Override
	public Query whereGreaterThanOrEqual(final String fieldName, final double fieldValue) {
		return whereGreaterThan(fieldName, fieldValue - PICO_DOUBLE);
	}

	@Override
	public Query whereNull(final String fieldName) {
		final Iterator<InMemoryDocument> iterator = mDocuments.values().iterator();

		InMemoryDocument entry;
		while (iterator.hasNext()) {
			entry = iterator.next();

			if (entry.getField(fieldName) != null) {
				iterator.remove();
			}
		}

		return this;
	}

	@Override
	public Query whereNotNull(final String fieldName) {
		final Iterator<InMemoryDocument> iterator = mDocuments.values().iterator();

		InMemoryDocument entry;
		while (iterator.hasNext()) {
			entry = iterator.next();

			if (entry.getField(fieldName) == null) {
				iterator.remove();
			}
		}

		return this;
	}

	@Override
	public Document[] find() {
		return mDocuments.values().toArray(new Document[mDocuments.size()]);
	}

	@Override
	public Document[] find(final int limit) {
		if (limit <= 0) {
			throw new IllegalArgumentException("The limit (`"+limit+"`) must be greater than `0`");
		}

		return find(limit, 0);
	}

	@Override
	public Document[] find(final int limit, final int offset) {
		if (limit <= 0) {
			throw new IllegalArgumentException("The limit is `"+limit+"` but it must be greater than `0`");
		}

		if (offset < 0) {
			throw new IllegalArgumentException("The offset is `"+offset+"` but it must be greater than or equal to `0`");
		}

		final int numResults = Math.min(mDocuments.size() - offset, limit);

		if (numResults <= 0) {
			return new Document[0];
		}

		final Document[] results = new Document[numResults];

		final Iterator<InMemoryDocument> iterator = mDocuments.values().iterator();

		// until the initial offset has been reached
		for (int i = 0; i < offset; i++) {
			// if more entries are available
			if (iterator.hasNext()) {
				// discard the next entry
				iterator.next();
			}
			// if another entry was expected but there was none
			else {
				// return an error
				throw new IllegalStateException("Expected `"+numResults+"` entries but there were only `"+i+"`");
			}
		}

		// until the number of elements to be returned has been reached
		for (int i = 0; i < numResults; i++) {
			// if more entries are available
			if (iterator.hasNext()) {
				// get the next entry and put it into the result list
				results[i] = iterator.next();
			}
			// if another entry was expected but there was none
			else {
				// return an error
				throw new IllegalStateException("Expected `"+numResults+"` entries but there were only `"+(offset + i)+"`");
			}
		}

		return results;
	}

	@Override
	public Document findOne() {
		// if there are entries available
		try {
			// return the first entry
			return mDocuments.values().iterator().next();
		}
		catch (NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * Forces the given object, no matter what type, to be a (primitive) number
	 *
	 * @param value the object to convert to a number
	 * @return the number coerced from the specified object
	 */
	private static double coerceNumber(final Object value) {
		if (value == null) {
			return 0;
		}

		Number number;

		if (value instanceof Byte) {
			number = (Byte) value;
		}
		else if (value instanceof Short) {
			number = (Short) value;
		}
		else if (value instanceof Integer) {
			number = (Integer) value;
		}
		else if (value instanceof Long) {
			number = (Long) value;
		}
		else if (value instanceof Float) {
			number = (Float) value;
		}
		else if (value instanceof Double) {
			number = (Double) value;
		}
		else if (value instanceof String) {
			try {
				number = Double.parseDouble((String) value);
			}
			catch (NumberFormatException e) {
				return 0;
			}
		}
		else {
			return 0;
		}

		return number.doubleValue();
	}

	@Override
	public String toString() {
		return mDocuments.toString();
	}

}
