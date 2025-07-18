#include <knight/std.h>
class Document {
public:
    virtual void open() = 0;
    virtual void save() = 0;
    virtual ~Document() {}
};

class TextDocument : public Document {
public:
void open() { 
knight::print("Opening text document");
} 

void save() { 
knight::print("Saving as .txt");
} 

public:
    virtual ~TextDocument() {}
};
class Spreadsheet : public Document {
public:
void open() { 
knight::print("Opening spreadsheet");
} 

void save() { 
knight::print("Saving as .csv");
} 

public:
    virtual ~Spreadsheet() {}
};
class SubSpreadsheet : public Spreadsheet {
public:
public:
    virtual ~SubSpreadsheet() {}
};
std::string main() { 
TextDocument something = TextDocument(); 
;
something.open();
something.save();
Spreadsheet idk = Spreadsheet(); 
;
idk.save();
SubSpreadsheet dsd = SubSpreadsheet(); 
;
dsd.open();
return 0;
} 

