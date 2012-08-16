package com.semenog.game.tetris;

import android.graphics.Canvas;

public interface ISurface {
	void onInitialize();
	void onDraw(Canvas canvas);
	void onUpdate(long gameTime);
}
