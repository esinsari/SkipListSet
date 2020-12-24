import java.util.*;

import java.lang.reflect.Array;

public class SkipListSet <T extends Comparable<T>> implements SortedSet<T>{
	
	private SkipListSetItem head;
	private SkipListSetItem tail;
	public int maxHeight = 2;
	public int size;  

	public void init() 
	{	
		size = 0;
		
     	head = new SkipListSetItem(null);   
		tail = new SkipListSetItem(null);   
		
		head.top = new SkipListSetItem(null);
		tail.top = new SkipListSetItem(null);
		
		head.right = tail;
		tail.left = head;
		
		head.top.below = head;
		tail.top.below = tail;
	}

	public SkipListSet() 
	{
		this.init();
	}
	
	
	@SuppressWarnings("unchecked")
	public void reBalance() 
	{	
		int adjust_size = size;
		Object[] array = this.toArray();
		
		this.clear();
		
		maxHeight = (int) (Math.log(adjust_size) / Math.log(2));

		for(Object e: array) 
		{	
			add((T) e);
		}
	}

	
	public int randomHeight() 
	{
		int height = 0;
		Random random = new Random();
		
		if(size > 8 && size < 16) 
			maxHeight = (int) (Math.log(size) / Math.log(2));
		
		height = random.nextInt(maxHeight) + 1;
		
		return height;
	}
	
	
	@Override
	public boolean add(T e) 
	{
		int height;
		SkipListSetItem newNode;
		SkipListSetItem baseNode;
		SkipListSetItem topNode;
		SkipListSetItem previous;
	
		if(contains(e)) return false;
	  		
		if(isEmpty())
			height = maxHeight;
		else
			height = randomHeight();
		
		baseNode = new SkipListSetItem(e);
		topNode = new SkipListSetItem(null);
		
		baseNode.level = 0;
		baseNode.height = height;
		baseNode.top = topNode;
		
		previous = getPrevious(head.getTop(), e); 
					
		if(isEmpty()) 
		{		
			while(head.level > height)
				previous = getPrevious(previous.below, e);
				
			while(previous.level > 0) 
			{
				newNode = new SkipListSetItem(e);
				newNode.level = previous.level;
				
				topNode.below = newNode;
				topNode = newNode;
				    
				head.right = newNode;
				newNode.right = tail; 
					
				previous = getPrevious(previous.below, e);			
			}
				
			if(previous.level == 0) 
			{
				topNode.below = baseNode;   
				head.right = baseNode;
				baseNode.right = tail;				
				tail.left = baseNode;
			}
				
			size++;
			return true;
			
		}
		else  
		{	
			while(previous.level > height)
				previous = getPrevious(previous.below, e);		
		
			while(previous.level > 0) 
			{
				newNode = new SkipListSetItem(e);
			    newNode.level = previous.level;
			    
			    topNode.below = newNode;
			    topNode = newNode;
			    
			    newNode.right = previous.right;
			    previous.right = newNode;
			  
			    previous = getPrevious(previous.below, e);			
			}
			
			if(previous.level == 0) 
			{
				topNode.below = baseNode;	
				baseNode.right = previous.right; 
				previous.right= baseNode;
				
				tail.left = baseNode;
			
			}
		
			size++;
			return true;
		}					
	}
	

	@Override
	public boolean addAll(Collection<? extends T> c) 
	{
		boolean flag = true;
		
		for(T node: c) 
		{	
			if(!add(node))
				return false;
		}
		
		return (flag) ? (true) : (false); 
	}
		
	
	@Override
	public void clear() 
	{
		this.init();
	}
			
	
	
	@Override
	public boolean contains(Object o)
	{	
		if(isEmpty()) 
			return false;
		
		SkipListSetItem next = head.right;
		SkipListSetItem temp = getPrevious(next, next.data);
		SkipListSetItem node = temp;
		SkipListSetItem node2;
		
		while(temp.right != null) 
		{
			if(temp.right.data == o) 
				return true;
			
			temp = temp.right;
		}
		
		if(node.below != null) 
		{
			while(node.below  != null)
			{
				node = getPrevious(node.below, node.data);
				
				if(node.right.data == o)
					return true;
				else 
				{
					node2 = node;
					while(node2.right != null) 
					{
						if(node2.right.data == o) 
							return true;
						
						node2 = node2.right;
					}
				}
				
				node = node.right;
			}	
		}
	
		return false;
	}
		
	
	@Override
	public boolean containsAll(Collection<?> c) 
	{
		boolean flag = true;
		
		for(Object node: c) 
		{
			if(!contains(node)) 
			{
				flag = false;
			}
		}
		
		if(flag) 
		{
			return true;
		}
		else
			return false;
	}
		
	
	
	@Override
	public boolean equals(Object c)
	{
		return (contains(c)) ? (true) : (false); 
	}
	
	

	@Override
	public int hashCode() {
		
		int hashVal = 0;
		
		Object[] array = this.toArray();
		
		for(Object e: array)
			hashVal += e.hashCode(); 
		
		return hashVal;
	}
	
	

	@Override
	public boolean isEmpty() 
	{
		return (size == 0) ? true: false;
	}
			
	

	@Override
	public Iterator<T> iterator() 
	{		  
		//Iteration starts from head
		return new SkipListSetIterator(head);
	}		


	
	@Override
	public boolean remove(Object o) 
	{
		if(isEmpty()) 
			return false;
		else if(!contains(o)) 
			return false;
		else 
		{
			SkipListSetItem temp = getPrevious(head.getTop(), o);
			
			while(temp != null) 
			{
				if(temp.right.data == o && temp.right != null) 
					temp.right = temp.right.right;
	
				temp = getPrevious(temp.below, o);
			}
			 
			size--;
		    return true;		
		}
	}
		
	
	
