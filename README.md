# Knight
Knight is an object-oriented programming language designed with a focus on having an easy-to-learn syntax and cross-platform compatibility. It is built on the Java Virtual Machine (JVM), enabling developers to run Knight code on any operating system where the JVM is supported.

## Key Features

- **Easy-to-Learn Syntax:** Knight aims to provide a gentle learning curve, making it an excellent choice for beginners and experienced developers alike.

- **Object-Oriented Paradigm:** Leveraging the power of object-oriented programming, Knight enables you to create modular and maintainable code.

- **Under Active Development:** Knight is an ongoing project, with continuous improvements, new features, and optimizations being added regularly and is currently not in a working state.

## Syntax
```knight
include test

(int) EXIT_SUCCESS = 0;

class Person
{
    (string) name;
    (int) age;

    fn hello(): string
    {
        (string) str1 = "Hello";
        (string) str2 = world();
        ret str1 + str2;
    }

    fn world(): string
    {
        ret "World";
    }
}

fn main(): int
{
    (int) num = 20;
    (int) num3;
    (string) name = "Mart";

    num3 = num + num;

    ret 0;
}
```

## License
Knight is released under the [MIT License](https://github.com/MartvdZalm/Knight/blob/master/LICENSE).

## Contact

You can contact me for any questions, feedback, or collaboration opportunities:

- Email: [martvanderzalm@gmail.com](martvanderzalm@gmail.com)
