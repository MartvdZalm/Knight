#include <knight/std.h>
int main() { 
knight::List<int> v;
v.add(25);
v.add(10);
v.add(66);
v.add(20);
v.sort();
v.print();
knight::print(v.size());
return 0;
} 

