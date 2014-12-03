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

import java.lang.String;

/** Implementations of this interface can be used to receive notifications about data changes in any child location */
public interface ChildEventListener extends EventListener {

	/**
	 * Method that will be triggered when a new child is added
	 *
	 * @param snapshot the new data snapshot for the child location
	 * @param previousChildName the sibling that is located before the new child or `null`
	 */
	public void onChildAdded(DataSnapshot snapshot, String previousChildName);

	/**
	 * Method that will be triggered when a child has changed
	 *
	 * @param snapshot the new data snapshot for the child location
	 * @param previousChildName the sibling that is located before the new child or `null`
	 */
	public void onChildChanged(DataSnapshot snapshot, String previousChildName);

	/**
	 * Method that will be triggered when a child has been removed
	 *
	 * @param snapshot the old data snapshot for the child location
	 */
	public void onChildRemoved(DataSnapshot snapshot);

	/**
	 * Method that will be triggered when a child has been moved due to an updated priority value
	 *
	 * @param snapshot the data snapshot for the child location
	 * @param previousChildName the sibling that is located before the new child or `null`
	 */
	public void onChildMoved(DataSnapshot snapshot, String previousChildName);

	/**
	 * Method that will be triggered in case of an error with this listener
	 *
	 * @param error the error that occurred
	 */
	public void onCancelled(FirebaseError error);

}
