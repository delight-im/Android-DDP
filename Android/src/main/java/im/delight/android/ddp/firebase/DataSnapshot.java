package im.delight.android.ddp.firebase;

/**
 * Copyright 2014 www.delight.im <info@delight.im>
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

import org.codehaus.jackson.type.TypeReference;
import java.io.IOException;
import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.firebase.util.Path;
import im.delight.android.ddp.MongoDb;
import java.util.Iterator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

/** Wrapper for data at a certain location */
public class DataSnapshot {

	private static final DataSnapshot mRoot = new DataSnapshot(null, null);
	private static final ObjectMapper mObjectMapper = new ObjectMapper();
	private final DataSnapshot mParent;
	private final String mKey;
	private Object mValue;
	private Object mPriority;
	private Map<String, DataSnapshot> mChildren;

	public static DataSnapshot fromPath(final String path) {
		// split the path into its single segments
		final String parts[] = new Path(path).getSegments();
		// create a variable for the instance to be returned and start with the root
		DataSnapshot current = mRoot;

		for (int i = 1; i < parts.length; i++) {
			current = current.child(parts[i], true);
		}

		return current;
	}

	private static JsonNode parseJson(final String json) {
		try {
			return mObjectMapper.readTree(json);
		}
		catch (Exception e) {
			throw new RuntimeException("Could not parse JSON value: "+json);
		}
	}

	protected void setValue(final int value) {
		mValue = value;
	}

	protected void setValue(final long value) {
		mValue = value;
	}

	protected void setValue(final float value) {
		mValue = value;
	}

	protected void setValue(final double value) {
		mValue = value;
	}

	protected void setValue(final boolean value) {
		mValue = value;
	}

	protected void setValue(final String value) {
		mValue = value;
	}

	protected void setValue(final Map<String, Object> value) {
		mValue = value;
	}

	protected void setValue(final Object value) {
		mValue = value;
	}

	protected void setFieldsFromJson(final String json) {
		JsonNode node = parseJson(json);
		final Iterator<Map.Entry<String, JsonNode>> iterator = node.getFields();

		Map.Entry<String, JsonNode> entry;
		String key;
		JsonNode value;
		while (iterator.hasNext()) {
			entry = iterator.next();
			key = entry.getKey();
			value = entry.getValue();

			if (key.equals(MongoDb.Field.ID)) {
				continue;
			}
			else if (key.equals(MongoDb.Field.VALUE)) {
				mValue = parseValue(value);
			}
			else if (key.equals(MongoDb.Field.PRIORITY)) {
				if (value.isNull()) {
					mPriority = null;
				}
				else {
					try {
						mPriority = value.getDoubleValue();
					}
					catch (Exception e1) {
						try {
							mPriority = value.getTextValue();
						}
						catch (Exception e2) {
							try {
								mPriority = value.getBooleanValue();
							}
							catch (Exception e3) {
								throw new RuntimeException("Unexpected type of priority: "+value.toString());
							}
						}
					}
				}
			}
			else {
				child(key, true).setFieldsFromJson(value.toString());
			}
		}

		if (mRoot != null) {
			Meteor.log(mRoot.toString());
		}
	}

	protected static Object parseValue(String json) {
		return parseValue(parseJson(json));
	}

	protected static Object parseValue(final JsonNode value) {
		if (value == null || value.isMissingNode() || value.isNull()) {
			return null;
		}

		if (value.isBigDecimal()) {
			return value.getDecimalValue();
		}
		if (value.isBigInteger()) {
			return value.getBigIntegerValue();
		}
		if (value.isBoolean()) {
			return value.getBooleanValue();
		}
		if (value.isDouble()) {
			return value.getDoubleValue();
		}
		if (value.isInt()) {
			return value.getIntValue();
		}
		if (value.isLong()) {
			return value.getLongValue();
		}
		if (value.isTextual()) {
			return value.getTextValue();
		}

		return value;
	}

	protected void removeFieldsFromJson(final String json) {
		JsonNode node = parseJson(json);
		final Iterator<JsonNode> iterator = node.getElements();

		String key;
		while (iterator.hasNext()) {
			key = iterator.next().getTextValue();

			if (key.equals(MongoDb.Field.ID)) {
				continue;
			}
			else if (key.equals(MongoDb.Field.VALUE)) {
				mValue = null;
			}
			else if (key.equals(MongoDb.Field.PRIORITY)) {
				mPriority = null;
			}
			else {
				mChildren.remove(iterator.next());
			}
		}
	}

