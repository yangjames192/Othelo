package com.example.othello;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
 
public class GameView extends View {
 
	private Paint paint;
	
	private int num;
	private int round; //game round, black goes first
	private int point_x[];
	private int point_y[];
	private char board[][];
	private char board_track[][][]; //used to track the game board after each move
	private int track_count;
	private int x_cood=0; //use to draw circle
	private int y_cood=0; 
	private boolean isStart;
	private int board_value[][];   //all board position value
	private int move_p;  //number of person's move
	private int move_ai;
	private int max = -20;
	private int max_i = 0;
	private int max_j = 0;
	private int count = 0;
	private static boolean isSingle = false;
	static GameActivity game = new GameActivity();
	
	private static GameView instance = null;
	  
	private Handler updateViewHandler = new Handler(){
		public void handleMessage(Message msg){ // run in UI thread.
		switch(msg.what){
		case 1:
			invalidate();
		break;
		}
		}
		};

		
	public GameView(Context context, AttributeSet attrs) {
		super(context);
		init();
		
		instance = this;
	}
	
	
	//wrap it to a singleton 
	public static GameView getInstance() {
		if(instance == null) {
			instance = new GameView(game, null);
	    }
		return instance;
	}
	   
	
	public int getR() {
		return round;
	}
	
	
	private void init(){
		 paint = new Paint();
		 paint.setAntiAlias(true);
		 paint.setColor(Color.BLUE);
		 paint.setStrokeWidth(5);
		 paint.setStyle(Paint.Style.STROKE);
		 paint.setTextSize(40);
		 num = 0;
		 round = 0;
		 track_count = 0;
		 move_p = 0;
		 move_ai = 0;
		 
		 isStart = false;
		 x_cood = 0;
		 y_cood = 0;
		 board_track = new char[64][8][8];
		 
		 board = new char[8][8];
		 //initialize board
		 for(int i = 0; i < 8; i++) {
			 for(int j = 0; j < 8; j++) {
				 board[i][j] = ' ';
			 }
		 }
		 board[3][3] = 'w'; //initial setup
		 board[4][4] = 'w';
		 board[3][4] = 'b';
		 board[4][3] = 'b';
		 
		 for(int i = 0; i < 8; i++) {
			 for(int j = 0; j < 8; j++) {
				 board_track[track_count][i][j] = board[i][j];
			 }
		 }
		 
		 //all x coords
		 point_x = new int[8];
		 for(int i = 0; i < 8; i++) {
			 
			 point_x[i] = 10 + i * 85;
		 }
		 
		 //all y coords
		 point_y = new int[8];
		 for(int i = 0; i < 8; i++) {
			 point_y[i] = 10 + i * 85;
		 }
		 
		 //initiate all position values
		 board_value = new int[8][8];
		 //left
		 for(int i = 0; i < 4; i++) {
			 for(int j = 0; j < 4; j++) {
				 if(i == 0 && j == 0) {
					 board_value[i][j] = 50;
					 
				 } else if((i == 0 && j == 1) || (i == 1 && j == 0)) {
					 board_value[i][j] = -1;
					 
				 } else if((i == 0 && j == 2) || (i == 2 && j == 0)) {
					 board_value[i][j] = 5;
				 } else if((i == 0 && j == 3) || (i == 3 && j == 0)) {
					 board_value[i][j] = 2;
				 } else if(i == 3 && j == 3) {
					 board_value[i][j] = 0;
				 } else {
					 board_value[i][j] = 1;
				 }
			 }
		 }
		 //right
		 for(int i = 0; i < 4; i++) {
			 for(int j = 4; j < 8; j++) {
				 if(i == 0 && j == 7) {
					 board_value[i][j] = 50;
					 
				 } else if((i == 0 && j == 6) || (i == 1 && j == 7)) {
					 board_value[i][j] = -1;
					 
				 } else if((i == 0 && j == 5) || (i == 2 && j == 7)) {
					 board_value[i][j] = 5;
				 } else if((i == 0 && j == 4) || (i == 3 && j == 7)) {
					 board_value[i][j] = 2;
				 } else if(i == 3 && j == 4) {
					 board_value[i][j] = 0;
				 } else {
					 board_value[i][j] = 1;
				 }
			 }
		 }
		 //left bottom
		 for(int i = 4; i < 8; i++) {
			 for(int j = 0; j < 4; j++) {
				 if(i == 7 && j == 0) {
					 board_value[i][j] = 50;
					 
				 } else if((i == 7 && j == 1) || (i == 6 && j == 0)) {
					 board_value[i][j] = -1;
					 
				 } else if((i == 7 && j == 2) || (i == 5 && j == 0)) {
					 board_value[i][j] = 5;
				 } else if((i == 7 && j == 3) || (i == 4 && j == 0)) {
					 board_value[i][j] = 2;
				 } else if(i == 4 && j == 3) {
					 board_value[i][j] = 0;
				 } else {
					 board_value[i][j] = 1;
				 }
			 }
		 }
		 
		 //right bottom
		 for(int i = 4; i < 8; i++) {
			 for(int j = 4; j < 8; j++) {
				 if(i == 7 && j == 7) {
					 board_value[i][j] = 50;
					 
				 } else if((i == 7 && j == 6) || (i == 6 && j == 7)) {
					 board_value[i][j] = -1;
					 
				 } else if((i == 7 && j == 5) || (i == 5 && j == 7)) {
					 board_value[i][j] = 5;
				 } else if((i == 7 && j == 4) || (i == 4 && j == 7)) {
					 board_value[i][j] = 2;
				 } else if(i == 4 && j == 4) {
					 board_value[i][j] = 0;
				 } else {
					 board_value[i][j] = 1;
				 }
			 }
		 }

	}
	
