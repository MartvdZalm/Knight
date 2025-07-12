#ifndef KNIGHT_UTIL_LIST_H
#define KNIGHT_UTIL_LIST_H

#include <vector>

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

#ifndef KNIGHT_UTIL_LIST_H
#define KNIGHT_UTIL_LIST_H
