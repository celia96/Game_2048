//cs201
//final project
//Celia Choy and Shan Zeng
//2048 game 

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class game2048 extends Applet implements ActionListener {

	private static final long serialVersionUID = 1L; // to avoid Eclipse warning

	// private static final LayoutManager FlowLayout = null;
	
    // instance variables

    Label title;         		// label used to show title of game
    Label explain;				// Label for instructions
    Button newgame; 			// Button for starting a new game
    GameCanvas mycanvas;		// Canvas that contains the game
    Image img1, img2, img3;		// Image for firework, congratulations, and gamover 
    AudioClip move, gameoverv;	// Sound that comes out when merge the tile and when the game is over
    
    
    // initializes the applet
    public void init() {
    	
    	setFont(new Font("Arial Black", Font.BOLD, 14));
    	mycanvas = new GameCanvas(this);
    	setLayout(new BorderLayout());
        add(top_panel(), "North");  // add the top panel
        add(mycanvas, "Center");	// add the game canvas
        // retrieve the images
        img1 = getImage(getDocumentBase(), "firework.gif");
        img2 = getImage(getDocumentBase(), "congrat.png");
        img3 = getImage(getDocumentBase(), "gameover.png");
        move = getAudioClip (getCodeBase(), "move.au");
        gameoverv = getAudioClip (getCodeBase(), "gameover.au");
    }
    
    // Panels with title, instruction, and new game button
    public Panel top_panel() {
		Panel p = new Panel();
		p.setLayout(new GridLayout(2, 1));
		p.add(top_panel1());
		p.add(top_panel2());
		return p;
                
	}
    
    // Panel with title 
    public Panel top_panel1() {
    	Panel p = new Panel();
    	p.setLayout(new GridLayout(1, 2));
    	title = new Label("2048", Label.CENTER);
    	title.setBackground(new Color(218,165,32));		// the color you wil get when you reach 2048
    	title.setForeground(Color.white);
    	title.setFont(new Font("Arial Black", Font.BOLD, 30)); 
    	p.add(title);
    	
    	return p;
    }
    
    // Panel with instruction and new game button
	public Panel top_panel2() {
		Panel p2 = new Panel();
    	p2.setLayout(new FlowLayout());
		explain = new Label("Merge the numbers and get the 2048 tile!");
		explain.setFont(new Font("Arial Black", Font.BOLD, 14));
		newgame = new Button("New Game");
		newgame.addActionListener(this);
        newgame.setBackground(Color.gray);
    	newgame.setForeground(Color.white);
    	p2.add(explain);
    	p2.add(newgame);
    	return p2;
	}
	
	
	// perform the newgame button
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == newgame) {
			mycanvas.newgame();
		}
	}

}


// Game Canvas 
class GameCanvas extends Canvas implements KeyListener {
		
	private static final long serialVersionUID = 1L;
	
	// local color constants
	static final Color back1 = new Color(160, 160, 160); // background_border color
	static final Color back2 = new Color(224, 224, 224); // background_box color

	// instance variables
	game2048 parent;		// instance variable to be able to access applet components
	int [][] tiles;       	// 2D array for numbers in the tile
	boolean gameover;		// boolean that tracks if the game is over
    Random random;			

