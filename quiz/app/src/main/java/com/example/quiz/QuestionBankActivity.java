
package com.example.quiz;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class QuestionBankActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout questionListContainer;
    private TextView tvQuestionCount;
    private Spinner spFilterSubject;

    private final String[] subjects = {
            "All Subjects",
            "Java",
            "Python",
            "C",
            "DBMS",
            "Operating Systems"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_bank);

        databaseHelper = new DatabaseHelper(this);

        questionListContainer =
                findViewById(R.id.questionListContainer);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        spFilterSubject = findViewById(R.id.spFilterSubject);

        Button btnAdd = findViewById(R.id.btnAddNewQuestion);
        Button btnBack = findViewById(R.id.btnCloseQuestionBank);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                subjects
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spFilterSubject.setAdapter(adapter);

        spFilterSubject.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view, int position, long id) {
                        loadQuestions();
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {
                    }
                });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(
                    QuestionBankActivity.this,
                    AddQuestionActivity.class
            );
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null
                && questionListContainer != null) {
            loadQuestions();
        }
    }

    private void loadQuestions() {
        questionListContainer.removeAllViews();

        ArrayList<Question> questions =
                databaseHelper.getAllQuestions();

        String selectedSubject =
                spFilterSubject.getSelectedItem() == null
                        ? "All Subjects"
                        : spFilterSubject.getSelectedItem().toString();

        int displayedCount = 0;

        for (Question question : questions) {
            if (!selectedSubject.equals("All Subjects")
                    && !selectedSubject.equals(question.subject)) {
                continue;
            }

            addQuestionCard(question);
            displayedCount++;
        }

        tvQuestionCount.setText(
                displayedCount + (displayedCount == 1
                        ? " question saved"
                        : " questions saved")
        );

        if (displayedCount == 0) {
            TextView empty = new TextView(this);
            empty.setText("No questions found.\nTap + Add New Question to begin.");
            empty.setTextSize(16);
            empty.setTextColor(Color.GRAY);
            empty.setGravity(Gravity.CENTER);
            empty.setPadding(16, 48, 16, 48);

            questionListContainer.addView(empty);
        }
    }

    private void addQuestionCard(Question question) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16, 16, 16, 16);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(20);
        background.setStroke(1, Color.rgb(220, 226, 238));
        card.setBackground(background);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        cardParams.setMargins(0, 0, 0, 14);
        card.setLayoutParams(cardParams);

        TextView subjectText = new TextView(this);
        subjectText.setText(question.subject);
        subjectText.setTextColor(Color.rgb(37, 99, 235));
        subjectText.setTextSize(13);
        subjectText.setTypeface(null, Typeface.BOLD);
        card.addView(subjectText);

        TextView questionText = new TextView(this);
        questionText.setText(question.question);
        questionText.setTextColor(Color.rgb(23, 37, 84));
        questionText.setTextSize(17);
        questionText.setTypeface(null, Typeface.BOLD);
        questionText.setPadding(0, 8, 0, 8);
        card.addView(questionText);

        TextView optionsText = new TextView(this);
        optionsText.setText(
                "A. " + question.option1 +
                        "\nB. " + question.option2 +
                        "\nC. " + question.option3 +
                        "\nD. " + question.option4
        );
        optionsText.setTextColor(Color.rgb(71, 85, 105));
        optionsText.setTextSize(14);
        card.addView(optionsText);

        String[] options = {
                question.option1,
                question.option2,
                question.option3,
                question.option4
        };

        if (question.correctAnswer >= 1
                && question.correctAnswer <= 4) {
            TextView answerText = new TextView(this);
            answerText.setText(
                    "Correct answer: " +
                            options[question.correctAnswer - 1]
            );
            answerText.setTextColor(Color.rgb(22, 128, 61));
            answerText.setTextSize(14);
            answerText.setTypeface(null, Typeface.BOLD);
            answerText.setPadding(0, 8, 0, 0);
            card.addView(answerText);
        }

        if (question.explanation != null
                && !question.explanation.trim().isEmpty()) {
            TextView explanationText = new TextView(this);
            explanationText.setText(
                    "Explanation: " + question.explanation
            );
            explanationText.setTextColor(Color.DKGRAY);
            explanationText.setTextSize(13);
            explanationText.setPadding(0, 6, 0, 0);
            card.addView(explanationText);
        }

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.END);
        buttons.setPadding(0, 12, 0, 0);

        Button editButton = new Button(this);
        editButton.setText("Edit");
        editButton.setAllCaps(false);

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setAllCaps(false);

        buttons.addView(editButton);
        buttons.addView(deleteButton);
        card.addView(buttons);

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    QuestionBankActivity.this,
                    AddQuestionActivity.class
            );
            intent.putExtra("question_id", question.id);
            startActivity(intent);
        });

        deleteButton.setOnClickListener(v ->
                confirmDelete(question));

        questionListContainer.addView(card);
    }

    private void confirmDelete(Question question) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Question?")
                .setMessage(
                        "Are you sure you want to delete this question?"
                )
                .setNegativeButton("Cancel", (dialog, which) ->
                        dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {
                    int result =
                            databaseHelper.deleteQuestion(question.id);

                    if (result > 0) {
                        Toast.makeText(
                                this,
                                "Question deleted",
                                Toast.LENGTH_SHORT
                        ).show();
                        loadQuestions();
                    } else {
                        Toast.makeText(
                                this,
                                "Unable to delete question",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .show();
    }
}