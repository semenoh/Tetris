package com.semenog.game.tetris;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class GameView extends SurfaceView implements ISurface{
	static final int BRICK_SIZE = 10;
	static final int BRICK_GAP_SIZE = 1; 

	protected int _iWidth;
	protected int _iHeight;
	
	protected int bWidth;
	protected int bHeight;
	
	boolean gameOver;
	
	int score = 0;

	Handler handler;
	SurfaceHolder holder;
	
	private GameLoopThread gameLoopThread;

	int bgColor;
	int fgColor;
	Paint p;
	
	Context context;
	
	boolean field[][];
	
	Figure figure;
	
	public GameView(Context context) {
		super(context);
		this.context = context;
		p = new Paint();
		gameOver = false;
		handler = new Handler();
		figure = new Figure();
		holder = getHolder();
		gameLoopThread = new GameLoopThread(holder, this);
		holder.addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
				_iWidth = width;
				_iHeight = height;
				initField();
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				gameLoopThread.setRunning(true);
				try {
					gameLoopThread.start();
				} catch (IllegalThreadStateException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				boolean retry = true;
				gameLoopThread.setRunning(false);
				while (retry){
					try {
						gameLoopThread.join();
						retry = false;
					} catch (InterruptedException e){
						e.printStackTrace();
					}
				}
			}
			
		});
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(bgColor);
		
		p.setColor(fgColor);

		for (int row = 1; row < field.length-1; ++row){
			for (int column = 1; column < field[row].length-1; ++column){
				if (field[row][column])
					canvas.drawRect(
							(BRICK_GAP_SIZE+BRICK_SIZE)*(column - 1), 
							(BRICK_GAP_SIZE+BRICK_SIZE)*(row - 1), 
							(BRICK_GAP_SIZE+BRICK_SIZE)*(column) - BRICK_GAP_SIZE, 
							(BRICK_GAP_SIZE+BRICK_SIZE)*(row) - BRICK_GAP_SIZE, 
							p);
			}
		}
		
		figure.DrawFigure(canvas);
	}
	
	public void initField(){
		bHeight = _iHeight/(BRICK_GAP_SIZE+BRICK_SIZE) + 2;
		bWidth = _iWidth/(BRICK_GAP_SIZE+BRICK_SIZE) + 2;
		field = new boolean[bHeight][bWidth];
		CleanupField();		
	}
	
	@Override
	public void onInitialize() {
		bgColor = getResources().getColor(R.color.bg_color);
		fgColor = getResources().getColor(R.color.fg_color);
		
		figure.pos = new Point(bWidth/2,0);
		figure.type = Figure.FigureType.randomFigure();
	}

	private void CleanupField() {
		for (int j = 0; j < field[0].length; ++j){//first and last lines
			field[0][j] = true;
			field[field.length-1][j] = true;
		}
		for (int i = 1; i < field.length - 1; ++i){
			field[i][0] = true;
			field[i][field[i].length-1] = true;
		}
	}

	void moveDownFigure(){
		if (canMoveFigure(Figure.FigureMotion.Down))
			++figure.pos.y;
		else{
			putFigure();
			getherComplitedLines();
			
			figure.refresh();
			figure.pos.set(bWidth/2, 0);
			
			gameOver = isGameOver();
		}
	}
	
	void getherComplitedLines(){
		boolean complitedLine;
		boolean emptyLine;
		int complitedLinesCount = 0;
		
		out:
		for (int row = figure.pos.y + figure.getMatrix().length - 1; row > 0; --row){
			complitedLine = true;
			emptyLine = true;
			for (int column = 1; column < field[0].length - 1; ++column){ //start from bottom most line figure reached
				if (field[row+complitedLinesCount][column])
					emptyLine = false; //in order to skip cheking if top of stack is reached
				field[row+complitedLinesCount][column] = field[row][column];
				if (!field[row][column])
					complitedLine = false;
			}
			if (emptyLine)
				break out; //skip cheking if top of stack is reached
			if (complitedLine)
				++complitedLinesCount;
		}
		
		score += complitedLinesCount*complitedLinesCount;
	}
	
	boolean canMoveFigure(Figure.FigureMotion motion){
		Point p;
		boolean f[][];
		switch (motion){
		case Down:
			p = new Point (0,1);
			f = figure.getMatrix();
			break;
		case Left:
			p = new Point (-1,0);
			f = figure.getMatrix();
			break;
		case Right:
			p = new Point (1,0);
			f = figure.getMatrix();
			break;
		case Rotate:
			p = new Point (0,0);
			f = figure.getMatrix(true);
			break;
		default:
			throw new IllegalArgumentException("Unhandled MotionType!");	
		}

		for (int row = 0 ; row < f.length; ++row){
			for (int column = 0; column < f[row].length; ++column){
				if (f[row][column] && field[figure.pos.y + p.y + row][figure.pos.x + p.x + column]){
					return false;
				}
			}
		}
		
		return true;
	}
	
	void putFigure(){
		boolean f [][] = figure.getMatrix();
		for (int row = 0; row < f.length; ++row)
			for (int column = 0; column < f[row].length; ++column)
				if (f[row][column])
					field[figure.pos.y+row][figure.pos.x+column] = f[row][column];
	}
	
	boolean isGameOver(){
		boolean f [][] = figure.getMatrix();
		for (int row = 0 ; row < f.length; ++row){
			for (int column = 0; column < f[row].length; ++column){
				if (f[row][column] && field[figure.pos.y+row+1][figure.pos.x+column]){
					handler.post(new Runnable(){
					    public void run(){
					        Toast.makeText(context, "Game Over!", Toast.LENGTH_LONG).show();
					    }
					});
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void onUpdate(long gameTime) {
		if (!gameOver)
			moveDownFigure();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		if (y < _iHeight/3) {
			if (canMoveFigure(Figure.FigureMotion.Rotate))
				figure.rotate();
		} else if (y > (_iHeight/3)*2) {
			fallDownFigure();
		} else {
			if (x > _iWidth/2) {
				if (canMoveFigure(Figure.FigureMotion.Right))
					++figure.pos.x;
			} else {
				if (canMoveFigure(Figure.FigureMotion.Left))
					--figure.pos.x;
			}
		}
		return super.onTouchEvent(event);
	}

	private void fallDownFigure() {
		// TODO Auto-generated method stub
		
	}
}
