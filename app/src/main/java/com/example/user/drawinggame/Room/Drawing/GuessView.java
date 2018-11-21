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

    private float mX, mY;
    private float widthScale;
    private float heightScale;
    private Paint paint;

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

    public void setPathPen(String x, String y, String penSize, String color) {
        float float_x = Float.parseFloat(x) * widthScale;
        float float_y = Float.parseFloat(y) * heightScale;

        mX = float_x;
        mY = float_y;
        mPath.moveTo(float_x, float_y);
        mPath.quadTo(mX, mY, (float_x + mX) / 2, (float_y + mY) / 2);

        if (color.equals("1"))
            paint.setColor(Color.BLACK); //顏色
        else
            paint.setColor(Color.rgb(250, 250, 250));

        paint.setStrokeWidth(Integer.parseInt(penSize)); //粗細
        Log.e("SEE", penSize);

        mCanvas.drawPath(mPath, paint);

        // invalidate();
        postInvalidate();

        mPath.reset();
    }

    public void convertSize(String width, String height){
        int ownWidth = this.getWidth();
        int ownHeight = this.getHeight();

        widthScale = Integer.parseInt(width) / ownWidth;
        heightScale = Integer.parseInt(height) / ownHeight;
    }
}
