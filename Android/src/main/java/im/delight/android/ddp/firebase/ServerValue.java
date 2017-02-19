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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServerValue {

	public static final Map<String, String> TIMESTAMP = create("timestamp");
	private static final String KEY_IN_MAP = "_placeholder";

	private static Map<String, String> create(final String placeholder) {
		Map<String, String> out = new HashMap<String, String>();
		out.put(KEY_IN_MAP, placeholder);

		return Collections.unmodifiableMap(out);
	}

}