	//set it to single game
	public static void setSingle(boolean s) {
		isSingle = s;
	}
	
	//start game
	public void start() {
		isStart = true;
		num = 1;
		invalidate();
	}
	
	//restart game
	public void restart() {
		init();
		isStart = true;
		num = 1;
		if(isSingle) {
		System.out.println("fuck"); }
		invalidate();
		System.out.println("what");
	}
	
	public void undo() {
		
		if(isStart) {
			if(track_count > 0) {
				track_count--;
				//go to previous move
				for(int i = 0; i < 8; i++) {
					 for(int j = 0; j < 8; j++) {
						 board[i][j] = board_track[track_count][i][j];
					 }
				}
				x_cood = -1;
				y_cood = -1;
				round++;
				System.out.println("what");
				invalidate();
			}
		}
	}
	
	//ai move
	public void aiMove() {
		//find out which position can move
		
		
		//check for all points can move a move
		
		x_cood = max_j;
	   	y_cood = max_i;
		if(!isOccupy(y_cood, x_cood)) {
   		 System.out.println("t");
   		 if(round%2 == 1) {
   			 if(flip(y_cood, x_cood, 'w', 'b', true)) {
   				 board[y_cood][x_cood] = 'w';
   				 round ++;
   				 
   				 printboard();
   			 }
   		 } else {
   			 if(flip(y_cood, x_cood, 'b', 'w', true)) {
   				 board[y_cood][x_cood] = 'b';
   				 round ++;
   				 //remember every move
   				 
   				 
   				 printboard();
   			 }
   		 }
		}
   		 
		//evaluate each position
	   	
	   	move_ai++;
		//make the move, by modify x_cood, y_cood
	}
	
