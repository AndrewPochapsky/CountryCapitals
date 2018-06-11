package dreadloaf.com.countryquiz;

import java.util.HashMap;

public class Question {
    //Key = Capital, Value = Country Name
    private HashMap<String, String> capitalsMap;
    //The capital which is the answer
    private String answer;

    public Question(HashMap<String,String> capitalsMap, String answer){
        this.capitalsMap = capitalsMap;
        this.answer = answer;
    }

    //Returns a String array containing all of the capitals
    public String[] getCapitals(){
        String[] capitals= new String[capitalsMap.size()];
        int index = 0;
        for(String capital : capitalsMap.keySet()){
            capitals[index] = capital;
            index++;
        }
        return capitals;
    }

    //Returns the capital part of the answer
    public String getAnswerCapital(){
        return answer;
    }

    //Returns the country name part of the answer
    public String getAnswerName(){
        return capitalsMap.get(answer);
    }

    //Returns true if correct, false if incorrect
    public boolean isCorrectAnswer(String userAnswer){
        return userAnswer.equals(answer);
    }
}
