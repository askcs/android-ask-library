package com.askcs.android.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.askcs.android.data.AppServiceSqlStorage;

public class Message implements Parcelable {

    private class Fields implements BaseColumns {
        public static final String UUID = "uuid";
        public static final String SUBJECT = "subject";
        public static final String QUESTION_TEXT = "question_text";
        public static final String TYPE = "comment";
        public static final String STATE = "state";
        public static final String CREATIONTIME = "creationTime";
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {

        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    private int mId;
    private String mUuid;
    private String mSubject;
    private String mQuestionText;
    private long mCreationTime;
    private String mState;
    private String mType;

    public Message() {
        super();
    }

    public Message(JSONObject json) throws JSONException {
        setUuid(json.getString(Fields.UUID));
        setSubject(json.getString(Fields.SUBJECT));
        setQuestionText(json.getString(Fields.QUESTION_TEXT));
        setType(json.getString(Fields.TYPE));
        setCreationTime(json.getLong(Fields.CREATIONTIME));
        setState(json.getString(Fields.STATE));
    }

    private Message(Parcel in) {
        setId(in.readInt());
        setUuid(in.readString());
        setSubject(in.readString());
        setQuestionText(in.readString()); 
        setType(in.readString());
        setState(in.readString());        
        setCreationTime(in.readLong());
    }

    public Message(String uuid, String subject, String questionText, String type, long creationTime, String state) {
        setUuid(uuid);
        setSubject(subject);
        setQuestionText(questionText);
        setCreationTime(creationTime);
        setState(state);
    }
    
    public Message(int id, String uuid, String subject, String questionText, String type, long creationTime, String state) {
        setId(id);
    	setUuid(uuid);
        setSubject(subject);
        setQuestionText(questionText);
        setType(type);
        setCreationTime(creationTime);
        setState(state);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public String getQuestionText() {
        return mQuestionText;
    }

    public long getCreationTime() {
        return mCreationTime;
    }

    public String getSubject() {
        return mSubject;
    }
    
    public int getId() {
        return mId;
    }

    public String getUuid() {
        return mUuid;
    }

    public String getState() {
        return mState;
    }
    
    public String getType() {
        return mType;
    }

    public void setQuestionText(String questionText) {
        this.mQuestionText = questionText;
    }

    public void setCreationTime(long creationTime) {
        this.mCreationTime = creationTime;
    }

    public void setSubject(String subject) {
        this.mSubject = subject;
    }

    public void setState(String state) {
        this.mState = state;
    }

    public void setUuid(String uuid) {
        this.mUuid = uuid;
    }
    
    public void setId(int id) {
        this.mId = id;
    }
    
    public void setType(String type) {
        this.mType = type;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(Fields.UUID, getUuid());
        json.put(Fields.SUBJECT, getSubject());
        json.put(Fields.QUESTION_TEXT, getQuestionText());
        json.put(Fields.TYPE, getType());
        json.put(Fields.STATE, getState());
        json.put(Fields.CREATIONTIME, getCreationTime());
        
        return json;
    }
    
    public ContentValues toContentValues(){
    	
    	ContentValues contentValues = new ContentValues();
    	if(mId!=0){ contentValues.put(AppServiceSqlStorage.C_MESSAGE_ID, mId);};
    	contentValues.put(AppServiceSqlStorage.C_MESSAGE_UUID, mUuid);
    	contentValues.put(AppServiceSqlStorage.C_MESSAGE_SUBJECT, mSubject);
    	contentValues.put(AppServiceSqlStorage.C_MESSAGE_QUESTION_TEXT, mQuestionText);
    	contentValues.put(AppServiceSqlStorage.C_MESSAGE_TYPE, mType);
    	contentValues.put(AppServiceSqlStorage.C_MESSAGE_STATE, mState);
    	contentValues.put(AppServiceSqlStorage.C_MESSAGE_CREATIONTIME, mCreationTime);
    	return contentValues;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
    	out.writeInt(getId());
        out.writeString(getUuid());
        out.writeString(getSubject());
        out.writeString(getQuestionText());
        out.writeString(getType());
        out.writeString(getState());
        out.writeLong(getCreationTime());

    }
}
