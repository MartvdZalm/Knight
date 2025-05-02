#include <knight/knight_std.h>
std::vector<std::string> answers = {"The universe says: Try again later.", "This is beyond my calculation skills.", "That's a great question! But I don't know the answer.", "The answer is... 42? I hope that's right.", "I think you need more coffee for this one.", "Here's an answer: It's a mystery!", "The answer is... possibly a llama?"}; 
std::string get_random_answer() { 
int index = knight::random(0, answers.size() 
-1) 
; 
;
return answers[index];
;
} 
int main() { 
knight::print("Welcome to the Nonsense Calculator!"); 
;
knight::print("I can perform *absolutely* random calculations."); 
;
knight::print("Enter the first number:"); 
;
std::string input1 = knight::read_line() 
; 
;
int num1 = knight::to_int(input1) 
; 
;
knight::print("Enter the second number:"); 
;
std::string input2 = knight::read_line() 
; 
;
int num2 = knight::to_int(input2) 
; 
;
knight::print("Choose an operation (add, subtract, multiply, divide, random):"); 
;
std::string operation = knight::read_line() 
; 
;
if (operation=="add") {
knight::print("The result is:"); 
;
knight::print(knight::to_string(num1+num2) 
); 
;

}
 else if (operation=="subtract") {
knight::print("The result is:"); 
;
knight::print(knight::to_string(num1-num2) 
); 
;

}
 else if (operation=="multiply") {
knight::print("The result is:"); 
;
knight::print(knight::to_string(num1*num2) 
); 
;

}
 else if (operation=="divide") {
if (num2==0) {
knight::print("Oops! Division by zero... that's a no-go."); 
;

}
else {
knight::print("The result is:"); 
;
knight::print(knight::to_string(num1/num2) 
); 
;

}
;

}
 else if (operation=="random") {
knight::print(get_random_answer() 
); 
;

}
else {
knight::print("I don't recognize that operation... but here's a random answer anyway:"); 
;
knight::print(get_random_answer() 
); 
;

}
;
return 0;
;
} 

