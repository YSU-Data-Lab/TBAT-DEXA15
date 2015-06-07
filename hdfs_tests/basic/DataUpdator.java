package basic;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
public class DataUpdator {
	
	public static int DEFAULT_BUFFER_SIZE=8192;
	
	public static void updateTBAT(String tbat_file_name, 
			String update_file_name) throws IOException{
		updateTBAT(tbat_file_name, update_file_name, DEFAULT_BUFFER_SIZE);
	}
	
	public static void updateTBAT(String tbat_file_name, 
			String update_file_name, int buffer_size) throws IOException{
		PrintWriter tbat_file_out = new PrintWriter(new FileWriter(tbat_file_name,true));
		BufferedReader update_file_in =new BufferedReader(new FileReader(update_file_name), buffer_size);
		String line="";
		String timestampstr="";
		timestampstr=String.format("%d", System.currentTimeMillis());
		timestampstr=timestampstr.substring(timestampstr.length()-8,timestampstr.length());
		while((line = update_file_in.readLine()) != null){
			tbat_file_out.println(timestampstr+","+line);
		}
		update_file_in.close();
		tbat_file_out.close();
	}
	
	
	
	public static void updateBAT1(String bat_file_name,
			String update_file_name) throws IOException{
		RandomAccessFile bat_file = new RandomAccessFile(new File(bat_file_name), "rw");
		BufferedReader update_file_in =new BufferedReader(new FileReader(update_file_name));
		int update_oid;
		int bat_oid;
		long current_pos=0;
		String current_line="";
		String update_line="";
		//read in update file
		while((update_line = update_file_in.readLine()) != null){
			String[] tokens=update_line.split(",");
			update_oid=Integer.parseInt(tokens[0].trim());
			//update bat file according to update_oid
			while((current_line=bat_file.readLine())!=null){
				String[] tokens_bat=current_line.split(",");
				bat_oid=Integer.parseInt(tokens_bat[0].trim());
				if(bat_oid == update_oid){
					current_pos=bat_file.getFilePointer();
					bat_file.seek(current_pos-current_line.length()-1);
					bat_file.writeBytes(update_line+"\n");
					bat_file.seek(0);//back to top of bat file
					break;
				}
			}
		}
		update_file_in.close();
		bat_file.close();
	}
	
	/**
	 * faster than updateBAT1, v2 uses buffered reader to read bat_file, and use randomaccessfile only when writing
	 * after one line is updated, the buffered reader will seek(0)
	 * this version works in all cases, including the update list is not sorted according to oid
	 */
	public static void updateBAT2(String bat_file_name,
			String update_file_name) throws IOException{
		updateBAT2(bat_file_name, update_file_name, DEFAULT_BUFFER_SIZE);
	}
	
	/**
	 * default BufferedReader size is 8192
	 * this version can change the buffered reader size
	 */
	public static void updateBAT2(String bat_file_name,
			String update_file_name, int buffer_size) throws IOException{
		RandomAccessFile bat_file_writer = new RandomAccessFile(new File(bat_file_name), "rw");
		FileInputStream bat_file_in=new FileInputStream(bat_file_name);
		BufferedReader bat_file_reader=new BufferedReader(new InputStreamReader(bat_file_in), buffer_size);
		BufferedReader update_file_in =new BufferedReader(new FileReader(update_file_name), buffer_size);
		
		int update_oid;
		int bat_oid;
		String current_line="";
		String update_line="";
		//read in update file
		while((update_line = update_file_in.readLine()) != null){
			long current_line_num=1;
			String[] tokens=update_line.split(",");
			update_oid=Integer.parseInt(tokens[0].trim());
//			System.out.println("update oid:"+update_oid);
			
			//update bat file according to update_oid
			current_line = bat_file_reader.readLine();//read the 1st line of bat file
			while(current_line != null){
				String[] tokens_bat=current_line.split(",");
				bat_oid=Integer.parseInt(tokens_bat[0].trim());
				
				if(bat_oid == update_oid){
					bat_file_writer.seek((current_line_num-1)*(current_line.length()+1));
					bat_file_writer.writeBytes(update_line+"\n");
					
					//reset buffered reader to the beginning of bat file
					bat_file_in.getChannel().position(0);
					bat_file_reader=new BufferedReader(new InputStreamReader(bat_file_in));
					current_line_num=1;
					break;
				}
				current_line_num++;
				current_line=bat_file_reader.readLine();
			}
		}
		update_file_in.close();
		bat_file_in.close();
		bat_file_reader.close();
		bat_file_writer.close();
	}
	
