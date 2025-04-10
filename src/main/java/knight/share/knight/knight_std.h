#ifndef KNIGHT_STD_H
#define KNIGHT_STD_H

#include <iostream>
#include <string>
#include <vector>
#include <sstream>
#include <algorithm>

namespace knight
{
    // Print function with support for different types
    template <typename T>
    void print(const T& value)
    {
        std::cout << value << std::endl;
    }

    // Print function for multiple arguments
    template <typename T, typename... Args>
    void print(const T& first, const Args&... rest)
    {
        std::cout << first << " ";
        print(rest...);
    }

    // Read input from user
    template <typename T>
    T read()
    {
        T value;
        std::cin >> value;
        return value;
    }

    // Read a full line from input
    std::string read_line()
    {
        std::string value;
        std::getline(std::cin, value);
        return value;
    }

    // String split function
    std::vector<std::string> split(const std::string& str, char delimiter)
    {
        std::vector<std::string> tokens;
        std::stringstream ss(str);
        std::string item;
        while (std::getline(ss, item, delimiter))
        {
            tokens.push_back(item);
        }
        return tokens;
    }

    // Trim leading and trailing spaces
    std::string trim(const std::string& str)
    {
        auto start = str.find_first_not_of(" \t\n\r");
        auto end = str.find_last_not_of(" \t\n\r");
        return (start == std::string::npos) ? "" : str.substr(start, end - start + 1);
    }

    // Min and max functions
    template <typename T>
    T min(const T& a, const T& b)
    {
        return (a < b) ? a : b;
    }

    template <typename T>
    T max(const T& a, const T& b)
    {
        return (a > b) ? a : b;
    }

    // Convert to uppercase
    std::string to_upper(std::string str)
    {
        std::transform(str.begin(), str.end(), str.begin(), ::toupper);
        return str;
    }

    // Convert to lowercase
    std::string to_lower(std::string str)
    {
        std::transform(str.begin(), str.end(), str.begin(), ::tolower);
        return str;
    }
}

#endif // KNIGHT_STD_H
