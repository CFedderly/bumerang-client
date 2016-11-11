package com.seng480b.bumerang;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class SquareImage extends ImageView {

    public SquareImage(final Context context){
        super(context);
    }

    public SquareImage(final Context context,final AttributeSet attributes) {
        super(context,attributes);
    }
    public SquareImage(final Context context, final AttributeSet attributes, final int defStyle){
        super(context,attributes,defStyle);
    }

    @Override
    protected void onMeasure(int width, int height){
        super.onMeasure(width,height);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (measuredWidth > measuredHeight){
            setMeasuredDimension(measuredHeight,measuredHeight);
        }else{
            setMeasuredDimension(measuredWidth,measuredWidth);
        }

    }


}
