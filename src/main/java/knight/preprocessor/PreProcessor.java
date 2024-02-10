package knight.preprocessor;

import java.io.*;

public class PreProcessor
{
    private BufferedReader content;
    private BufferedReader source;

    public PreProcessor(String filename)
    {
        try {
        	source = new BufferedReader(new FileReader(filename));
        	content = new BufferedReader(new FileReader(filename));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BufferedReader process() throws IOException
    {
        String word = nextWord();

        while (word != null) {

            if (word.equals("include")) {

                String filename = nextWord();
                String includedContent = processInclude(filename);

                source = replaceInclude(source, filename, includedContent);
            }

            word = nextWord();
        }

        return source;
    }

    private String nextWord() throws IOException
    {
        String word = "";
        int c;

        while ((c = content.read()) != -1 && Character.isWhitespace(c)) {

        }

        if (c == -1) {
            return null;
        }

        do {
            word += (char) c;
            c = content.read();
        } while (c != -1 && !Character.isWhitespace(c));

        return word;
    }

    private String processInclude(String filename)
    {
        StringBuilder includedContent = new StringBuilder();

        try {
        	BufferedReader reader = new BufferedReader(new FileReader(filename + ".knight"));

            String line;
            while ((line = reader.readLine()) != null) {
                includedContent.append(line).append("\n");
            }
        } catch (IOException e) {
        	e.printStackTrace();
        }

        return includedContent.toString();
    }

    private BufferedReader replaceInclude(BufferedReader originalContent, String filename, String includedContent)
    {
        StringBuilder stringBuilder = new StringBuilder();

        try {
        	String line;
	        while ((line = originalContent.readLine()) != null) {
	            stringBuilder.append(line).append("\n");
	        }
        } catch (IOException e) {
        	e.printStackTrace();
        }

        String replacedContent = stringBuilder.toString().replace("include " + filename, includedContent);

        return new BufferedReader(new StringReader(replacedContent));
    }
}