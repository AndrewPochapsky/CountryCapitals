package dreadloaf.com.countryquiz;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Question implements Parcelable {
    //Key = Capital, Value = Country Name
    private HashMap<String, String> capitalsMap;
    //The capital which is the answer
    private String answer;

    public Question(HashMap<String,String> capitalsMap, String answer){
        this.capitalsMap = capitalsMap;
        this.answer = answer;
    }

    protected Question(Parcel in) {
        answer = in.readString();
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

    //region Parcelable Implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(answer);
    }


    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
    //endregion
}
