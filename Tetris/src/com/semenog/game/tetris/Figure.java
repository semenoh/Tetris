package com.semenog.game.tetris;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class Figure {
	public enum FigureMotion{
		Left, Right, Down, Rotate;
	}
	
	public enum FigureType {
		I, O, L, J, S, Z, T;
		
		private static final List<FigureType> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
		private static final int SIZE = VALUES.size();
		private static final Random RANDOM = new Random();

		//TODO: randomize
		public static FigureType randomFigure() {
			return VALUES.get(RANDOM.nextInt(SIZE));
		}	
	}
	
	static final int BRICK_SIZE = 10;
	static final int BRICK_GAP_SIZE = 1; 
	static final int FIGURE_MATRIX_SIZE = 4;
	Point pos;
	FigureType type;
	private int rotation;
	
	public Figure () {
		this.pos = new Point(); 
		this.type = FigureType.randomFigure();
		this.rotation = 0;
	}
	
	public void refresh(){
		this.type = FigureType.randomFigure();
		this.rotation = 0;		
	}
	
	private int getNextRotation(){
		return (3 == rotation) ? 0 : rotation + 1;
	}
	
	public void rotate(){
		rotation = getNextRotation();
	}

	boolean[][] getMatrix(boolean rotated){
		return getMatrix(getNextRotation());		
	}
	
	boolean[][] getMatrix(){
		return getMatrix(rotation);
	}
	
	private boolean[][] getMatrix(int rotation){
		boolean[][] bricks = null;
		switch(type){
		case O:
			switch (rotation){
			case 0:
			case 1:
			case 2:
			case 3:
				bricks = new boolean[][]{
						{ true, true },	// X X
						{ true, true }	// X X
				};
				break;
			}
			break;
		case I:
			switch (rotation){
			case 0:
			case 2:
				bricks = new boolean[][]{
						{ true },	// X
						{ true },	// X
						{ true },	// X
						{ true }	// X
				};
				break;
			case 1:
			case 3:
				bricks = new boolean[][]{
						{ true, true, true, true}		// X X X X
				};
				break;				
			}
			break;
		case S:
			switch (rotation){
			case 0:
			case 2:
				bricks = new boolean[][]{
						{ false, true, true },	//   X X
						{ true, true, false }	// X X
				};
				break;
			case 1:
			case 3:
				bricks = new boolean[][]{
						{ true, false },// X
						{ true, true },	// X X
						{ false, true }	//   X
				};
				break;				
			}
			break;
		case Z:
			switch (rotation){
			case 0:
			case 2:
				bricks = new boolean[][]{
						{ true, true, false},	// X X
						{ false, true, true}	//   X X
				};
				break;
			case 1:
			case 3:
				bricks = new boolean[][]{
						{ false, true },//   X
						{ true, true },	// X X
						{ true, false }	// X
				};
				break;				
			}
			break;
		case L:
			switch (rotation){
			case 0:
				bricks = new boolean[][]{
						{ true, false},		// X 
						{ true, false},		// X
						{ true, true }		// X X
				};
				break;
			case 1:
				bricks = new boolean[][]{
						{ false, false, true },		//     X
						{ true,  true,  true }		// X X X
				};
				break;
			case 2:
				bricks = new boolean[][]{
						{ true,  true }, // X X
						{ false, true }, //   X
						{ false, true }  //   X
				};
				break;
			case 3:
				bricks = new boolean[][]{
						{ true, true,  true },		// X X X
						{ true, false, false}		// X 
				};
				break;				
			}
			break;
		case J:
			switch (rotation){
			case 0:
				bricks = new boolean[][]{
						{ false, true},	//   X
						{ false, true},	//   X
						{ true,  true}	// X X
				};
				break;
			case 1:
				bricks = new boolean[][]{
						{ true,  true,  true},	// X X X 
						{ false, false, true}	//     X 
				};
				break;
			case 2:
				bricks = new boolean[][]{
						{ true, true  },	// X X
						{ true, false },	// X
						{ true, false }		// X
				};
				break;
			case 3:
				bricks = new boolean[][]{
						{ true, false, false },	// X 
						{ true, true,  true  }	// X X X
				};
				break;				
			}
			break;
		case T:
			switch (rotation){
			case 0:
				bricks = new boolean[][]{
						{ false, true, false },	//   X 
						{ true,  true, true }	// X X X
				};
				break;
			case 1:
				bricks = new boolean[][]{
						{ false,  true },	//   X 
						{ true,  true },	// X X 
						{ false, true }		//   X   
				};
				break;
			case 2:
				bricks = new boolean[][]{
						{ true, true, true },	// X X X
						{ false, true, false }	//   X
				};
				break;
			case 3:
				bricks = new boolean[][]{
						{ true, false },// X 
						{ true, true },	// X X 
						{ true, false }	// X   
				};
				break;				
			}
			break;
			default:
				throw new IllegalArgumentException("Unhandled FiguteType!");
		}
		return bricks;
	}	
	
	void DrawFigure(Canvas c){
		DrawFigure(c, getMatrix(), false);
	}
	void DrawFigure(Canvas c, boolean[][] matrix){
		DrawFigure(c, matrix, false);
	}
	void DrawFigure(Canvas c, boolean[][] matrix, boolean clean){
		Paint p = new Paint();
		p.setColor(0xFFFFFFFF);
		DrawFigure(c, matrix, p);
	}
	void DrawFigure(Canvas c, boolean[][] matrix, Paint p){
		c.save();
		for (int row = 0; row < matrix.length; ++row){
			for (int column = 0; column < matrix[row].length; ++column){
				if (matrix[row][column])
					c.drawRect(
							(BRICK_GAP_SIZE+BRICK_SIZE)*(pos.x + column -1), 
							(BRICK_GAP_SIZE+BRICK_SIZE)*(pos.y + row - 1), 
							(BRICK_GAP_SIZE+BRICK_SIZE)*(pos.x + column) - BRICK_GAP_SIZE, 
							(BRICK_GAP_SIZE+BRICK_SIZE)*(pos.y + row) - BRICK_GAP_SIZE, 
							p);
			}
		}
		c.restore();
	}
}
