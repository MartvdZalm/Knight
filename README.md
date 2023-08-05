# Knight
Knight is an object-oriented programming language designed with a focus on having an easy-to-learn syntax and cross-platform compatibility. It is built on the Java Virtual Machine (JVM), enabling developers to run Knight code on any operating system where the JVM is supported.

## Key Features

- **Easy-to-Learn Syntax:** Knight aims to provide a gentle learning curve, making it an excellent choice for beginners and experienced developers alike.

- **Object-Oriented Paradigm:** Leveraging the power of object-oriented programming, Knight enables you to create modular and maintainable code.

- **Cross-Platform Compatibility:** Thanks to the JVM, Knight code can be executed on any platform with JVM support, ensuring broad reach across different operating systems.

- **Under Active Development:** Knight is an ongoing project, with continuous improvements, new features, and optimizations being added regularly.

## Syntax
```knight
obj FactorialCalculator {

    fn factorial(n: int): int
    {
        if (n < 1) {
            ret 1;
        }
        ret n * factorial(n - 1);
    }

    fn main(): int
    {
        number: int = 5;
        result: int = factorial(number);
        print('Factorial of' . number . 'is' . result);
        ret 0;
    }
}
```

## License
Knight is released under the [MIT License](https://github.com/MartvdZalm/Knight/blob/master/LICENSE).

## Contact

You can contact me for any questions, feedback, or collaboration opportunities:

- Email: [your.email@example.com](martvanderzalm@gmail.com)