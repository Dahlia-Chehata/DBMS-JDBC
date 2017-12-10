package databaseManager;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import wagu.Board;
import wagu.Table;
 
public class TableFormatter {
    private static void printTableFormally(String tableString) {
        String[] splittedTableString = tableString.split("\n");
        if (splittedTableString.length == 4) {
            for (int i = 0; i < splittedTableString.length - 1; i++) {
                System.out.println(splittedTableString[i]);
            }
        } else if (splittedTableString.length > 4) {
            for (int i = 0; i < splittedTableString.length; i++) {
                System.out.println(splittedTableString[i]);
            }
        }
    }
 
    public static List<List<String>> toList(ArrayList<ArrayList<String>> x) {
        if(x.size()>0){
	    	x = transpose(x);
	        List<List<String>> temp = new ArrayList<List<String>>();
	        for (List<String> innerList : x) {
	            temp.add(innerList);
	        }
	       
	        return temp;
        }//if
        List<List<String>> rowsList = Arrays.asList(); 
        return rowsList;
    }
 
    public static ArrayList<ArrayList<String>> transpose(ArrayList<ArrayList<String>> x) {
        ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
        final int N = x.get(0).size();
        for (int i = 0; i < N; i++) {
            ArrayList<String> col = new ArrayList<String>();
            for (ArrayList<String> row : x) {
                col.add((String) row.get(i));
            }
            ret.add(col);
        }
        return ret;
    }
 
    public static void print(ArrayList<String> columnName, List<List<String>> rowsList) {
        Board board = new Board(75);
        String tableStr = board.setInitialBlock(new Table(board, 75, columnName, rowsList).tableToBlocks()).build()
                .getPreview();
        printTableFormally(tableStr);
 
    }
 
}