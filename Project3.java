import java.awt.Canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.Random;
import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.awt.Font;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/*
 * 
 * Project 3
 * 
 * Odebiyi James
 * CS 3345
 * 
 * This program randomly generates a maze using the wall breaking algorithm and at the press of a button draws the path and also
 * gives the output in the form of SEN....
 * 
 * 
 *  a, b and bonus are all solved.
 * 
 * 
 * 
 */

public class Project3 extends JPanel 
{
	// Declare and initialize variables to display onto the screen	

    public static Window mazeWindow = null;
    public static Project3 mazePanel = null;
    
	
	static int window_width = 600;
	static int window_height = 600;
	static long randomSeed = System.currentTimeMillis(); // this is the seed value for the random number generator, it will be the same throughout calls of function

	// declare variables to hold the width, height of the maze as well as the cell width
	
	public static int MAZE_WIDTH = 0;
	public static int MAZE_HEIGHT = 0; 
	public static int xOffset = 0;
    public static int yOffset = 0; 
    public static int size; // this is the number of elements in the maze
    public static boolean buttonHighlighted = false; // button variable which determines whether something is highlighted or not
    
    public static Cell [][] maze; // array for holding the information of each cell in the array, it will be used to output to the screen

	public static String choice = null; // variable that represents the choice of the user during user input
	public static boolean drawPath = false; // set to true when its time to draw the path
	public static String outputPath; // the final path will be stored in a string which be used to draw onto the screen
	

public Project3(int width, int height, int maze_width, int maze_height, int xOffset, int yOffset) 
{
	this.MAZE_WIDTH = maze_width;
	this.MAZE_HEIGHT = maze_height;
	this.xOffset = xOffset;
	this.yOffset = yOffset;
	setPreferredSize(new Dimension(width, height));
	setFocusable(true);

}
public Project3() { }

// window class 
public static class Window extends JFrame implements MouseListener{
	
public JFrame frame; // window
public Canvas canvas; // canvas to draw the grid on
public int width;
public int height;
	
	public Window(String title, int width, int height, Project3 mazePanel) 
	{
		
		this.width = width;
		this.height = height;
		canvas = new Canvas();
	
		frame = new JFrame(title); // create Jframe object and initilize properties of window
		frame.setContentPane(mazePanel);
		frame.getContentPane().addMouseListener(this);
		
		frame.setSize(this.width, this.height);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(canvas); // add canvas to frame
		frame.pack();
		
	}
	// Mouse methods
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		if(buttonHighlighted == false) // if button is clicked then draw the path
		{
		if(e.getX() >= xOffset + (window_width/3) && e.getX() < xOffset + (window_width/3) + 100 && e.getY() >= window_height - 40 && e.getY() <= window_height - 10 )
			{
			buttonHighlighted = true;
		
			drawPath = true;
			mazePanel.revalidate();
			mazePanel.repaint();
			}
		}
		
		
		
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	//mouse listener class for button click

}


// create class for cell

public static class Cell
{
	
	// each cell has booleans which determines if a particular wall of the cell is present or not
	
	public boolean northPresent = true;
	public boolean southPresent = true;
	public boolean westPresent = true;
	public boolean eastPresent = true;

	// boolean variables to determine start and end point of the maze
	
	public boolean isStart = false;   
	public boolean isEnd = false;
	
	// declare and initialize path searching variables
	
	public String pathToEnd = "PATH: ";
	
	// store it's position in the maze
	
	public int index = 0;
	
	//
	
	public Cell(boolean isStart, boolean isEnd) // pass in starting values to determine whether a wall exists or not
	{
	
		if(isStart == true) // set the west and north walls of the starting cell to open
		{
			this.isStart = true; 
			this.index = 0;
			northPresent = false;
			westPresent = false;	
		}
		else if(isEnd == true) // set the south and east walls of the end cell to open
		{
			this.isEnd = true;
			this.index = size - 1;
			southPresent = false;
			eastPresent = false;
		}	
		
	}

	public Cell(int index) 
	{ // default constructor for a cell
		this.index = index;
		isStart = false;
		isEnd = false;
	}
	// constructor for file input
	
