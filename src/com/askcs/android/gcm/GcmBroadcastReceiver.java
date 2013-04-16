package com.askcs.android.gcm;

import android.content.Context;

import com.askcs.android.R;
import com.google.android.gcm.GCMBroadcastReceiver;

/**
 * Our own extension of the defaultGCM broadcast receiver. We need this
 * extension because the {@link GcmIntentService} is not located in the root
 * package, as is default.
 * 
 * @author Steven Mulder <steven@sense-os.nl>
 */
public class GcmBroadcastReceiver extends GCMBroadcastReceiver {

	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		String cname = context.getString(R.string.gcm_handler);
		if (cname == null || cname.length() == 0) {
			cname = GcmIntentService.class.getName();
		}
		return cname;
	}
}
