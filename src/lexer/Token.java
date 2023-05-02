package src.lexer;

public class Token 
{
    private int row;
    private int col;
    private Symbol symbol;

    public Token(Symbol symbol, int row, int col) 
    {
        this.symbol = symbol;
        this.row = row;
        this.col = col;
    }

    public int getRow() 
    {
        return row;
    }

    public int getCol() 
    {
        return col;
    }

    public Tokens getToken()
    {
        return symbol.getToken();
    }

    public String getSymbol()
    {
        return symbol.getSymbol();
    }

    @Override
    public String toString()
    {
        return row + ":" + col + " " + getToken() + (getSymbol() == null ? "()" : ("(" + getSymbol() + ")"));
    }
}