	/**
	 * faster than updateBAT2, no need to seek(0) in bat_file when one line is updated.
	 * this version only works when the update list file is sorted.
	 */
	public static void updateBAT3(String bat_file_name,
			String update_file_name) throws IOException{
		RandomAccessFile bat_file_writer = new RandomAccessFile(new File(bat_file_name), "rw");
		FileInputStream bat_file_in=new FileInputStream(bat_file_name);
		BufferedReader bat_file_reader=new BufferedReader(new InputStreamReader(bat_file_in));
		BufferedReader update_file_in =new BufferedReader(new FileReader(update_file_name));
		int update_oid;
		int bat_oid;
		long current_pos=0;
		long current_line_num=1;
		String current_line="";
		String update_line="";
		//read in update file
		while((update_line = update_file_in.readLine()) != null){
			String[] tokens=update_line.split(",");
			update_oid=Integer.parseInt(tokens[0].trim());
			
			//update bat file according to update_oid
			current_line = bat_file_reader.readLine();
			while(current_line != null){
				String[] tokens_bat=current_line.split(",");
				bat_oid=Integer.parseInt(tokens_bat[0].trim());
				
				if(bat_oid == update_oid){
					bat_file_writer.seek((current_line_num-1)*(current_line.length()+1));
					bat_file_writer.writeBytes(update_line+"\n");
					current_line_num++;
					
					break;
				}
				current_line_num++;
				current_line=bat_file_reader.readLine();
			}
		}
		update_file_in.close();
		bat_file_in.close();
		bat_file_reader.close();
		bat_file_writer.close();
	}
	
	/**
	 * binary search for one update_line according to oid
	 * @param bat_file_in
	 * @param update_line
	 * @throws IOException
	 */
	public static int binarySearchUpdateBAT(RandomAccessFile bat_file, int line_length, String update_line) throws IOException{
		int update_oid=Integer.parseInt(update_line.split(",")[0].trim());
//		System.out.println("update oid:"+update_oid);
		
		int low=0;
		int high=(int)bat_file.length()/line_length-1;
		int mid, bat_oid_mid;
		String bat_current_line;
		
		while(low<=high){
			mid=(low+high)/2;
			bat_file.seek(mid*line_length);
			bat_current_line=bat_file.readLine();
//			System.out.println(bat_current_line);
			bat_oid_mid=Integer.parseInt(bat_current_line.split(",")[0].trim());
			if(bat_oid_mid == update_oid){
				//update this line
				bat_file.seek(mid*line_length);
				bat_file.writeBytes(update_line+"\n");
				bat_file.seek(0);//reset file pointer after updating
				return mid+1;//return this line number
			}else if(bat_oid_mid < update_oid) low=mid+1;
			else high=mid-1;
		}
		return -1;
	}
	
	
	
	
	/**
	 * update bat using binary search
	 * Assumption: the oids in the BAT file are sorted
	 */
	public static void updateBAT_BinarySearch(String bat_file_name, String update_file_name) throws IOException{
		RandomAccessFile bat_file = new RandomAccessFile(new File(bat_file_name), "rw");
		BufferedReader update_file_in =new BufferedReader(new FileReader(update_file_name));
		int line_length=bat_file.readLine().length()+1;
		bat_file.seek(0);
		String update_line=null;
		while((update_line = update_file_in.readLine()) != null){
			binarySearchUpdateBAT(bat_file,line_length,update_line);
		}
		update_file_in.close();
	}
	
	/**
	 * binary search tbat file according to target tbun
	 */
	public static int binarySearchUpdateTBAT(RandomAccessFile tbat_file, int line_length, TBUN tbun_target) throws IOException{
		int low=0;
		int high=(int)tbat_file.length()/line_length-1;
		int mid, tbat_oid_mid;
		String tbat_current_line;
		
		while(low<=high){
			mid=(low+high)/2;
			tbat_file.seek(mid*line_length);
			tbat_current_line=tbat_file.readLine();
//			System.out.println(mid+":"+tbat_current_line);
			tbat_oid_mid=Integer.parseInt(tbat_current_line.split(",")[1].trim());//tbat oid index is 1!
			if(tbat_oid_mid == tbun_target.oid){
				//update this line
				tbat_file.seek(mid*line_length);
				tbat_file.writeBytes(tbun_target+"\n");
				tbat_file.seek(0);//reset file pointer after updating
				return mid+1;//return this line number
			}else if(tbat_oid_mid < tbun_target.oid) low=mid+1;
			else high=mid-1;
		}
		System.out.println("Not found "+tbun_target+" !");
		return -1;
	}
	
