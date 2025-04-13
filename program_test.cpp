#include <knight/knight_std.h>
int calculate(int a, int b) { 
	return a*b;
} 
int main() { 
int secret = knight::random(1, 100) 
; 
int guess = 0; 
int tries = 0; 
knight::print("Welcome to Guess the Number!"); 
knight::print("I'm thinking of a number between 1 and 100"); 
knight::print(calculate(10, 10) 
); 
while (guess!=secret) {
std::string input = knight::read_line() 
; 
knight::print("Enter your guess:"); 
guess=knight::to_int(input) 
;
tries=tries+1;
if (guess<secret) {
knight::print("Too low!"); 

}
 else if (guess>secret) {
knight::print("Too high!"); 

}
else {
knight::print("Correct! You guessed it in" + knight::to_string(tries) 
 + "tries!"); 

}
}	return 0;
} 

