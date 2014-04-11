import java.io.File;
import java.util.Scanner;
import java.io.PrintStream;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
public class generatecfl{
	public static ArrayList<String> cfl;
	public static void main(String args[]) throws Exception{
		file2hex.convertToHex(new PrintStream("temp.hex"), new File(args[0]));
		Scanner in = new Scanner(new File("temp.hex"));
		cfl = new ArrayList<String>();
		Scanner sc = new Scanner(new File("cfl.dat"));
		while(sc.hasNextLine())
			cfl.add(sc.nextLine());
		PrintStream out = new PrintStream("cfl.dat");
		String text = in.nextLine();
		String functionStart[] = {"55 8B EC","56 57 5D","56 57 5C","56 57 89"};
		int start=0,end=0,added=0;
		while(start!=-1){
			start = text.indexOf(functionStart[0],end);
			for(String s:functionStart){
				int temp=text.indexOf(s,end);
				start=(temp<start&&temp>=0)?temp:start;
			}
			if(start!=-1){
				end = text.indexOf("C3",start);
				if(!exists(text.substring(start,end+2))){
					cfl.add(text.substring(start,end+2));
					added++;
				}
			}
		}
		for(String s:cfl){
			out.println(s);
		}
		System.out.println("Number of common functions added = "+added);
	}
	public static boolean exists(String s){
		if(cfl.contains(s)) return true;
		return false;
	}
} 
class file2hex{
   	public static void convertToHex(PrintStream out, File file) throws IOException {
		InputStream is = new FileInputStream(file);
 		int bytesCounter =0;		
		int value = 0;
		StringBuilder sbHex = new StringBuilder();
		StringBuilder sbText = new StringBuilder();
		StringBuilder sbResult = new StringBuilder();	
		while ((value = is.read()) != -1) {    
	    	//convert to hex value with "X" formatter
            sbHex.append(String.format("%02X ", value));
	    	//If the chracater is not convertable, just print a dot symbol "." 
	    	if (!Character.isISOControl(value)) {
	      		sbText.append((char)value);
	    	}else {
	        	sbText.append(".");
	    	}
	    	if(bytesCounter==15){
	      		//sbResult.append(sbHex).append("      ").append(sbText).append("\n");
	      		sbResult.append(sbHex);
	       		sbHex.setLength(0);
	   	    	sbText.setLength(0);
	    	   	bytesCounter=0;
	    	}else{
	        	bytesCounter++;
	   		}
       	}
 		//if still got content
		if(bytesCounter!=0){			
	    	for(; bytesCounter<16; bytesCounter++){
				sbHex.append("   ");
	    	}
	    	sbResult.append(sbHex).append("      ").append(sbText).append("\n");
		}
        out.print(sbResult);
        is.close();
  	}
   	static void main(String[] args) throws IOException{	
    	//write the output into a file
    	convertToHex(new PrintStream(args[1]), new File(args[0]));
    } 
}