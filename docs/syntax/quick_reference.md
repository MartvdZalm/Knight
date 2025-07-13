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

## List
```
List<int> v;
v.add(25);
v.add(10);
v.add(66);
v.add(20);
v.sort();
v.print();

print(v.size());
```