	/**
	 * @param oid_position the place of oid in the line for appendix =1, for normal update file =0
	 * 
	 */
	public static void sortMergeFileToTBAT(String tbat_file_name, String appendix_file_name, int oid_position) throws IOException{
		BufferedReader appendix_file_in =new BufferedReader(new FileReader(appendix_file_name));
		String line="";
		ArrayList<TBUN> buffer=new ArrayList<TBUN>(1000);
		
		System.out.println("load buffer");
		if(oid_position==1){//for appendix file
			while((line=appendix_file_in.readLine())!=null){
				String[] tbun_fields=line.split(",");
				long timestamp=Long.parseLong(tbun_fields[0].trim());
				int oid=Integer.parseInt(tbun_fields[1].trim());
				int value=Integer.parseInt(tbun_fields[2].trim());
				buffer.add(new TBUN<Integer>(timestamp,oid,value));
			}
		}else{//for normal update file
			long timestamp=System.currentTimeMillis();
			while((line=appendix_file_in.readLine())!=null){
				String[] tbun_fields=line.split(",");
				int oid=Integer.parseInt(tbun_fields[0].trim());
				int value=Integer.parseInt(tbun_fields[1].trim());
				buffer.add(new TBUN<Integer>(timestamp,oid,value));
			}
		}
		
		System.out.println("buffer size:"+buffer.size());
		
		appendix_file_in.close();
		
		System.out.println("sorting buffer");
		Collections.sort(buffer);//a modified merge sort
		
		System.out.println("binarySearchUpdateTBAT");
		RandomAccessFile tbat_file = new RandomAccessFile(new File(tbat_file_name), "rw");
		int line_length=tbat_file.readLine().length()+1;
		tbat_file.seek(0);
		for(TBUN tbun:buffer){
			DataUpdator.binarySearchUpdateTBAT(tbat_file,line_length,tbun);
		}
		
	}
	
	/**
	 * In this version2, we load the lines of file into the memory first and then parse into TBUN ArrayList
	 * @param oid_position the place of oid in the line for appendix =1, for normal update file =0
	 * 
	 */
	public static void sortMergeFileToTBAT2(String tbat_file_name, String appendix_file_name, int oid_position) throws IOException{
		BufferedReader appendix_file_in =new BufferedReader(new FileReader(appendix_file_name));
		
		ArrayList<TBUN> buffer=new ArrayList<TBUN>(1000);
		ArrayList<String> lines=new ArrayList<String>(1000);
		
		System.out.println("load raw buffer");
		
		String line_temp="";
		while((line_temp=appendix_file_in.readLine())!=null){
			lines.add(line_temp);
		}
		
		System.out.println("load tbun buffer");
		
		if(oid_position==1){//for appendix file
			for(String line:lines){
				String[] tbun_fields=line.split(",");
				long timestamp=Long.parseLong(tbun_fields[0].trim());
				int oid=Integer.parseInt(tbun_fields[1].trim());
				int value=Integer.parseInt(tbun_fields[2].trim());
				buffer.add(new TBUN<Integer>(timestamp,oid,value));
			}
		}else{//for normal update file
			long timestamp=System.currentTimeMillis();
			for(String line:lines){
				String[] tbun_fields=line.split(",");
				int oid=Integer.parseInt(tbun_fields[0].trim());
				int value=Integer.parseInt(tbun_fields[1].trim());
				buffer.add(new TBUN<Integer>(timestamp,oid,value));
			}
		}
		
		System.out.println("buffer size:"+buffer.size());
		
		appendix_file_in.close();
		
		System.out.println("sorting buffer");
		Collections.sort(buffer);//a modified merge sort
		
		System.out.println("binarySearchUpdateTBAT");
		RandomAccessFile tbat_file = new RandomAccessFile(new File(tbat_file_name), "rw");
		int line_length=tbat_file.readLine().length()+1;
		tbat_file.seek(0);
		for(TBUN tbun:buffer){
			DataUpdator.binarySearchUpdateTBAT(tbat_file,line_length,tbun);
		}
		
	}
}