	public void printboard() {
		for(int i = 0; i < 8; i++) {
			 for(int j = 0; j < 8; j++) {
				 System.out.print(board[i][j]);
			 }
			 System.out.println();
		}
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
	 // TODO Auto-generated method stub
		 super.onDraw(canvas);
		
		 int startx =10;
         int starty = 10;
         
         paint.setColor(Color.GREEN);
         for(int i = 0; i<9;i++) {
         	canvas.drawLine(startx+85*i,starty,startx+85*i,starty+680, paint);
         }
         for(int i=0;i<9;i++){
         	canvas.drawLine(startx,starty+85*i,startx+680,starty+85*i, paint);
         }	
         
         //draw the initial setup
         if(num == 0) {
        	 paint.setStyle(Paint.Style.STROKE);
        	 paint.setColor(Color.BLACK);
	         canvas.drawCircle(10+3*85+45, 10+3*85+45, 30, paint);
	         canvas.drawCircle(10+4*85+45, 10+4*85+45, 30, paint);
	         
	         paint.setStyle(Paint.Style.FILL);
	         paint.setColor(Color.WHITE);
	         canvas.drawCircle(10+3*85+45, 10+3*85+45, 30, paint);
	         canvas.drawCircle(10+4*85+45, 10+4*85+45, 30, paint);
	         
	         
	         paint.setStyle(Paint.Style.STROKE);
	         paint.setColor(Color.WHITE);
	         canvas.drawCircle(10+4*85+45, 10+3*85+45, 30, paint);
	         canvas.drawCircle(10+3*85+45, 10+4*85+45, 30, paint);
	         
	         paint.setStyle(Paint.Style.FILL);
	         paint.setColor(Color.BLACK);
	         canvas.drawCircle(10+4*85+45, 10+3*85+45, 30, paint);
	         canvas.drawCircle(10+3*85+45, 10+4*85+45, 30, paint);
         }
         
         
         
         if(num==1) {
        	 //change state of board
        	 System.out.println("wt: "+y_cood+" "+x_cood);
        	 if(!isOccupy(y_cood, x_cood)) {
        		 System.out.println("t");
        		 if(round%2 == 1) {
        			 if(flip(y_cood, x_cood, 'w', 'b', true)) {
        				 board[y_cood][x_cood] = 'w';
        				 round ++;
        				 
        				 track_count++;
        				 for(int i = 0; i < 8; i++) {
        	    			 for(int j = 0; j < 8; j++) {
        	    				 board_track[track_count][i][j] = board[i][j];
        	    			 }
        				 }
        				 move_p++;
        				 printboard();
        			 }
        		 } else {
        			 if(flip(y_cood, x_cood, 'b', 'w', true)) {
        				 board[y_cood][x_cood] = 'b';
        				 round ++;
        				 //remember every move
        				 
        				 track_count++;
        				 for(int i = 0; i < 8; i++) {
        	    			 for(int j = 0; j < 8; j++) {
        	    				 board_track[track_count][i][j] = board[i][j];
        	    			 }
        				 }
        				 move_p++;
        				 printboard();
        			 }
        		 }
        	 }
        		 
        		 
        		 for(int i = 0; i < 8; i++) {
        			 for(int j = 0; j < 8; j++) {
        				 if(board[i][j] == 'w') {
        					 //draw a black circle unfilled
        					 paint.setStyle(Paint.Style.STROKE);
        			         paint.setColor(Color.BLACK);
        			         canvas.drawCircle(point_x[j]+45, point_y[i]+45, 30, paint);
        			         
        			         //draw a filled white circle
        			         paint.setStyle(Paint.Style.FILL);
        					 paint.setColor(Color.WHITE);
        					 canvas.drawCircle(point_x[j]+45, point_y[i]+45, 30, paint);
        				 } else if(board[i][j] == 'b') {
        					 paint.setStyle(Paint.Style.STROKE);
        			         paint.setColor(Color.WHITE);
        			         canvas.drawCircle(point_x[j]+45, point_y[i]+45, 30, paint);
        			         //draw a black circle
        			         paint.setStyle(Paint.Style.FILL);
        					 paint.setColor(Color.BLACK);
        					 canvas.drawCircle(point_x[j]+45, point_y[i]+45, 30, paint);
        				 }
        				 
        			 }
            	 }
        	 
        	 
        	 
        	 
        	 //check for all points can move a move
        	count = 0;
        	 max = -20;
        	 max_i = -1;
        	 max_j = -1;
        	 for(int i = 0; i < 8; i++) {
    			 for(int j = 0; j < 8; j++) {
    				 if(!isOccupy(i, j)) {
	    				 if(round%2 == 0) {
	    					 if(flip(i, j, 'b', 'w', false)) {
	    						 paint.setStyle(Paint.Style.STROKE);
	        			         paint.setColor(Color.BLACK);
	        			         canvas.drawCircle(point_x[j]+45, point_y[i]+45, 30, paint);
	        			         
	        			         count++;
	        			         if(max < board_value[i][j]) {
	 		  						max = board_value[i][j];
	 		  						max_i = i;
	 		  						max_j = j;
	 		  					}
	    					 }
	    				 } else {
	    					 if(flip(i, j, 'w', 'b', false)) {
	    						 paint.setStyle(Paint.Style.STROKE);
	        			         paint.setColor(Color.WHITE);
	        			         canvas.drawCircle(point_x[j]+45, point_y[i]+45, 30, paint);
	        			         
	        			         count++;
	        			         if(max < board_value[i][j]) {
	 		  						max = board_value[i][j];
	 		  						max_i = i;
	 		  						max_j = j;
	 		  					}
	    					 }
	    				 }
    				 }
    			 }
        	 }
        	 
        	 if(count == 0) {
        		 round++;
	        	 max = -20;
	        	 max_i = -1;
	        	 max_j = -1;
	        	 for(int i = 0; i < 8; i++) {
	    			 for(int j = 0; j < 8; j++) {
	    				 if(!isOccupy(i, j)) {
		    				 if(round%2 == 0) {
		    					 if(flip(i, j, 'b', 'w', false)) {
		    						 paint.setStyle(Paint.Style.STROKE);
		        			         paint.setColor(Color.BLACK);
		        			         canvas.drawCircle(point_x[j]+45, point_y[i]+45, 30, paint);
		        			         
		        			         if(max < board_value[i][j]) {
		 		  						max = board_value[i][j];
		 		  						max_i = i;
		 		  						max_j = j;
		 		  					}
		    					 }
		    				 } else {
		    					 if(flip(i, j, 'w', 'b', false)) {
		    						 paint.setStyle(Paint.Style.STROKE);
		        			         paint.setColor(Color.WHITE);
		        			         canvas.drawCircle(point_x[j]+45, point_y[i]+45, 30, paint);
		        			         
		        			         if(max < board_value[i][j]) {
		 		  						max = board_value[i][j];
		 		  						max_i = i;
		 		  						max_j = j;
		 		  					}
		    					 }
		    				 }
	    				 }
	    			 }
	        	 }
        	 }
        	 
         }
         
         String str;
         if(round%2 == 0) {
        	 str = "Black";
         } else {
        	 str = "White";
         }
         
         //draw text
         paint.setColor(Color.BLACK);
         paint.setStyle(Paint.Style.FILL);
         canvas.drawText("Player Turn: "+str, 10, 750, paint);
         canvas.drawText("Score:", 10, 800, paint);
         canvas.drawText("Black: "+count('b'), 10, 850, paint);
         canvas.drawText("White: "+count('w'), 10, 900, paint);
         
         return;
	}
	
