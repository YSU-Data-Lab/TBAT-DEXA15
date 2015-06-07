package basic;

import java.io.*;
import java.util.ArrayList;

public class DataRetriever {
//	public static int DEFAULT_BUFFER_SIZE=8192;

	/**
	 * 
	 * select the value of the target oid
	 * given a tbat with appendix appended at the end of the tbat file
	 * 
	 * @param file_name
	 * @param num_lines_body
	 * @param line_length
	 * @param oid_position the position of the oid (for tbat =1, for bat=1)
	 * @param target_oid
	 * @return target_value
	 * @throws IOException
	 */
	
	public static final int NOT_FOUND=Integer.MIN_VALUE;
	
	/**
	 * search in appendix by offset
	 * file is the updated tbat file
	 * offset must start from 1!!!
	 * value_position is the index position (starting from 0) of the value attribute in the line of tbat
	 * e.g. "ts oid1 value1", the value position is 2
	 */
	public static int searchAppendixByOffSet(RandomAccessFile file, int num_lines_body, int line_length, 
			int offset, int value_position) throws IOException{
		file.seek((offset+num_lines_body-1)*line_length);
		String line=file.readLine();
		int value=Integer.parseInt(line.split(",")[value_position].trim());
		file.seek(0);
		return value;
	}
	
	/**
	 * fyu
	 * search only the body of a TBAT using binary search, regardless of the appendix
	 * used for searching in combination with btree (which stores data in the appendix)
	 */
	public static int selectTBAT_body(String file_name,int num_lines_body, int line_length, int target_oid) throws IOException{
		int value=0;
		int oid_position=1;
			RandomAccessFile file=new RandomAccessFile(new File(file_name), "r");
			value=binarySearchValue(file, num_lines_body, line_length, oid_position, target_oid);
			file.close();
		
		return value;
	}
	
	public static int selectTBAT_Uncleaned(String file_name,int num_lines_body, int line_length, int target_oid) throws IOException{
		int value=0;
		int oid_position=1;
		BufferedReader append_reader=new BufferedReader(new FileReader(file_name));
		value=searchAppendedFile(append_reader, num_lines_body, line_length, oid_position, target_oid);
		append_reader.close();
		if(value==Integer.MIN_VALUE){
			RandomAccessFile file=new RandomAccessFile(new File(file_name), "r");
			value=binarySearchValue(file, num_lines_body, line_length, oid_position, target_oid);
			file.close();
		}
		return value;
	}
	
	
	
	
	public static int selectTBAT_Uncleaned2(String file_name,int num_lines_body, int line_length, int target_oid) throws IOException{
		int value=0;
		int oid_position=1;
			RandomAccessFile file=new RandomAccessFile(new File(file_name), "r");
			value=binarySearchValue(file, num_lines_body, line_length, oid_position, target_oid);
			file.close();
		
		return value;
	} //method for Eric Jones Thesis
	
	/**
	 * select the value of the target oid
	 * given a tbat file and a list of split appendix files 
	 */
	public static int selectTBAT_Uncleaned_Split(String tbat_file_name, 
			ArrayList<String> appendix_file_names, 
			int num_lines_body, int line_length, int target_oid) 
			throws IOException{
		int value=NOT_FOUND;
		int oid_position=1;
		if(!appendix_file_names.isEmpty()){
			for(String appendix_file_name:appendix_file_names){
				BufferedReader append_reader=new BufferedReader(new FileReader(appendix_file_name));
				//no line needs to be skipped in split appendix files
				value=searchAppendedFile(append_reader, 0, line_length, oid_position, target_oid);
				append_reader.close();
				if(value!=NOT_FOUND) return value;
			}
		}
		
		RandomAccessFile file=new RandomAccessFile(new File(tbat_file_name), "r");
		value=binarySearchValue(file, num_lines_body, line_length, oid_position, target_oid);
		file.close();
		
		return value;
	}
	
	public static int selectBAT(String file_name,int num_lines, int line_length, int target_oid) throws IOException{
		int oid_position=0;
		RandomAccessFile file=new RandomAccessFile(new File(file_name), "r");
		int value=binarySearchValue(file, num_lines, line_length, oid_position, target_oid);
		file.close();
		return value;
	}
	
		
	/**
	 * binary search for oid in the body of tbat (not the appended part)
	 * @param oid_position the position of the oid (for tbat =1, for bat=1)
	 */
	public static int binarySearchValue(RandomAccessFile file, int num_lines_body, int line_length, 
			int oid_position, int target_oid) throws IOException{
		int low=0;
		int high=num_lines_body-1;
		int mid, bat_oid_mid;
		String bat_current_line;
		
		while(low<=high){
			mid=(low+high)/2;
			file.seek(mid*line_length);
			bat_current_line=file.readLine();
			bat_oid_mid=Integer.parseInt(bat_current_line.split(",")[oid_position].trim());
			if(bat_oid_mid == target_oid){
				file.seek(0);//reset file pointer after updating
				return Integer.parseInt(bat_current_line.split(",")[oid_position+1].trim());
			}else if(bat_oid_mid < target_oid) low=mid+1;
			else high=mid-1;
		}
		return Integer.MIN_VALUE;
	}
	
	
	
	
	/**
	 * @param oid_position the position of the oid (for tbat =1, for bat=0)
	 */
	public static int searchAppendedFile(BufferedReader append_reader, int num_lines_body, int line_length, 
			int oid_position, int target_oid) throws IOException{

		append_reader.skip((num_lines_body)*line_length); 
		// skip the body of the updated tbat file, only read the appended part at the end
		String current_line;
		int temp_oid;
		int temp_value;
		int value=Integer.MIN_VALUE;
		
		while((current_line=append_reader.readLine())!=null){
			temp_oid=Integer.parseInt(current_line.split(",")[oid_position].trim());
			if(temp_oid==target_oid){
				value=Integer.parseInt(current_line.split(",")[oid_position+1].trim());
			}
		}
		return value;
	}
	
	public static int getLineLength(String file_name) throws IOException {
		RandomAccessFile randomReader=new RandomAccessFile(new File(file_name),"r");
		String first_line=randomReader.readLine();
		randomReader.close();
		int line_length=first_line.length()+1;//include '\n'
		return line_length;
	}
}
