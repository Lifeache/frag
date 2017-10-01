package com.msx.frag;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
//<httpsrc>
//		<page path="www.baidu.com">
//		<select value="div[src$=.jpg]"/>
//		<attr value="src">
//		</page>
//		<page>
//
//		</page>
//		</httpsrc>
public class ZoomImageView extends ImageView implements OnScaleGestureListener,
OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener

{
	private static final String TAG = ZoomImageView.class.getSimpleName();

	public static final float SCALE_MAX = 4.0f;
	/**
	 * 初始化时的缩放比例，如果图片宽或高大于屏幕，此值将小于0
	 */
	private float initScale = 1.0f;

	/**
	 * 用于存放矩阵的9个值
	 */
	private final float[] matrixValues = new float[9];

	private boolean once = true;

	/**
	 * 缩放的手势检测
	 */
	private ScaleGestureDetector mScaleGestureDetector = null;

	private final  Matrix mScaleMatrix = new Matrix();

	private int lastPointerCount = -1;

	private boolean isCanDrag;

	private float mLastX = -1.0f;

	private float mLastY = -1.0f;

	private boolean isCheckTopAndBottom;

	private boolean isCheckLeftAndRight;

	private double mTouchSlop;

	private GestureDetector mGestureDetector;

    private GestureActionListener gestureActionListener;
	
	public ZoomImageView(Context context)
	{
		this(context, null);
	}

	public ZoomImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		super.setScaleType(ScaleType.MATRIX);
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		this.setOnTouchListener(this);
	}

	public void setGestureDetector(GestureDetector gestureDetector)
	{
		this.mGestureDetector = gestureDetector;
	}

	public void setGestureActionListener(final GestureActionListener gestureActionListener){
        this.gestureActionListener = gestureActionListener;
        mGestureDetector = new GestureDetector(getContext(),
			new GestureDetector.SimpleOnGestureListener()
			{
				@Override
				public boolean onDoubleTap(MotionEvent e)
				{
                    gestureActionListener.onDoubleTap(e);
					return true;
				}

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
                {
                    if ((e1.getRawX() - e2.getRawX()) > 100
                            && Math.abs(velocityX) > 180
                            && Math.abs(e2.getRawY() - e1.getRawY()) < 100)
                    {//向左滑动
                        gestureActionListener.onSwipeLeft();
                        return true;
                    }
                    if ((e2.getRawX() - e1.getRawX()) > 100
                            && Math.abs(velocityX) > 180
                            && Math.abs(e2.getRawY() - e1.getRawY()) < 100)
                    { //向右滑动
                        gestureActionListener.onSwipeRight();
                        return true;
                    }
                    //下滑
                    if (e2.getRawY() - e1.getRawY() > 100 && Math.abs(velocityY) > 2500
                            && Math.abs(e2.getRawX() - e1.getRawX()) < 100)
                    {
                       gestureActionListener.onSwipeDown();
                        return true;
                    }
                    //上滑
                    if (e1.getRawY() - e2.getRawY() > 100 && Math.abs(velocityY) > 2500
                            && Math.abs(e2.getRawX() - e1.getRawX()) < 100)
                    {
                        gestureActionListener.onSwipeUp();
                        return true;
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    gestureActionListener.onLongPress(e);
                }
            });
    }

	@Override
	public boolean onScale(ScaleGestureDetector detector)
	{
		float scale = getScale();
		float scaleFactor = detector.getScaleFactor();

		if (getDrawable() == null)
			return true;

		/**
		 * 缩放的范围控制
		 */
		if ((scale < SCALE_MAX && scaleFactor > 1.0f)
			|| (scale > initScale && scaleFactor < 1.0f))
		{
			/**
			 * 最大值最小值判断
			 */
			if (scaleFactor * scale < initScale)
			{
				scaleFactor = initScale / scale;
			}
			if (scaleFactor * scale > SCALE_MAX)
			{
				scaleFactor = SCALE_MAX / scale;
			}
			/**
			 * 设置缩放比例
			 */
			mScaleMatrix.postScale(scaleFactor, scaleFactor,
								   detector.getFocusX(), detector.getFocusX());
			checkBorderAndCenterWhenScale();
			setImageMatrix(mScaleMatrix);
		}
		return true;

	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector)
	{
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector)
	{
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		mScaleGestureDetector.onTouchEvent(event);
		if (gestureActionListener != null){
		    mGestureDetector.onTouchEvent(event);
		}
		float x = 0, y = 0;
		// 拿到触摸点的个数
		final int pointerCount = event.getPointerCount();
		// 得到多个触摸点的x与y均值
		for (int i = 0; i < pointerCount; i++)
		{
			x += event.getX(i);
			y += event.getY(i);
		}
		x = x / pointerCount;
		y = y / pointerCount;

		/**
		 * 每当触摸点发生变化时，重置mLasX , mLastY 
		 */
		if (pointerCount != lastPointerCount)
		{
			isCanDrag = false;
			mLastX = x;
			mLastY = y;
		}


		lastPointerCount = pointerCount;

		switch (event.getAction())
		{
			case MotionEvent.ACTION_MOVE:
				Log.e(TAG, "ACTION_MOVE");
				float dx = x - mLastX;
				float dy = y - mLastY;

				if (!isCanDrag)
				{
					isCanDrag = isCanDrag(dx, dy);
				}
				if (isCanDrag)
				{
					RectF rectF = getMatrixRectF();
					if (getDrawable() != null)
					{
						isCheckLeftAndRight = isCheckTopAndBottom = true;
						// 如果宽度小于屏幕宽度，则禁止左右移动
						if (rectF.width() < getWidth())
						{
							dx = 0;
							isCheckLeftAndRight = false;
						}
						// 如果高度小雨屏幕高度，则禁止上下移动
						if (rectF.height() < getHeight())
						{
							dy = 0;
							isCheckTopAndBottom = false;
						}
						mScaleMatrix.postTranslate(dx, dy);
						checkMatrixBounds();
						setImageMatrix(mScaleMatrix);
					}
				}
				mLastX = x;
				mLastY = y;
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				Log.e(TAG, "ACTION_UP");
				lastPointerCount = 0;
				break;
		}

		return true;
	}


	/**
	 * 获得当前的缩放比例
	 * 
	 * @return
	 */
	public final float getScale()
	{
		mScaleMatrix.getValues(matrixValues);
		return matrixValues[Matrix.MSCALE_X];
	}

	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	@Override
	public void onGlobalLayout()
	{
		if (once)
		{
			Drawable d = getDrawable();
			if (d == null)
				return;
			Log.e(TAG, d.getIntrinsicWidth() + " , " + d.getIntrinsicHeight());
			int width = getWidth();
			int height = getHeight();
			// 拿到图片的宽和高
			int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();
			float scale = 1.0f;
			// 如果图片的宽或者高大于屏幕，则缩放至屏幕的宽或者高
			if (dw > width && dh <= height)
			{
				scale = width * 1.0f / dw;
			}
			if (dh > height && dw <= width)
			{
				scale = height * 1.0f / dh;
			}
			// 如果宽和高都大于屏幕，则让其按按比例适应屏幕大小
			if (dw > width && dh > height)
			{
				scale = Math.min(width * 1.0f / dw, height  * 1.0f / dh);
			}
			initScale = scale;
			// 图片移动至屏幕中心
			mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
			mScaleMatrix
				.postScale(scale, scale, getWidth() / 2, getHeight() / 2);
			setImageMatrix(mScaleMatrix);
			once = false;
		}

	}
	
	/**
	 * 在缩放时，进行图片显示范围的控制
	 */
	private void checkBorderAndCenterWhenScale()
	{

		RectF rect = getMatrixRectF();
		float deltaX = 0;
		float deltaY = 0;

		int width = getWidth();
		int height = getHeight();

		// 如果宽或高大于屏幕，则控制范围
		if (rect.width() >= width)
		{
			if (rect.left > 0)
			{
				deltaX = -rect.left;
			}
			if (rect.right < width)
			{
				deltaX = width - rect.right;
			}
		}
		if (rect.height() >= height)
		{
			if (rect.top > 0)
			{
				deltaY = -rect.top;
			}
			if (rect.bottom < height)
			{
				deltaY = height - rect.bottom;
			}
		}
		// 如果宽或高小于屏幕，则让其居中
		if (rect.width() < width)
		{
			deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
		}
		if (rect.height() < height)
		{
			deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
		}
		Log.e(TAG, "deltaX = " + deltaX + " , deltaY = " + deltaY);

		mScaleMatrix.postTranslate(deltaX, deltaY);

	}

	/**
	 * 根据当前图片的Matrix获得图片的范围
	 * 
	 * @return
	 */
	private RectF getMatrixRectF()
	{
		Matrix matrix = mScaleMatrix;
		RectF rect = new RectF();
		Drawable d = getDrawable();
		if (null != d)
		{
			rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rect);
		}
		return rect;
	}

	/**
	 * 移动时，进行边界判断，主要判断宽或高大于屏幕的
	 */
	private void checkMatrixBounds()
	{
		RectF rect = getMatrixRectF();

		float deltaX = 0, deltaY = 0;
		final float viewWidth = getWidth();
		final float viewHeight = getHeight();
		// 判断移动或缩放后，图片显示是否超出屏幕边界
		if (rect.top > 0 && isCheckTopAndBottom)
		{
			deltaY = -rect.top;
		}
		if (rect.bottom < viewHeight && isCheckTopAndBottom)
		{
			deltaY = viewHeight - rect.bottom;
		}
		if (rect.left > 0 && isCheckLeftAndRight)
		{
			deltaX = -rect.left;
		}
		if (rect.right < viewWidth && isCheckLeftAndRight)
		{
			deltaX = viewWidth - rect.right;
		}
		mScaleMatrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * 是否是推动行为
	 * 
	 * @param dx
	 * @param dy
	 * @return
	 */
	private boolean isCanDrag(float dx, float dy)
	{
		return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
	}

	public void loadImgae(Bitmap bitmap){
        long ms = SystemClock.currentThreadTimeMillis();
        setImageBitmap(bitmap);
        float s = getScale();
        mScaleMatrix.postScale(1.0f / s, 1.0f / s);
        RectF rect = getMatrixRectF();
        float dx = 0;
        float dy = 0;
        if (rect.width() < getWidth()){
            dx = getWidth() / 2.0f - rect.centerX();
        } else {
            dx = -rect.left;
        }
        if (rect.height() < getHeight()){
            dy = getHeight() / 2.0f - rect.centerY();
        } else {
            dy = -rect.top;
        }
        mScaleMatrix.postTranslate(dx,dy);
        setImageMatrix(mScaleMatrix);
        Log.d("arr","cost:" + (SystemClock.currentThreadTimeMillis() - ms));
	}

}

interface GestureActionListener{

	public void onDoubleTap(MotionEvent me);

    public void onLongPress(MotionEvent me);

	public void onSwipeUp();

    public void onSwipeDown();

    public void onSwipeLeft();

    public void onSwipeRight();

}