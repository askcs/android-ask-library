package com.askcs.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.askcs.android.R;

public class FontableEditText extends EditText {
  
  public FontableEditText( Context context ) {
    super( context );
  }
  
  public FontableEditText( Context context, AttributeSet attrs ) {
    super( context, attrs );
    UiUtil.setCustomFont( this, context, attrs,
        R.styleable.com_askcs_android_widget_FontableEditText,
        R.styleable.com_askcs_android_widget_FontableEditText_font );
  }
  
  public FontableEditText( Context context, AttributeSet attrs, int defStyle ) {
    super( context, attrs, defStyle );
    UiUtil.setCustomFont( this, context, attrs,
        R.styleable.com_askcs_android_widget_FontableEditText,
        R.styleable.com_askcs_android_widget_FontableEditText_font );
  }
}
