#include <iostream>
#include <string>
#include <fstream>
#include <vector>
#include <filesystem>

int main() { 
println("Hello World");
return 0;
} 
class Out {
public:
    static void write(const std::string& input) {
        std::cout << input;
    }
    
    static void writeln(const std::string& input) {
        std::cout << input << std::endl;
    }
};void print(std::string input) { 
Out.write(input);
} 
void println(std::string input) { 
Out.write(input);
Out.write("\n");
} 

