package com.askcs.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.askcs.android.R;

public class FontableButton extends Button {

	public FontableButton( Context context ) {
		super( context );
	}

	public FontableButton( Context context, AttributeSet attrs ) {
		super( context, attrs );
		UiUtil.setCustomFont( this, context, attrs,
				R.styleable.com_askcs_android_widget_FontableButton,
				R.styleable.com_askcs_android_widget_FontableButton_font );
	}

	public FontableButton( Context context, AttributeSet attrs, int defStyle ) {
		super( context, attrs, defStyle );
		UiUtil.setCustomFont( this, context, attrs,
				R.styleable.com_askcs_android_widget_FontableButton,
				R.styleable.com_askcs_android_widget_FontableButton_font );
	}
}
