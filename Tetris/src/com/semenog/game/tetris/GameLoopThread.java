package com.semenog.game.tetris;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameLoopThread extends Thread {
	static final long FPS = 10;
	private GameView view;
	private boolean running = false;
	private SurfaceHolder surfaceHolder;
	private ISurface panel;
	private long timer;
	
	public GameLoopThread(SurfaceHolder surfHolder, ISurface panel){
//		this.view = view;
		
		this.surfaceHolder = surfHolder;
		this.panel = panel;
		
		panel.onInitialize();
	}
	
	public void setRunning(boolean runn){
		running = runn;
	}
	
	@Override
	public void run(){
		long ticksPS = 2000 / FPS;
		long startTime;
		long sleepTime;
		
		while (running){
			Canvas c = null;
			timer = startTime = System.currentTimeMillis();
			panel.onUpdate(timer);
			try {
				c = surfaceHolder.lockCanvas();
				synchronized (surfaceHolder){
					panel.onDraw(c);
				}
			} finally {
				if (null != c){
					surfaceHolder.unlockCanvasAndPost(c);
				}
			}
			sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
			try {
				sleep((sleepTime > 0)? sleepTime : 10);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
