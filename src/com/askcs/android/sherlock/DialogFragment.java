package com.askcs.android.sherlock;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class DialogFragment extends SherlockDialogFragment {
	
	
	static public enum Dismiss {
		POSITIVE, NEUTRAL, NEGATIVE
	};
	
	
	static public interface DialogListener {
		
		public void onPositive( DialogFragment dialog, Map<String, ? extends Object> data );

		public void onNegative( DialogFragment dialog, Map<String, ? extends Object> data );

		public void onNeutral( DialogFragment dialog, Map<String, ? extends Object> data );
	}
	
	
	
	protected Set<DialogListener> mListeners = new HashSet<DialogListener>();
	
	public void addListener( DialogListener listener ) {
		mListeners.add( listener );
	}
	
	public void removeListener( DialogListener listener ) {
		mListeners.remove( listener );
	}
	
	public void removeAllListeners() {
		mListeners.clear();
	}
	
	public void callListenersNegative() {
		callListenersNegative( null );
	}
	
	public void callListenersNegative( Map<String, ? extends Object> data ) {
		callListeners( Dismiss.NEGATIVE, data );
	}
	
	public void callListenersNeutral() {
		callListenersNeutral( null );
	}
	
	public void callListenersNeutral( Map<String, ? extends Object> data ) {
		callListeners( Dismiss.NEUTRAL, data );
	}
	
	public void callListenersPositive() {
		callListenersPositive( null );
	}
	
	public void callListenersPositive( Map<String, ? extends Object> data ) {
		callListeners( Dismiss.POSITIVE, data );
	}
	
	public void callListeners( Dismiss kind ) {
		callListeners( kind, null );
	}
	
	public void callListeners( Dismiss kind, Map<String, ? extends Object> data ) {
		switch( kind ) {
			case NEGATIVE :
				for ( DialogListener listener : mListeners ) {
					listener.onNegative( this, data );
				}
				break;
			case NEUTRAL :
				for ( DialogListener listener : mListeners ) {
					listener.onNeutral( this, data );
				}
				break;
			case POSITIVE :
				for ( DialogListener listener : mListeners ) {
					listener.onPositive( this, data );
				}
				break;
			default :
				throw new RuntimeException( "Unhandled case " + kind );
		}
		
	}


}
