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
};

class Out
 {
public:
void write(    std::string input    );
void writeln(    std::string input    );
};
void print(std::string input);
void println(std::string input);
std::string read();
int readInt();


int main() {
    calculate(10, 10    );
    Person person = Person()    ;
    person.age = 19    ;
    person.name = "Mart van der Zalm"    ;
    return 0    ;
}


int calculate(int a, int b) {
    return a * b    ;
}


void Out::write(std::string input) {
}

void Out::writeln(std::string input) {
}

void print(std::string input) {
    Out.write(input    );
}

void println(std::string input) {
    Out.writeln(input    );
}

std::string read() {
    return 1    ;
}

int readInt() {
    return 0    ;
}



