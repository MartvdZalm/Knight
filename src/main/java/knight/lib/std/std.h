
#ifndef KNIGHT_CORE_H
#define KNIGHT_CORE_H

#include <iostream>
#include <string>

namespace knight {
    // Print functions
    template<typename T>
    void print(const T& value)
    {
        std::cout << value << std::endl;
    }
    
    template<typename T, typename... Args>
    void print(const T& first, const Args&... rest)
    {
        std::cout << first << " ";
        print(rest...);
    }
    
    int to_int(const std::string& s)
    {
        return std::stoi(s);
    }
    
    std::string to_string(int value)
    {
        return std::to_string(value);
    }
}

#endif // KNIGHT_CORE_H
