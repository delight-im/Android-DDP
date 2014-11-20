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

import java.util.Map;

public class OnDisconnect {

	protected OnDisconnect() { }

	public void setValue(final Object value) {
		throw new RuntimeException("Not implemented yet"); // TODO implement
	}

	public void setValue(final Object value, final Object priority) {
		throw new RuntimeException("Not implemented yet"); // TODO implement
	}

	public void setValue(final Object value, final Firebase.CompletionListener listener) {
		throw new RuntimeException("Not implemented yet"); // TODO implement
	}

	public void setValue(final Object value, final Object priority, final Firebase.CompletionListener listener) {
		throw new RuntimeException("Not implemented yet"); // TODO implement
	}

	public void updateChildren(final Map<String, Object> children) {
		throw new RuntimeException("Not implemented yet"); // TODO implement
	}

	public void updateChildren(final Map<String, Object> children, final Firebase.CompletionListener listener) {
		throw new RuntimeException("Not implemented yet"); // TODO implement
	}

	public void removeValue() {
		throw new RuntimeException("Not implemented yet"); // TODO implement
	}

	public void removeValue(final Firebase.CompletionListener listener) {
		throw new RuntimeException("Not implemented yet"); // TODO implement
	}

	public void cancel() {
		throw new RuntimeException("Not implemented yet"); // TODO implement
	}

	public void cancel(final Firebase.CompletionListener listener) {
		throw new RuntimeException("Not implemented yet"); // TODO implement
	}

}
