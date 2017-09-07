package com.example.bigyoung.colorfulring.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.example.bigyoung.colorfulring.R;

/**
 * Created by BigYoung on 2017/9/6.
 */

public class ColorfulRing extends View {
    private  String mTextContent;
    private int mMajorColor;//主颜色
    private int mSecondaryColor;//次颜色
    private int mRadius;//半径
    private int mCurrentValue;//当前值
    private int mMaxValue;//最大值
    public Paint mPaint;
    public int mArcWidth;
    public int mHeight;
    public int mWidth;
    public int WIDTH_BLANK;//横向空白
    public int HEIGHT_BLANK;//纵向空白
    public float mTextDimen;//文字尺寸
    private Rect mTextBounds;

    public ColorfulRing(Context context) {
        this(context, null);
    }

    public ColorfulRing(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorfulRing(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自定义属性
        TypedArray arrayAttr = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorfulRing, defStyleAttr, 0);
        int num = arrayAttr.getIndexCount();
        for (int i = 0; i < num; i++) {//筛选目的属性
            int attr = arrayAttr.getIndex(i);
            switch (attr) {
                case R.styleable.ColorfulRing_major_color:
                    mMajorColor = arrayAttr.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.ColorfulRing_secondary_color:
                    mSecondaryColor = arrayAttr.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.ColorfulRing_current_value:
                    mCurrentValue = arrayAttr.getInt(attr, 0);
                    break;
                case R.styleable.ColorfulRing_max_value:
                    mMaxValue = arrayAttr.getInt(attr, 0);
                    break;
                case R.styleable.ColorfulRing_radius:
                    mRadius = (int) arrayAttr.getDimension(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            100, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.ColorfulRing_text_size:
                    mTextDimen = arrayAttr.getDimension(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            12, getResources().getDisplayMetrics()));
                    break;
            }
        }
        arrayAttr.recycle();
        //创造画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setStrokeCap(Paint.Cap.ROUND);//绘制的结尾是圆弧
        //环的宽度取半径的1/8
        mArcWidth = mRadius / 8;
        WIDTH_BLANK = 2 * mArcWidth;
        HEIGHT_BLANK = 2 * mArcWidth;
        //文本渲染区域
        mTextBounds = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //宽度模式必须设定为wrap_content,只需设置半径值就可以确定范围
        mWidth = 2 * mRadius + 4 * mArcWidth + WIDTH_BLANK;
        mHeight = mRadius + mArcWidth + mArcWidth + HEIGHT_BLANK;
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStrokeWidth(mArcWidth);//设置环的宽度
        mPaint.setStyle(Paint.Style.STROKE);//空心

        int centerX = mWidth / 2;
        //确定圆弧绘制范围
        RectF oval = new RectF(centerX - mRadius, HEIGHT_BLANK / 2 + mArcWidth, centerX + mRadius, HEIGHT_BLANK / 2 + mArcWidth + 2 * mRadius);
        mPaint.setColor(mMajorColor);
        canvas.drawArc(oval, 180, 180, false, mPaint);
        //绘制代表当前进度的弧线
        double percent = mCurrentValue * 1.0 / mMaxValue;
        int currentArc = (int) (180 * percent);
        int edgeX = (int) (mRadius + mRadius * Math.cos(Math.toRadians(180 - currentArc)));//计算当前进度圆弧在X轴上的偏移量
        Shader mShader = new LinearGradient(centerX - mRadius - mArcWidth / 2, 0, centerX - mRadius + edgeX + mArcWidth, 0,
                new int[]{mMajorColor, mSecondaryColor}, null, Shader.TileMode.REPEAT);//centerX-mRadius+edgeX+mArcWidth,为了保证圆弧尾端的颜色不被覆盖
        mPaint.setShader(mShader);
        canvas.drawArc(oval, 180, currentArc, false, mPaint);
        //绘制弧底部的基线
        mPaint.setShader(null);//清除渐变色
        mPaint.setColor(mMajorColor);
        canvas.drawLine(centerX - mRadius - mArcWidth, HEIGHT_BLANK / 2 + mRadius + mArcWidth+mArcWidth/2, centerX + mRadius + mArcWidth, HEIGHT_BLANK / 2 + mRadius + mArcWidth+mArcWidth/2, mPaint);
        //绘制文字
        mTextContent=mCurrentValue*100/mMaxValue+"%";//拼装待绘制文字
        mPaint.setTextSize(mTextDimen);//必须在测量mTextBounds之前执行
        mPaint.getTextBounds(mTextContent, 0, mTextContent.length(), mTextBounds);
        mPaint.setColor(mMajorColor);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);//抗锯齿
        //居中显示
        canvas.drawText(mTextContent,(mWidth-mTextBounds.width())*1.0f/2,mRadius,mPaint);
    }

    public void setCurrentValue(int currentValue) {
        mCurrentValue = currentValue;
        postInvalidate();//重新绘制
    }

    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
        postInvalidate();//重新绘制
    }
}
