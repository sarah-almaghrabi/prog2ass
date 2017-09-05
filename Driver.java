package rmit.cosc.s3613385;

import java.util.*; 
import java.util.concurrent.atomic.AtomicInteger;



public class Driver {
	Scanner sn = new Scanner(System.in);

	// counters to track game number
	AtomicInteger swimGameCounter = new AtomicInteger(1);
	AtomicInteger runGameCounter = new AtomicInteger(1);
	AtomicInteger cyclGameCounter = new AtomicInteger(1); 

	// each game must has a refree
	Official   refree; 


	// arraylist of players for each game 
	ArrayList<Athelets> athelets = new ArrayList<Athelets> ();

	// to save the points of all the atheltes
	TreeMap< String, Integer> results = new TreeMap<String , Integer> ();

	// to keep history of all games have been played
	HashMap<String , Athelets[] > history = new HashMap<String , Athelets[] > ();

	String prediction=null;  

	public String selectGame(String gameID){
		//check if there is already a selected game 
		if(gameID!=null){

			System.out.println("CAUTION : There is a game already running "); 
		}
		else    // if not, display the sport menu 
		{
			String sportOptions [] = {
					"Swimming  "
					,"Running "
					,"Cycling   "
					,"Back     " 
			} ;
			Menu sportsMenu = new Menu ("SPORTS" , sportOptions);

			int chose ;
			do{
				sportsMenu.display();
				chose = sportsMenu.getValidChoice();

				switch (chose){
				case 1 : 
				{ 
					gameID = Game.createGameID('s', swimGameCounter.getAndIncrement());
					System.out.println( "Swimming Game " + gameID +" Selected .. "  );
					Game.creatPlayers("swimming");	
					//	getPlayers();
					showPlayers();
					selectPlayers(); 

				}
				return gameID; 
				case 2 :
				{ 
					gameID = Game.createGameID('r', runGameCounter.getAndIncrement());
					System.out.println( "Running Game " + gameID +" Selected .. "  );
					Game.creatPlayers("running");	
					getPlayers();
				}
				return gameID; 
				case 3 : 
				{ 
					gameID = Game.createGameID('c', cyclGameCounter.getAndIncrement());
					System.out.println( "Cycling Game " + gameID +" Selected .. "  );
					Game.creatPlayers("cycling");
					getPlayers();
				}

				return gameID; 
				case 4 : 
					// back to menu  
					return null;

				default: 
					System.out.println("Invalid input. Please select a number from 1 - 4 ");
					break;

				}

			}while(chose!=4);

		}
		return gameID;

	}


	public void selectPlayers() {
		int input []=new int[8]; 
		int count =0 , indx =0;
		System.out.println("to run the game you must chose 5 to 8 players: "
				+ "\n     ( Write their number or 0 to finish and exit ) ");
		do{
			System.out.println("chose the " +(count +1)+"th player" );
			input[indx++]= Integer.parseInt(sn.next());

			if(input[indx-1]==0 )

				if(count <5 )
				{
					System.out.println("You have to have at least 5 players " );
					continue;

				}else 
					break; 

			count++;

		}while( (count < 9 || count <5) );

	}


	public String startGame(String gameID, String prediction){
		if(gameID==null)
		{
			System.out.println("Sorry you have to select a game first");
		}else  {
			refree =  Game.createOffical();
			char x = gameID.charAt(0);
			if (refree==null || athelets.size()<5  || athelets.size()>8 ){
				System.out.println("Warning !! \n game must have at least 5 players "
						+ "and must have an Official");
			}else {
				switch (x){
				case 's': {
					refree.runGame(gameID,2000); // 200000
				}
				break;
				case 'r': {
					refree.runGame(gameID,2000);// 20000
				}
				break;
				case 'c':{
					refree.runGame(gameID,1000); // 800000
				}
				break; 
				default:
					break;

				}

				for(Athelets s: athelets)
					Game.startGame(s);
				Collections.sort(athelets, Athelets.TimeComparator);


				refree.showResults(athelets);

				System.out.println( "		Refree [ "+ refree.getName()+ " ]" );

				if(prediction!=null)
				{
					checkPrediction(prediction);
				} 

				saveResults(gameID);

			}

		}
		return null;
	}


	public void saveResults(String gameID) {

		for(Athelets a : athelets)
			// if the results list filled for first time 
			if(results.get((a.getId() +"\t" + a.getName())) == null)
				results.put((a.getId() +"\t" + a.getName()), a.getPoint());
		// if the results list already filled before .. update the points 
			else
			{
				int pastPoint =  results.get((a.getId() +"\t" + a.getName())) ;
				int lastPoint =  a.getPoint();
				results.put((a.getId() +"\t" + a.getName()), (pastPoint + lastPoint));
			}

		// save all the games played before 
		history.put(("Game " + gameID +"  Refree [ "+ refree.getName()+ " ]"  ) , 
				athelets.toArray(new Athelets[athelets.size()]));

		// clear the athelets list to be ready for another new game 
		athelets.removeAll(athelets);
	}

