package exp_select;

import static java.lang.System.out;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import basic.*;

public class exp_select {
	
	public static void main(String args[]) throws IOException{
		String program_start_date_time=new SimpleDateFormat("yyyy/MM/dd HH:mm:ssZ").format(Calendar.getInstance().getTime());
		long start=System.currentTimeMillis();
		PrintWriter result_file= new PrintWriter(new FileWriter("data/result-select.txt"));
		
		int num_lines_body = 0, max_exp_times=0;
		ArrayList<Double> pers_update=new ArrayList();
		ArrayList<Double> pers_select=new ArrayList();
		
		//----------accept input arguments--------------
		if(args.length<=3){
			out.println("Please input num_lines "
					+ " max_exp_times -u update_per1 per2 per3 ... -s select_per1 per2 per3 ...");
			System.exit(0);
		}else{
			num_lines_body = Integer.parseInt(args[0]);
			max_exp_times = Integer.parseInt(args[1]);
			
			if(args[2].equalsIgnoreCase("-u")){
				int i=3;
				while(!args[i].equalsIgnoreCase("-s")){
					pers_update.add(Double.parseDouble(args[i++]));
				}
				i++;
				while(i<args.length){
					pers_select.add(Double.parseDouble(args[i++]));
				}
			}else{
				int i=3;
				while(!args[i].equalsIgnoreCase("-u")){
					pers_select.add(Double.parseDouble(args[i++]));
				}
				i++;
				while(i<args.length){
					pers_update.add(Double.parseDouble(args[i++]));
				}
			}
			
			Process p = Runtime.getRuntime().exec("hostname");
			BufferedReader command_input =
			    new BufferedReader(new InputStreamReader(p.getInputStream()));
			result_file.println("* Hostname: "+command_input.readLine());
			command_input.close();
			p.destroy();
			result_file.println("* Total lines: "+num_lines_body);
			result_file.println("* BAT & TBAT Update Percentages: "+pers_update);
			result_file.println("* Selection Percentages: "+pers_select);
			result_file.println();
		}
		
		//-------------experiment body-------------
		for (double per_update:pers_update){
			out.println("per_update:"+per_update);
			String tbat_file_name="data/tbat_"+per_update+".txt";
			String bat_file_name="data/bat_"+per_update+".txt";
			int tbat_line_length=DataRetriever.getLineLength(tbat_file_name);
			int bat_line_length=DataRetriever.getLineLength(bat_file_name);
			
			ArrayList<Double> bat_select_time_medians=new ArrayList<Double>();
			ArrayList<Double> tbat_select_time_medians=new ArrayList<Double>();
			ArrayList<Double> bat_select_time_means=new ArrayList<Double>();
			ArrayList<Double> tbat_select_time_means=new ArrayList<Double>();
			ArrayList<Double> bat_select_time_maxs=new ArrayList<Double>();
			ArrayList<Double> tbat_select_time_maxs=new ArrayList<Double>();
			ArrayList<Double> bat_select_time_mins=new ArrayList<Double>();
			ArrayList<Double> tbat_select_time_mins=new ArrayList<Double>();
			ArrayList<Double> overhead_medians=new ArrayList<Double>();
			ArrayList<Double> overhead_means=new ArrayList<Double>();
			
			for (double per_select:pers_select){
				out.println("per_update:"+per_update+" | per_select:"+per_select);
				
				ArrayList<Double> bat_select_time_temp=new ArrayList<Double>();
				ArrayList<Double> tbat_select_time_temp=new ArrayList<Double>();
				
				List<Integer> select_list=DataCreator.makeUpdateList(per_select, num_lines_body);
				for(int i=0;i<max_exp_times;i++){
					out.println("\tloop:"+(i+1));
					int value;
//					System.out.println("TBAT Selection:");
					
					//select tbat uncleaned
					long tbat_start=System.currentTimeMillis();
					for(int target_oid:select_list){
						value=DataRetriever.selectTBAT_Uncleaned(tbat_file_name, num_lines_body, tbat_line_length, target_oid);
//						System.out.println("targe_oid="+target_oid+" | tbat_value="+value);
					}
					Double tbat_time=(double)(System.currentTimeMillis()-tbat_start)/1000.0d;
					tbat_select_time_temp.add(tbat_time);
					
//					System.out.println();
//					System.out.println("BAT Selection:");
					
					//select bat
					long bat_start=System.currentTimeMillis();
					for(int target_oid:select_list){
						value=DataRetriever.selectBAT(bat_file_name, num_lines_body, bat_line_length, target_oid);
//						System.out.println("targe_oid="+target_oid+" | bat_value="+value);
					}
					Double bat_time=(double)(System.currentTimeMillis()-bat_start)/1000.0d;
					bat_select_time_temp.add(bat_time);
				}//end max_exp_time
				
				bat_select_time_medians.add(MathTool.median(bat_select_time_temp));
				tbat_select_time_medians.add(MathTool.median(tbat_select_time_temp));
				bat_select_time_means.add(MathTool.mean(bat_select_time_temp));
				tbat_select_time_means.add(MathTool.mean(tbat_select_time_temp));
				bat_select_time_maxs.add(Collections.max(bat_select_time_temp));
				tbat_select_time_maxs.add(Collections.max(tbat_select_time_temp));
				bat_select_time_mins.add(Collections.min(bat_select_time_temp));
				tbat_select_time_mins.add(Collections.min(tbat_select_time_temp));
				overhead_medians.add(MathTool.median(tbat_select_time_temp)
						/MathTool.median(bat_select_time_temp));
				overhead_means.add(MathTool.mean(tbat_select_time_temp)
						/MathTool.mean(bat_select_time_temp));
			}//end pers_select
			
			result_file.println("# Summary for update per:"+per_update+" \n");
			
			result_file.format("\t %-4s | %-10s | %-10s | %-10s | %-10s\n",
					"per","tbat min","tbat_max","bat_min","bat_max");
			
			for(int i=0;i<pers_select.size();i++){
				result_file.format("\t %-3.2f | %-10.5f | %-10.5f | %-10.5f | %-10.5f\n",
						pers_select.get(i), tbat_select_time_mins.get(i), tbat_select_time_maxs.get(i),
						bat_select_time_mins.get(i),bat_select_time_maxs.get(i));
			}
			
			result_file.println("\n");
			
			result_file.format("\t %-4s | %-10s| %-10s | %-10s\n",
					"per","tbat median","bat median","median overhead");
			for(int i=0;i<pers_select.size();i++){
				result_file.format("\t %-3.2f | %-10.5f | %-10.5f | %-10.5f\n",
						pers_select.get(i), tbat_select_time_medians.get(i), bat_select_time_medians.get(i)
						, overhead_medians.get(i));
			}
			
			result_file.println("\n");
			
			result_file.format("\t %-4s | %-10s | %-10s | %-10s\n",
					"per","tbat means","bat means","means overhead");
			for(int i=0;i<pers_select.size();i++){
				result_file.format("\t %-3.2f | %-10.5f | %-10.5f | %-10.5f\n",
						pers_select.get(i), tbat_select_time_means.get(i), bat_select_time_means.get(i)
						, overhead_means.get(i));
			}
			result_file.println("\n");
		}//end pers_update
		
		
		//-------------summary and elapsed time calculation------
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
