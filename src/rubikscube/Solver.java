package rubikscube;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import java.util.PriorityQueue;


public class Solver {

	private static class SearchNode implements Comparable<SearchNode> {
		
		int Cost; // F = G + H
		int DistFromStart; // G
		int Heuristic; // H
		RubiksCube CurrState; // This is state of the cube
		String path; // Stores the instructions that it took to get to this specific state

		public SearchNode( int DistFromStart, int Heuristic, RubiksCube Curr_State, String path){
			this.DistFromStart = DistFromStart;
			this.Heuristic = Heuristic;
			this.CurrState = Curr_State;
			this.path = path;
			Cost = DistFromStart + Heuristic;
		}

		public int compareTo( SearchNode other ){
			if( this.Cost > other.Cost ) return 1; // The 1 represents true that our cost is greater than the other cost
			else if ( this.Cost < other.Cost ) return -1; // The -1 represents that the other cost function is greater than this cost
			else return 0; // The zero represents equals 
		}
	}
	public static int calculateHeuristic( RubiksCube Cube ){
		char correctColour = 'W'; // Just set to W to initialize
        int incorrectNum = 0;
        for (int i = 0; i < 6; i++) {
            if (i == 0) correctColour = 'W';
            if (i == 1) correctColour = 'Y';
            if (i == 2) correctColour = 'B';
            if (i == 3) correctColour = 'G';
            if (i == 4) correctColour = 'O';
            if (i == 5) correctColour = 'R';

            if (Cube.state[i][0][0] != correctColour) incorrectNum++;
            if (Cube.state[i][0][2] != correctColour) incorrectNum++;
            if (Cube.state[i][2][0] != correctColour) incorrectNum++;
            if (Cube.state[i][2][2] != correctColour) incorrectNum++;
        }
        // 9 comes from the maximum number of corners fixed during turn (3) * num of stickers in each corner
        // Need highest number so we dont overestimate the heuristc
		// (UPDATE) If you you turn a face of solved cube it displaces 4 corners. 
		// Each corner has 3 stickers so 4*3 = 12 incorrect ceil(12/9) = 2 should be 1 because we know its only 1 move away
        return (int)Math.ceil(incorrectNum / 12.0); // Divide by 12 at max we would be shifting 12 stickers
	}
	public static void main(String[] args) throws IOException, IncorrectFormatException {
//		System.out.println("number of arguments: " + args.length);
//		for (int i = 0; i < args.length; i++) {
//			System.out.println(args[i]);
//		}
		HashSet<RubiksCube> visited = new HashSet<>();
		RubiksCube SolvedState = new RubiksCube();
		int solvedHash = SolvedState.hashCode();

		if (args.length < 2) {
			System.out.println("File names are not specified");
			System.out.println("usage: java " + MethodHandles.lookup().lookupClass().getName() + " input_file output_file");
			return;
		}
		
		
		// TODO
		//File input = new File(args[0]);
		String Input_Filename = args[0];
		RubiksCube StartCube = new RubiksCube( Input_Filename );
		String StartPath = "";
		int StartHeuristic = calculateHeuristic( StartCube );
		
		PriorityQueue<SearchNode> Queue = new PriorityQueue<>();

		String SolvedPath = "";
		// Init the start node
		SearchNode StartNode = new SearchNode( 0, StartHeuristic, StartCube, StartPath );

		Queue.add(StartNode);

		char[] moves = {'F', 'B', 'L', 'R', 'U', 'D'}; // For the inner Loop

		while( !Queue.isEmpty() ){
			SearchNode Current = Queue.poll();
			visited.add( Current.CurrState );
			for( int i = 0; i < 6; i++ ){
				RubiksCube CurrentNeighbour = Current.CurrState.getNeighbor( moves[i] );
				if( !visited.contains( CurrentNeighbour ) ){
					// If this node has not been visited yet then add it to the priority queue
					SearchNode NeighbourNode = new SearchNode( Current.DistFromStart + 1, calculateHeuristic(CurrentNeighbour), CurrentNeighbour, Current.path + moves[i] );
					Queue.add( NeighbourNode );
					visited.add( CurrentNeighbour );
					if( CurrentNeighbour.hashCode() == solvedHash ){
						SolvedPath = NeighbourNode.path;
						// args[1] = SolvedPath; args[1] only houses the name of the file that you need to write to 
						// need to create a function that writes the string to the file args[1]
					try (BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]))) {
						writer.write(SolvedPath);
						System.out.println("Text written to the file successfully.");
					} catch (IOException e) {
						System.out.println("An error occurred while writing to the file: " + e.getMessage());
						e.printStackTrace();
					}
    			}
			}
		}
	}
}
		// solve...
		//File output = new File(args[1]);
}