	public void checkPrediction(String prediction) {
		boolean found = false ; 
		for(Athelets a : athelets){
			if(a.getPoint()==5)
				if(a.getId().equals(prediction))
				{	
					System.out.println("\n----------------------------------------------------");
					System.out.println("         :)"
							+ "\n    CONGRATULATIONS --"
							+ "\n              YOU WON");
					System.out.println("----------------------------------------------------");
					//else 
					found= true; 
				}
		}
		if(!found){
			System.out.println("\n----------------------------------------------------");
			System.out.println("\n    :( "
					+ "\n  Your prediction is not correct.. "
					+ "\n              --     Try next time ");
			System.out.println("----------------------------------------------------");

		}
		prediction  = null;
	}

	public void getPlayers() {
		int i=0;
		Athelets ath;
		if( athelets.isEmpty()){

			do{ 
				ath =  Game.getPlayers(i++);
				if(ath!=null)
					athelets.add(ath);
				else
					break; 

			}while(i<8);

		}
	}

	public void showPlayers() {
		int i=0;
		Athelets ath;
		do{ 
			ath =  Game.getPlayers(i++);
			if(ath!=null)
				System.out.println((i) + "-  \t" + ath.getName() );
			//athelets.add(ath);
			else
				break; 

		}while(true);

		/*	i=1;
		for( Athelets a: athelets){
			System.out.println(i++ + "-  \t" + a.getName() );

		}*/ 
	}

	public String predictWinner(String gameID) {
		if(gameID==null)
		{
			System.out.println("Sorry you have to select a game first");
		}else  {


			boolean notValid =true;
			System.out.println(" ******** Players List *********");
			System.out.format("%8s%15s", "ID", "NAME" );
			System.out.println("\n--------------------------------------");

			for(Athelets a: athelets )
				a.print3();
			System.out.println("\n----------------------------------------------------");


			System.out.println("NOTE : The format should be exactly similar to the ID shown ");
			System.out.println("Enter the ID of your predected winner, or 0 to return :  ");
			do {
				prediction = sn.nextLine();
				if(prediction.trim().length()==0){
					System.out.println("You didn't enter an id .. try again ..");
					notValid=true; 
				} else if( prediction.trim().equals("0")){
					return null; 
				} else{

					for(Athelets s: athelets )
						if( s.getId().equals(prediction.trim().toUpperCase())  )
						{
							System.out.println("Got your Prediction .. " +s.getName());

							notValid =false;
						}
					if(notValid )
						System.out.println("This ID doesnt exist .. try again ..");
				}
			} while ( notValid);

			return prediction.trim().toUpperCase();
		}
		return gameID;
	}


	public void displayAllGames() {

		if(history.isEmpty())
			System.out.println("Sorry, the list is empty !! no game has been played before .. ");
		else
		{
			for (String name: this.history.keySet()){

				System.out.println("\n=====================================");
				System.out.println( name);
				System.out.format("%10s%15s%15s", "ID", "NAME", "TIME"  );
				System.out.println("\n-------------------------------------");

				for (Athelets a : history.get(name)) 
					a.print2();
			}
		} 



	}


	public void displayAllAthlets() {
		if(results.isEmpty())
			System.out.println("Sorry, the list is empty !! no game has been played before .. ");
		else
		{  

			System.out.println("\n=====================================");
			System.out.println("This is the list of athelets who already have played game ");
			System.out.format("%10s%15s%15s", "ID", "NAME", "POINTS"  );
			System.out.println("\n-------------------------------------");
			MyComparator comparator = new MyComparator(results);
			Map<String, Integer> newMap = new TreeMap<String, Integer>(comparator);
			newMap.putAll(results);

			for (String name: newMap.keySet()){
				String [] line= name.split("\t");
				System.out.format("%10s%15s%15d", line[0], line[1], results.get(name)   );
				System.out.println();

			} 

		}
	}

	// this is inner class to sort the treeMapp by value 
	// this part of code has been taken from this link:
	// https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java

	class MyComparator implements Comparator<Object> {
		Map<String, Integer> map;
		public MyComparator(Map<String, Integer> map) {
			this.map = map;
		}
		public int compare(Object o1, Object o2) {

			if (map.get(o2) == map.get(o1))
				return 1;
			else
				return ((Integer) map.get(o2)).compareTo((Integer) map.get(o1));

		}
	}
}
