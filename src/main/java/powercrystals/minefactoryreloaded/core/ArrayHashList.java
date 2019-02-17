package powercrystals.minefactoryreloaded.core;

import com.google.common.base.Objects;
import com.google.common.primitives.Ints;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

@SuppressWarnings("unchecked")
public class ArrayHashList<E extends Object> extends AbstractCollection<E> implements Cloneable {
	// TODO: implements List<E>, java.io.Serializable

	protected static final class Entry {

		protected final Object key;
		protected final int hash;
		protected Entry nextInBucket;

		protected Entry(Object key, int keyHash) {

			this.key = key;
			this.hash = keyHash;
		}
	}

	protected static int hash(Object n) {

		int h = n == null ? 0 : n.hashCode();
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	private static int roundUpToPowerOf2(int number) {

		return number >= Ints.MAX_POWER_OF_TWO
				? Ints.MAX_POWER_OF_TWO
						: (number > 2) ? Integer.highestOneBit((number - 1) << 1) : 2;
	}

	private transient Object[] elementData;
	protected transient int size;
	protected transient int mask;
	protected transient Entry[] hashTable;
	protected transient int modCount;

	public ArrayHashList() {

		elementData = new Object[10];
		hashTable = new Entry[8];
		mask = 7;
	}

	public ArrayHashList(int size) {

		elementData = new Object[size];
		size = roundUpToPowerOf2(size) >> 1;
						hashTable = new Entry[size];
						mask = size - 1;
	}

	public ArrayHashList(Collection<E> col) {

		int size = col.size();
		elementData = new Object[size];
		size = roundUpToPowerOf2(size) >> 1;
		hashTable = new Entry[size];
		mask = size - 1;
		addAll(col);
	}

	@Override
	public int size() {

		return size;
	}

	@Override
	public boolean add(E obj) {

		int hash = hash(obj);
		if (seek(obj, hash) != null)
			return false;

		ensureCapacityInternal(size + 1);
		elementData[size++] = obj;
		insert(new Entry(obj, hash));
		rehashIfNecessary();

		return true;
	}

	public E get(int index) {

		checkElementIndex(index);
		return index(index);
	}

	/**
	 * Returns the index of the only occurrence of the specified element
	 * in this list, or -1 if this list does not contain the element.
	 * More formally, returns the lowest index <tt>i</tt> such that
	 * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
	 * or -1 if there is no such index.
	 *
	 * <tt>lastIndexOf</tt> not provided because this collection does not allow duplicates.
	 */
	public int indexOf(Object obj) {

		Entry e = seek(obj, hash(obj));
		if (e == null)
			return -1;

		Object o = e.key;
		Object[] data = elementData;
		int i = size;
		while (i --> 0)
			if (data[i] == o)
				break;
		return i;
	}

	@Override
	public boolean contains(Object obj) {

		return seek(obj, hash(obj)) != null;
	}

	public E remove(int index) {

		checkElementIndex(index);

		E oldValue = index(index);
		delete(seek(oldValue, hash(oldValue)));
		fastRemove(index);

		return oldValue;
	}

	@Override
	public boolean remove(Object obj) {

		Entry e = seek(obj, hash(obj));
		if (e == null)
			return false;

		Object o = e.key;
		Object[] data = elementData;
		for (int i = size; i --> 0; )
			if (data[i] == o) {
				fastRemove(i);
				break;
			}
		delete(e);
		return true;
	}

	private void fastRemove(int index) {

		modCount++;
		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(elementData, index + 1, elementData, index, numMoved);
		elementData[--size] = null; // clear to let GC do its work
	}

	// { following methods (until the next }) copied mostly verbatim from ArrayList
	@Override
	public void clear() {
		modCount++;

		// clear to let GC do its work
		for (int i = 0; i < size; i++)
			elementData[i] = null;

		for (int i = hashTable.length; i --> 0; )
			hashTable[i] = null;

		size = 0;
	}

	/**
	 * Trims the capacity of this <tt>ArrayHashList</tt> instance to be the
	 * list's current size.  An application can use this operation to minimize
	 * the storage of an <tt>ArrayHashList</tt> instance.
	 */
	public void trimToSize() {

		++modCount;
		if (size < elementData.length) {
			elementData = Arrays.copyOf(elementData, size);
		}
	}

	/**
	 * Increases the capacity of this <tt>ArrayHashList</tt> instance, if
	 * necessary, to ensure that it can hold at least the number of elements
	 * specified by the minimum capacity argument.
	 *
	 * @param   minCapacity   the desired minimum capacity
	 */
	public void ensureCapacity(int minCapacity) {

		if (minCapacity > 0) {
			ensureCapacityInternal(minCapacity);
		}
	}

	private void ensureCapacityInternal(int minCapacity) {

		++modCount;
		// overflow-conscious code
		if (minCapacity - elementData.length > 0)
			grow(minCapacity);
	}

	/**
	 * The maximum size of array to allocate.
	 * Some VMs reserve some header words in an array.
	 * Attempts to allocate larger arrays may result in
	 * OutOfMemoryError: Requested array size exceeds VM limit
	 */
	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

	/**
	 * Increases the capacity to ensure that it can hold at least the
	 * number of elements specified by the minimum capacity argument.
	 *
	 * @param minCapacity the desired minimum capacity
	 */
	private void grow(int minCapacity) {

		// overflow-conscious code
		int oldCapacity = elementData.length;
		int newCapacity = oldCapacity + (oldCapacity >> 1);
		if (newCapacity - minCapacity < 0)
			newCapacity = minCapacity;
		if (newCapacity - MAX_ARRAY_SIZE > 0)
			newCapacity = hugeCapacity(minCapacity);
		// minCapacity is usually close to size, so this is a win:
		elementData = Arrays.copyOf(elementData, newCapacity);
	}

	private static int hugeCapacity(int minCapacity) {

		if (minCapacity < 0) // overflow
			throw new OutOfMemoryError();
		return (minCapacity > MAX_ARRAY_SIZE) ?
				Integer.MAX_VALUE :
					MAX_ARRAY_SIZE;
	}
	// }

	E index(int index) {
		return (E) elementData[index];
	}

	protected Entry seek(Object obj, int hash) {

		for (Entry entry = hashTable[hash & mask];
				entry != null;
				entry = entry.nextInBucket)
			if (hash == entry.hash && Objects.equal(obj, entry.key))
				return entry;

		return null;
	}

	protected void insert(Entry entry) {

		int bucket = entry.hash & mask;
		entry.nextInBucket = hashTable[bucket];
		hashTable[bucket] = entry;
	}

	protected void delete(Entry entry) {

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
	}

	protected void rehashIfNecessary() {

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

	@Override
	public ArrayHashList<E> clone() {

		return new ArrayHashList<>(this);
	}

	@Override
	public Iterator<E> iterator() {

		return new Itr();
	}

	/*public ListIterator<E> listIterator() {

		return listIterator(0);
	}

	public ListIterator<E> listIterator(int index) {

		checkPositionIndex(index);
		return new ListItr(index);
	}//*/

	protected boolean isElementIndex(int index) {

		return index >= 0 && index < size;
	}

	protected boolean isPositionIndex(int index) {

		return index >= 0 && index <= size;
	}

	protected String outOfBoundsMsg(int index) {

		return "Index: "+index+", Size: "+size;
	}

	protected void checkElementIndex(int index) {

		if (!isElementIndex(index))
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	protected void checkPositionIndex(int index) {

		if (!isPositionIndex(index))
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	private class Itr implements Iterator<E> {
		int cursor;       // index of next element to return
		int lastRet = -1; // index of last element returned; -1 if no such
		int expectedModCount = modCount;

		@Override
		public boolean hasNext() {
			return cursor != size;
		}

		@Override
		public E next() {
			checkForComodification();
			int i = cursor;
			if (i >= size)
				throw new NoSuchElementException();
			Object[] elementData = ArrayHashList.this.elementData;
			if (i >= elementData.length)
				throw new ConcurrentModificationException();
			cursor = i + 1;
			return (E) elementData[lastRet = i];
		}

		@Override
		public void remove() {
			if (lastRet < 0)
				throw new IllegalStateException();
			checkForComodification();

			try {
				ArrayHashList.this.remove(lastRet);
				cursor = lastRet;
				lastRet = -1;
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		final void checkForComodification() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
		}
	}
	/*
	private class ListItr extends Itr implements ListIterator<E> {
		ListItr(int index) {
			super();
			cursor = index;
		}

		@Override
		public boolean hasPrevious() {
			return cursor != 0;
		}

		@Override
		public int nextIndex() {
			return cursor;
		}

		@Override
		public int previousIndex() {
			return cursor - 1;
		}

		@Override
		@SuppressWarnings("unchecked")
		public E previous() {
			checkForComodification();
			int i = cursor - 1;
			if (i < 0)
				throw new NoSuchElementException();
			Object[] elementData = ArrayHashList.this.elementData;
			if (i >= elementData.length)
				throw new ConcurrentModificationException();
			cursor = i;
			return (E) elementData[lastRet = i];
		}

		@Override
		public void set(E e) {
			if (lastRet < 0)
				throw new IllegalStateException();
			checkForComodification();

			try {
				ArrayHashList.this.set(lastRet, e);
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		@Override
		public void add(E e) {
			checkForComodification();

			try {
				int i = cursor;
				ArrayHashList.this.add(i, e);
				cursor = i + 1;
				lastRet = -1;
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}//*/

}
