package com.askcs.android.data;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class SqlStorageBase extends SQLiteOpenHelper {
  
  public SqlStorageBase( Context context, String database, int version ) {
    super( context, database, null, version );
    
  }
  
  public void close() {
    this.close();
  }
  
  public void insert( ContentValues values, String table ) {
    
    SQLiteDatabase db = this.getWritableDatabase();
    
    db.insertWithOnConflict( table, null, values,
        SQLiteDatabase.CONFLICT_REPLACE );
    
  }
  
  public void deleteAll( String table ) {
    
    SQLiteDatabase db = this.getWritableDatabase();
    
    db.delete( table, null, null );
  }
  
  public Cursor selectData( String table, String selection, String orderBy,
      String limit ) {
    SQLiteDatabase db = this.getReadableDatabase();
    
    return db.query( table, null, selection, null, null, null, orderBy, limit );
    
  }
  
  public Cursor selectData( String rawQuery ) {
    SQLiteDatabase db = this.getReadableDatabase();
    
    return db.rawQuery( rawQuery, null );
  }
  
  public void update( ContentValues values, String table, String column,
      String id ) {
    SQLiteDatabase db = this.getWritableDatabase();
    
    db.update( table, values, column + "=" + id, null );
  }
  
  public void updateById( ContentValues values, String table, int id ) {
    SQLiteDatabase db = this.getWritableDatabase();
    
    db.update( table, values, "_id=" + id, null );
  }
  
  public boolean batchImport( List<ContentValues> list, String table ) {
    SQLiteDatabase db = this.getWritableDatabase();
    // Begin the transaction
    db.beginTransaction();
    try {
      for ( ContentValues contentValues : list ) {
        db.insertWithOnConflict( table, null, contentValues,
            SQLiteDatabase.CONFLICT_REPLACE );
      }
      
      // Transaction is successful and all the records have been inserted
      db.setTransactionSuccessful();
    } catch ( Exception e ) {
      return false;
    } finally {
      // End the transaction
      db.endTransaction();
    }
    return true;
  }
  
  public boolean batchDeleteOnId( List<Integer> idList, String table ) {
    SQLiteDatabase db = this.getWritableDatabase();
    // Begin the transaction
    db.beginTransaction();
    try {
      for ( int id : idList ) {
        db.delete( table, "_id =" + id, null );
      }
      
      // Transaction is successful and all the records have been inserted
      db.setTransactionSuccessful();
    } catch ( Exception e ) {
      return false;
    } finally {
      // End the transaction
      db.endTransaction();
    }
    return true;
  }
  
  public boolean batchDeleteOnUuid( List<String> uuidList, String table ) {
    SQLiteDatabase db = this.getWritableDatabase();
    // Begin the transaction
    db.beginTransaction();
    try {
      for ( String uuid : uuidList ) {
        db.delete( table, "uuid =" + uuid, null );
      }
      
      // Transaction is successful and all the records have been inserted
      db.setTransactionSuccessful();
    } catch ( Exception e ) {
      return false;
    } finally {
      // End the transaction
      db.endTransaction();
    }
    return true;
  }
  
}