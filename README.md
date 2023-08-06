# Knight
Knight is an object-oriented programming language designed with a focus on having an easy-to-learn syntax and cross-platform compatibility. It is built on the Java Virtual Machine (JVM), enabling developers to run Knight code on any operating system where the JVM is supported.

## Key Features

- **Easy-to-Learn Syntax:** Knight aims to provide a gentle learning curve, making it an excellent choice for beginners and experienced developers alike.

- **Object-Oriented Paradigm:** Leveraging the power of object-oriented programming, Knight enables you to create modular and maintainable code.

- **Cross-Platform Compatibility:** Thanks to the JVM, Knight code can be executed on any platform with JVM support, ensuring broad reach across different operating systems.

- **Under Active Development:** Knight is an ongoing project, with continuous improvements, new features, and optimizations being added regularly.

## Syntax
```knight
obj Test {

    fn multiply(a: int, b: int): int {
        ret a * b;
    }

    fn is_prime(n: int): bool {
        if (n <= 1) {
            ret false;
        }
        for (i: int = 2; i * i <= n; i++) {
            if (n % i == 0) {
                ret false;
            }
        }  
        ret true;
    }

    fn factorial(n: int): int {
        if (n < 1) {
            ret 1;
        }
        ret n * factorial(n - 1);
    }

    fn apply_twice(func: fn(int): int, x: int): int {
        ret func(x);
    }

    fn main(): int {

        square: fn(int): int = fn (x: int): int {
            ret x * x;
        };

        squared_result: int = square(3);
        result: int = apply_twice(square, 3);

        ret 0;
    }
}
```

## License
Knight is released under the [MIT License](https://github.com/MartvdZalm/Knight/blob/master/LICENSE).

## Contact

You can contact me for any questions, feedback, or collaboration opportunities:

- Email: [martvanderzalm@gmail.com](martvanderzalm@gmail.com)