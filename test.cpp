#include <iostream>
#include <functional>
#include <string>
#include <vector>


int calculate(int a, int b);
class Person
 {
public:
    std::string name;
    int age;
std::string getName(    );
};

void print(std::string input);



int calculate(int a, int b) {
    return a * b    ;
}

std::string Person::getName() {
    return name    ;
}


void print(std::string input) {
    std::cout << input << std::endl;
}



