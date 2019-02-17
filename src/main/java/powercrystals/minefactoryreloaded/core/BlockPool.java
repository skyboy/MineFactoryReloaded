package powercrystals.minefactoryreloaded.core;

import com.google.common.base.Objects;
import com.google.common.primitives.Ints;
import net.minecraft.util.math.BlockPos;

public class BlockPool {

	final public static class BlockNode {

		public int x, y, z;

		public BlockNode next;
		public BlockNode prev;
		final BlockPool pool;
		public BlockNode(BlockPool pool, int x, int y, int z) {

			reset(x, y, z);
			this.pool = pool;
		}

		void reset(int X, int Y, int Z) {

			x = X;
			y = Y;
			z = Z;
		}
		
		public void free() {

			synchronized(pool) {
				pool.unshift(this);
			}
		}

		@Override
		public boolean equals(Object n) {

			if (n == null || n.getClass() != BlockNode.class)
				return false;
			BlockNode bn = (BlockNode)n;
			return (x == bn.x & y == bn.y & z == bn.z) && bn.pool == pool;
		}

		@Override
		public String toString() {

			return "BlockNode[(" + x + ',' + y + ',' + z + ");" + pool + ']';
		}

		private static final int HASH_A = 0x19660d;
		private static final int HASH_C = 0x3c6ef35f;

		@Override
		public int hashCode() {

			final int xTransform = HASH_A * (x ^ 0x5DDE) + HASH_C;
			final int zTransform = HASH_A * (z ^ 0x03ED) + HASH_C;
			final int yTransform = HASH_A * (y ^ 0x06FA) + HASH_C;
			return xTransform ^ zTransform ^ yTransform;
		}

	}

	private static final class Entry {
		final BlockNode key;
		final int hash;
		Entry nextInBucket;

		Entry(BlockNode key, int keyHash) {
			this.key = key;
			this.hash = keyHash;
		}
	}

	private static int hash(BlockNode n) {

		int h = n.hashCode();
		return h;
		//h ^= (h >>> 20) ^ (h >>> 12);
		//return h ^ (h >>> 7) ^ (h >>> 4);
	}

	final static BlockPool pool = new BlockPool(false);

	BlockNode head;
	BlockNode tail;

	private int size;

	private transient int mask;
	private transient Entry[] hashTable;

	private final boolean _noDupe;

	public BlockPool(boolean preventDupes) {

		_noDupe = preventDupes;
		if (_noDupe) {
			hashTable = new Entry[16];
			mask = 15;
		}
	}

	public BlockPool() {

		this(true);
	}

	public static BlockNode getNext(BlockPos pos) {

		return getNext(pos.getX(), pos.getY(), pos.getZ());
	}

	public static BlockNode getNext(int x, int y, int z) {

		BlockNode r;
		synchronized (pool) {
			if (pool.head == null) {
				r = new BlockNode(pool, x, y, z);
				return r;
			}
			r = pool.shift();
		}
		r.reset(x, y, z);
		r.next = null;
		r.prev = null;
		return r;
	}

	public void push(BlockNode obj) {

		if (_noDupe) {
			int hash = hash(obj);
			if (seek(obj, hash) != null) {
				obj.free();
				return;
			}
			insert(new Entry(obj, hash));
			rehashIfNecessary();
		}
		obj.prev = tail;
		obj.next = null;
		if (tail != null)
			tail.next = obj;
		else
			head = obj;
		tail = obj;
	}

	public BlockNode pop() {

		BlockNode obj = tail;
		if (obj != null) {
			if (_noDupe)
				delete(seek(obj, hash(obj)));
			tail = obj.prev;
			obj.prev = null;
			if (tail != null)
				tail.next = null;
			else
				head = null;
		}
		return obj;
	}

	public BlockNode peek() {

		return tail;
	}

	public BlockNode poke() {

		return head;
	}

	public void unshift(BlockNode obj) {

		if (_noDupe) {
			int hash = hash(obj);
			if (seek(obj, hash) != null) {
				obj.free();
				return;
			}
			insert(new Entry(obj, hash));
			rehashIfNecessary();
		}
		obj.next = head;
		obj.prev = null;
		if (head != null)
			head.prev = obj;
		else
			tail = obj;
		head = obj;
	}

	public BlockNode shift() {

		BlockNode obj = head;
		if (obj != null) {
			if (_noDupe)
				delete(seek(obj, hash(obj)));
			head = obj.next;
			obj.next = null;
			if (head != null)
				head.prev = null;
			else
				tail = null;
		}
		return obj;
	}

	public int size() {

		return size;
	}

	private Entry seek(BlockNode obj, int hash) {

		for (Entry entry = hashTable[hash & mask];
				entry != null;
				entry = entry.nextInBucket)
			if (hash == entry.hash && Objects.equal(obj, entry.key))
				return entry;

		return null;
	}

	public boolean contains(BlockNode obj) {

		return seek(obj, hash(obj)) != null;
	}

	private void insert(Entry entry) {

		int bucket = entry.hash & mask;
		entry.nextInBucket = hashTable[bucket];
		hashTable[bucket] = entry;
		++size;
	}

	private void delete(Entry entry) {

		int bucket = entry.hash & mask;
		Entry prev = null, cur = hashTable[bucket];
		l: {
			if (cur != entry) for (; true; cur = cur.nextInBucket) {
				if (cur == entry) {
					prev.nextInBucket = entry.nextInBucket;
					break l;
				}
				prev = cur;
			}
			hashTable[bucket] = cur.nextInBucket;
		}
		--size;
	}

	private void rehashIfNecessary() {

		Entry[] old = hashTable, newTable;
		if (size > old.length * 2 && old.length < Ints.MAX_POWER_OF_TWO) {
			int newTableSize = old.length * 2, newMask = newTableSize - 1;
			newTable = hashTable = new Entry[newTableSize];
			mask = newMask;

			for (int bucket = old.length; bucket --> 0 ; ) {
				Entry entry = old[bucket];
				while (entry != null) {
					Entry nextEntry = entry.nextInBucket;
					int keyBucket = entry.hash & newMask;
					entry.nextInBucket = newTable[keyBucket];
					newTable[keyBucket] = entry;
					entry = nextEntry;
				}
			}
		}
	}

}
