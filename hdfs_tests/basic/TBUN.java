package basic;



public class TBUN <T>extends BUN implements Comparable <TBUN>{
	public long timestamp;
	public static final String tbat_format = "%s,%10d,%10d";
	public TBUN(long timestamp, int oid, T value) {
		super(oid, value);
		this.timestamp=timestamp;
	}

	public String toString(){
		String timestampstr=String.format("%d", timestamp);
		if(timestampstr.length()>=8){
			timestampstr=timestampstr.substring(timestampstr.length()-8,timestampstr.length());
		}
		return String.format(tbat_format, timestampstr, oid, value);
	}
	
	public int compareTo(TBUN tbun2){
		int diff_oid=oid-tbun2.oid;
		if(diff_oid!=0){
			return diff_oid;
		}else{
			return (int)(timestamp-tbun2.timestamp);
		}
	}
	
}
