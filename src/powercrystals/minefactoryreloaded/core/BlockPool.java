package powercrystals.minefactoryreloaded.core;

import powercrystals.core.position.BlockPosition;

public class BlockPool
{
	final public static class BlockNode
	{
		public BlockPosition bp;
		BlockNode next;
		BlockNode prev;
		BlockPool pool;
		public BlockNode(BlockPool pool, int x, int y, int z)
		{
			bp = new BlockPosition(x, y, z);
			this.pool = pool;
		}
		void reset(int x, int y, int z)
		{
			bp.x = x;
			bp.y = y;
			bp.z = z;
		}
		public void free()
		{
			synchronized(pool)
			{
				pool.unshift(this);
			}
			pool.size();
		}
		@Override
		public boolean equals(Object n)
		{
			if (n == null || n.getClass() != BlockNode.class)
				return false;
			return bp.equals(((BlockNode)n).bp);
		}
		@Override
		public String toString()
		{
			return "BlockNode["+bp+";"+pool+"]";
		}
		@Override
		public int hashCode()
		{
			return bp.hashCode();
		}
	}
	static BlockPool pool = new BlockPool(false);
	BlockNode head;
	BlockNode tail;
	private long size;
	private boolean _noDupe;
	
	private BlockPool(boolean preventDupes)
	{
		_noDupe = preventDupes;
	}
	
	public BlockPool()
	{
		this(true);
	}

	public synchronized static BlockNode getNext(int x, int y, int z)
	{
		if (pool.head == null)
		{
			return new BlockNode(pool, x, y, z);
		}
		BlockNode r = pool.shift();
		r.reset(x, y, z);
		r.next = null;
		r.prev = null;
		return r;
	}
	
	public void push(BlockNode obj)
	{
		if (_noDupe)
		{
			BlockNode n = tail;
			while (n != null)
			{
				if (n.equals(obj))
				{
					obj.free();
					return;
				}
				n = n.prev;
			}
		}
		obj.prev = tail;
		obj.next = null;
		if (tail != null)
			tail.next = obj;
		else
			head = obj;
		tail = obj;
		++size;
	}
	
	public BlockNode pop()
	{
		BlockNode obj = tail;
		if (obj != null)
		{
			tail = obj.prev;
			obj.prev = null;
			if (tail != null)
				tail.next = null;
			else
				head = null;
			--size;
		}
		return obj;
	}
	
	public BlockNode peek()
	{
		return tail;
	}
	
	public BlockNode poke()
	{
		return head;
	}
	
	public void unshift(BlockNode obj)
	{
		if (_noDupe)
		{
			BlockNode n = head;
			while (n != null)
			{
				if (n.equals(obj))
				{
					obj.free();
					return;
				}
				n = n.next;
			}
		}
		obj.next = head;
		obj.prev = null;
		if (head != null)
			head.prev = obj;
		else
			tail = obj;
		head = obj;
		++size;
	}
	
	public BlockNode shift()
	{
		BlockNode obj = head;
		if (obj != null)
		{
			head = obj.next;
			obj.next = null;
			if (head != null)
				head.prev = null;
			else
				tail = null;
			--size;
		}
		return obj;
	}
	
	public long size()
	{
		return size;
	}
}