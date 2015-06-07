package exp_select;

import static java.lang.System.out;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import basic.DataCreator;
import basic.DataUpdator;

public class prepare_files {

	public static void main(String[] args) throws IOException {
		String program_start_date_time=new SimpleDateFormat("yyyy/MM/dd HH:mm:ssZ").format(Calendar.getInstance().getTime());
		long start=System.currentTimeMillis();
//		PrintWriter result_file= new PrintWriter(new FileWriter("data/result-selection.txt"));
		
		int num_lines = 0, max_exp_times=0;
		ArrayList<Double> pers=new ArrayList();
		
		if(args.length<2){
			out.println("Please input: num_lines per1 per2 per3 ... ");
			System.exit(0);
		}else{
			num_lines = Integer.parseInt(args[0]);
			for(int i=1;i<args.length;i++){
				pers.add(Double.parseDouble(args[i]));
			}
		}
		
		out.println("num_lines:"+num_lines);
		out.println("pers:"+pers);
		
		String tbat_file_name, bat_file_name;
		
		for(double per:pers){
			out.println("per="+per);
			
			bat_file_name="data/bat_"+per+".txt";
			tbat_file_name="data/tbat_"+per+".txt";
			//create bat tbat
			DataCreator.prepareData(num_lines, bat_file_name, tbat_file_name);
			out.println("\t bat and tbat created");
			
			//create update lists
			String update_file_name="data/update_"+per+".txt";
			DataCreator.prepareUpdateList3(per, num_lines, update_file_name);
			out.println("\t update list created");
			
			//update tbat
			DataUpdator.updateTBAT(tbat_file_name, update_file_name);
			out.println("\t tbat updated (unclean)");
			
			//update bat
			DataUpdator.updateBAT_BinarySearch(bat_file_name, update_file_name);
			out.println("\t bat updated");
		}
		
		
		long end=System.currentTimeMillis();
		double elapsedTime=(end-start)/1000.0;
		out.println("Elapsed Time:"+elapsedTime+"s");
	}

}
