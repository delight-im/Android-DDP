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

/** A query can be executed to find a specified number of documents, after any number of requirements has been supplied before */
public interface Query {

	/**
	 * Adds a filter to the query requiring the given field to have exactly the specified value
	 *
	 * @param fieldName the name of the field to check
	 * @param fieldValue the value to check against
	 * @return this instance for chaining
	 */
	Query whereEqual(String fieldName, Object fieldValue);

	/**
	 * Adds a filter to the query requiring the given field to have a value other than the specified one
	 *
	 * @param fieldName the name of the field to check
	 * @param fieldValue the value to check against
	 * @return this instance for chaining
	 */
	Query whereNotEqual(String fieldName, Object fieldValue);

	/**
	 * Adds a filter to the query requiring the given field to have a value less than the specified value
	 *
	 * @param fieldName the name of the field to check
	 * @param fieldValue the value to check against
	 * @return this instance for chaining
	 */
	Query whereLessThan(String fieldName, double fieldValue);

	/**
	 * Adds a filter to the query requiring the given field to have a value less than or equal to the specified value
	 *
	 * @param fieldName the name of the field to check
	 * @param fieldValue the value to check against
	 * @return this instance for chaining
	 */
	Query whereLessThanOrEqual(String fieldName, double fieldValue);

	/**
	 * Adds a filter to the query requiring the given field to have a value greater than the specified value
	 *
	 * @param fieldName the name of the field to check
	 * @param fieldValue the value to check against
	 * @return this instance for chaining
	 */
	Query whereGreaterThan(String fieldName, double fieldValue);

	/**
	 * Adds a filter to the query requiring the given field to have a value greater than or equal to the specified value
	 *
	 * @param fieldName the name of the field to check
	 * @param fieldValue the value to check against
	 * @return this instance for chaining
	 */
	Query whereGreaterThanOrEqual(String fieldName, double fieldValue);

	/**
	 * Adds a filter to the query requiring the given field to have `null` as its value (or no value)
	 *
	 * @param fieldName the name of the field to check
	 * @return this instance for chaining
	 */
	Query whereNull(String fieldName);

	/**
	 * Adds a filter to the query requiring the given field to have a value other than `null`
	 *
	 * @param fieldName the name of the field to check
	 * @return this instance for chaining
	 */
	Query whereNotNull(String fieldName);

	/**
	 * Adds a filter to the query requiring the given field to have one of the specified values
	 *
	 * @param fieldName the name of the field to check
	 * @param fieldValues the values to check against
	 * @return this instance for chaining
	 */
	Query whereIn(String fieldName, Object[] fieldValues);

	/**
	 * Adds a filter to the query requiring the given field to have a value different from all the specified ones
	 *
	 * @param fieldName the name of the field to check
	 * @param fieldValues the values to check against
	 * @return this instance for chaining
	 */
	Query whereNotIn(String fieldName, Object[] fieldValues);

	/**
	 * Executes the query and returns all matching entries
	 *
	 * @return an array (never `null`) containing zero or more matches
	 */
	Document[] find();

	/**
	 * Executes the query and returns at most the specified number of matching entries
	 *
	 * @param limit the maximum number of entries to return
	 * @return an array (never `null`) containing zero or more matches
	 */
	Document[] find(int limit);

	/**
	 * Executes the query and returns the matching entries in the specified range
	 *
	 * @param limit the maximum number of entries to return
	 * @param offset the number of entries to skip at the beginning
	 * @return an array (never `null`) containing zero or more matches
	 */
	Document[] find(int limit, int offset);

	/**
	 * Executes the query and returns the first matching entry
	 *
	 * @return the first entry that matches the query or `null`
	 */
	Document findOne();

}
