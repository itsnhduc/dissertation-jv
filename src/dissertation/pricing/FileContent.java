package dissertation.pricing;

import java.util.HashMap;
import java.util.List;

public class FileContent {
    public final HashMap<String, List<Integer>> content;

    public FileContent() {
        content = new HashMap<String, List<Integer>>() {};
    }
    
    public FileContent(HashMap<String, List<Integer>> content) {
        this.content = content;
    }
}
