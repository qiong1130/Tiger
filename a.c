// This is automatically generated by the Tiger compiler.
// Do NOT modify!

// structures
struct intArray
{
  int length;
  int *array;
};

struct Factorial
{
  struct Factorial_vtable *vptr;
};
struct Fac
{
  struct Fac_vtable *vptr;
};
// vtables structures
struct Factorial_vtable
{
};

struct Fac_vtable
{
  int (*ComputeFac)(struct Fac *, int);
};


// methods declarations
int Fac_ComputeFac(struct Fac *this, int num);

// vtables
struct Factorial_vtable Factorial_vtable_ = 
{
};

struct Fac_vtable Fac_vtable_ = 
{
  Fac_ComputeFac
};


// methods
int Fac_ComputeFac(struct Fac *this, int num)
{
  int  num_aux;
  struct intArray * sdd;

  if (num < 1)
    num_aux = 1;
  else
    num_aux = num * this->vptr->ComputeFac(this, num - 1);
  while (num < 1)
    num_aux = 2;
  sdd = (struct intArray *)malloc(sizeof(struct intArray));
  sdd->length = 4;
  sdd->array = (int *)malloc((4)*sizeof(int));
  return num_aux;
}

// main method
int Tiger_main ()
{
  struct Fac *temp_1;
  System_out_println ((temp_1=((struct Fac*)(Tiger_new (&Fac_vtable_, sizeof(struct Fac)))), temp_1->vptr->ComputeFac(temp_1, 10)));
}



