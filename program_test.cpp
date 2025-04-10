#include <knight/knight_std.h>
class Person {
int age;
std::string name;
};
int calculate(int a, int b) { 
	return a * b;
} 
int main() { 
	std::string name = "Mart van der Zalm";
	int age = 18;
	int a = 20;
	int result = a + age;
knight::read();
knight::print(result);
knight::print(age);
	return 0;
} 