	// initialize the canvas
	public GameCanvas(game2048 s) {
		parent = s;
		setFont(new Font("Arial Black", Font.BOLD, 25));
		tiles = new int[4][4];	
		// every tile starts with 0
		for (int i = 0; i < tiles[0].length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                tiles[i][j] = 0;
            }
		}
		// initial condition when the game is started: two boxes are added and the game is not over
		gameover = false;		
		addbox(2);
		addKeyListener(this);
	    
	}
	
	// add a box in a random place
	public void addbox(int n) {
		ArrayList<Point> empty_space = new ArrayList<>();
		random = new Random();
		// get the coordinates for the empty tiles
		for (int i = 0; i < tiles[0].length; i++) {
            for (int j = 0; j < tiles.length; j++) {
            	if (tiles[i][j] == 0) {
            		empty_space.add(new Point(i, j));
            	}
            }
		}
		
		for (int a = 0; a < n; a++) {
			// store the coordinates in an empty_space array list
			Point p = empty_space.get(random.nextInt(empty_space.size()));
			if (empty_space.isEmpty() ) {
				return;
			} else {
				// add 2 or 4 number tile in an empty space (but 90% chance the number 2 tile will come out
				tiles[(int)p.getX()][(int)p.getY()] = Math.random() < 0.9 ? 2 : 4;
				empty_space.remove(p);	// because it's not empty anymore
			}
		}
	}
	
	// this method is called when the new game button is pressed
	public void newgame() {
		tiles = new int[4][4];
		for (int i = 0; i < tiles[0].length; i++) {
            for (int j = 0; j < tiles.length; j++) {
                tiles[i][j] = 0;
            }
		}
		gameover= false;
		repaint();
		addbox(2);
	}
	
	// painting everything in canvas: the tiles, borders, and numbers
	public void paint(Graphics g) {
		Dimension d = getSize();        // size of canvas
		
		int total_length = d.width/4;							// the length of outer border of the tiles
		int border_length = (d.width/4)/10;						// the length of the border
		int box_length = total_length - (border_length * 2);	// the length of the tile
		
		g.setColor(back1);
		g.fillRect(0, 0, d.width, d.width);
		
		for (int j = 0; j < tiles[0].length; j++) {
			for (int i = 0; i < tiles.length; i++) {	
				// get the value of the tile and change it into string
				String number = String.valueOf(tiles[i][j]);
				g.setColor(checkColor(tiles[i][j]));
				g.fillRect(i * total_length + border_length, j * total_length + border_length, box_length, box_length);
            	// calculate the x, y coordinates of the numbers
				int x = (total_length * i) + (total_length)/2; 
            	int y = (total_length * j) + (total_length)/2;
            	// make 0 invisble by making its color same as the background
            	if (tiles[i][j] == 0) {
            		g.setColor(back2);
            	} else if (tiles[i][j] == 2 || tiles[i][j] == 4) {	// for number 2 and 4 tile, it will have black color font
            		g.setColor(Color.black);
            	} else {	// other number tiles will have white color font
            		g.setColor(Color.white);
				}
            	// center the number
            	centerString(g, number, x, y); 
			}
		}
		// change the instruction and update the goal
		changegoal(g);
		
		// If the game is over, it will show a game over image
		if (gameover == true) {
			g.drawImage(parent.img3, d.width/4, total_length, d.width/2, d.height/2, this);
			parent.explain.setText("You Lost! Try again!");
		}
		
    }
	
	// method to center the string
	public static void centerString(Graphics g, String s, int x, int y) {
        FontMetrics fm = g.getFontMetrics(g.getFont());
        int xs = x - fm.stringWidth(s)/2 + 1;
        int ys = y + fm.getAscent()/3 + 1;
        g.drawString(s, xs, ys);
    }

	// Update the goal as the games proceeds
    public void changegoal(Graphics g) {	
    	Dimension d = getSize(); 
    	int max = tiles[0][0];
    	// check the biggest number tile
    	for (int j = 0; j < tiles[0].length; j++) {
			for (int i = 0; i < tiles.length; i++) {	
				if (tiles[i][j] >= max)
					max = tiles[i][j];
			}
		}
    	if (max == 8) 
       		parent.explain.setText("Your next goal is to get to the 16 tile!");
    	else if (max == 16)
    		parent.explain.setText("Your next goal is to get to the 32 tile!");
    	else if (max == 32) 
    		parent.explain.setText("Your next goal is to get to the 64 tile!");
    	else if (max == 64)
			parent.explain.setText("Your next goal is to get to the 128 tile!");
		else if (max == 128)
			parent.explain.setText("Your next goal is to get to the 256 tile!");
		else if (max == 256)
			parent.explain.setText("Your next goal is to get to the 512 tile!");
		else if (max == 512)
			parent.explain.setText("Your next goal is to get to the 1024 tile!");
		else if (max == 1024)
			parent.explain.setText("Your next goal is to get to the 2048 tile!");
		else if (max == 2048) {
			parent.explain.setText("Congratulations! You reached the Goal!");
			g.drawImage(parent.img1, d.width/6, d.height/9, (d.width/3)*2, (d.height/3)*2, this);
			g.drawImage(parent.img2, d.width/4, d.height/4, d.width/2, d.height/3, this);
		} else
			parent.explain.setText("Merge the numbers and get the 2048 tile!");
    }
	
	// assign different colors for different number
	public Color checkColor(int n) {
		if (n == 0)
			return back2;
		else if (n == 2)
			return new Color(255,248,220);
		else if (n == 4)
			return new Color(245,222,179);
		else if (n == 8)
			return new Color(255,178,102);
		else if (n == 16)
			return new Color(255, 128, 0);
		else if (n == 32)
			return new Color(255,99,71);
		else if (n == 64)
			return new Color(255,69,0);
		else if (n == 128)
			return new Color(249,218,19);
		else if (n == 256)
			return new Color(48,215,113);
		else if (n == 512)
			return new Color(36,196,89);
		else if (n == 1024)
			return new Color(101,156,18);
		else if (n == 2048)
			return new Color(218,165,32);
		else
			return back1;
	}
			
	//method to remove all 0s when down key is pressed 
	//remove all tracing zeros at the end of the array
	public void remove0(int[][] t) {
		for (int j = 0; j < 4; j++) {
		
			if (t[j][3]==0 ){ 
				if (t[j][2]==0){
					if (t[j][1]==0){
						//remove 3 trailing 0s 
					
						if (t[j][0]!=0){
					
							t[j][3]=t[j][0];
							t[j][0]=0;
						}
						else {
							
							t[j][3] = 0; ////a place holder, can delete?
						}
					}
					else{
						//remove 2 tracing 0s
				
						t[j][3]= t[j][1];
						t[j][1]=t[j][0];
						t[j][0]=0;
					}
				}
				else {
					//remove 1 tracing 0
			
					t[j][3]= t[j][2];
					t[j][2]= t[j][1];
					t[j][1]=t[j][0];
					t[j][0]=0;
				}
			}		
		}
	
			for (int j= 0; j < 4; j++) {
		
				//remove zero in between 
				if (t[j][3] != 0 && t[j][2] == 0 
						&& t[j][1] == 0 && t[j][0] != 0) {
			
					t[j][2] = t[j][0];
					t[j][0]=0;
				}
				
				if (t[j][3] != 0 && t[j][2] != 0 
						&& t[j][1] == 0 && t[j][0] != 0) {
					
					t[j][1] = t[j][0];
					t[j][0]=0;
				}
		
				if (t[j][3] != 0 && t[j][2] == 0 && t[j][1] != 0){
			
					t[j][2]=t[j][1];
					t[j][1] = t[j][0];
					t[j][0]=0;
				}
			}
	
	}

	//check if any tile has been moved 
	public boolean noMove(int[][] x, int[][] y){
		for (int i=0; i<4; i++)
			for (int j=0;j<4;j++)
				if (x[i][j] != y[i][j])	//if some tile is moved 
					return false;  
		//if all tiles are the same 
		return true;
		
	}
	//check if any tile has been moved: return false if not full, return true if full
	public boolean full(int[][] x){
		for (int i=0; i<4; i++)
			for (int j=0;j<4;j++)
				if (x[i][j] == 0)	//if some tile is not filled
					return false;  
		//if all tiles are the same 
		return true;
	}
	//test if there can be any more move, save a copy of pre-test tiles
	public void gameOver(){		
		int[][] ini = new int[4][4];
		for (int i=0; i<4; i++)
			for (int j=0;j<4;j++)
				ini[i][j] = tiles[i][j];
		//simulation of moving all directions
		moveDown();
		moveUp();
		moveLeft();
		moveRight();
		//if after any possible move
		//the tiles are not changed
		if (noMove(ini,tiles)&& (full(tiles))){
			gameover = true;
			parent.gameoverv.play();
			}
		//convert tiles back to pre-test state 
		for (int i=0; i<4; i++)
			for (int j=0;j<4;j++)
				tiles[i][j] = ini[i][j];
	}
	// move method that takes in an it[][]
	public void move(int[][] t) {
		remove0(t);	//first remove all zeros
		for (int j = 0; j < 4; j++) {
			for (int i = 3; i >0; i--) {	
				if (t[j][i-1]==t[j][i]) {
					t[j][i] = 2*t[j][i];
					t[j][i-1]=0;
				}
			}
		}
		remove0(t);	//again remove all zeros

	}

	// called when down key is pressed
	public void moveDown(){
		// make a copy of tiles before they are moved
		int[][] original = new int[4][4];
		for (int i=0; i<4; i++)
			for (int j=0;j<4;j++)
				original[i][j] = tiles[i][j];
		move(tiles);
		// check whether the tiles were moved or not
		// If moved, add 1 box
		if (!noMove(original,tiles)) {
			addbox(1);
			parent.move.play();	// play this audio file whenever the tiles are merged
		}
		repaint();
		
	}
	// called when up key is pressed
	public void moveUp(){
		int[][] original = new int[4][4];
		for (int i=0; i<4; i++)
			for (int j=0;j<4;j++)
				original[i][j] = tiles[i][j];
		// Transform the tiles to make them as if the down key is pressed
		int[][] tilesCopy = new int[4][4];	
		for (int i = 0; i <4; i++) {
			tilesCopy[i][0]= tiles[i][3];
			tilesCopy[i][1]= tiles[i][2];
			tilesCopy[i][2]= tiles[i][1];
			tilesCopy[i][3]= tiles[i][0];
		}
		// call the move method
		move(tilesCopy);
		// Transform them back into how they are supposed to be
		for (int i = 0; i <4; i++) {
			tiles[i][0]= tilesCopy[i][3];
			tiles[i][1]= tilesCopy[i][2];
			tiles[i][2]= tilesCopy[i][1];
			tiles[i][3]= tilesCopy[i][0];
		}
		if (!noMove(original,tiles)) {
			addbox(1);
			parent.move.play();
		} 
		repaint();
	
	}
	// called when right key is pressed
	public void moveRight(){
		int[][] original = new int[4][4];
		for (int i=0; i<4; i++)
			for (int j=0;j<4;j++)
				original[i][j] = tiles[i][j];
		int[][] tilesCopy = new int[4][4];	
		for (int j = 0; j <4; j++) {
			tilesCopy[0][j]= tiles[j][3];
			tilesCopy[1][j]= tiles[j][2];
        	tilesCopy[2][j]= tiles[j][1];
        	tilesCopy[3][j]= tiles[j][0];
		}
		move(tilesCopy);
		for (int  j= 0; j <4; j++) {
			tiles[j][0]= tilesCopy[3][j];
			tiles[j][1]= tilesCopy[2][j];
			tiles[j][2]= tilesCopy[1][j];
			tiles[j][3]= tilesCopy[0][j];
		}
		if (!noMove(original,tiles)) {
			addbox(1);
			parent.move.play();
		} 
		repaint();
	
	}
	// called when left key is pressed
	public void moveLeft(){
		int[][] original = new int[4][4];
		for (int i=0; i<4; i++)
			for (int j=0;j<4;j++)
				original[i][j] = tiles[i][j];
		int[][] tilesCopy = new int[4][4];	
		for (int j = 0; j <4; j++) {
			tilesCopy[j][0]= tiles[3][j];
			tilesCopy[j][1]= tiles[2][j];
			tilesCopy[j][2]= tiles[1][j];
			tilesCopy[j][3]= tiles[0][j];
		}
		move(tilesCopy);
		for (int  j= 0; j <4; j++) {
			tiles[0][j]= tilesCopy[j][3];
			tiles[1][j]= tilesCopy[j][2];
			tiles[2][j]= tilesCopy[j][1];
			tiles[3][j]= tilesCopy[j][0];
		}
		if (!noMove(original,tiles)) {
			addbox(1);
			parent.move.play();
		}
		repaint();
	
	}
		
	public void keyTyped(KeyEvent e) {

		
	}

	public void keyPressed(KeyEvent e) {
	    int keyCode = e.getKeyCode();               
        if (keyCode == KeyEvent.VK_UP) { 
            moveUp();
        } else if (keyCode == KeyEvent.VK_DOWN) {
        	 moveDown(); 
        } else if (keyCode == KeyEvent.VK_LEFT ) {
        	 moveLeft();
        } else if (keyCode == KeyEvent.VK_RIGHT ) {
        	 moveRight();
        }
        gameOver(); // check if the game is over
        repaint();
    }

	public void keyReleased(KeyEvent e) {
		
	}
	
}
