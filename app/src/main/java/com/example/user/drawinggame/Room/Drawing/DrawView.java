package com.example.user.drawinggame.Room.Drawing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.user.drawinggame.Room.Client_FunctionCode;

import java.net.Socket;

public class DrawView extends View {

    private static final float TOUCH_TOLERANCE = 4;

    private Context mContext;       // drawView 的內容
    private Bitmap mBitmap;         // 保存像素
    private Canvas mCanvas;         // 畫布

    private Path mPath;             // 畫線
    private Paint mPaint;           // 畫筆

    private Paint circlePaint;      // 畫圓圈的筆
    private Path circlePath;        // 畫圓形
    int penSize;                    // 畫筆粗細 || 提示點大小
    private float mX, mY;

    private Socket roomSocket;
    private String userID;


    public DrawView(Context context, Paint mPaint, int penSize, Socket roomSocket, String userID) {
        super(context);
        this.mPaint = mPaint;
        this.penSize = penSize;
        this.mContext = context;

        this.roomSocket = roomSocket;
        this.userID = userID;

        mPath = new Path();

        circlePath = new Path();
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE); // 顏色
        circlePaint.setStyle(Paint.Style.STROKE); // 不填滿
        circlePaint.setStrokeJoin(Paint.Join.ROUND); // 圓角
        circlePaint.setStrokeWidth(4); // 線的寬度
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
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(circlePath, circlePaint);
    }

    private void touch_start(float x, float y) {
        mPath.moveTo(x, y);
        mPath.lineTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, penSize / 2, Path.Direction.CW);
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);

        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);

        // kill this so we don't double draw
        mPath.reset();
        circlePath.reset();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        int width = getWidth();
        int height = getHeight();

        String state = "U";

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                state = "D";
                touch_start(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                state = "M";
                touch_move(x, y);
                break;
            case MotionEvent.ACTION_UP:
                state = "U";
                touch_up();
                break;
        }

        new Client_FunctionCode("05", roomSocket, userID,
                width + " " + height + " " + x + " " + y + " " + String.format("%02d", penSize) + " " + mPaint.getColor() + " " + state);


        invalidate();
        return true;
    }
}
