import java.util.Iterator;

public class IndexedAVL<T extends Comparable<? super T>>
{
      /* Data fields */

    private int count;
    private BiNode root;
    private ParentTable<T> table;

      /* Constructor */

    public IndexedAVL(ParentTable<T> table)
    {
        this.count = 0;
        this.root = null;
        this.table = table;
    } //end ctor

      /* Private methods */

      /* Fix imbalances in a subtree. */
    private BiNode balance(BiNode root)
    {
        int bf = getBF(root);
        if(bf < -1)
        {
            if(getBF(root.getLC()) < 0)
            {
                root = rotateR(root);
            } //end if:if
            else
            {
                root = rotateLR(root);
            } //end if:else
        } //end if
        else if(bf > 1)
        {
            if(getBF(root.getRC()) > 0)
            {
                root = rotateL(root);
            } //end elseif:if
            else
            {
                root = rotateRL(root);
            } //end elseif:else
        } //end elseif
        return root;
    } //end balance

      /* Left-rotation. */
    private BiNode rotateL(BiNode root)
    {
        BiNode swap = root.getRC();
        root.setRC(swap.getLC());
        swap.setLC(root);
        return swap;
    } //end rotateL

      /* Right-left rotation. */
    private BiNode rotateRL(BiNode root)
    {
        BiNode swap = root.getRC();
        root.setRC(rotateR(swap));
        return rotateL(root);
    } //end rotateRL

      /* Left-right rotation. */
    private BiNode rotateLR(BiNode root)
    {
        BiNode swap = root.getLC();
        root.setLC(rotateL(swap));
        return rotateR(root);
    } //end rotateLR

      /* Right rotation. */
    private BiNode rotateR(BiNode root)
    {
        BiNode swap = root.getLC();
        root.setLC(swap.getRC());
        swap.setRC(root);
        return swap;
    } //end rotateR

      /* Get balance factor. */
    private int getBF(BiNode root)
    {
        int lh = root.hasLC() ? getHeight(root.getLC())+1 : 0;
        int rh = root.hasRC() ? getHeight(root.getRC())+1 : 0;
        return rh - lh;
    } //end getBF

      /* Get maximum distance from root to leaf. */
    private int getHeight(BiNode root)
    {
        int lh = root.hasLC() ? getHeight(root.getLC())+1 : 0;
        int rh = root.hasRC() ? getHeight(root.getRC())+1 : 0;
        return (lh > rh) ? lh : rh;
    } //end getHeight

      /*
       * Attempt to insert node into subtree.
       * A false return value indicates that a node with the given
       * data was already present and that the tree was not modified.
       */
    private boolean addFrom(BiNode root, T data)
    {
        boolean retval = false;
        int cmp = data.compareTo(root.getData());
        if(cmp < 0)
        {
            if(!root.hasLC())
            {
                root.setLC(new BiNode(data));
                retval = true;
            } //end if:if
            else if(addFrom(root.getLC(), data))
            {
                root.setLC(balance(root.getLC()));
                retval = true;
            } //end if:elseif
        } //end if
        else if(cmp > 0)
        {
            if(!root.hasRC())
            {
                root.setRC(new BiNode(data));
                retval = true;
            } //end elseif:if
            else if(addFrom(root.getRC(), data))
            {
                root.setRC(balance(root.getRC()));
                retval = true;
            } //end elseif:elseif
        } //end elseif
        return retval;
    } //end addFrom

      /* Public methods */

    public boolean empty()
    {
        return count == 0;
    } //end empty

    public int getCount()
    {
        return count;
    } //end getCount

      /* Add a new element to the tree. */
    public boolean add(T data)
    {
        if(data == null)
        {
            throw new IllegalArgumentException
                    ("Cannot add null element to tree.");
        } //end if
        else if(empty())
        {
            root = new BiNode(data);
        } //end elseif
        else if(addFrom(root, data))
        {
            root = balance(root);
        } //end elseif
        else
        {
            return false;
        } //end else
        ++count;
        return true;
    } //end add

      /*
       * Convert the entire tree into a single, printable string.
       * The returned string depicts each node beside its corresponding
       * (virtual) array index within the clockwise-rotated tree.
       */
    private StringBuilder buildString
            (StringBuilder sb, BiNode root, int index, int height)
    {
        if(root.hasLC())
        {
            buildString(sb, root.getLC(), index - (1<<height), height-1);
        } //end if
        else
        {
            for(int i = 0; i <= height; ++i)
            {
                sb.append("\n");
            } //end else:for
        } //end else
        for(int i = 0; i <= height; ++i)
        {
            sb.append("\t");
        } //end for
        sb.append("[").append(index).append("](")
                .append(root.getData()).append(")\n");
        if(root.hasRC())
        {
            buildString(sb, root.getRC(), index + (1<<height), height-1);
        } //end if
        else
        {
            for(int i = 0; i <= height; ++i)
            {
                sb.append("\n");
            } //end else:for
        } //end else
        return sb;
    } //end buildString

    public String toString()
    {
        if(empty())
        {
            return "";
        } //end if
        int height = getHeight(root);
        return buildString(new StringBuilder(), root, 1<<height, height-1)
                .toString();
    } //end toString

      /* Inner class */

    private class BiNode
    {
          /* Data fields */

        private T data;
        private BiNode left_child, right_child;

          /* Constructor */

        public BiNode(T data)
        {
            this.data = data;
            this.left_child = null;
            this.right_child = null;
        } //end ctor

          /* Private methods */

          /* Update lookup table when we unlink a child node. */
        private void orphan(BiNode child)
        {
            if(child == null)
            {
                return;
            } //end if
            Iterator<T> it = table.childTableIterator(child.data);
            if(it != null)
            {
                while(it.hasNext())
                {
                    table.remove(this.data, it.next());
                } //end if:while
            } //end if
            table.remove(this.data, child.data);
        } //end orphan

          /* Update lookup table when we link a child node. */
        private void adopt(BiNode child)
        {
            if(child == null)
            {
                return;
            } //end if
            Iterator<T> it = table.childTableIterator(child.data);
            if(it != null)
            {
                while(it.hasNext())
                {
                    table.add(this.data, it.next());
                } //end while
            } //end if
            table.add(this.data, child.data);
        } //end adopt

          /* Public methods */

        public T getData()
        {
            return data;
        } //end getData

        public BiNode getLC()
        {
            return left_child;
        } //end getLC

        public BiNode getRC()
        {
            return right_child;
        } //end getRC

        public boolean hasLC()
        {
            return left_child != null;
        } //end hasLC

        public boolean hasRC()
        {
            return right_child != null;
        } //end hasRC

        public void setLC(BiNode left_child)
        {
            orphan(this.left_child);
            adopt(left_child);
            this.left_child = left_child;
        } //end setLC

        public void setRC(BiNode right_child)
        {
            orphan(this.right_child);
            adopt(right_child);
            this.right_child = right_child;
        } //end setRC

    } //end BiNode

} //end IndexedAVL
