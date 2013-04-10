package com.askcs.android.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.askcs.android.R;

public class FontableTextView extends TextView {

	public FontableTextView( Context context ) {
		super( context );
	}

	public FontableTextView( Context context, AttributeSet attrs ) {
		super( context, attrs );
		UiUtil.setCustomFont(
				this,
				context,
				attrs,
				R.styleable.com_askcs_android_widget_FontableTextView,
				R.styleable.com_askcs_android_widget_FontableTextView_font );
	}

	public FontableTextView( Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs, defStyle );
		UiUtil.setCustomFont(
				this,
				context,
				attrs,
				R.styleable.com_askcs_android_widget_FontableTextView,
				R.styleable.com_askcs_android_widget_FontableTextView_font );
	}
}
