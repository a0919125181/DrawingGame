package com.example.user.drawinggame.Room.Drawing.PaintAPP;

import android.graphics.Path;

class FingerPath {
    int color;
    int strokeWidth;
    Path path;

    FingerPath(int color, int strokeWidth, Path path) {
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}
