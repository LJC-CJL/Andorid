package com.ljc.buttonsurround;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;
/**
 * @Author LJC
 * 2021/6/22
 */
import androidx.annotation.Nullable;
public class Surround extends View {
    private int progressColor; // 进度条颜色  可自定义
    private int backgroundColor;//背景颜色  可自定义
    private int progressWidth;//进度条宽度  可自定义
    private Paint innerPaint,outerPaint,textPaint,interceptpaint;
    private int radious;//圆角大小
    private Path path,interceptpath;
    private PathMeasure pathMeasure;
    private ValueAnimator valueAnimator;
    private Float mFloatPos;
    private int LinewidthLength,LineheightLength;
    private float stopD = 0f;
    public Surround(Context context) {
        this(context,null);
    }

    public Surround(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Surround(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array =context.obtainStyledAttributes(attrs,R.styleable.Surround);
        progressColor = array.getColor(R.styleable.Surround_progressColor,progressColor);
        backgroundColor = array.getColor(R.styleable.Surround_backgroundColor,backgroundColor);
        progressWidth = (int) array.getDimension(R.styleable.Surround_progressWidth,progressWidth);
        array.recycle();

        //画笔定义

        innerPaint = new Paint();
        innerPaint.setAntiAlias(true);
        innerPaint.setColor(backgroundColor);
        innerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        outerPaint = new Paint();
        outerPaint.setAntiAlias(true);
        outerPaint.setColor(backgroundColor);
        outerPaint.setStrokeWidth(progressWidth);
        outerPaint.setStyle(Paint.Style.STROKE);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
         textPaint.setTextSize(dip2px(20));
         textPaint.setStyle(Paint.Style.FILL);
        interceptpaint = new Paint();
        interceptpaint.setAntiAlias(true);
        interceptpaint.setColor(progressColor);
        interceptpaint.setStrokeWidth(progressWidth);
        interceptpaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制内圈
        RectF rectF = new RectF(0,0,getWidth(),getHeight());
        canvas.drawRoundRect(rectF,radious,radious,innerPaint);

        //绘制外圈
        LinewidthLength = getWidth();
        LineheightLength = getHeight()/2;
        path = new Path();
        interceptpath = new Path();
        path.reset();
        path.addRoundRect(
                getPaddingLeft()+progressWidth/2,
                getPaddingTop()+progressWidth/2,
                getMeasuredWidth()-progressWidth/2,
                getMeasuredHeight()-progressWidth/2,
                radious,radious,Path.Direction.CW
        );
        pathMeasure  = new PathMeasure();
        pathMeasure.setPath(path,true);
        canvas.drawPath(path,outerPaint);
        float length = pathMeasure.getLength();
         stopD  = mFloatPos * length;
        Log.d("stopd", "onDraw: ->"+stopD);
        float startD = (float) (stopD- ((0.5 - Math.abs(mFloatPos - 0.5)) * length));

        pathMeasure.getSegment(startD,stopD,interceptpath,true);
        canvas.drawPath(interceptpath,interceptpaint);

        // 绘制文字
        String Text = "START";
        Rect  textbounds = new Rect();
        textPaint.getTextBounds(Text,0,Text.length(),textbounds);
        int x = getWidth()/2 - textbounds.width()/2;
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        int dy =(fontMetricsInt.bottom - fontMetricsInt.top)/2 - fontMetricsInt.bottom;
        //求基线
        int baseline = getHeight()/2 + dy;
        canvas.drawText(Text,x,baseline,textPaint);

    }
    public void startAnim(){
        if (valueAnimator == null){
            valueAnimator = ValueAnimator.ofFloat(0,1);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mFloatPos = (Float) animation.getAnimatedValue();
                      invalidate();


                }
            });
            valueAnimator.setDuration(2000);
            valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.start();
        }
    }



    public synchronized void setRadious(int radious){
        if (radious <0){

        }
        this.radious = radious;
        invalidate();
    }

    private float dip2px(int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }
    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}
