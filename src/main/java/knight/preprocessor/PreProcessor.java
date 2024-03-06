/*
 * MIT License
 * 
 * Copyright (c) 2023, Mart van der Zalm
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package knight.preprocessor;

import java.io.*;

/*
 * File: PreProcessor.java
 * @author: Mart van der Zalm
 * Date: 2024-02-07
 * Description:
 */
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