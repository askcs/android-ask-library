package com.askcs.android.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class TypefaceSpan extends MetricAffectingSpan {

	Typeface mTypeface;

	public TypefaceSpan( Context context, String name ) {
		mTypeface = UiUtil.getFont( context, name );
	}

	@Override
	public void updateMeasureState( TextPaint tp ) {
		tp.setTypeface( mTypeface );
	}

	@Override
	public void updateDrawState( TextPaint tp ) {
		tp.setTypeface( mTypeface );
	}

}
