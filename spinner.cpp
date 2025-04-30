#include <knight/knight_std.h>
std::vector<std::string> spinner = {"|", "/", "-", "\\"}; 
int main() { 
for (std::string s : spinner) {knight::print(s); 
}	return 0;
} 

