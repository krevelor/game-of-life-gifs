/*
 *		My implentation of the Game of Life
 *		Implemented via Elliot Kroo's GifSequenceWriter
 *		For edge behavior, I've treated the outer layer as a sort of buffer
 *		Edge interactions are therefore not stable
 *		This is the pure binary implementation. See GOLColors for one which layers rgb channels
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


public class GOL{
	
	private static int [][] board;
	
	// method which unpacks the source png into a grid of values
	// additionally returns the starting board as a BufferedImage 
	//		so it can be used as the first frame of the resulting gif
	public static BufferedImage readStartBoard( String imgname ){
		try{
			BufferedImage image = ImageIO.read( new File( imgname ) );
			board = new int[image.getHeight()][image.getWidth()];
			for( int i = 0; i < board.length; i++ ){
				for( int j = 0; j < board[0].length; j++ ){
					int pixelrgb = image.getRGB( j, i );
					Color pixelcolor = new Color( pixelrgb );
					if( pixelcolor.getRed() == 0 && pixelcolor.getGreen() == 0 && pixelcolor.getBlue() == 0 ){
						board[i][j] = 1;
					}else if( pixelcolor.getRed() == 255 && pixelcolor.getGreen() == 255 && pixelcolor.getBlue() == 255 ){
						board[i][j] = 0;
					}else{
						System.out.println("pixel issue");
					}
				}
			}
			return image;
		}catch( IOException e ){
			System.err.println( "Error attempting to read init file\n" );
			System.exit(1);
		}
		return null;
	}
	
	// calculates the next iteration from the current board
	// bulk of grid follows standard rules
	// edges have modified behavior - corners are always set to dead,
	//		and edge cells just use the value of the interior cell theyre next to
	public static void nextGen(){
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
		board = newboard;
	}
	
	// updates the current BufferedImage to the current grid
	//		so that it can become the next frame of the output gif
	public static void nextImg (BufferedImage image){
		Color black = new Color( 0, 0, 0 );
		Color white = new Color( 255, 255, 255 );
		for( int i = 0; i < board.length; i++ ){
			for( int j = 0; j < board[0].length; j++ ){
				if( board[i][j] == 1 ){
					image.setRGB( j, i, black.getRGB() );
				}else{
					image.setRGB( j, i, white.getRGB() );
				}
			}
		}
	}
	
	// main
	// requires three or four args:
	//		- iterations, the number of generations to computer
	//		- inputImage, the png to use as the starting board
	//		- outputImage, the file to write the gif to
	//		- frameDuration, which optionally sets the frame length. uses 100 ms by default
	public static void main( String[]args ) throws Exception{
		if( args.length != 3 && args.length != 4 ){
			System.out.println( "Usage: GOL iterations inputImage outputImage [frameDuration] \n" );
			System.exit(1);
		}
		int delay = (args.length == 4) ? 10*Integer.parseInt( args[3] ) : 100;
		BufferedImage bi = readStartBoard( args[1] );
		ImageOutputStream out = new FileImageOutputStream( new File( args[2] ) );
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