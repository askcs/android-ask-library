package com.askcs.android.appservices;

/**
 * Class for synchronizing objects that are presented by the ASK REST API.
 * 
 * @author Ian Zwanink <izwanink@ask-cs.com>
 */
public interface RestReceiver {

    /**
     * Requests the objects from the ASK API and stores them locally
     * 
     * @return true if call completed successfully
     */
    public abstract boolean get();

    /**
     * Requests a specific objects from the ASK API and stores them locally
     * 
     * @param uuid
     * @return true if call completed successfully
     */
    public abstract boolean get(String uuid);
}
