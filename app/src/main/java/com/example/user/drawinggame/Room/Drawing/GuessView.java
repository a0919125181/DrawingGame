package com.example.user.drawinggame.Room.Drawing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

public class GuessView extends View {

    private Context context;    // drawView的內容
    private Bitmap mBitmap;     // 保存像素
    private Canvas mCanvas;     // 畫布
    private Path mPath;         // 畫線

    private float widthScale;
    private float heightScale;
    private Paint paint;
    private Paint clearPen;

    public GuessView(Context context) {
        super(context);
        this.context = context;

        mPath = new Path();

        paint = new Paint();
        paint.setAntiAlias(true);               // 抗鋸齒
        paint.setDither(true);                  // 防抖動
        paint.setStyle(Paint.Style.STROKE);     // 不填滿
        paint.setStrokeJoin(Paint.Join.ROUND);  // 圓角
        paint.setStrokeCap(Paint.Cap.ROUND);    // 單點形狀

        clearPen = new Paint();
        clearPen.setColor(Color.rgb(250, 250, 250));
        clearPen.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mBitmap, 0, 0, new Paint(Paint.DITHER_FLAG));
        canvas.drawPath(mPath, paint);
    }

    public void setPathPen(String pastX, String pastY, String x, String y, String penSize, String color) {
        float float_x = Float.parseFloat(x) * widthScale;
        float float_y = Float.parseFloat(y) * heightScale;
        float float_pastX = Float.parseFloat(pastX) * widthScale;
        float float_pastY = Float.parseFloat(pastY) * heightScale;

        mPath.moveTo(float_x, float_y);
        mPath.quadTo(float_x, float_y, float_pastX, float_pastY);

        if (color.equals("1"))
            paint.setColor(Color.BLACK); //顏色
        else
            paint.setColor(Color.rgb(250, 250, 250));

        paint.setStrokeWidth(Integer.parseInt(penSize)); //粗細

        mCanvas.drawPath(mPath, paint);

        postInvalidate();
        mPath.reset();
    }

    public void convertSize(String width, String height) {
        int ownWidth = this.getWidth();
        int ownHeight = this.getHeight();

        widthScale = (float) ownWidth / (float) Integer.parseInt(width);
        heightScale = (float) ownHeight / (float) Integer.parseInt(height);
    }

    public void drawWholeWhite(){
        mCanvas.drawRect(0, 0, this.getWidth(), this.getHeight(), clearPen);
        postInvalidate();
    }
}
