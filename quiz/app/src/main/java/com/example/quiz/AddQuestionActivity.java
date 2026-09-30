
package com.example.quiz;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddQuestionActivity extends AppCompatActivity {

    private Spinner spSubject;
    private EditText etQuestion;
    private EditText etOption1;
    private EditText etOption2;
    private EditText etOption3;
    private EditText etOption4;
    private EditText etExplanation;

    private RadioGroup rgCorrectAnswer;

    private RadioButton rbAnswerA;
    private RadioButton rbAnswerB;
    private RadioButton rbAnswerC;
    private RadioButton rbAnswerD;

    private Button btnSaveQuestion;
    private Button btnCancelQuestion;

    private DatabaseHelper databaseHelper;

    private int questionId = -1;

    private final String[] subjects = {
            "Java",
            "Python",
            "C",
            "DBMS",
            "Operating Systems"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_question);

        databaseHelper = new DatabaseHelper(this);

        connectViews();
        setupSubjectSpinner();
        setupButtons();

        questionId = getIntent().getIntExtra("question_id", -1);

        if (questionId != -1) {
            loadQuestionForEditing();
        }
    }

    private void connectViews() {
        spSubject = findViewById(R.id.spSubject);

        etQuestion = findViewById(R.id.etQuestion);
        etOption1 = findViewById(R.id.etOption1);
        etOption2 = findViewById(R.id.etOption2);
        etOption3 = findViewById(R.id.etOption3);
        etOption4 = findViewById(R.id.etOption4);
        etExplanation = findViewById(R.id.etExplanation);

        rgCorrectAnswer = findViewById(R.id.rgCorrectAnswer);

        rbAnswerA = findViewById(R.id.rbAnswerA);
        rbAnswerB = findViewById(R.id.rbAnswerB);
        rbAnswerC = findViewById(R.id.rbAnswerC);
        rbAnswerD = findViewById(R.id.rbAnswerD);

        btnSaveQuestion = findViewById(R.id.btnSaveQuestion);
        btnCancelQuestion = findViewById(R.id.btnCancelQuestion);
    }

    private void setupSubjectSpinner() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_item,
                        subjects
                ) {
                    @Override
                    public View getView(
                            int position,
                            View convertView,
                            ViewGroup parent) {

                        TextView textView = (TextView)
                                super.getView(
                                        position,
                                        convertView,
                                        parent
                                );

                        textView.setTextColor(0xFF20243A);
                        textView.setTextSize(16);

                        return textView;
                    }

                    @Override
                    public View getDropDownView(
                            int position,
                            View convertView,
                            ViewGroup parent) {

                        TextView textView = (TextView)
                                super.getDropDownView(
                                        position,
                                        convertView,
                                        parent
                                );

                        textView.setTextColor(0xFF20243A);
                        textView.setBackgroundColor(0xFFFFFFFF);
                        textView.setPadding(
                                20, 18, 20, 18
                        );
                        textView.setTextSize(16);

                        return textView;
                    }
                };

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spSubject.setAdapter(adapter);
    }

    private void setupButtons() {
        btnSaveQuestion.setOnClickListener(v -> saveQuestion());

        btnCancelQuestion.setOnClickListener(v -> finish());

        if (questionId != -1) {
            btnSaveQuestion.setText("Update Question");
        } else {
            btnSaveQuestion.setText("Save Question");
        }

        btnSaveQuestion.setAllCaps(false);
        btnCancelQuestion.setAllCaps(false);
    }

    private void loadQuestionForEditing() {
        Question question =
                databaseHelper.getQuestionById(questionId);

        if (question == null) {
            Toast.makeText(
                    this,
                    "Question not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        etQuestion.setText(question.question);
        etOption1.setText(question.option1);
        etOption2.setText(question.option2);
        etOption3.setText(question.option3);
        etOption4.setText(question.option4);

        etExplanation.setText(
                question.explanation == null
                        ? ""
                        : question.explanation
        );

        for (int i = 0; i < subjects.length; i++) {
            if (subjects[i].equalsIgnoreCase(question.subject)) {
                spSubject.setSelection(i);
                break;
            }
        }

        switch (question.correctAnswer) {
            case 1:
                rbAnswerA.setChecked(true);
                break;

            case 2:
                rbAnswerB.setChecked(true);
                break;

            case 3:
                rbAnswerC.setChecked(true);
                break;

            case 4:
                rbAnswerD.setChecked(true);
                break;

            default:
                rgCorrectAnswer.clearCheck();
                break;
        }

        btnSaveQuestion.setText("Update Question");
    }

    private void saveQuestion() {
        String questionText =
                etQuestion.getText().toString().trim();

        String option1 =
                etOption1.getText().toString().trim();

        String option2 =
                etOption2.getText().toString().trim();

        String option3 =
                etOption3.getText().toString().trim();

        String option4 =
                etOption4.getText().toString().trim();

        String explanation =
                etExplanation.getText().toString().trim();

        String subject =
                spSubject.getSelectedItem().toString();

        // Validate the question.
        if (questionText.isEmpty()) {
            etQuestion.setError("Enter a question");
            etQuestion.requestFocus();
            return;
        }

        // Validate all four options.
        if (option1.isEmpty()) {
            etOption1.setError("Enter option A");
            etOption1.requestFocus();
            return;
        }

        if (option2.isEmpty()) {
            etOption2.setError("Enter option B");
            etOption2.requestFocus();
            return;
        }

        if (option3.isEmpty()) {
            etOption3.setError("Enter option C");
            etOption3.requestFocus();
            return;
        }

        if (option4.isEmpty()) {
            etOption4.setError("Enter option D");
            etOption4.requestFocus();
            return;
        }

        // Validate the correct answer.
        int selectedId = rgCorrectAnswer.getCheckedRadioButtonId();

        if (selectedId == -1) {
            Toast.makeText(
                    this,
                    "Please select the correct answer",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        int correctAnswer;

        if (selectedId == R.id.rbAnswerA) {
            correctAnswer = 1;
        } else if (selectedId == R.id.rbAnswerB) {
            correctAnswer = 2;
        } else if (selectedId == R.id.rbAnswerC) {
            correctAnswer = 3;
        } else {
            correctAnswer = 4;
        }

        Question question = new Question(
                questionId == -1 ? 0 : questionId,
                subject,
                questionText,
                option1,
                option2,
                option3,
                option4,
                correctAnswer,
                explanation
        );

        if (questionId == -1) {
            // Insert a new question.
            long result = databaseHelper.insertQuestion(question);

            if (result != -1) {
                Toast.makeText(
                        this,
                        "Question saved successfully!",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            } else {
                Toast.makeText(
                        this,
                        "Unable to save question",
                        Toast.LENGTH_LONG
                ).show();
            }

        } else {
            // Update an existing question.
            int result = databaseHelper.updateQuestion(question);

            if (result > 0) {
                Toast.makeText(
                        this,
                        "Question updated successfully!",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            } else {
                Toast.makeText(
                        this,
                        "Unable to update question",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}