	protected DataSnapshot(final DataSnapshot parent, final String key) {
		mParent = parent;
		mKey = key;
		mValue = null;
		mPriority = null;
		mChildren = new HashMap<String, DataSnapshot>();
	}

	protected void remove() {
		mValue = null;
		mPriority = null;
		mChildren.clear();

		if (mParent != null) {
			mParent.mChildren.remove(getKey());
		}

		if (mRoot != null) {
			Meteor.log(mRoot.toString());
		}
	}

	public DataSnapshot getParent() {
		return mParent;
	}

	private DataSnapshot child(final String path, final boolean forceCreation) {
		if (hasChild(path)) {
			return mChildren.get(path);
		}
		else {
			if (forceCreation) {
				DataSnapshot newChild = new DataSnapshot(this, path);
				mChildren.put(path, newChild);
				return newChild;
			}
			else {
				return null;
			}
		}
	}

	/**
	 * Returns a data snapshot for the location at the given relative path
	 *
	 * @param path the path to a child
	 * @return the data snapshot
	 */
	public DataSnapshot child(final String path) {
		return child(path, false);
	}

	/**
	 * Returns whether this data snapshot has a child at the specified location
	 *
	 * @param path the location at which to check for a child
	 * @return whether a child exists at the location or not
	 */
	public boolean hasChild(final String path) {
		return mChildren.containsKey(path);
	}

	/**
	 * Returns whether this data snapshot has any child nodes or not
	 *
	 * @return whether any child nodes exist or not
	 */
	public boolean hasChildren() {
		return getChildrenCount() > 0;
	}

	/**
	 * Returns the data from this snapshot as a Java primitive or object
	 *
	 * @return the data as a Java primitive or object
	 */
	public Object getValue() {
		if (mChildren == null || mChildren.size() == 0) {
			return mValue;
		}
		else {
			final JsonNode jsonNode = parseJson(toJson());
			try {
				return mObjectMapper.readValue(jsonNode, new TypeReference<Map<String, Object>>() {});
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Returns the data from this snapshot as an instance of the specified class (JSON will be deserialized with the Jackson library)
	 *
	 * @param valueType the type to return which is inferred from the specified class
	 * @return the data as an instance of the specified class
	 */
	public <T> T getValue(Class<T> valueType) {
		final JsonNode jsonNode = parseJson(toJson());
		try {
			return mObjectMapper.readValue(jsonNode, valueType);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String toJson() {
		return toJson(0);
	}

	private String toJson(final int depth) {
		try {
			final StringBuilder out = new StringBuilder();

			if (depth > 0) {
				out.append(mObjectMapper.writeValueAsString(mKey));
				out.append(":");
			}

			if (mChildren != null && mChildren.size() > 0) {
				out.append("{");
				int counter = 0;
				for (Map.Entry<String, DataSnapshot> entry : mChildren.entrySet()) {
					if (counter > 0) {
						out.append(",");
					}
					out.append(entry.getValue().toJson(depth + 1));
					counter++;
				}
				out.append("}");
			}
			else {
				out.append(mObjectMapper.writeValueAsString(mValue));
			}

			return out.toString();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the number of children that this data snapshot has
	 *
	 * @return the number of children
	 */
	public long getChildrenCount() {
		return mChildren.size();
	}

	/**
	 * Returns a reference to the location of this data snapshot's source
	 *
	 * @return a reference to the source location
	 */
	@SuppressWarnings("static-method")
	public Firebase getRef() {
		throw new RuntimeException("Not implemented yet"); // TODO implement
	}

	/**
	 * Returns the name of the source reference of this data snapshot's source
	 *
	 * @return the name of the source reference
	 */
	public String getKey() {
		return mKey;
	}

	/**
	 * Returns a list of all child snapshots that can be iterated over
	 *
	 * @return the list of this snapshot's children
	 */
	public Iterable<DataSnapshot> getChildren() {
		return mChildren.values();
	}

	/**
	 * Returns the priority value for this snapshot
	 *
	 * @return the priority as a `String`, `Double` or `null`
	 */
	public Object getPriority() {
		return mPriority;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DataSnapshot [mKey=");
		builder.append(mKey);
		builder.append(", mValue=");
		builder.append(mValue);
		builder.append(", mPriority=");
		builder.append(mPriority);
		builder.append(", mChildren=");
		builder.append(mChildren);
		builder.append("]");
		return builder.toString();
	}

}
