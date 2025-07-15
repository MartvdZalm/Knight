# Knight Quick Reference
This quick reference guide wil help you quickly understand the core syntax and features of
the Knight language.

## Comments
- Single-line comment: ```// This is a comment```
- Multi-line comment: ```/* This is a multi-line comment */```

## Variables
```
int x = 5;
int[] nums = {1, 2};
```

## Functions
```
fn add(int a, int b): int
{
	ret a + b;
}
```
## Lambdas
```
fn(int x): bool { ret x > 0; }
```

## Foreach
```
int[] nums = {10, 20, 30};

for (int num : nums) {
	print(num);
}
```

## If-Else
```
if (x > 10) {
    print("Large");
} else {
    print("Small");
}
```

## Interfaces
```
interface Document
{
    fn open(): void;
    fn save(): void;
}
```

## Classes
```
class TextDocument implements Document
{
    fn open(): void { 
        print("Opening text document"); 
    }
    
    fn save(): void { 
        print("Saving as .txt"); 
    }
}
```

## Object Instantiation
```
TextDocument doc = new TextDocument();
doc.open();
```

## Inheritance
```
class Spreadsheet extends Document
{
    fn open(): void {
        print("Opening spreadsheet");
    }
    
    fn save(): void {
        print("Saving as .csv");
    }
}
```

## Arrays
```
// Declaration
int[] numbers = {1, 2, 3};

// Access
int first = numbers[0];

// Modification
numbers[1] = 20;
```

