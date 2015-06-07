package exp_update;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import basic.*;
import static java.lang.System.out;

public class exp_update {
	
	static int reader_buffer_size=1; // buffer size for BufferedReader (characters); normal default size is 8192 characters

	public static void main(String[] args) throws IOException{
		String program_start_date_time=new SimpleDateFormat("yyyy/MM/dd HH:mm:ssZ").format(Calendar.getInstance().getTime());
		long start=System.currentTimeMillis();
		String bat_file_name="data/bat.txt";
		String tbat_file_name="data/tbat.txt";
		PrintWriter result_file= new PrintWriter(new FileWriter("data/result.txt"));
		
		int num_lines = 0, max_exp_times=0;
		ArrayList<Double> pers=new ArrayList();
		
		if(args.length<=3){
			out.println("Please input num_lines "
					+ " max_exp_times per1 per2 per3 ... ");
			System.exit(0);
		}else{
			num_lines = Integer.parseInt(args[0]);
			max_exp_times = Integer.parseInt(args[1]);
			for(int i=2;i<args.length;i++){
				pers.add(Double.parseDouble(args[i]));
			}
			Process p = Runtime.getRuntime().exec("hostname");
			BufferedReader command_input =
			    new BufferedReader(new InputStreamReader(p.getInputStream()));
			result_file.println("* Hostname: "+command_input.readLine());
			command_input.close();
			p.destroy();
			result_file.println("* Total lines: "+num_lines);
			result_file.println("* Percentages: "+pers);
			result_file.println();
		}
		
//		HashMap<Double, ArrayList<Double>> bat_update_time_table = new HashMap<Double, ArrayList<Double>>();
//		HashMap<Double, ArrayList<Double>> tbat_update_time_table = new HashMap<Double, ArrayList<Double>>();
		ArrayList<Double> bat_update_time_medians=new ArrayList<Double>();
		ArrayList<Double> tbat_update_time_medians=new ArrayList<Double>();
		ArrayList<Double> bat_update_time_means=new ArrayList<Double>();
		ArrayList<Double> tbat_update_time_means=new ArrayList<Double>();
		ArrayList<Double> bat_update_time_maxs=new ArrayList<Double>();
		ArrayList<Double> tbat_update_time_maxs=new ArrayList<Double>();
		ArrayList<Double> bat_update_time_mins=new ArrayList<Double>();
		ArrayList<Double> tbat_update_time_mins=new ArrayList<Double>();
		ArrayList<Double> overhead_medians=new ArrayList<Double>();
		ArrayList<Double> overhead_means=new ArrayList<Double>();
		
		for(double per:pers){
			out.println("per:"+per);
			
			ArrayList<Double> bat_update_time_temp=new ArrayList<Double>();
			ArrayList<Double> tbat_update_time_temp=new ArrayList<Double>();
			
			result_file.println("percentage = "+per+"\n");
			result_file.format("\t %-2s | %-10s | %-10s | %-10s\n", "i", "tbat_time", "bat_time", "overhead");
			
			for(int i=0;i<max_exp_times;i++){
				out.println("loop:"+(i+1));
				//create bat tbat
				DataCreator.prepareData(num_lines, bat_file_name, tbat_file_name);
				
				//create update lists
				String update_file_name="data/update"+per+".txt";
				DataCreator.prepareUpdateList2(per, num_lines, update_file_name);
				
				//update tbat
				long tbat_start=System.currentTimeMillis();
				DataUpdator.updateTBAT(tbat_file_name, update_file_name, reader_buffer_size);
				Double tbat_time=(double)(System.currentTimeMillis()-tbat_start)/1000.0d;
				tbat_update_time_temp.add(tbat_time);
				
				//update bat
				long bat_start=System.currentTimeMillis();
//				DataUpdator.updateBAT2(bat_file_name, update_file_name);
//				DataUpdator.updateBAT2(bat_file_name, update_file_name, reader_buffer_size);
				DataUpdator.updateBAT_BinarySearch(bat_file_name, update_file_name);
				Double bat_time=(double)(System.currentTimeMillis()-bat_start)/1000.0d;
				bat_update_time_temp.add(bat_time);
				
				result_file.format("\t %-2d | %-10.3f | %-10.3f | %-10.3f\n", i, tbat_time,
						bat_time, bat_time/tbat_time);
			}//end of max_exp_times loop
			
			out.println();
			result_file.println();
			
			double m=2.0; //parameter to remove outlier
			tbat_update_time_temp=MathTool.removeOutlier(tbat_update_time_temp, m);
			bat_update_time_temp=MathTool.removeOutlier(bat_update_time_temp, m);
			
			result_file.println("\nAfter Removing Outlier with m=:"+m+" \n");
			result_file.println("\t tbat_time:"+tbat_update_time_temp);
			result_file.println("\t bat_time:"+bat_update_time_temp+"\n");
			
//			bat_update_time_table.put(per, bat_update_time_temp);
//			tbat_update_time_table.put(per, tbat_update_time_temp);
			bat_update_time_medians.add(MathTool.median(bat_update_time_temp));
			tbat_update_time_medians.add(MathTool.median(tbat_update_time_temp));
			bat_update_time_means.add(MathTool.mean(bat_update_time_temp));
			tbat_update_time_means.add(MathTool.mean(tbat_update_time_temp));
			bat_update_time_maxs.add(Collections.max(bat_update_time_temp));
			tbat_update_time_maxs.add(Collections.max(tbat_update_time_temp));
			bat_update_time_mins.add(Collections.min(bat_update_time_temp));
			tbat_update_time_mins.add(Collections.min(tbat_update_time_temp));
			overhead_medians.add(MathTool.median(bat_update_time_temp)
					/MathTool.median(tbat_update_time_temp));
			overhead_means.add(MathTool.mean(bat_update_time_temp)
					/MathTool.mean(tbat_update_time_temp));
			
		}//end of pers loop
		
		result_file.println("Update time summary: \n");
		
		result_file.format("\t %-4s | %-10s | %-10s | %-10s | %-10s\n",
				"per","tbat min","tbat_max","bat_min","bat_max");
		
		for(int i=0;i<pers.size();i++){
			result_file.format("\t %-3.2f | %-10.5f | %-10.5f | %-10.5f | %-10.5f\n",
					pers.get(i), tbat_update_time_mins.get(i), tbat_update_time_maxs.get(i),
					bat_update_time_mins.get(i),bat_update_time_maxs.get(i));
		}
		
		result_file.println("\n");
		
		result_file.format("\t %-4s | %-10s| %-10s | %-10s\n",
				"per","tbat median","bat median","median overhead");
		for(int i=0;i<pers.size();i++){
			result_file.format("\t %-3.2f | %-10.5f | %-10.5f | %-10.5f\n",
					pers.get(i), tbat_update_time_medians.get(i), bat_update_time_medians.get(i)
					, overhead_medians.get(i));
		}
		
		result_file.println("\n");
		
		result_file.format("\t %-4s | %-10s | %-10s | %-10s\n",
				"per","tbat means","bat means","means overhead");
		for(int i=0;i<pers.size();i++){
			result_file.format("\t %-3.2f | %-10.5f | %-10.5f | %-10.5f\n",
					pers.get(i), tbat_update_time_means.get(i), bat_update_time_means.get(i)
					, overhead_means.get(i));
		}
		
		
		
		long end=System.currentTimeMillis();
		double elapsedTime=(end-start)/1000.0;
		out.println("Elapsed Time:"+elapsedTime+"s");
		
		result_file.println("\n\t Elapsed Time:"+elapsedTime+"s");
		
		String program_end_date_time=new SimpleDateFormat("yyyy/MM/dd HH:mm:ssZ").format(Calendar.getInstance().getTime());
		result_file.println("\t Program Started at: "+program_start_date_time);
		result_file.println("\t Program Ended at:   "+program_end_date_time);
		result_file.close();
	}//---end of main---

}
