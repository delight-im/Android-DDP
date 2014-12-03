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

import im.delight.android.ddp.firebase.ChildEventListener;
import im.delight.android.ddp.firebase.DataSnapshot;
import im.delight.android.ddp.firebase.FirebaseError;
import im.delight.android.ddp.firebase.ValueEventListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ListenerPool {

	public static class ValueEvent extends LinkedList<ValueEventListener> implements ValueEventListener {

		private static final long serialVersionUID = -7794932104369317518L;
		private static final Map<String, ValueEvent> mInstances = new HashMap<String, ValueEvent>();
		private Path mPath;

		private ValueEvent(String path) {
			super();
			mPath = new Path(path);
		}

		public static ValueEvent getInstance(final String path) {
			if (!mInstances.containsKey(path)) {
				mInstances.put(path, new ValueEvent(path));
			}
			return mInstances.get(path);
		}

		@Override
		public void onDataChange(DataSnapshot snapshot) {
			// loop over all listeners in this collection (i.e. for the current path)
			for (ValueEventListener listener : this) {
				// execute the specified callback for all the single listeners
				if (listener != null) {
					listener.onDataChange(snapshot);
				}
			}

			// propagate the event to parent nodes
			if (mPath.hasEventPropagation()) {
				ValueEvent.getInstance(mPath.getParent()).onDataChange(snapshot.getParent());
			}
		}

		@Override
		public void onCancelled(FirebaseError error) {
			// loop over all listeners in this collection (i.e. for the current path)
			for (ValueEventListener listener : this) {
				// execute the specified callback for all the single listeners
				if (listener != null) {
					listener.onCancelled(error);
				}
			}
		}

	}

	public static class ChildEvent extends LinkedList<ChildEventListener> implements ChildEventListener {

		private static final long serialVersionUID = -5536271813072935889L;
		private static final Map<String, ChildEvent> mInstances = new HashMap<String, ChildEvent>();
		private Path mPath;

		private ChildEvent(String path) {
			super();
			mPath = new Path(path);
		}

		public static ChildEvent getInstance(final String path) {
			if (!mInstances.containsKey(path)) {
				mInstances.put(path, new ChildEvent(path));
			}
			return mInstances.get(path);
		}

		@Override
		public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
			// loop over all listeners in this collection (i.e. for the current path)
			for (ChildEventListener listener : this) {
				// execute the specified callback for all the single listeners
				if (listener != null) {
					listener.onChildAdded(snapshot, previousChildName);
				}
			}

			// propagate the event to parent nodes
			if (mPath.hasEventPropagation()) {
				ChildEvent.getInstance(mPath.getParent()).onChildAdded(snapshot.getParent(), null);
			}
		}

		@Override
		public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
			// loop over all listeners in this collection (i.e. for the current path)
			for (ChildEventListener listener : this) {
				// execute the specified callback for all the single listeners
				if (listener != null) {
					listener.onChildChanged(snapshot, previousChildName);
				}
			}

			// propagate the event to parent nodes
			if (mPath.hasEventPropagation()) {
				ChildEvent.getInstance(mPath.getParent()).onChildChanged(snapshot.getParent(), null);
			}
		}

		@Override
		public void onChildRemoved(DataSnapshot snapshot) {
			// loop over all listeners in this collection (i.e. for the current path)
			for (ChildEventListener listener : this) {
				// execute the specified callback for all the single listeners
				if (listener != null) {
					listener.onChildRemoved(snapshot);
				}
			}

			// propagate the event to parent nodes
			if (mPath.hasEventPropagation()) {
				ChildEvent.getInstance(mPath.getParent()).onChildRemoved(snapshot.getParent());
			}
		}

		@Override
		public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
			// loop over all listeners in this collection (i.e. for the current path)
			for (ChildEventListener listener : this) {
				// execute the specified callback for all the single listeners
				if (listener != null) {
					listener.onChildMoved(snapshot, previousChildName);
				}
			}

			// propagate the event to parent nodes
			if (mPath.hasEventPropagation()) {
				ChildEvent.getInstance(mPath.getParent()).onChildMoved(snapshot.getParent(), null);
			}
		}

		@Override
		public void onCancelled(FirebaseError error) {
			// loop over all listeners in this collection (i.e. for the current path)
			for (ChildEventListener listener : this) {
				// execute the specified callback for all the single listeners
				if (listener != null) {
					listener.onCancelled(error);
				}
			}
		}

	}

}
