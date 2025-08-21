#include <iostream>
#include <string>
#include <vector>

// Standard Library Implementation
// Standard Library Implementation
class Out {
public:
    static void write(const std::string& input) {
        std::cout << input;
    }
    
    static void writeln(const std::string& input) {
        std::cout << input << std::endl;
    }
};

class In {
public:
    static std::string read() {
        std::string temp;
        std::getline(std::cin, temp);
        return temp;
    }
    
    static int readInt() {
        int temp;
        std::cin >> temp;
        std::cin.ignore(); // Clear newline
        return temp;
    }
};

void print(const std::string& input) {
    Out::write(input);
}

void println(const std::string& input) {
    Out::writeln(input);
}

std::string read() {
    return In::read();
}

int readInt() {
    return In::readInt();
}

int main() { 
print("Hello World: ");
print(20);
return 0;
} 
int calculate(int a, int b) { 
return a*b;
} 

