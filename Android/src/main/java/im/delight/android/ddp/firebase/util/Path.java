package im.delight.android.ddp.firebase.util;

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

import java.lang.String;

public class Path {

	public static class Special {

		public static final String CONNECTED_STATE = "/.info/connected";
		public static final String SERVER_TIME_OFFSET = "/.info/serverTimeOffset";

	}

	/** The separator that is used between single nodes or segments in the path */
	public static final String SEPARATOR = "/";
	private final String mPath;

	public Path(final String value) {
		mPath = value;
	}

	public String getChild(final String childPath) {
		return mPath + SEPARATOR + childPath;
	}

	public String[] getSegments() {
		// split the path on occurrences of the separator (some characters have to be escaped with `Pattern.quote()`)
		return mPath.split(SEPARATOR);
	}

	public String getLastSegment() {
		// split the path into its single segments
		final String parts[] = mPath.split(SEPARATOR);
		// if we have a single segment only
		if (parts.length == 1) {
			// we are at the root and should return `null`
			return null;
		}
		// if we have more than one segment
		else if (parts.length > 1) {
			// we return the last segment only
			return parts[parts.length - 1];
		}
		else {
			throw new RuntimeException("Unexpected number of parts: "+parts.length);
		}
	}

	public boolean hasEventPropagation() {
		return isSubscribable() && getParent() != null;
	}

	public String getParent() {
		// get the position of the last segment separator
		final int lastSeparator = mPath.lastIndexOf(SEPARATOR);
		// if the separator was found
		if (lastSeparator > -1) {
			// return a path for the parent location
			return mPath.substring(0, lastSeparator);
		}
		// if the separator was not found
		else {
			// we are already at the root and should return `null`
			return null;
		}
	}

	public static String getRoot() {
		return SEPARATOR;
	}

	public boolean isSubscribable() {
		return !mPath.equals(Special.SERVER_TIME_OFFSET) && !mPath.equals(Special.CONNECTED_STATE);
	}

	@Override
	public String toString() {
		return mPath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mPath == null) ? 0 : mPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Path)) {
			return false;
		}
		Path other = (Path) obj;
		if (mPath == null) {
			if (other.mPath != null) {
				return false;
			}
		}
		else if (!mPath.equals(other.mPath)) {
			return false;
		}
		return true;
	}

}
