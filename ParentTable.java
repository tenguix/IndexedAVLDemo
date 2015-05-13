import java.util.Iterator;
import java.util.NoSuchElementException;

public class ParentTable<T extends Comparable<? super T>> extends HashTable
{
      /* Protected methods */

    protected final void init(int initial_size)
    {
        super.init(initial_size);
        for(int i = 0; i < table_size; ++i)
        {
            table[i] = new ParentTableNode(null, null, null);
        } //end for
    } //end init

    protected final void rehash()
    {
        ParentTableIterator pit = new ParentTableIterator();
        super.rehash();
        while(pit.hasNext())
        {
            T parent = pit.next();
            Iterator<T> cit = pit.childTableIterator();
            while(cit.hasNext())
            {
                add(parent, cit.next());
            } //end while:while
        } //end while
    } //end rehash

      /* Private methods */

    @SuppressWarnings("unchecked")
    private ParentTableNode getNodeBefore(T parent)
    {
        ParentTableNode cur = (ParentTableNode) table[hashIndex(parent)];
        while(cur.hasNext() && parent.compareTo(cur.getNext().getParent()) < 0)
        {
            cur = cur.getNext();
        } //end while
        return cur;
    } //end getNodeBefore

      /* Public methods */

      /* Check to see if a parent with the given data exists. */
    public final boolean contains(T parent)
    {
        ParentTableNode cur = getNodeBefore(parent);
        return cur.hasNext() && cur.getNext().getParent().equals(parent);
    } //end contains

      /*
       * Check to see if a parent-child relationship which
       * corresponds with the two elements given has been established.
       */
    public final boolean contains(T parent, T child)
    {
        ParentTableNode cur = getNodeBefore(parent);
        return cur.hasNext() && cur.getNext().getChildren().contains(child);
    } //end contains

      /*
       * Attempt to add a new entry to the lookup table.
       * A false return value indicates that the given pair was already
       * present in the table and that the table was not modified.
       */
    public final boolean add(T parent, T child)
    {
        ParentTableNode cur = getNodeBefore(parent);
        if(cur.hasNext() && cur.getNext().getParent().equals(parent))
        {
            if(!cur.getNext().getChildren().add(child))
            {
                return false;
            } //end if:if
        } //end if
        else
        {
            ChildTable<T> vt = new ChildTable<T>();
            vt.add(child);
            cur.setNext(new ParentTableNode(parent, vt, cur.getNext()));
        } //end else
        if(++count > threshold)
        {
            rehash();
        } //end if
        return true;
    } //end add

      /* Remove a parent and all of its children from the table. */
    public final boolean remove(T parent)
    {
        boolean retval = false;
        ParentTableNode cur = getNodeBefore(parent);
        if(cur.hasNext() && cur.getNext().getParent().equals(parent))
        {
            cur.setNext(cur.getNext().getNext());
            --count;
            retval = true;
        } //end if
        return retval;
    } //end remove

      /* Remove a single parent-child relationship from the table. */
    public final boolean remove(T parent, T child)
    {
        boolean retval = false;
        ParentTableNode cur = getNodeBefore(parent);
        if(cur.hasNext() && cur.getNext().getParent().equals(parent)
                && cur.getNext().getChildren().remove(child))
        {
            if(cur.getNext().getChildren().empty())
            {
                cur.setNext(cur.getNext().getNext());
            } //end if:if
            --count;
            retval = true;
        } //end if
        return retval;
    } //end remove

      /* Get an iterator for parent node data. */
    public final Iterator<T> parentTableIterator()
    {
        return (Iterator<T>) new ParentTableIterator();
    } //end parentIterator

      /* Get an iterator for the given parent's children. */
    public final Iterator<T> childTableIterator(T parent)
    {
        Iterator<T> retval = null;
        ParentTableNode cur = getNodeBefore(parent);
        if(cur.hasNext() && cur.getNext().getParent().equals(parent))
        {
            retval = cur.getNext().getChildren().iterator();
        } //end if
        return retval;
    } //end childTableIterator

      /* Convert the entire table into a single, printable string. */
    public final String toString()
    {
        if(empty())
        {
            return "";
        } //end if
        ParentTableIterator pit = new ParentTableIterator();
        StringBuilder sb = new StringBuilder();
        while(pit.hasNext())
        {
            sb.append("(").append(pit.next()).append(")=")
                    .append(pit.childTable()).append("\n");
        } //end while
        return sb.toString();
    } //end toString

      /* Inner classes */

    private final class ParentTableNode
    {
          /* Data fields */

        private T parent;
        private ChildTable<T> children;
        private ParentTableNode next;

          /* Constructor */

        public ParentTableNode(T parent, ChildTable<T> children,
                               ParentTableNode next)
        {
            this.parent = parent;
            this.children = children;
            this.next = next;
        } //end ctor

          /* Public methods */

        public T getParent()
        {
            return parent;
        } //end getParent

        public ChildTable<T> getChildren()
        {
            return children;
        } //end getChildren

        public ParentTableNode getNext()
        {
            return next;
        } //end next

        public boolean hasNext()
        {
            return next != null;
        } //end hasNext

        public void setNext(ParentTableNode next)
        {
            this.next = next;
        } //end setNext

    } //end ParentTableNode

    private final class ParentTableIterator implements Iterator<T>
    {
          /* Data fields */

        private Object[] table;
        private int index;
        private ParentTableNode cur, prev;
        private T next;

          /* Constructor */

        @SuppressWarnings("unchecked")
        public ParentTableIterator()
        {
            this.table = ParentTable.this.table;
            this.index = 0;
            this.cur = (ParentTableNode) table[0];
            this.next = fetch();
        } //end ctor

          /* Private method */

        @SuppressWarnings("unchecked")
        private T fetch()
        {
            prev = cur;
            while(!cur.hasNext() && ++index < table.length)
            {
                cur = (ParentTableNode) table[index];
            } //end while
            return cur.hasNext() ? (cur = cur.getNext()).getParent() : null;
        } //end fetch

          /* Public parentIterator methods */

        public ChildTable<T> childTable()
        {
            return prev.getChildren(); //We must be smart here.
        } //end childTable

        public Iterator<T> childTableIterator()
        {
            return prev.getChildren().iterator(); //We must be smart here, too.
        } //end childTableIterator

          /* Public Iterator<T> methods */

        public boolean hasNext()
        {
            return next != null;
        } //end hasNext

        public T next()
        {
            if(next == null)
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

    } //end ParentTableIterator

} //end ParentTable
