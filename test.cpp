#include <iostream>
#include <functional>
#include <string>
#include <vector>

int main();

int calculate(int a, int b);
class Person
 {
public:
    std::string name;
    int age;
std::string getName(    );
void print(    );
};

void print(std::string input);
void println(std::string input);


int main() {
    Person person = Person()    ;
    person.age = 19    ;
    person.name = "Mart van der Zalm"    ;
    println(person.getName()    );
    person.print(    );
    return 0    ;
}


int calculate(int a, int b) {
    return a * b    ;
}

std::string Person::getName() {
    return name    ;
}

void Person::print() {
    println("Name=" + name + ", Age=" + std::to_string(age)    );
}


void print(std::string input) {
    std::cout << input;
}

void println(std::string input) {
    std::cout << input;
    std::cout << "\n";
}



