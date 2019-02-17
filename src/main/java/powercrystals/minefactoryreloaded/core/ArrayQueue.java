package powercrystals.minefactoryreloaded.core;

public class ArrayQueue<T>
{
	private Object[] _values;
	private int _positionRead;
	private int _positionWrite;
	
	public ArrayQueue(int size)
	{
		_values = new Object[size];
		_positionRead = 0;
		_positionWrite = 0;
	}
	
	private void moveNextRead()
	{
		_positionRead++;
		if(_positionRead >= _values.length)
		{
			_positionRead = 0;
		}
	}
	
	private void moveNextWrite()
	{
		_positionWrite++;
		if(_positionWrite >= _values.length)
		{
			_positionWrite = 0;
		}
	}
	
	@SuppressWarnings("unchecked")
	public T peek()
	{
		return (T)_values[_positionRead];
	}
	
	public T pop()
	{
		T value = peek();
		moveNextRead();
		return value;
	}
	
	public void push(T value)
	{
		_values[_positionWrite] = value;
		moveNextWrite();
	}
	
	public int size()
	{
		return _values.length;
	}
	
	public void clear()
	{
		_positionRead = 0;
		_positionWrite = 0;
		for(int i = 0; i < _values.length; i++)
		{
			_values[i] = null;
		}
	}
	
	public void fill(T value)
	{
		for(int i = 0; i < _values.length; i++)
		{
			_values[i] = value;
		}
	}
	
	@SuppressWarnings("unchecked")
	public T[] toArray(T[] input)
	{
		int max = Math.min(input.length, _values.length);
		for(int i = 0; i < max; i++)
		{
			int readPos = _positionRead + i;
			if(readPos >= _values.length)
			{
				readPos -= _values.length;
			}
			input[i] = (T)_values[readPos];
		}
		return input;
	}
}
