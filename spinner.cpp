#include <knight/knight_std.h>
std::vector<std::string> spinner = {"|", "/", "-", "\\"}; 
std::vector<int> nums = {4, 5, 1, 2, 3}; 
bool check(std::string value) { 
if (value=="|") {
return false;

}
return true;
} 
int main() { 
knight::sort(nums, [](int a, int b) -> bool { return a>b;
}); 
for (int n : nums) {knight::print(n); 
}return 0;
} 

