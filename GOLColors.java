/*
 *		My implentation of the Game of Life
 *		Implemented via Elliot Kroo's GifSequenceWriter
 *		For edge behavior, I've treated the outer layer as a sort of buffer
 *		Edge interactions are therefore not stable
 *		This implementation uses three non interacting layers as RGB
 *		While this does not actually change the dynamics of the game, 
 *			it does give more visually interesting outputs.
 *
 *		Eden Carrier 2021
 *
*/

import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.*;
import java.awt.image.*;
import java.io.*;
import java.util.Iterator;
import java.awt.Color;

import GifSequenceWriter;


public class GOLColors{
	
	private static int [][] rboard;
	private static int [][] gboard;
	private static int [][] bboard;
	
	// method which unpacks the source pngs into grids of values
	// color 012 rgb
	public static void readStartBoard( String imgname, int color ){
		try{
			BufferedImage image = ImageIO.read( new File( imgname ) );
			int [][] board = new int[image.getHeight()][image.getWidth()];
			for( int i = 0; i < board.length; i++ ){
				for( int j = 0; j < board[0].length; j++ ){
					int pixelrgb = image.getRGB( j, i );
					Color pixelcolor = new Color( pixelrgb );
					if( pixelcolor.getRed()*(color) == 0 && pixelcolor.getGreen()*(color-1) == 0 && pixelcolor.getBlue()*(color-2) == 0 ){
						board[i][j] = 1;
					}else if( pixelcolor.getRed() == 255 && pixelcolor.getGreen() == 255 && pixelcolor.getBlue() == 255 ){
						board[i][j] = 0;
					}else{
						System.out.println("pixel issue");
					}
				}
			}
			if( color == 0 ){
				rboard = board;
			}else if( color == 1 ){
				gboard = board;
			}else{
				bboard = board;
			}
		}catch( IOException e ){
			System.err.println( "Error attempting to read init file\n" );
			System.exit(1);
		}
	}
	
	// calculates the next iteration from the current boards
	// each color grid is computed separately
	// bulk of grid follows standard rules
	// edges have modified behavior - corners are always set to dead,
	//		and edge cells just use the value of the interior cell theyre next to
	public static void nextGen(){
		for( int c = 0; c < 3; c++ ){
			int [][] board = null;
			if( c == 0 ){
				board = rboard;
			}else if( c == 1 ){
				board = gboard;
			}else{
				board = bboard;
			}
			int [][] newboard = new int[board.length][board[0].length];
			for( int i = 0; i < board.length; i++ ){
				for( int j = 0; j < board[0].length; j++ ){
					if( ( i == 0 || i == board.length - 1 ) && ( j == 0 || j == board.length - 1 ) ){
						newboard[i][j] = 0;
						continue;
					}
					if( i == 0 ){
						newboard[i][j] = board[i+1][j];
						continue;
					}
					if( i == board.length - 1 ){
						newboard[i][j] = board[i-1][j];
						continue;
					}
					if( j == 0 ){
						newboard[i][j] = board[i][j+1];
						continue;
					}
					if( j == board[0].length - 1 ){
						newboard[i][j] = board[i][j-1];
						continue;
					}
					int count = 0;
					count += board[i-1][j];
					count += board[i-1][j-1];
					count += board[i][j-1];
					count += board[i+1][j-1];
					count += board[i+1][j];
					count += board[i+1][j+1];
					count += board[i][j+1];
					count += board[i-1][j+1];
					if( count == 3 || board[i][j] == 1 && count == 2 ){
						newboard[i][j] = 1;
					}else{
						newboard[i][j] = 0;
					}
				}
			}
			if( c == 0 ){
				rboard = newboard;
			}else if( c == 1 ){
				gboard = newboard;
			}else{
				bboard = newboard;
			}

		}
	}
	
	// updates the current BufferedImage to the current grids
	//		so that it can become the next frame of the output gif
	// rgb is layered into eight colors
	public static void nextImg( BufferedImage image){
		Color black = new Color( 0, 0, 0 );
		Color blue = new Color( 0, 0, 255 );
		Color green = new Color( 0, 255, 0 );
		Color teal = new Color( 0, 255, 255 );
		Color red = new Color( 255, 0, 0 );
		Color purple = new Color( 255, 0, 255 );
		Color yellow = new Color( 255, 255, 0 );
		Color white = new Color( 255, 255, 255 );
		Color[] colors = { black, blue, green, teal, red, purple, yellow, white };
		for( int i = 0; i < rboard.length; i++ ){
			for( int j = 0; j < rboard[0].length; j++ ){
				int outval = 4*rboard[i][j] + 2*gboard[i][j] + bboard[i][j];
				image.setRGB( j, i, colors[outval].getRGB() );
			}
		}
	}
	
	// main
	// requires five or six args:
	//		- iterations, the number of generations to computer
	//		- inputRed, the png to use as the starting board for red channel
	//		- inputGreen, the png to use as the starting board for green channel
	//		- inputBlue, the png to use as the starting board for blue channel
	//		- outputImage, the file to write the gif to
	//		- frameDuration, which optionally sets the frame length. uses 100 ms by default
	public static void main( String[]args ) throws Exception{
		if( args.length != 5 && args.length != 6 ){
			System.out.println( "Usage: GOLColors iterations inputRed inputGreen inputBlue outputImage [frameDuration] \n" );
			System.exit(1);
		}
		int delay = (args.length == 6) ? 10*Integer.parseInt( args[5] ) : 100;
		readStartBoard( args[1], 0 );
		readStartBoard( args[2], 1 );
		readStartBoard( args[3], 2 );
		BufferedImage bi = ImageIO.read( new File( args[1] ) );
		nextImg( bi );		// buffered image is initialized to red grid, but updated to rgb before being added to gif
		ImageOutputStream out = new FileImageOutputStream( new File( args[4] ) );
		GifSequenceWriter writer = new GifSequenceWriter( out, bi.getType(), delay, true );
		for( int i = 0; i < 10; i++ ){
			writer.writeToSequence( bi );
		}
		int iter = Integer.parseInt( args[0] );
		for( int i = 1; i < iter; i++ ){
			nextGen();
			nextImg( bi );
			writer.writeToSequence( bi );
		}
		
		writer.close();
		out.close();
	}
}