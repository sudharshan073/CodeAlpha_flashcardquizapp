
package com.example.quiz;

public class Question {

    public int id;
    public String subject;
    public String question;
    public String option1;
    public String option2;
    public String option3;
    public String option4;

    // 1 = A, 2 = B, 3 = C, 4 = D
    public int correctAnswer;

    public String explanation;

    public Question() {
    }

    public Question(
            int id,
            String subject,
            String question,
            String option1,
            String option2,
            String option3,
            String option4,
            int correctAnswer,
            String explanation) {

        this.id = id;
        this.subject = subject;
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
    }
}