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

import im.delight.android.ddp.firebase.FirebaseError;

/** Implementations of this interface can be used to receive notifications about data changes at a certain location */
public interface ValueEventListener extends EventListener {

	/**
	 * Method that will be triggered with a data snapshot whenever data is added or changes
	 *
	 * @param snapshot the current data
	 */
	public void onDataChange(DataSnapshot snapshot);

	/**
	 * Method that will be triggered in case of an error with this listener
	 *
	 * @param error the error that occurred
	 */
	public void onCancelled(FirebaseError error);

}