	//return black or white's count
	public int count(char c) {
		int count = 0;
		for(int i = 0; i < 8; i++) {
			 for(int j = 0; j < 8; j++) {
				 if(board[i][j] == c) {
					 count++;
				 }
			 }
		}
		return count;
	}
	
	//determine whether the given position is a valid move and flip the opponent's deck
	public boolean flip(int x, int y, char c, char o, boolean isMove) {
		//right horizontal direction
		boolean canMove = false;
		boolean isFlip = false;
		int count = 0;
		int y_end = 0;
		for(int i = y+1; i < 8; i++) {
	    	if(board[x][i] == c) {
	    		y_end = i;
	    		isFlip = true;
	    		break;
	    	} else if(board[x][i] == o) {
	    		count++;
	    	} else {
	    		isFlip = false;
	    	}
		}
		if(isFlip && count == y_end - y-1) {
			
			for(int i = y+1; i < y_end; i++) {
				if(isMove) {
					board[x][i] = c;
				}
				canMove = true;
			}
		}
		
		//left horizontal direction
		int y_start = 0;
		count = 0;
		isFlip = false;
		for(int i = y-1; i >= 0; i--) {
			if(board[x][i] == c) {
				y_start = i;
				isFlip = true;
			    break;
			} else if(board[x][i] == o) {
	    		count++;
			} else {
				isFlip = false;
			}
		}
		if(isFlip && count == y-y_start-1) {
			
			for(int i = y_start+1; i < y; i++) {
				if(isMove) {
					board[x][i] = c;
				}
				canMove = true;
			}
		}
		
		//top vertical 
		int x_end = 0;
		count = 0;
		isFlip = false;
		for(int i = x+1; i < 8; i++) {
	    	if(board[i][y] == c) {
	    		x_end = i;
	    		isFlip = true;
	    		break;
	    	} else if(board[i][y] == o) {
	    		count++;
	    	} else {
	    		isFlip = false;
	    	}
		}
		if(isFlip && count == x_end - x -1) {
			
			for(int i = x+1; i < x_end; i++) {
				if(isMove) {
					board[i][y] = c;
				}
				canMove = true;
			}
		}
		
		//bottom vertical
		count = 0;
		int x_start = 0;
		isFlip = false;
		for(int i = x-1; i >= 0; i--) {
			
			if(board[i][y] == c) {
				x_start = i;
				isFlip = true;
			    break;
			} else if(board[i][y] == o) {
	    		count++;
			} else {
				isFlip = false;
			}
		}
		if(isFlip && count == x-x_start-1) {
			
			for(int i = x_start+1; i < x; i++) {
				if(isMove) {
					board[i][y] = c;
				}
				canMove = true;
			}
		}
		
		
		//right diagonal - y = -x
		x_end = 0;
		y_end = 0;
		count = 0;
		isFlip = false;
		for(int i = x+1, j= y+1; i < 8 && j < 8; i++, j++) {
		    if(board[i][j] == c) {
		    	x_end = i;
		    	y_end = j;
		    	isFlip = true;
		    	break;
		    } else if(board[i][j] == o) {
	    		count++;
			} else {
		    	isFlip = false;
		    }
			
		}
		if(isFlip && count == x_end - x -1) {
			
			for(int i = x+1, j= y+1; i < x_end && j < y_end; i++, j++) {
				canMove = true;
				if(isMove) {
					board[i][j] = c;
				}
			}
		}
		
		//left vertical
		x_start = 0;
		y_start = 0;
		count = 0;
		isFlip = false;
		for(int i = x-1, j= y-1; i >= 0 && j >= 0; i--, j--) {
		    if(board[i][j] == c) {
		    	x_start = i;
		    	y_start = j;
		    	isFlip = true;
		    	break;
		    } else if(board[i][j] == o) {
	    		count++;
			} else {
		    	isFlip = false;
		    }
			
		}
		if(isFlip && count == x - x_start -1) {
			
			for(int i = x_start+1, j= y_start+1; i < x && j < y; i++, j++) {
				canMove = true;
				if(isMove) {
					board[i][j] = c;
				}
			}
		}
		
		//right diagonal y = x upward
				x_end = 0;
				y_end = 0;
				count = 0;
				isFlip = false;
				for(int i = x-1, j= y+1; i >= 0 && j < 8; i--, j++) {
				    if(board[i][j] == c) {
				    	x_end = i;
				    	y_end = j;
				    	isFlip = true;
				    	break;
				    } else if(board[i][j] == o) {
			    		count++;
					} else {
				    	isFlip = false;
				    }
					
				}
				if(isFlip && count == y_end - y -1) {
					
					for(int i = x-1, j= y+1; i > x_end && j < y_end; i--, j++) {
						canMove = true;
						if(isMove) {
							board[i][j] = c;
						}
					}
				}
				
				//left diagonal
				x_start = 0;
				y_start = 0;
				count = 0;
				isFlip = false;
				for(int i = x+1, j= y-1; i < 8 && j >= 0; i++, j--) {
					
				    if(board[i][j] == c) {
				    	x_start = i;
				    	y_start = j;
				    	isFlip = true;
				    	break;
				    } else if(board[i][j] == o) {
			    		count++;
					} else {
				    	isFlip = false;
				    }
					
				}
				if(isFlip && count == x_start-x-1) {
					
					for(int i = x_start-1, j = y_start+1; i > x && j < y; i++, j++) {
						if(isMove) {
							board[i][j] = c;
						}
						canMove = true;
					}
				}
		return canMove;
	}
	
