#ifndef KNIGHT_STD_H
#define KNIGHT_STD_H

#include <iostream>
#include <string>
#include <vector>
#include <sstream>
#include <algorithm>
#include <chrono>
#include <ctime>
#include <cstdlib>

namespace knight
{
    template <typename T>
    void print(const T& value)
    {
        std::cout << value << std::endl;
    }

    template <typename T, typename... Args>
    void print(const T& first, const Args&... rest)
    {
        std::cout << first << " ";
        print(rest...);
    }

    template <typename T>
    T read()
    {
        T value;
        std::cin >> value;
        return value;
    }

    std::string read_line()
    {
        std::string value;
        std::getline(std::cin, value);
        return value;
    }

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

    std::string trim(const std::string& str)
    {
        auto start = str.find_first_not_of(" \t\n\r");
        auto end = str.find_last_not_of(" \t\n\r");
        return (start == std::string::npos) ? "" : str.substr(start, end - start + 1);
    }

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

    std::string to_upper(std::string str)
    {
        std::transform(str.begin(), str.end(), str.begin(), ::toupper);
        return str;
    }

    std::string to_lower(std::string str)
    {
        std::transform(str.begin(), str.end(), str.begin(), ::tolower);
        return str;
    }

    std::string now()
    {
        auto now = std::chrono::system_clock::now();
        std::time_t time_now = std::chrono::system_clock::to_time_t(now);
        return std::ctime(&time_now);
    }

    int random(int min, int max)
    {
        static bool seeded = false;
        if (!seeded)
        {
            std::srand(std::time(nullptr));
            seeded = true;
        }
        return min + std::rand() % (max - min + 1);
    }

    int to_int(const std::string& s)
    {
        return std::stoi(s);
    }

    std::string to_string(int n)
    {
        return std::to_string(n);
    }

    std::string join(const std::vector<std::string>& strings, const std::string& delimiter)
    {
        std::ostringstream oss;
        for (size_t i = 0; i < strings.size(); ++i)
        {
            oss << strings[i];
            if (i != strings.size() - 1) oss << delimiter;
        }
        return oss.str();
    }

    std::string join(const std::vector<int>& vec, const std::string& delimiter)
    {
        std::ostringstream oss;
        for (size_t i = 0; i < vec.size(); ++i)
        {
            oss << vec[i];
            if (i != vec.size() - 1) oss << delimiter;
        }
        return oss.str();
    }

    template <typename T, typename Func>
    std::vector<T> filter(const std::vector<T>& vec, Func func)
    {
        std::vector<T> result;
        for (const auto& item : vec)
        {
            if (func(item)) result.push_back(item);
        }
        return result;
    }

    template <typename T>
    void sort(std::vector<T>& vec)
    {
        std::sort(vec.begin(), vec.end());
    }

    template <typename T, typename Comparator>
    void sort(std::vector<T>& vec, Comparator comp)
    {
        std::sort(vec.begin(), vec.end(), comp);
    }

    template <typename T>
    class List
    {
    public:
        List() {}

        void add(const T& value)
        {
            data.push_back(value);
        }

        T& operator[](size_t index)
        {
            return data[index];
        }

        size_t size() const
        {
            return data.size();
        }

        void sort()
        {
            std::sort(data.begin(), data.end());
        }

        void print() const
        {
            for (const auto& item : data) {
                std::cout << item << " ";
            }
            std::cout << std::endl;
        }

    private:
        std::vector<T> data;
    };
}

#endif // KNIGHT_STD_H
