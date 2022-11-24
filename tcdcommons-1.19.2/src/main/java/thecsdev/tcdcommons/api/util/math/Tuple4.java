package thecsdev.tcdcommons.api.util.math;

public class Tuple4<T1, T2, T3, T4> extends Tuple3<T1, T2, T3>
{
	public T4 Item4;
	
	public Tuple4() { this(null, null, null, null); }
	public Tuple4(T1 item1, T2 item2, T3 item3, T4 item4)
	{
		super(item1, item2, item3);
		this.Item4 = item4;
	}
	
	@Override
	public void clear()
	{
		super.clear();
		this.Item4 = null;
	}
	
	@Override
	public boolean isCleared() { return super.isCleared() && this.Item4 == null; }
	
	@Override
	public boolean isFull() { return super.isFull() && this.Item4 != null; }
}