	public Cell(boolean n, boolean e, boolean s, boolean w, int index)
	{
		this.index = index;
	
		if(index == 0) isStart = true;
		else if(index == size - 1) isEnd = true;
		
		northPresent = n;
		eastPresent =  e;
	    southPresent = s;
		westPresent =  w;
		
		
	}

}

public void paintComponent(Graphics g) // paint component which draws the maze
{
	

	super.paintComponent(g);
 // draw resultant maze

	// first draw the background color
	
	g.setColor(Color.white);
	g.fillRect(0,0,window_width, window_height);
	
	if(drawPath == true) drawPath(g); // draw path if it is necessary
 
	drawMaze(g);
	
	// draw GUI for drawing path
	
	if(buttonHighlighted == false) g.setColor(Color.green);
	else g.setColor(Color.red);
	g.fillRoundRect(xOffset + (window_width / 3), window_height - 40, 100, 30, 20, 20);
	g.setColor(Color.black);
	Font buttonFont = new Font ("Impact", 1, 17);
	g.setFont(buttonFont);
	g.drawString("Draw Path", xOffset + (window_width / 3) + 15, window_height - 20);

	

g.dispose();

}

public void drawMaze(Graphics g)
{
	
	
	g.setColor(Color.black);
	Integer counter = 0; // for displaying the cell number
	String s;
	// draw the maze using each cell's data


	 for(int y = yOffset, j = 0; j < MAZE_HEIGHT; j++, y += yOffset)
		{
	
	for(int x = xOffset, i = 0; i < MAZE_WIDTH; i++ , x += xOffset)
	{
	
			if(maze[i][j].northPresent == true) g.drawLine(x, y, x + xOffset, y);
		    if(maze[i][j].southPresent == true) g.drawLine(x, y + yOffset, x + xOffset, y + yOffset);
			if(maze[i][j].westPresent == true)  g.drawLine(x, y, x, y + yOffset);
			if(maze[i][j].eastPresent == true)  g.drawLine(x + xOffset, y, x + xOffset, y + yOffset);
		
		counter++;
		}
	
		}

	
}

public static void generateMaze() 
{
	// generate maze using wall breaking algorithm. 
	// for the bonus question
	
	// initialize random variable to hold a random value between 0 and the size the of the maze , 0 included
	Random random = new Random(randomSeed);
	// initialize disjoint set data structure representation of maze
	
	DisjointSet mazeDisjointSet = new DisjointSet(size);
	  
	// declare variables to select random cell and wall as well as to hold current cell and neighboring cell

	int randomCell, randomWall, currentCell, neighborCell;
	
	int root1 = 0, root2 = 0; // roots of the path of a cell, used to check if cells are in the same path
	int numOfElements = size; // set the number of elements

	while(numOfElements > 1) // perform the wall breaking until there is a path between every cell in the maze
	{
		
		
		randomCell = random.nextInt(size); // select a random cell
		
		// select a random wall to knock down
		
		randomWall =  random.nextInt(4);
		
		// declare variables to hold the randomly selected cell, its neighbor in the direction of the wall and a temp variable
		
		currentCell = randomCell;
	
	switch(randomWall)
	{
	
	/*
	 * 0 - north wall
	 * 1 - east wall
	 * 2 - south wall
	 * 3 - west
 	 */
	
	// declare pointers to point to the current node and neighboring cell infront of the wall to be destroyed

	case(0): // if the north wall is selected, check if it isn't already connected to the neighbor above it (same root)
	
		if(currentCell - MAZE_WIDTH > 0) // ensure index is within bounds
			{
			neighborCell = currentCell - MAZE_WIDTH; // go to cell above current
	
		// check if it's not already in the same set (path)
	
			root1 = mazeDisjointSet.find(neighborCell); 
			root2 = mazeDisjointSet.find(currentCell);
		
		if(root1 != root2) // if both cells are not in the same path then knock down the wall and merge cells
		{
			// join them together and then destroy wall
			
			mazeDisjointSet.union(root1, root2); 
			maze[currentCell % MAZE_WIDTH][currentCell / MAZE_WIDTH].northPresent = false; // destroy the north wall of the current cell
			maze[neighborCell % MAZE_WIDTH][neighborCell / MAZE_WIDTH].southPresent = false; // destroy the south wall of the neightbor cell
			numOfElements--;
		}
			}

	break;
		
	case(1): // if the east wall is selected, check if it isn't already connected to the neighbor to the right (same root)
		
		if(currentCell + 1 < size) // ensure index is within bounds 
			{
			neighborCell = currentCell + 1; // go to the cell to the right of current
	
		// check if it's not already in the same set (path)
			
			//ensure the neigboring cell is on the same row
		if(neighborCell / MAZE_WIDTH != currentCell / MAZE_WIDTH) break;
	
			root1 = mazeDisjointSet.find(neighborCell);
			root2 = mazeDisjointSet.find(currentCell);
		
		if(root1 != root2) // if both cells are not in the same path then knock down the wall and merge cells
		{
			
			mazeDisjointSet.union(root1, root2);
			maze[currentCell % MAZE_WIDTH][currentCell / MAZE_WIDTH].eastPresent = false; // destroy the east wall of the current cell
			maze[neighborCell % MAZE_WIDTH][neighborCell / MAZE_WIDTH].westPresent = false; // destroy the west wall of the neightbor cell
			numOfElements--;
		}
			}

	break;

	
	}
	}
	


}


public static void fileInput()  // This file inputs
{
    // declare and initialize an array of characters to store the boolean value that presents the presence of a wall
     // String which will be converted to char array
    String line;
    Boolean n, e, s, w; // boolean values that represent the individual walls of the cell
    /*
     cellData[0]: northPresent ?
     cellData[1]: eastPresent ?
     cellData[2]: southPresent ?
     cellData[3]: westPresent ?
    */
    
    // initialize variables for file input
    File mazeFile = null;
    Scanner fileScanner = null;
    Scanner inputScanner = null; //scanner to get file name
    String fileName = null;
    
    // prompt the user to enter in a fileName
    System.out.println("Enter in a file name");
    inputScanner = new Scanner(System.in);
    fileName = inputScanner.next();
 
     // initialize input file object
     try
     {
   mazeFile = new File(fileName);
     // initialize scanner instance to read file in java
     
    fileScanner = new Scanner(mazeFile);
     
     // read each line one by one
     
    // first two lines are maze width and then maze height
    
    line = fileScanner.nextLine();
    MAZE_WIDTH  = Integer.parseInt(line);
    line = fileScanner.nextLine();
    MAZE_HEIGHT = Integer.parseInt(line);
    
	// Initialize maze

	maze = new Cell[MAZE_WIDTH][MAZE_HEIGHT];
	
	size = MAZE_WIDTH * MAZE_HEIGHT; // number of cells in maze

	xOffset = window_width /(MAZE_WIDTH+2); // adjust the size of the cells depending on the number of them
    yOffset = window_height /(MAZE_HEIGHT+2); 
    int position = 0;
     while(fileScanner.hasNextLine()) // while there is still input
    {  

  
for(int j = 0; j < MAZE_HEIGHT; j++)
{	
    for(int i = 0; i < MAZE_WIDTH; i++)
    {
    	
        
       line = fileScanner.nextLine();
       // enter in the properties on the maze using data from the text file
       
       // convert each value into a boolean
       
       n = line.charAt(0) == '1';
       e = line.charAt(1) == '1';
       s = line.charAt(2) == '1';
       w = line.charAt(3) == '1';
       
         maze[i][j] = new Cell(n, e, s, w, position++); // initilize every other cell 
        
    }
}
        
    }
    
     }
     catch(Exception ex)
     {
    	 System.out.println("ERROR! INVALID FILE FORMAT!");
    	  ex.printStackTrace();
       
         
     }
 
}
  
public static void fileOutput()
{
	// Prompt the suer to enter in a file name
	
	System.out.println("Enter in a file name: ");
	Scanner scanner = new Scanner(System.in); // initialzie scanner object for user input

	try
	{
		// get a file name
		String fileName = scanner.next(); 
		// initialize the object wrapping the file to be written to
		FileWriter outFile = new FileWriter(fileName);
		
		// create print writer object
		
		PrintWriter printWriter = new PrintWriter(outFile);
		
		// first output the width and height
		
		printWriter.println(MAZE_WIDTH);
		printWriter.println(MAZE_HEIGHT);
		
		// output the maze onto the file
		
		for(int j = 0; j < MAZE_HEIGHT; j++)
		{
			
			for(int i = 0; i < MAZE_WIDTH; i++)
			{
				// print a 1 or 0 depending on the presence of a wall
				
			printWriter.print(maze[i][j].northPresent ? 1 : 0);
			printWriter.print(maze[i][j].eastPresent  ? 1 : 0);
			printWriter.print(maze[i][j].southPresent ? 1 : 0);
			printWriter.print(maze[i][j].westPresent  ? 1 : 0);
			
			// Go to next line unless on the last  cell

			if(!(i == MAZE_WIDTH - 1 && j == MAZE_HEIGHT - 1)) printWriter.print("\n");
				
			}
		}
		
		printWriter.close();
	}
	catch(Exception ex)
	{
		
		System.out.println("Invalid Input!");
		ex.printStackTrace();
	}
	
	
}
    
// this function finds the path from the starting cell to the exit cell and outputs it as a string in the form of NWSE , N meaning go North , W meaning go West etc.

public static String pathFinder(int length, Cell start)
{
	
	// declare node for current cell
	
	Cell currentCell;
	// declare nodes for neighbors
	
	Cell northCell = null;
	Cell eastCell = null;
	Cell southCell = null;
	Cell westCell = null;
			
// queue for BFS of next nodes
	
	LinkedList<Cell> queue = new LinkedList<Cell>();
	queue.add(start);

	// set up distances array
	
	int distances[] = new int[length + 1];
	Arrays.fill(distances,-1);
	
	distances[start.index] = 0;
	int index = 0; // represents the index of the current cell
	int north_index = 0; // represents the index of the north neighbor
	int east_index = 0; // represents the index of the east neighbor
	int south_index = 0; // represents the index of the south neighbor
	int west_index = 0; // represents the index of the south neighbor

	
	// while queue is still full
	
	while(!queue.isEmpty())
	{
		
	
		currentCell = queue.poll(); // remove the first element in queue
		
		index = currentCell.index;

		// store the addresses of the neigbors in all directions
	
		northCell = (north_index = index - MAZE_WIDTH) > 0 ? maze[north_index % MAZE_WIDTH][north_index / MAZE_WIDTH] : null;
		eastCell  = (east_index = index + 1) < size ? maze[east_index % MAZE_WIDTH][east_index / MAZE_WIDTH] : null;
		southCell = (south_index = index + MAZE_WIDTH) < size ? maze[south_index % MAZE_WIDTH][south_index / MAZE_WIDTH] : null;
		westCell  = (west_index = index - 1) > 0 ? maze[west_index % MAZE_WIDTH][west_index / MAZE_WIDTH] : null;

	
		if(northCell != null) {
		if(distances[northCell.index] == -1 && canGo(currentCell, northCell, 'n'))
		{	
	           distances[northCell.index] = distances[currentCell.index] + 1; // mark as visited
	           
               //append direction to path for the next node
	           
               northCell.pathToEnd = currentCell.pathToEnd + "N";
               

               // if desination has been found, return the total path string
               
               if(northCell.isEnd == true) return northCell.pathToEnd;
               
               queue.add(northCell); // add the north cell to the queue

		}
		}
		
		// check east neighbor if there is an empty wall
		
		if(eastCell != null)
		{
		if(distances[eastCell.index] == -1 && canGo(currentCell, eastCell, 'e'))
		{	
	
	           distances[eastCell.index] = distances[currentCell.index] + 1; // mark as visited
	           
               //append direction to path for the next node
	           
               eastCell.pathToEnd = currentCell.pathToEnd + "E";

               // if desination has been found, return the total path string
               
               if(eastCell.isEnd == true) return eastCell.pathToEnd;
               
               queue.add(eastCell); // add the north cell to the queue

		}
		}
		
		// check south neighbor
		if(southCell != null)
		{
		if(distances[southCell.index] == -1 && canGo(currentCell, southCell, 's'))
		{	
	           distances[southCell.index] = distances[currentCell.index] + 1; // mark as visited
	           
               //append direction to path for the next node
	           
               southCell.pathToEnd = currentCell.pathToEnd + "S";
              
               
               // if desination has been found, return the total path string
               
               if(southCell.isEnd == true) return southCell.pathToEnd;
               
               queue.add(southCell); // add the north cell to the queue

		}
		}
		
		// check west neighbor
		if(westCell != null)
		{
		if(distances[westCell.index] == -1 && canGo(currentCell, westCell, 'w'))
		{	
	           distances[westCell.index] = distances[currentCell.index] + 1; // mark as visited
	           
               //append direction to path for the next node
	           
               westCell.pathToEnd = currentCell.pathToEnd + "W";
               
         
               // if desination has been found, return the total path string
               
               if(westCell.isEnd == true) return westCell.pathToEnd;
               
               queue.add(westCell); // add the north cell to the queue

		}
		}
		
	
	}
	
	
	return "No path has been found";
	

}
// is true if there is a path between current cell and neighbor cell
public static boolean canGo(Cell currentCell, Cell neighborCell, char direction)
{
	

	switch(direction) // check if the walls between the current cell and the neighboring cell in that direction are gone
	{

	case('n'):
		
	return (!currentCell.northPresent && !neighborCell.southPresent);
		
	case('e'):
		
	return (!currentCell.eastPresent && !neighborCell.westPresent);
	
	case('s'):
		
	return (!currentCell.southPresent && !neighborCell.northPresent);
		
	case('w'):
		
	return (!currentCell.westPresent && !neighborCell.eastPresent);
	
	}
	
	return false;
}

// this function will draw the path from the starting cell to the end

public static void drawPath(Graphics g) 
{

	g.setColor(Color.LIGHT_GRAY);
	// set color of graphics to gray
	
	// convert path string into char array
	
	char pathArray[] = outputPath.toCharArray();
	
	// draw the path using the string
	
	for(int i = 0, x = xOffset, y = yOffset; i < pathArray.length; i++)
	{
		
		
		// fill the current cell in the path

	
		g.fillRect(x, y, xOffset, yOffset);
	
		// select the next cell to go to
		
		switch(pathArray[i])
		{
		
		case('N'):
			
		y = y - yOffset; // go top cell
			
		break;
		
		case('S'):
			
		y = y + yOffset;  // go to bottom cell	
			
		break;
		
		case('W'):
			
		x = x - xOffset;  // go to left cell
			
		break;
		
		case('E'):
		
		x = x + xOffset;  // go to right cell
		
		break;
		
		}
		
		if(i == pathArray.length - 1)
		{
		// paint the last cell as well
	
		g.fillRect(x , y, xOffset, yOffset);
	
		}
		
		
	}
	

	

}


public static void main(String [] args)
{
	
	
	// initialize scanner object for input
	
	Scanner inputScanner = new Scanner(System.in);
	
	// variable for input validation
	
	boolean proceed = false;


	// ask user if he wants to generate a maze or get one from a file
	
	System.out.println("Welcome to the Maze Generation Program \n ");
	System.out.println("Do you wish to: \n \n1) Input maze from a file and output the path \n \n2) Randomly generate a maze and display path at the press of a button \n ");

	do // perform input validation on choice
	{
		System.out.println("Enter 1 or 2 :");
	
		choice = Integer.toString(inputScanner.nextInt()); // if choice is not either 1 or 2 then try again
		
	if(choice.charAt(0) < 49 || choice.charAt(0) > 50) 
		{
			System.out.println("Oops! Thats is not a valid option!");
			System.out.println(choice.charAt(0));
		
			continue;
		}
		
	
	proceed = true; // set proceed to true to exit input validation
	
	}
	while(proceed == false);
	
	// reset proceed to false for next input validation
	
	proceed = false;
	
	switch(choice)
	{
	
	case("1"):

		
	// ask the user maze dimensions
	
	do
	{
		
	System.out.println("Enter in the dimensions of the maze: ");
	
	
	try 
	{
		MAZE_WIDTH = inputScanner.nextInt();
		MAZE_HEIGHT = inputScanner.nextInt();
		
	}
	catch(Exception e)
	{
		System.out.println("ERROR! /n Invalid Dimensions!");
		continue;
		
	}
	
	proceed = true;
	
	}while(proceed == false);
	// reset input validation
	proceed = false;
	
		// initialize variables for outputting to the canvas

	 xOffset = window_width /(MAZE_WIDTH+2); // adjust the size of the cells depending on the number of them
     yOffset = window_height /(MAZE_HEIGHT+2); 
	


	// declare and initialzie maze
	
	
	maze = new Cell[MAZE_WIDTH][MAZE_HEIGHT];
	
	size = MAZE_WIDTH * MAZE_HEIGHT; // number of cells in maze
	

	// generate array for maze
	int position = 0;
	for(int j = 0; j < MAZE_HEIGHT; j++)
	{			
	for(int i = 0; i < MAZE_WIDTH; i++)
	{
	
			if(i == 0 && j == 0) maze[i][j] = new Cell(true, false); // initialize the starting point
			else if(i == MAZE_WIDTH - 1 && j == MAZE_HEIGHT - 1) maze[i][j] = new Cell(false, true); // initialize the end point
			else maze[i][j] = new Cell(position); // initilize every other cell
			
			position++; // incremement index
	}
	}
	
	// generate maze
	

	generateMaze();
		
	// display output onto screen
	
	// Initiliaze content pane object
	
	
    mazePanel = new Project3(window_width,window_height, MAZE_WIDTH, MAZE_HEIGHT, xOffset, yOffset);
	

	// Set up output window
	
	 mazeWindow = new Window("Maze Generator", window_width, window_height, mazePanel);
	 
	
	break;
	
	case("2"): // display from file

	
		fileInput();

	// Initiliaze content pane object
	
	
	mazePanel = new Project3(window_width,window_height, MAZE_WIDTH, MAZE_HEIGHT, xOffset, yOffset);
	mazeWindow = new Window("Maze Generator", window_width, window_height, mazePanel);
		
	
		break;
	
	
	}
	
	
	// after the maze is generate either randomly or from a file, ask the user to Q1) Output path to terminal in the form NSWE 
	// Q2) Draw the path to the end of the maze 

	
	// store path in a string called path
	
	outputPath = pathFinder(size, maze[0][0]);
	
	
	System.out.println("Maze has been generated!");

	// if there user presses the button then draw path
	choice = "n";
	
	System.out.println("\n1) TO OUTPUT PATH TO TERMINAL \n2) TO OUTPUT MAZE TO FILE");
	do // perform input validation on choice
	{
		
			choice = inputScanner.next();
			
		
		if(choice.charAt(0) < 49 || choice.charAt(0) > 50)
		{
			
			System.out.println("Wrong input, try again");
			continue;
		}


		proceed = true;
		
		
	}
	while(proceed == false);
	
	proceed = false;
	
	switch(choice.charAt(0))
	{
	
	
	case('1'):
		

	System.out.println(outputPath);

		
	break;
	
	case('2'):
		
	fileOutput();
		
	break;
	}
	
	
	


}

// This part holds the disjoint set ADT

public static class DisjointSet 
{


	// disjoint set constructor
	
	// construct the disjoint set object
	
	public int [ ] s; // array for the disjoint set
	
	public DisjointSet(int numElements)
	{
		s = new int [numElements];
		for(int i = 0; i < s.length;  i++)
		{
			s[i] = -1;
		}
		

		
	}
	
	// merge the two trees by making the parent link of one tree's root link to the other tree
	// Assuming root1 and root2 are distinct and represent set names
	
	public void union(int root1, int root2)
	{
		// if root2 is deeper, make root2 the new root
		
			   if( s[ root2 ] < s[ root1 ] )  
			         s[ root1 ] = root2;        
			    else
			      {  // change the height if they are the same and make root 1 the new root
			         if( s[ root1 ] == s[ root2 ] )
			            s[ root1 ]--;          
			         s[ root2 ] = root1;       
			      }

			   
	}
	
	// return the representative of the set containing x
	
	public int find(int x)
	{
			if(s[x] < 0) // x is the root of the set it is in
				return x;
			else 
				return find( s[x] ); // recursively climb up the tree to the root, this is the representative of the setr
	}
		
}



}