	//whether the position is occupied or not
	public boolean isOccupy(int x, int y) {
		if(x == -1 || y == -1) {
			return true;
		}
		if(board[x][y] == ' ') {
			return false;
		}
		
		return true;
	}
	
	public boolean onTouchEvent(MotionEvent event) {
	    num=1;
	    // Schedules a repaint.
	    int x = (int)event.getX();
	    int y = (int)event.getY();
	    //System.out.println("james"+x);
	    //get the region where it touched
	    for(int i = 0; i < 8; i++) {
	    	for(int j = 0; j < 8; j++) {
	    		if((x >= point_x[i] && x <= point_x[i]+85) && (y >= point_y[j] && y <= point_y[j]+85)) {
	    			//x_cood = point_x[i];
	    			//y_cood = point_y[j];
	    			x_cood = i;
	    			y_cood = j;
	    		}
	    	}
	    }
	    if(isStart) {
	    	invalidate();
	    	//if it is single player game, let AI move
	    	
	       	 if(isSingle && move_ai == move_p - 1) {
		    		try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    		aiMove();
		    		invalidate();
		    		
		    		if(count == 0) {
		    			try {
							Thread.sleep(400);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		    			aiMove();
		    			invalidate();
		    		}
		    		
		    }
	    }
	    return true;
	  }
 
}