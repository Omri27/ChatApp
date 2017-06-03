package com.example.omri.findmerun.Entities;

/**
 * Created by Omri on 26/11/2016.
 */

public class Question {
    private String question;
    private int answer;

    public Question(String question,int answer) {

        this.question = question;
        this.answer = answer;
    }
    public Question() {

    }
    public String getQuestion() {
        return question;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer)
    {
        this.answer=answer;
    }
}
