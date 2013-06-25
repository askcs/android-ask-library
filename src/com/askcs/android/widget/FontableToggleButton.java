package com.askcs.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.askcs.android.R;

public class FontableToggleButton extends ToggleButton {
  
  public FontableToggleButton( Context context ) {
    super( context );
  }
  
  public FontableToggleButton( Context context, AttributeSet attrs ) {
    super( context, attrs );
    UiUtil.setCustomFont( this, context, attrs,
        R.styleable.com_askcs_android_widget_FontableToggleButton,
        R.styleable.com_askcs_android_widget_FontableToggleButton_font );
  }
  
  public FontableToggleButton( Context context, AttributeSet attrs, int defStyle ) {
    super( context, attrs, defStyle );
    UiUtil.setCustomFont( this, context, attrs,
        R.styleable.com_askcs_android_widget_FontableToggleButton,
        R.styleable.com_askcs_android_widget_FontableToggleButton_font );
  }
}
