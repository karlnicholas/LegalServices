package opjpa;

import load.LoadHistoricalOpinions;

public class LoadOpinionsNoSpring {

	public static void main(String[] args) throws Exception {
		LoadHistoricalOpinions loadHistoricalOpinions = new LoadHistoricalOpinions();
    	loadHistoricalOpinions.initializeDB();
    	System.out.println("loadHistoricalOpinions.initializeDB(): DONE");
	}
        
}
