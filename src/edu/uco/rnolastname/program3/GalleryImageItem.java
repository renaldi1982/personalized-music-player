package edu.uco.rnolastname.program3;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class GalleryImageItem extends ImageView {
	private Bitmap image;
	private String title;

	
	public GalleryImageItem(Context context) {
		super(context);		
	}

	public GalleryImageItem(Context context, AttributeSet attrs) {
		super(context,attrs);		
	}
	
	public GalleryImageItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);	
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); 
    }		
}
