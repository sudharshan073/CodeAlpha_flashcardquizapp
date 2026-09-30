
package com.example.quiz;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private TextView tvProfileInitial;
    private TextView tvTotalQuizzes;
    private TextView tvBestScore;
    private TextView tvBookmarks;

    private Button btnStartQuiz;
    private Button btnJava;
    private Button btnPython;
    private Button btnC;
    private Button btnDBMS;
    private Button btnOS;
    private Button btnAnalytics;
    private Button btnHistory;
    private Button btnBookmarks;
    private Button btnLeaderboard;
    private Button btnBadges;
    private Button btnProfile;
    private Button btnLogout;
    private Button btnOpenQuestionBank;

    private DatabaseHelper databaseHelper;
    private SharedPreferences preferences;

    private final ArrayList<QuizItem> quizQuestions =
            new ArrayList<>();

    private int currentQuestion = 0;
    private int score = 0;
    private int selectedAnswer = -1;
    private String currentSubject = "Java";
    private boolean answerSubmitted = false;

    private LinearLayout quizContainer;
    private TextView questionCounter;
    private TextView questionText;
    private TextView scoreText;
    private TextView explanationText;
    private Button[] optionButtons;
    private Button nextButton;

    private static final String PREFS = "FlashcardQuizStats";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AuthHelper.isLoggedIn(this)) {
            openLogin();
            return;
        }

        showDashboard();
    }

    private void openLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showDashboard() {
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);
        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);

        connectViews();
        displayUserInformation();
        connectDashboardButtons();
        updateStatistics();
    }

    private void connectViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvProfileInitial = findViewById(R.id.tvProfileInitial);
        tvTotalQuizzes = findViewById(R.id.tvTotalQuizzes);
        tvBestScore = findViewById(R.id.tvBestScore);
        tvBookmarks = findViewById(R.id.tvBookmarks);

        btnStartQuiz = findViewById(R.id.btnStartQuiz);
        btnJava = findViewById(R.id.btnJava);
        btnPython = findViewById(R.id.btnPython);
        btnC = findViewById(R.id.btnC);
        btnDBMS = findViewById(R.id.btnDBMS);
        btnOS = findViewById(R.id.btnOS);

        btnAnalytics = findViewById(R.id.btnAnalytics);
        btnHistory = findViewById(R.id.btnHistory);
        btnBookmarks = findViewById(R.id.btnBookmarks);
        btnLeaderboard = findViewById(R.id.btnLeaderboard);
        btnBadges = findViewById(R.id.btnBadges);
        btnProfile = findViewById(R.id.btnProfile);

        btnLogout = findViewById(R.id.btnLogout);
        btnOpenQuestionBank =
                findViewById(R.id.btnOpenQuestionBank);
    }

    private void displayUserInformation() {
        String name = AuthHelper.getName(this);

        if (name == null || name.trim().isEmpty()) {
            name = "Student";
        }

        tvWelcome.setText("Welcome back, " + name);
        tvProfileInitial.setText(
                name.substring(0, 1).toUpperCase());
    }

    private void connectDashboardButtons() {
        btnStartQuiz.setOnClickListener(v -> showSubjectPicker());

        btnJava.setOnClickListener(v -> startQuiz("Java"));
        btnPython.setOnClickListener(v -> startQuiz("Python"));
        btnC.setOnClickListener(v -> startQuiz("C"));
        btnDBMS.setOnClickListener(v -> startQuiz("DBMS"));
        btnOS.setOnClickListener(v ->
                startQuiz("Operating Systems"));

        btnOpenQuestionBank.setOnClickListener(v -> {
            startActivity(new Intent(
                    MainActivity.this,
                    QuestionBankActivity.class));
        });

        btnLogout.setOnClickListener(v -> confirmLogout());

        btnAnalytics.setOnClickListener(v -> showAnalytics());
        btnHistory.setOnClickListener(v -> showHistory());

        btnBookmarks.setOnClickListener(v ->
                Toast.makeText(this,
                        "Bookmarks feature can be connected next.",
                        Toast.LENGTH_SHORT).show());

        btnLeaderboard.setOnClickListener(v ->
                showLeaderboard());

        btnBadges.setOnClickListener(v ->
                showBadges());

        btnProfile.setOnClickListener(v ->
                showProfile());

        TextView navHome = findViewById(R.id.navHome);
        TextView navQuiz = findViewById(R.id.navQuiz);
        TextView navHistory = findViewById(R.id.navHistory);
        TextView navProfile = findViewById(R.id.navProfile);

        navHome.setOnClickListener(v -> showDashboard());
        navQuiz.setOnClickListener(v -> showSubjectPicker());
        navHistory.setOnClickListener(v -> showHistory());
        navProfile.setOnClickListener(v -> showProfile());
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton("Cancel", (dialog, which) ->
                        dialog.dismiss())
                .setPositiveButton("Logout", (dialog, which) -> {
                    AuthHelper.logout(this);
                    openLogin();
                })
                .show();
    }

    private void updateStatistics() {
        int total = preferences.getInt("total_quizzes", 0);
        int best = preferences.getInt("best_score", 0);
        int bookmarks = preferences.getInt("bookmark_count", 0);

        tvTotalQuizzes.setText(String.valueOf(total));
        tvBestScore.setText(best + "%");
        tvBookmarks.setText(String.valueOf(bookmarks));
    }

    private void showSubjectPicker() {
        String[] subjects = {
                "Java",
                "Python",
                "C",
                "DBMS",
                "Operating Systems"
        };

        new AlertDialog.Builder(this)
                .setTitle("Choose a subject")
                .setItems(subjects, (dialog, which) ->
                        startQuiz(subjects[which]))
                .setNegativeButton("Cancel", null)
                .show();
    }

    // --------------------------------------------------
    // QUIZ QUESTIONS
    // --------------------------------------------------

    private static class QuizItem {
        String question;
        String[] options;
        int correctAnswer;
        String explanation;

        QuizItem(String question, String a, String b,
                 String c, String d, int answer,
                 String explanation) {
            this.question = question;
            this.options = new String[]{a, b, c, d};
            this.correctAnswer = answer;
            this.explanation = explanation;
        }
    }

    private void startQuiz(String subject) {
        currentSubject = subject;
        quizQuestions.clear();

        addBuiltInQuestions(subject);
        addCustomQuestions(subject);

        if (quizQuestions.isEmpty()) {
            Toast.makeText(this,
                    "No questions available for " + subject
                            + ". Add questions in Question Bank.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Collections.shuffle(quizQuestions);
        currentQuestion = 0;
        score = 0;
        selectedAnswer = -1;
        answerSubmitted = false;

        showQuizScreen();
        displayCurrentQuestion();
    }

    private void addBuiltInQuestions(String subject) {
        switch (subject) {
            case "Java":
                quizQuestions.add(new QuizItem(
                        "Which keyword is used to inherit a class?",
                        "this", "extends", "implements", "super",
                        1, "The extends keyword inherits a class."));

                quizQuestions.add(new QuizItem(
                        "Which method is the entry point of a Java application?",
                        "start()", "run()", "main()", "init()",
                        2, "Java applications normally start at main()."));

                quizQuestions.add(new QuizItem(
                        "Which type stores true or false?",
                        "int", "String", "boolean", "char",
                        2, "The boolean type stores true or false."));

                quizQuestions.add(new QuizItem(
                        "Which keyword creates an object?",
                        "class", "new", "void", "static",
                        1, "The new keyword creates an object."));

                break;

            case "Python":
                quizQuestions.add(new QuizItem(
                        "Which symbol starts a comment in Python?",
                        "//", "#", "/*", "--",
                        1, "A single-line Python comment starts with #."));

                quizQuestions.add(new QuizItem(
                        "Which data type stores key-value pairs?",
                        "List", "Tuple", "Dictionary", "Set",
                        2, "A dictionary stores key-value pairs."));

                quizQuestions.add(new QuizItem(
                        "Which keyword defines a function?",
                        "function", "define", "fun", "def",
                        3, "Python uses def to define a function."));

                quizQuestions.add(new QuizItem(
                        "Which collection is ordered and mutable?",
                        "List", "Tuple", "String", "Integer",
                        0, "Lists are ordered and mutable."));

                break;

            case "C":
                quizQuestions.add(new QuizItem(
                        "Which function prints output in C?",
                        "input()", "printf()", "print()", "display()",
                        1, "printf() prints formatted output in C."));

                quizQuestions.add(new QuizItem(
                        "Which symbol ends a C statement?",
                        ":", ";", ".", ",",
                        1, "A semicolon ends a C statement."));

                quizQuestions.add(new QuizItem(
                        "Which header provides printf()?",
                        "stdlib.h", "math.h", "stdio.h", "string.h",
                        2, "printf() is declared in stdio.h."));

                quizQuestions.add(new QuizItem(
                        "Which operator checks equality?",
                        "=", "==", "!=", "&&",
                        1, "The == operator checks equality."));

                break;

            case "DBMS":
                quizQuestions.add(new QuizItem(
                        "What is a primary key?",
                        "A duplicate value",
                        "A unique row identifier",
                        "A table name",
                        "A query",
                        1, "A primary key uniquely identifies each row."));

                quizQuestions.add(new QuizItem(
                        "Which SQL command retrieves data?",
                        "GET", "OPEN", "SELECT", "FETCH TABLE",
                        2, "SELECT retrieves data from tables."));

                quizQuestions.add(new QuizItem(
                        "What does SQL stand for?",
                        "Structured Query Language",
                        "Simple Question Language",
                        "System Query Logic",
                        "Sequential Query Link",
                        0, "SQL means Structured Query Language."));

                quizQuestions.add(new QuizItem(
                        "Which key references another table's key?",
                        "Primary key", "Foreign key",
                        "Candidate key", "Super key",
                        1, "A foreign key establishes a relationship."));

                break;

            case "Operating Systems":
                quizQuestions.add(new QuizItem(
                        "Which component manages processes?",
                        "Compiler", "Operating system",
                        "Text editor", "Browser",
                        1, "The operating system manages processes."));

                quizQuestions.add(new QuizItem(
                        "What is deadlock?",
                        "A type of file",
                        "Processes waiting indefinitely",
                        "A CPU register",
                        "A network cable",
                        1, "Deadlock can occur when processes wait for resources held by each other."));

                quizQuestions.add(new QuizItem(
                        "Which is a CPU scheduling algorithm?",
                        "FCFS", "HTML", "SQL", "XML",
                        0, "FCFS means First Come, First Served."));

                quizQuestions.add(new QuizItem(
                        "What does RAM provide?",
                        "Permanent storage",
                        "Temporary working memory",
                        "Printed output",
                        "Internet access",
                        1, "RAM holds data and programs in active use."));

                break;
        }
    }

    private void addCustomQuestions(String subject) {
        try {
            ArrayList<Question> saved =
                    databaseHelper.getAllQuestions();

            for (Question q : saved) {
                if (q.subject == null ||
                        !q.subject.equalsIgnoreCase(subject)) {
                    continue;
                }

                if (q.option1 == null || q.option2 == null
                        || q.option3 == null || q.option4 == null) {
                    continue;
                }

                if (q.correctAnswer < 1 || q.correctAnswer > 4) {
                    continue;
                }

                String explanation = q.explanation == null
                        ? "" : q.explanation;

                quizQuestions.add(new QuizItem(
                        q.question,
                        q.option1,
                        q.option2,
                        q.option3,
                        q.option4,
                        q.correctAnswer - 1,
                        explanation
                ));
            }
        } catch (Exception e) {
            Toast.makeText(this,
                    "Could not load saved questions.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // --------------------------------------------------
    // QUIZ SCREEN
    // --------------------------------------------------

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private TextView makeText(String text, int size,
                              int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        if (bold) {
            view.setTypeface(null, Typeface.BOLD);
        }
        return view;
    }

    private Button makeButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(15);
        return button;
    }

    private void showQuizScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(243, 246, 255));

        quizContainer = new LinearLayout(this);
        quizContainer.setOrientation(LinearLayout.VERTICAL);
        quizContainer.setPadding(dp(20), dp(24), dp(20), dp(24));

        scroll.addView(quizContainer);
        setContentView(scroll);

        Button back = makeButton("← Back to Dashboard");
        quizContainer.addView(back);
        back.setOnClickListener(v -> showDashboard());

        TextView title = makeText(
                currentSubject + " Quiz", 25,
                Color.rgb(23, 37, 84), true);
        title.setPadding(0, dp(20), 0, dp(12));
        quizContainer.addView(title);

        questionCounter = makeText(
                "", 14, Color.DKGRAY, false);
        quizContainer.addView(questionCounter);

        questionText = makeText(
                "", 20, Color.rgb(23, 37, 84), true);
        questionText.setPadding(0, dp(24), 0, dp(18));
        quizContainer.addView(questionText);

        optionButtons = new Button[4];

        for (int i = 0; i < 4; i++) {
            final int answerIndex = i;
            optionButtons[i] = makeButton("");
            optionButtons[i].setGravity(
                    Gravity.CENTER_VERTICAL | Gravity.START);
            optionButtons[i].setMinHeight(dp(58));

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, dp(5), 0, dp(5));

            quizContainer.addView(optionButtons[i], params);

            optionButtons[i].setOnClickListener(v -> {
                if (!answerSubmitted) {
                    selectedAnswer = answerIndex;
                    answerSubmitted = true;
                    checkAnswer();
                }
            });
        }

        scoreText = makeText(
                "", 16, Color.rgb(22, 128, 61), true);
        scoreText.setPadding(0, dp(12), 0, dp(4));
        quizContainer.addView(scoreText);

        explanationText = makeText(
                "", 14, Color.DKGRAY, false);
        explanationText.setPadding(0, dp(5), 0, dp(12));
        quizContainer.addView(explanationText);

        nextButton = makeButton("Submit Answer");
        nextButton.setTextColor(Color.WHITE);
        nextButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(49, 85, 217)));

        LinearLayout.LayoutParams nextParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(54));
        nextParams.setMargins(0, dp(18), 0, dp(10));
        quizContainer.addView(nextButton, nextParams);

        nextButton.setOnClickListener(v -> {
            if (!answerSubmitted) {
                if (selectedAnswer == -1) {
                    Toast.makeText(this,
                            "Select an answer first.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                answerSubmitted = true;
                checkAnswer();
            } else {
                currentQuestion++;
                if (currentQuestion < quizQuestions.size()) {
                    selectedAnswer = -1;
                    answerSubmitted = false;
                    displayCurrentQuestion();
                } else {
                    finishQuiz();
                }
            }
        });
    }

    private void displayCurrentQuestion() {
        QuizItem q = quizQuestions.get(currentQuestion);

        questionCounter.setText(
                "Question " + (currentQuestion + 1)
                        + " of " + quizQuestions.size());

        questionText.setText(q.question);

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(
                    (char) ('A' + i) + ". " + q.options[i]);
            optionButtons[i].setEnabled(true);
            optionButtons[i].setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.WHITE));
            optionButtons[i].setTextColor(
                    Color.rgb(23, 37, 84));
        }

        scoreText.setText("");
        explanationText.setText("");
        nextButton.setText("Submit Answer");
        selectedAnswer = -1;
        answerSubmitted = false;
    }

    private void checkAnswer() {
        QuizItem q = quizQuestions.get(currentQuestion);

        if (selectedAnswer == q.correctAnswer) {
            score++;
            scoreText.setText("Correct answer!");
        } else {
            scoreText.setText(
                    "Incorrect. Correct answer: "
                            + q.options[q.correctAnswer]);
        }

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setEnabled(false);

            if (i == q.correctAnswer) {
                optionButtons[i].setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                Color.rgb(190, 245, 210)));
            } else if (i == selectedAnswer) {
                optionButtons[i].setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                Color.rgb(255, 215, 215)));
            }
        }

        explanationText.setText(
                q.explanation == null || q.explanation.isEmpty()
                        ? "" : "Explanation: " + q.explanation);

        nextButton.setText(
                currentQuestion == quizQuestions.size() - 1
                        ? "View Results" : "Next Question");
    }

    private void finishQuiz() {
        int total = quizQuestions.size();
        int percentage = total == 0
                ? 0 : Math.round(score * 100f / total);

        int quizzes = preferences.getInt("total_quizzes", 0);
        int best = preferences.getInt("best_score", 0);

        preferences.edit()
                .putInt("total_quizzes", quizzes + 1)
                .putInt("best_score", Math.max(best, percentage))
                .putInt("last_score", percentage)
                .putInt("last_correct", score)
                .putInt("last_total", total)
                .putString("last_subject", currentSubject)
                .putLong("last_time", System.currentTimeMillis())
                .apply();

        new AlertDialog.Builder(this)
                .setTitle("Quiz Completed!")
                .setMessage(
                        "Subject: " + currentSubject
                                + "\nCorrect answers: " + score
                                + " / " + total
                                + "\nFinal score: " + percentage + "%")
                .setCancelable(false)
                .setPositiveButton("Dashboard", (dialog, which) ->
                        showDashboard())
                .setNeutralButton("Try Again", (dialog, which) ->
                        startQuiz(currentSubject))
                .show();
    }

    // --------------------------------------------------
    // OTHER DASHBOARD FEATURES
    // --------------------------------------------------

    private void showAnalytics() {
        int total = preferences.getInt("total_quizzes", 0);
        int best = preferences.getInt("best_score", 0);
        int last = preferences.getInt("last_score", 0);

        new AlertDialog.Builder(this)
                .setTitle("Your Analytics")
                .setMessage(
                        "Total quizzes: " + total
                                + "\nBest score: " + best + "%"
                                + "\nLatest score: " + last + "%")
                .setPositiveButton("Close", null)
                .show();
    }

    private void showHistory() {
        int total = preferences.getInt("total_quizzes", 0);

        if (total == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Quiz History")
                    .setMessage("You have not completed a quiz yet.")
                    .setPositiveButton("Close", null)
                    .show();
            return;
        }

        String subject = preferences.getString(
                "last_subject", "Unknown");
        int correct = preferences.getInt("last_correct", 0);
        int count = preferences.getInt("last_total", 0);
        int percentage = preferences.getInt("last_score", 0);

        new AlertDialog.Builder(this)
                .setTitle("Latest Quiz")
                .setMessage(
                        "Subject: " + subject
                                + "\nCorrect answers: " + correct
                                + " / " + count
                                + "\nScore: " + percentage + "%"
                                + "\nCompleted quizzes: " + total)
                .setPositiveButton("Close", null)
                .show();
    }

    private void showLeaderboard() {
        new AlertDialog.Builder(this)
                .setTitle("Leaderboard")
                .setMessage(
                        AuthHelper.getName(this)
                                + "\nYour best score: "
                                + preferences.getInt(
                                "best_score", 0) + "%")
                .setPositiveButton("Close", null)
                .show();
    }

    private void showBadges() {
        int total = preferences.getInt("total_quizzes", 0);
        int best = preferences.getInt("best_score", 0);

        String badge = total >= 10 ? "Quiz Champion"
                : total >= 5 ? "Dedicated Learner"
                  : total >= 1 ? "First Quiz Completed"
                    : "Complete your first quiz to earn a badge.";

        if (best == 100) {
            badge = "Perfect Score Achieved!";
        }

        new AlertDialog.Builder(this)
                .setTitle("Achievements")
                .setMessage(badge)
                .setPositiveButton("Close", null)
                .show();
    }

    private void showProfile() {
        String name = AuthHelper.getName(this);

        new AlertDialog.Builder(this)
                .setTitle("My Profile")
                .setMessage(
                        "Name: " + name
                                + "\nCompleted quizzes: "
                                + preferences.getInt(
                                "total_quizzes", 0)
                                + "\nBest score: "
                                + preferences.getInt(
                                "best_score", 0) + "%")
                .setPositiveButton("Close", null)
                .setNegativeButton("Logout", (dialog, which) ->
                        confirmLogout())
                .show();
    }

    @Override
    public void onBackPressed() {
        if (quizContainer != null
                && quizContainer.getParent() != null) {
            quizContainer = null;
            showDashboard();
        } else {
            super.onBackPressed();
        }
    }
}