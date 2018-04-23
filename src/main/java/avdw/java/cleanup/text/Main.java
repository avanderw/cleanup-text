package avdw.java.cleanup.text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.rules.RuleMatch;

public class Main
{

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        Path path = Paths.get("...\\randomizer\\assets\\json\\ice-breaker.json");

        byte[] encoded = Files.readAllBytes(path);
        String content = new String(encoded, "UTF-8");

        List<String> lines;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        lines = gson.fromJson(content, ArrayList.class);

        //Collections.sort(lines);
        //Files.write(path, gson.toJson(lines).getBytes());
        //similarity(lines);
        spelling(lines, path, gson);

    }

    private static void spelling(List<String> lines, Path path, Gson gson) throws IOException
    {
        List<String> cleanLines = new ArrayList();
        Scanner scanner = new Scanner(System.in);
        JLanguageTool langTool = new JLanguageTool(Language.BRITISH_ENGLISH);
        langTool.activateDefaultPatternRules();
        for (String line : lines)
        {
            List<RuleMatch> matches = langTool.check(line);
            for (RuleMatch match : matches)
            {
                System.out.println(line);
                System.out.println(String.format("%s --- %s => %s", new Object[]
                {
                    match.getMessage(),
                    line.substring(match.getFromPos(), match.getToPos()),
                    match.getSuggestedReplacements()
                }));

                int answer = scanner.nextInt();
                if (answer >= 0)
                {
                    line = line.substring(0, match.getFromPos()) + match.getSuggestedReplacements().get(answer) + line.substring(match.getToPos());
                }
            }
            cleanLines.add(line);
        }

        Files.write(path, gson.toJson(cleanLines).getBytes());
    }

    private static void similarity(List<String> lines)
    {
        NormalizedLevenshtein l = new NormalizedLevenshtein();
        JaroWinkler jw = new JaroWinkler();
        for (int x = 0; x < lines.size(); x++)
        {
            String first = lines.get(x);
            for (int y = 0; y < lines.size(); y++)
            {
                if (x == y)
                {
                    continue;
                }

                String second = lines.get(y);

                if (jw.similarity(first, second) > .97)
                {

                    System.out.println(first + ":::" + second + ":::" + jw.similarity(first, second));
                }
            }
        }
    }
}
