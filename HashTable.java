public abstract class HashTable
{
      /* Data fields */

    protected Object[] table; //The hash table itself
    protected int count; //Number of elements in table
    protected int table_size; //Number of hash buckets
    protected float load_factor; //Max ratio of elements to buckets
    protected int threshold; //Max count as determined by load factor

      /* Constructor */

    protected HashTable(int initial_size, float load_factor)
    {
        if(initial_size <= 0 || load_factor <= 0f)
        {
            throw new IllegalArgumentException
                    ("Size and load factor must both be greater than zero.");
        } //end if
        this.load_factor = load_factor;
        init(initial_size);
    } //end ctor

    public HashTable()
    {
        this(127, 0.5f);
    } //end ctor

      /* Protected methods */

    protected void init(int initial_size)
    {
        this.count = 0;
        this.table_size = nextPrime(initial_size);
        this.table = new Object[table_size];
        this.threshold = (int)(load_factor * table_size);
    } //end init

    protected final int nextPrime(int n)
    {
        if(n > 3)
        {
          OUTER:
            for(n |= 1 ;; n += 2)
            { //Skip over even numbers.
                for(int i = 3; i*i <= n; i += 2)
                { //Check factors up to the square root of n.
                    if(n % i == 0)
                    { //Not prime.
                        continue OUTER;
                    }
                } //end if:for:for
                break;
            } //end if:for
        } //end if
        return n;
    } //end nextPrime

    protected final int hashIndex(Object data)
    {
        int index = data.hashCode() % table_size;
        if(index < 0)
        {
            index += table_size;
        } //end if
        return index;
    } //end hashIndex

    protected void rehash()
    {
        init(table_size * 2);
    } //end rehash

    public boolean empty()
    {
        return count == 0;
    } //end empty

} //end HashTable
