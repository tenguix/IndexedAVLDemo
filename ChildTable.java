import java.util.Iterator;
import java.util.NoSuchElementException;

public class ChildTable<T extends Comparable<? super T>> extends HashTable
{
      /* Protected methods */

    protected void init(int initial_size)
    {
        super.init(initial_size);
        for(int i = 0; i < table_size; ++i)
        {
            table[i] = new ChildTableNode(null, null);
        } //end for
    } //end init

    protected void rehash()
    {
        Iterator<T> it = iterator();
        super.rehash();
        while(it.hasNext())
        {
            add(it.next());
        } //end while
    } //end rehash

      /* Private methods */

    @SuppressWarnings("unchecked")
    private ChildTableNode getNodeBefore(T data)
    {
        ChildTableNode cur = (ChildTableNode) table[hashIndex(data)];
        while(cur.hasNext() && data.compareTo(cur.getNext().getData()) < 0)
        {
            cur = cur.getNext();
        } //end while
        return cur;
    } //end getNodeBefore

      /* Public methods */

      /* Check to see if a child with the given data exists. */
    public final boolean contains(T data)
    {
        ChildTableNode cur = getNodeBefore(data);
        return cur.hasNext() && cur.getNext().getData().equals(data);
    } //end contains

    /*
     * Add a single child to the table.
     * A false return value indicates that an entry for the given data
     * already present in the table and that the table was not modified.
     */
    public final boolean add(T data)
    {
        boolean retval;
        ChildTableNode cur = getNodeBefore(data);
        if(cur.hasNext() && cur.getNext().getData().equals(data))
        {
            retval = false;
        } //end if
        else
        {
            cur.setNext(new ChildTableNode(data, cur.getNext()));
            if(++count > threshold)
            {
                rehash();
            } //end else:if
            retval = true;
        } //end else
        return retval;
    } //end add

      /* Remove a single entry. */
    public final boolean remove(T data)
    {
        boolean retval = false;
        ChildTableNode cur = getNodeBefore(data);
        if(cur.hasNext() && cur.getNext().getData().equals(data))
        {
            cur.setNext(cur.getNext().getNext());
            --count;
            retval = true;
        } //end if
        return retval;
    } //end remove

      /* Get an iterator for child node data. */
    public final Iterator<T> iterator()
    {
        return new ChildTableIterator();
    } //end iterator

      /* Convert the entire table into a single, printable string. */
    public final String toString()
    {
        if(empty())
        {
            return "{}";
        } //end if
        Iterator<T> it = iterator();
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(it.next());
        while(it.hasNext())
        {
            sb.append(",").append(it.next());
        } //end while
        return sb.append("}").toString();
    } //end toString

      /* Inner classes */

    private final class ChildTableNode
    {
          /* Data fields */

        private T data;
        private ChildTableNode next;

          /* Constructor */

        public ChildTableNode(T data, ChildTableNode next)
        {
            this.data = data;
            this.next = next;
        } //end ctor

          /* Public methods */

        public T getData()
        {
            return data;
        } //end getData

        public ChildTableNode getNext()
        {
            return next;
        } //end getNext

        public boolean hasNext()
        {
            return next != null;
        } //end hasNext

        public void setNext(ChildTableNode next)
        {
            this.next = next;
        } //end setNext

    } //end ChildTableNode

    private final class ChildTableIterator implements Iterator<T>
    {
          /* Data fields */

        private Object[] table;
        private int index;
        private ChildTableNode cur;
        private T next;

          /* Constructor */

        @SuppressWarnings("unchecked")
        public ChildTableIterator()
        {
            this.table = ChildTable.this.table;
            this.index = 0;
            this.cur = (ChildTableNode) table[0];
            this.next = fetch();
        } //end ctor

          /* Private method */

        @SuppressWarnings("unchecked")
        private T fetch()
        {
            while(!cur.hasNext() && ++index < table.length)
            {
                cur = (ChildTableNode) table[index];
            } //end while
            return cur.hasNext() ? (cur = cur.getNext()).getData() : null;
        } //end fetch

          /* Public methods */

        public boolean hasNext()
        {
            return next != null;
        } //end hasNext

        public T next()
        {
            if(!hasNext())
            {
                throw new NoSuchElementException
                        ("Cannot iterate past the end of the table.");
            } //end if
            T retval = next;
            next = fetch();
            return retval;
        } //end next

        public void remove()
        {
            throw new UnsupportedOperationException
                    ("This iterator does not support the 'remove' method.");
        } //end remove

    } //end ChildTableIterator

} //end ChildTable
