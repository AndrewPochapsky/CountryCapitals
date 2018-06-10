package dreadloaf.com.countryquiz;

import java.util.HashMap;

public class Question {
    //Key = Capital, Value = Name
    private HashMap<String, String> capitals;
    private String answer;

    public Question(HashMap<String,String> capitals, String answer){
        this.capitals = capitals;
        this.answer = answer;
    }

    public HashMap<String,String> getCapitals() {
        return capitals;
    }

    public String getAnswer() {
        return answer;
    }
}
