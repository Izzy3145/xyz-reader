package com.example.xyzreader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageView;

/**
 * Created by izzystannett on 08/05/2018.
 */

//class to determine 3*2 aspect ratio on thumbnail images

public class ThreeTwoImageView extends AppCompatImageView {

    //constructors
    public ThreeTwoImageView(Context context){
        super(context);
    }

    public ThreeTwoImageView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public ThreeTwoImageView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int threeTwoHeight = MeasureSpec.getSize(widthMeasureSpec) * 5/7;
        int threeTwoHeightSpec = MeasureSpec.makeMeasureSpec(threeTwoHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, threeTwoHeightSpec);
    }
}