	@Override
	public boolean removeAll(Collection<?> c) 
	{
		if(isEmpty()) 
		{
			return false;
		}
		else 
		{
			boolean flag = true;
			
			for(Object node: c) 
			{	
				if(!remove(node))
					return false;
			}
			
			return (flag) ? (true) : (false); 
		}
	}
		
	
	
	@Override
	public boolean retainAll(Collection<?> c) 
	{
		Iterator<T> iterator = this.iterator();

		while(iterator.hasNext()) 
		{
			T data = (T) iterator.next();

			if(!c.contains(data))
				remove(data);
		}

		return true;
	}
			
	
	
	@Override
	public int size() 
	{
		return size;
	}
				
	

	@Override
	public Object[] toArray() 
	{
		int i = 0;
		Object[] array = new Object[size];		
		
		Iterator<T> iterator = this.iterator();
		
        while(iterator.hasNext()) 
        {
            array[i] = iterator.next();
            i++;
        }    
		return array;
	}
	
	
	@Override
    @SuppressWarnings({"unchecked", "hiding" })
	public <T> T[] toArray(T[] e) 
    {
    	if (e.length < size) 
    	{ 
    		  e = (T[]) Array.newInstance(e.getClass().getComponentType(), size);
    	} 
    	else if (e.length > size) 
    	{
    		  e[size] = null;
    	}
    	
    	return e;
	}
	  

	public T first() 
	{
		SkipListSetItem next = head.right;
		SkipListSetItem temp = getPrevious(next, next.data);
		
		while(temp.level != 0)
		{
			temp = getPrevious(temp.below, temp.data);
		}
				
		return (isEmpty()) ? null : temp.data;
	}
	
	
	public T last() 
	{	
		return (isEmpty()) ? null : tail.left.data;
	}

	
	public Comparator<? super T> comparator() {
        return null;
    }


    public SortedSet<T> subSet(T fromElement, T toElement) {
        return null;
    }

    
    public SortedSet<T> headSet(T toElement) {
        return null;
    }

    
    public SortedSet<T> tailSet(T fromElement) {
        return null;
    }
    
    
	@SuppressWarnings({ "unchecked" })
	public SkipListSetItem getPrevious(SkipListSetItem node, Object data) 
	{
		if (node == null) 
			return null;
		
		while (true) 
		{
			if (node.right.data != null) 
			{
				if(node.right.data.compareTo((T)data) >= 0) 
					return node;
				else 
				{
					node = node.right;
					continue;
			    }
			}
			else 
				return node;			
		}
	}
	
 
    
	public void printNode(SkipListSetItem n) 
    {	
		int i = n.height;
		
		while(i != 0)
		{
			System.out.print("[ " + n.data + " ] -- ");	
			i--;
		}   	
		
		int k = n.height;
		System.out.println("HEIGHT: " + k);
    }
       

  
    public void printGraph() 
    {
      	SkipListSetItem next = head.right;
    	SkipListSetItem temp = getPrevious(next, next.data);
    
    	System.out.println("\n   << SKIP LIST >>");
    	
    	while(temp.level != 0)
   		{
   			temp = temp.below;
    	}

		while (temp.right != null) 
		{
			printNode(temp);
			temp = temp.right;
		}		
    }
  
 
 
 	private class SkipListSetItem
 	{
 		T data;
 		int level; 
 		int height;
 		SkipListSetItem right;
 		SkipListSetItem left;
 		SkipListSetItem top;
 		SkipListSetItem below;
 	    
 		public SkipListSetItem(T data) 
 		{
 			this.data = data;
 			this.level = 0;
 			this.height = 0;
 			this.right = null;
 			this.left = null;
 			this.top = null;
 		}			
 		
 		public SkipListSetItem getRight()
 		{
 			return this.right; 
 		}
 		
 		public SkipListSetItem getBelow()
 		{
 			return this.below; 
 		}
 		
 		public SkipListSetItem getTop()
 		{
 			return (this.top != null) ? this : this.top.below; 
 		}
 		
 	}

 	
	private class SkipListSetIterator <E extends Comparable<E>> implements Iterator<E>{

		SkipListSetItem next;
		SkipListSetItem temp;
	
        public SkipListSetIterator(SkipListSetItem head) 
        {
            this.next = head.right;
            this.temp = find(next, next.data);
        }
     
        
		public boolean hasNext() 
		{
			SkipListSetItem node = temp;
		
			if(node.right != null)
				return true;
			else 
			{
				while(node.below  != null)
				{
					node = find(node.below, node.data);
					if (node.right != null)
						return true;	
				}
				
				return false;
			}
		}
		
		@SuppressWarnings("unchecked")
		public E next() 
		{            
			E val = (E)temp.data;
			
			if(temp.right != null)
				temp = temp.right;
			else 
			{
				while(temp.below  != null)
				{
					temp = find(temp.below, temp.data);
					if (temp.right != null) 
					{
						temp = temp.right;
						return val;	
					}
				}
			}
			return val;
		}
		
		public boolean remove(Object o) 
		{
			return false;
		}
	
		
		@SuppressWarnings("unchecked")
		public SkipListSetItem find(SkipListSetItem node, Object data) 
		{
			if (node == null) 
				return null;

			while (true) 
			{
				if (node.right.data != null) 
				{
					if(node.right.data.compareTo((T)data) >= 0)
						return node;
					else 
					{
						node = node.right;
						continue;
				    }
				}
				else 
					return node;			
			}
		}
		

	}
}
