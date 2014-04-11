import java.io.File;
import java.util.Scanner;
import java.io.PrintStream;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
public class fsign{
	public static ArrayList<String> cfl;
	public static ArrayList<String> sign;
	public static ArrayList<String> sign_new;
	public static ArrayList<String> sign_random;
	public static void main(String args[]) throws Exception{
		file2hex.convertToHex(new PrintStream("temp.hex"), new File(args[0]));
		Scanner in = new Scanner(new File("temp.hex"));
		cfl = new ArrayList<String>();
		sign = new ArrayList<String>();
		sign_random = new ArrayList<String>();
		sign_new = new ArrayList<String>();
		Scanner sc = new Scanner(new File("cfl.dat"));
		while(sc.hasNextLine())
			cfl.add(sc.nextLine());
		sc = new Scanner(new File("signatures.dat"));
		while(sc.hasNextLine())
			sign.add(sc.nextLine());
		PrintStream out = new PrintStream("signatures.dat");
		sc = new Scanner(new File("signatures_random.dat"));
		while(sc.hasNextLine())
			sign_random.add(sc.nextLine());
		PrintStream out_random = new PrintStream("signatures_random.dat");
		String text = in.nextLine();
		String functionStart[] = {"55 8B EC","56 57 5D","56 57 5C","56 57 89"};
		int start=0,end=0,cand=0,func=0;
		while(start!=-1){
			start = text.indexOf(functionStart[0],end);
			for(String s:functionStart){
				int temp=text.indexOf(s,end);
				start=(temp<start&&temp>=0)?temp:start;
			}
			if(start!=-1){
				end = text.indexOf("C3",start);
				func++;
				if(!exists(text.substring(start,end+2))){
					sign_new.add(text.substring(start,end+2));
					cand++;
				}
			}
		}
		//Entropy
		double highest_entropy = Double.MIN_VALUE;
		String highest_entropy_signature = "";
		for(String s:sign_new){
			String bytes[] = s.split(" ");
			double entropy=0;
			for(int i=0;i<=255&&i<bytes.length;i++){
				int count=0;
				for(int j=0;j<bytes.length;j++)
					if(bytes[i].equals(bytes[j])) count++;
				entropy -= (((double)count)/((double)bytes.length))*Math.log10(((double)count)/((double)bytes.length));
			}
			if(entropy>highest_entropy){
				highest_entropy=entropy;
				highest_entropy_signature=s;
			}
		}
		//Random
		String random_signature = sign_new.get((int)(Math.random()*cand)%cand);
		
		for(String s:sign){
			out.println(s);
		}
		out.println(highest_entropy_signature);
		for(String s:sign_random){
			out_random.println(s);
		}
		out_random.println(random_signature);
		System.out.println("Number of total functions = "+func);
		System.out.println("Number of candidate functions = "+cand);
		System.out.println("Highest entropy = "+highest_entropy);
	}
	public static boolean exists(String s){
		if(cfl.contains(s)||sign.contains(s)||sign_random.contains(s)||sign_new.contains(s)) return true;
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