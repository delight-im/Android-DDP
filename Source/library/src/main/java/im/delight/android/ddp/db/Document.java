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

/** A document has an ID and contains any number of fields, identified by their names */
public interface Document {

	/**
	 * Returns the ID of the document
	 *
	 * @return the ID
	 */
	String getId();

	/**
	 * Returns the field with the specified name from the document
	 *
	 * @param name the name of the field to return
	 * @return the field data of any type (e.g. `String`, `Integer`, `Long`, `Boolean`) or `null`
	 */
	Object getField(String name);

	/**
	 * Lists all fields from the document by returning their names
	 *
	 * @return an array containing the names of all fields
	 */
	String[] getFieldNames();

	/**
	 * Returns the number of fields in the document
	 *
	 * @return the count
	 */
	int count();

}
