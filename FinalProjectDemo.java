import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Scanner;

public class FinalProjectDemo
{
    public static void main(String[] argv)
            throws IOException, InterruptedIOException
    {
        Scanner sc = new Scanner(System.in);
        ParentTable<String> table = new ParentTable<String>();
        IndexedAVL<String> tree = new IndexedAVL<String>(table);

          /* Read test data from any files given on command line. */
        for(String filename : argv)
        {
            readFile(filename, tree);
        } //end for

          /* Interpret simple interactive commands. */
        while(sc.hasNext())
        {
            String cmd = sc.next();
            if(cmd.equals("read"))
            {
                if(!sc.hasNext())
                {
                    throw new InterruptedIOException();
                } //end while:if:if
                readFile(sc.next(), tree);
            } //end while:if
            else if(cmd.equals("print"))
            {
                if(!sc.hasNext())
                {
                    throw new InterruptedIOException();
                } //end while:elseif:if
                String arg = sc.next();
                if(arg.equals("tree"))
                {
                    System.out.println(separator);
                    System.out.print(tree);
                    System.out.println(separator);
                } //end while:elseif:if
                else if(arg.equals("table"))
                {
                    System.out.println(separator);
                    System.out.print(table);
                    System.out.println(separator);
                } //end while:elseif:elseif
                else
                {
                    throw new IOException
                            ("Invalid 'print' argument.");
                } //end while:elseif:else
            } //end while:if
            else if(cmd.equals("query")      //This is the
                    || cmd.equals("lookup")) //parent-child lookup.
            {
                  /* Read in parent data and child data. */
                if(!sc.hasNext())
                {
                    throw new InterruptedIOException();
                } //end while:elseif:if
                String parent = sc.next();
                if(!sc.hasNext())
                {
                    throw new InterruptedIOException();
                } //end while:elseif:if
                String child = sc.next();
                  /* Calculate execution time of lookup. */
                boolean retval;
                long start, finish;
                start = System.nanoTime();
                retval = table.contains(parent, child);
                finish = System.nanoTime();
                  /* Print time (in milliseconds) and the return value. */
                System.err.printf("[execution time: %5.3f ms]%n",
                                  (float)(finish - start)/1000000);
                System.out.println(retval);
            } //end while:elseif
            else if(cmd.equals("add"))
            {
                if(!sc.hasNext())
                {
                    throw new InterruptedIOException();
                } //end while:elseif:if
                tree.add(sc.next());
            } //end while:elseif
            else
            {
                throw new IOException
                        ("Command '"+ cmd +"' not recognized.");
            } //end while:else
        } //end while
    } //end main

    public static String separator =
            "----------------------------------------------------------------";

      /* Make stderr more fancy with an animated indicator. */
    public static String[] frames = { "/", "-", "\\", "|" };
    public static int frame_idx = 0;
    public static String indicator()
    {
        return "\b" + frames[frame_idx = (frame_idx+1) % frames.length];
    } //end indicator

      /* Read word-like tokens from a given file and add to tree. */
    public static void readFile(String filename, IndexedAVL<String> tree)
    {
        Scanner sc;
        String basename;
        try
        {
            File f = new File(filename);
            basename = f.getName();
            sc = new Scanner(f);
        } //end try
        catch(FileNotFoundException ex)
        {
            System.err.printf("Error: file \"%s\" not found.%n", filename);
            return;
        } //end catch
        sc.useDelimiter("(?!\\b-\\b)\\W+"); //Allow hyphenated words.
        int prior_count = tree.getCount();
        System.err.printf("Reading tokens from \"%s\" ... ", basename);
        while(sc.hasNext())
        {
            if(tree.add(sc.next()))
            {
                System.err.print(indicator());
            } //end while:if
        } //end while
        System.err.printf("\b added %d new entries.%n",
                          tree.getCount() - prior_count);
    } //end readFile

} //end FinalProjectDemo
