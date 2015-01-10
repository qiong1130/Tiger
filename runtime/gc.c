#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
// The Gimple Garbage Collector.


//===============================================================//
// The Java Heap data structure.

/*   
      ----------------------------------------------------
      |                        |                         |
      ----------------------------------------------------
      ^\                      /^
      | \<~~~~~~~ size ~~~~~>/ |
    from                       to
 */
struct JavaHeap
{
  int size;         // in bytes, note that this if for semi-heap size
  char *from;       // the "from" space pointer
  char *fromFree;   // the next "free" space in the from space
  char *to;         // the "to" space pointer
  char *toStart;    // "start" address in the "to" space
  char *toNext;     // "next" free space pointer in the to space
};

// The Java heap, which is initialized by the following
// "heap_init" function.
struct JavaHeap heap;

// Lab 4, exercise 10:
// Given the heap size (in bytes), allocate a Java heap
// in the C heap, initialize the relevant fields.
void Tiger_heap_init (int heapSize)
{
  // You should write 7 statement here:
  // #1: allocate a chunk of memory of size "heapSize" using "malloc"
	char *Heap = (char *)malloc(heapSize);
  // #2: initialize the "size" field, note that "size" field
  // is for semi-heap, but "heapSize" is for the whole heap.
	heap.size = heapSize/2;
  // #3: initialize the "from" field (with what value?)
 	heap.from = Heap;
  // #4: initialize the "fromFree" field (with what value?)
	heap.fromFree = Heap;
  // #5: initialize the "to" field (with what value?)
	heap.to = heap.from+heap.size;
  // #6: initizlize the "toStart" field with NULL;
 	heap.toStart = heap.to;
  // #7: initialize the "toNext" field with NULL;
 	heap.toNext = heap.to;
  return;
}

// The "prev" pointer, pointing to the top frame on the GC stack. 
// (see part A of Lab 4)
int gloRemain=0;
int count=0;
void* prev;
static void Tiger_gc ();


//===============================================================//
// Object Model And allocation


// Lab 4: exercise 11:
// "new" a new object, do necessary initializations, and
// return the pointer (reference).
/*    ----------------
      | vptr      ---|----> (points to the virtual method table)
      |--------------|
      | isObjOrArray | (0: for normal objects)
      |--------------|
      | length       | (this field should be empty for normal objects)
      |--------------|
      | forwarding   | 
      |--------------|\
p---->| v_0          | \      
      |--------------|  s
      | ...          |  i
      |--------------|  z
      | v_{size-1}   | /e
      ----------------/
*/
// Try to allocate an object in the "from" space of the Java
// heap. Read Tiger book chapter 13.3 for details on the
// allocation.
// There are two cases to consider:
//   1. If the "from" space has enough space to hold this object, then
//      allocation succeeds, return the apropriate address (look at
//      the above figure, be careful);
//   2. if there is no enough space left in the "from" space, then
//      you should call the function "Tiger_gc()" to collect garbages.
//      and after the collection, there are still two sub-cases:
//        a: if there is enough space, you can do allocations just as case 1; 
//        b: if there is still no enough space, you can just issue
//           an error message ("OutOfMemory") and exit.
//           (However, a production compiler will try to expand
//           the Java heap.)
void *Tiger_new (void *vtable, int size)
{
  // Your code here:
  int * vptr = 0;
  int remain = heap.size-(heap.fromFree-heap.from);
  if(remain >= (size+16))
  {
  	 vptr = (int *)heap.fromFree;
  	 heap.fromFree += size+16;
  	 memset(vptr,0,size+16);
  	 vptr[0] = (int)vtable;
  	 vptr[1] = 0;
  	 vptr[2] = size;
  }
  else
  {
  	 gloRemain = remain;
	 Tiger_gc();
	 remain = heap.size - (heap.fromFree - heap.from);
	 if(remain >= (size+16))
	 {
		vptr = (int *)heap.fromFree;
  	 	heap.fromFree += size+16;
  	 	memset(vptr,0,size+16);
  	 	vptr[0] = (int)vtable;
  	 	vptr[1] = 0;
  	 	vptr[2] = size;
	 }
	 else 
	 {
	 	printf("OutOfMemory\n");
	 	exit(0);
	 }
  }
  return vptr;
}

