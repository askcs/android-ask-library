package com.askcs.android.appservices;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.askcs.android.data.AppServiceSqlStorage;

public class RestCacheItem implements Parcelable {

    private class Fields implements BaseColumns {
        public static final String ACTION = AppServiceSqlStorage.C_RESTCACHE_ACTION;
        public static final String URL = AppServiceSqlStorage.C_RESTCACHE_URL;
        public static final String CONTENT = AppServiceSqlStorage.C_RESTCACHE_CONTENT;
    }

    public static final Parcelable.Creator<RestCacheItem> CREATOR = new Parcelable.Creator<RestCacheItem>() {

        public RestCacheItem createFromParcel(Parcel in) {
            return new RestCacheItem(in);
        }

        public RestCacheItem[] newArray(int size) {
            return new RestCacheItem[size];
        }
    };

    private int mId;
    private String mAction;
    private String mUrl;
    private String mContent;


    public RestCacheItem() {
        super();
    }

    public RestCacheItem(JSONObject json) throws JSONException {
        setAction(json.getString(Fields.ACTION));
        setUrl(json.getString(Fields.URL));
        setContent(json.getString(Fields.CONTENT));


    }

    private RestCacheItem(Parcel in) {
        setId(in.readInt());
        setAction(in.readString());
        setUrl(in.readString());
        setContent(in.readString()); 
    }

    public RestCacheItem(String action, String url, String content) {

        setAction(action);
        setUrl(url);
        setContent(content);
    }
    
    public RestCacheItem(int id, String action, String url, String content) {
        setId(id);
        setAction(action);
        setUrl(url);
        setContent(content);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    

    public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public String getAction() {
		return mAction;
	}

	public void setAction(String action) {
		this.mAction = action;
	}

	public String getUrl() {
		return mUrl;
	}

	public void setUrl(String url) {
		this.mUrl = url;
	}

	public String getContent() {
		return mContent;
	}

	public void setContent(String content) {
		this.mContent = content;
	}

	public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(Fields.ACTION, getAction());
        json.put(Fields.URL, getUrl());
        json.put(Fields.CONTENT, getContent());

        return json;
    }
    
    public ContentValues toContentValues(){
    	
    	ContentValues contentValues = new ContentValues();
    	if(mId!=0){ contentValues.put(AppServiceSqlStorage.C_RESTCACHE_ID, mId);};
    	contentValues.put(AppServiceSqlStorage.C_RESTCACHE_ACTION, mAction);
    	contentValues.put(AppServiceSqlStorage.C_RESTCACHE_URL, mUrl);
    	contentValues.put(AppServiceSqlStorage.C_RESTCACHE_CONTENT, mContent);

    	return contentValues;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
    	out.writeInt(getId());
        out.writeString(getAction());
        out.writeString(getUrl());
        out.writeString(getContent());
    }
}
