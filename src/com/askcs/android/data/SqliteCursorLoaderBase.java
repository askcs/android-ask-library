package com.askcs.android.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

public abstract class SqliteCursorLoaderBase extends AsyncTaskLoader<Cursor> {
  
  @SuppressWarnings( "unused" )
  private static final String TAG = "SqliteCursorLoaderBase";
  
  public SqliteCursorLoaderBase( Context context ) {
    super( context );
  }
  
  Cursor mCursor;
  
  /**
   * Called when there is new data to deliver to the client. The super class
   * will take care of delivering it; the implementation here just adds a little
   * more logic.
   */
  @Override
  public void deliverResult( Cursor cursor ) {
    if ( isReset() ) {
      // An async query came in while the loader is stopped. We
      // don't need the result.
      if ( cursor != null ) {
        onReleaseResources( cursor );
      }
    }
    
    Cursor oldCursor = mCursor;
    mCursor = cursor;
    
    if ( isStarted() ) {
      // If the Loader is currently started, we can immediately
      // deliver its results.
      super.deliverResult( cursor );
    }
    
    if ( oldCursor != null && oldCursor != cursor && !oldCursor.isClosed() ) {
      oldCursor.close();
    }
    
  }
  
  /**
   * Handles a request to start the Loader.
   */
  @Override
  protected void onStartLoading() {
    if ( mCursor != null ) {
      // If we currently have a result available, deliver it
      // immediately.
      deliverResult( mCursor );
    }
    
    // Has something interesting in the configuration changed since we
    // last built the app list?
    if ( takeContentChanged() || mCursor == null ) {
      forceLoad();
    }
  }
  
  /**
   * Handles a request to stop the Loader.
   */
  @Override
  protected void onStopLoading() {
    // Attempt to cancel the current load task if possible.
    cancelLoad();
  }
  
  /**
   * Handles a request to cancel a load.
   */
  @Override
  public void onCanceled( Cursor cursor ) {
    super.onCanceled( cursor );
    
    // At this point we can release the resources associated with 'apps'
    // if needed.
    onReleaseResources( cursor );
  }
  
  /**
   * Handles a request to completely reset the Loader.
   */
  @Override
  protected void onReset() {
    super.onReset();
    
    // Ensure the loader is stopped
    onStopLoading();
    
    // At this point we can release the resources associated with 'notes'
    // if needed.
    if ( mCursor != null ) {
      onReleaseResources( mCursor );
      mCursor = null;
    }
  }
  
  protected void onReleaseResources( Cursor cursor ) {
    if ( cursor != null && !cursor.isClosed() ) {
      cursor.close();
    }
    
  }
}
