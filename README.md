# Knight
Knight is a compiler for the Knight programming language. It is built using Java and runs on the Java Virtual Machine (JVM).

# Motivation 
I created this compiler to improve my programming skills and because I enjoy working on a project that is always evolving. Initially, I used code from other sources as I was learning how a compiler works. As I became more familiar with the process, I modified the code to fit my own preferences and style.

# Knight example
```knight
KNIGHT Empire
{
    int width:
    int length:
    int height:

    Test test:

    Function MAIN
    {
        test = new Test():

        width = 10:
        length = 2:
        height = 5:

        println('width=' + width + ', length=' + length + ', height=' + height):
        println('Surface: ' + test.surface(width, length)):
        println('Capacity: ' + test.capacity(width, length, height)):
    }
}

KNIGHT Test
{
    Function int surface(int width, int length)
    {
        return width * length:
    }

    Function int capacity(int width, int length, int height)
    {
        return width * height * length:
    }
}