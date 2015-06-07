package basic;

public class BUN<T> {
	public int oid;
	public T value;
	public BUN(int oid, T value){
		this.oid=oid;
		this.value=value;
	}
	public String toString(){
		return "("+oid+","+value+")";
	}
}