// "new" an array of size "length", do necessary
// initializations. And each array comes with an
// extra "header" storing the array length and other information.
/*    ----------------
      | vptr         | (this field should be empty for an array)
      |--------------|
      | isObjOrArray | (1: for array)
      |--------------|
      | length       |
      |--------------|
      | forwarding   | 
      |--------------|\
p---->| e_0          | \      
      |--------------|  s
      | ...          |  i
      |--------------|  z
      | e_{length-1} | /e
      ----------------/
*/
// Try to allocate an array object in the "from" space of the Java
// heap. Read Tiger book chapter 13.3 for details on the
// allocation.
// There are two cases to consider:
//   1. If the "from" space has enough space to hold this array object, then
//      allocation succeeds, return the apropriate address (look at
//      the above figure, be careful);
//   2. if there is no enough space left in the "from" space, then
//      you should call the function "Tiger_gc()" to collect garbages.
//      and after the collection, there are still two sub-cases:
//        a: if there is enough space, you can do allocations just as case 1; 
//        b: if there is still no enough space, you can just issue
//           an error message ("OutOfMemory") and exit.
//           (However, a production compiler will try to expand
//           the Java heap.)
void *Tiger_new_array (int length)
{
  // Your code here:
  int remain = heap.size-(heap.fromFree-heap.from);
  int* vptr = 0;
  int s = length*4+16;
  if(remain >= s)
  {
      vptr = (int *)heap.fromFree;
      memset(vptr,0,s);
      vptr[1] = 1;
      vptr[2] = length;
 	  heap.fromFree += s;
  }
  else
  {
  	  gloRemain = remain;
  	  Tiger_gc();
  	  remain = heap.size-(heap.fromFree-heap.from);
  	  if(remain >= s)
  	  {
  	  	  vptr = (int *)heap.fromFree;
	      memset(vptr,0,s);
	      vptr[1] = 1;
	      vptr[2] = length;
	 	  heap.fromFree += s;
  	  }
  	  else
  	  {
  	  	  printf("OutOfMemory\n");
	 	  exit(0);
  	  }
  }
  return vptr;
}

//===============================================================//
// The Gimple Garbage Collector

// Lab 4, exercise 12:
// A copying collector based-on Cheney's algorithm.
void * Copy(void * p)
{ 
    int *obj = (int *)p;
    char *q = (char *)p;
    int s = 0;
    if(q != 0 && q < (heap.from+heap.size) && q >= heap.from)  
    {   
    	if((char*)obj[3] >= heap.toStart && (char*)obj[3] < heap.toNext)
			return (void*)obj[3];  
	    if(obj[1] == 0)
	    	s = obj[2]+16;
	    else
	    	s = obj[2]*4+16;
	    obj[3] = (int)heap.toNext;
	    memset(heap.toNext,0,s); 
	    memcpy(heap.toNext,p,s);
	    heap.toNext += s;
	    return (void *)obj[3];
    }
    else
    	return p;
}
static void Tiger_gc ()
{
  // Your code here:
  void *currentFrame = prev;
  int i;
  count++;
  clock_t begin, end;
  start = clock();
  while(currentFrame != 0)
  {
      char *string = *(char **)(currentFrame+4);
 	  int *paramter = *(int **)(currentFrame+8);
      for(i=0; *(char *)(string+i)!='\0'; i++)
      {
    	if(string[i] =='1')
    	{
    	  paramter[i] = (int)Copy((int*)paramter[i]); 
    	}
      }
	  int localNum = *(int *)(currentFrame+12);
	  if(localNum > 0)
	  {
		int *a = &currentFrame[4];
		for(i=0; i<localNum; i++)
		{
			a[i] = Copy((int *)a[i]);
		}
  	  }
  	  currentFrame = *(int **)currentFrame;  
   }
    char *scan = heap.toStart;
    int *scanInt = (int *)scan;
    char *obj_vptr;
    int num = 0,size = 0;
    while(scan < heap.toNext)
    {
    	int * vptr_ = *(int**)(scanInt);
    	obj_vptr = (char *)(vptr_[0]);
	    if((int)scanInt[1] == 0)
	    {
			for(num=0; obj_vptr[num]!='\0'; num++)
		    {
		    	if(obj_vptr[num] == '1')
		    	{
		    		scanInt[4+num] = (int)Copy((int *)scanInt[4+num]);
		    	}
		    }
		    size = scan[2]+16;
		    scan += size;
	    }
	    else
	    {
	    	size = scan[2];
			size *= 4;
			size += 16;
			scan += size;
	    }        
    }
	char *temp = heap.from;
	heap.from = heap.toStart;
    heap.toStart = temp;
    heap.fromFree = heap.toNext;
    heap.toNext = heap.toStart;
    heap.to = heap.toStart;
    int free_size = heap.size-gloRemain-(heap.fromFree-heap.from);
    end = clock();
    double time = (double)(end-begin);
    printf("%d round of GC: %fms, collected %d bytes\n",count,time,free_size);
}

