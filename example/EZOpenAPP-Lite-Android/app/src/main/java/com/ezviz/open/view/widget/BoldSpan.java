package com.ezviz.open.view.widget;

import android.text.TextPaint;
import android.text.style.StyleSpan;

public class BoldSpan extends StyleSpan {

    public BoldSpan(int style) {
        super(style);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setFakeBoldText(true);
        super.updateDrawState(ds);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        paint.setFakeBoldText(true);
        super.updateMeasureState(paint);
    }

}
