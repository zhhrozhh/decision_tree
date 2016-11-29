//package proj2;

import java.util.*;

public class DTtest {
	public static void main(String[] args) {
		DecisionTree DT = new DecisionTree();
		Scanner reader = new Scanner(System.in);
		
		while(true){
			System.out.print(">> ");
			//List<String>w = Arrays.asList( reader.next().split(" "));
			
			if (reader.next().equals(new String("DT"))){
				//System.out.println(w.size());
				try {DT.process(reader.next());}
				catch (Exception e) {
					e.printStackTrace();
					return;
				}
				DT.build();
				DT = new DecisionTree();
			}
			else return;
			
		}
	}
}

