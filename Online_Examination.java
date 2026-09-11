package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

public class OnlineExamSystem extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Login Screen
    private JTextField userField;
    private JPasswordField passField;

    // Profile Screen
    private JTextField nameField;
    private JPasswordField newPassField;

    // User Credentials
    private String currentUsername = "student";
    private String currentPassword = "123";
    private String displayName = "John Doe";

    // Questions and Answers
    private Question[] questions;
    private int currentQuestionIndex = 0;
    private Map<Integer, Integer> selectedAnswers = new HashMap<>();

    // Exam Components
    private JLabel timerLabel;
    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private ButtonGroup optionsGroup;
    private JButton prevButton, nextButton, submitButton;

    // Timer
    private Timer examTimer;
    private int totalTimeInSeconds = 1800; // 30 minutes
    private int timeTakenInSeconds = 0;

    // Result Screen
    private JLabel scoreLabel;
    private JLabel timeTakenLabel;
    private JTextArea breakdownArea;

    public OnlineExamSystem() {
        setTitle("Online Examination System");
        setSize(700, 500);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        initQuestions();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "Login");
        mainPanel.add(createProfilePanel(), "Profile");
        mainPanel.add(createExamPanel(), "Exam");
        mainPanel.add(createResultPanel(), "Result");

        add(mainPanel);
        cardLayout.show(mainPanel, "Login");
    }

    private void initQuestions() {
        questions = new Question[]{
                new Question("Which keyword is used to create an object in Java?",
                        new String[]{"class", "new", "object", "create"}, 1),
                new Question("Which of the following is NOT a primitive data type in Java?",
                        new String[]{"int", "boolean", "String", "double"}, 2),
                new Question("Which package contains the Scanner class?",
                        new String[]{"java.io", "java.util", "java.awt", "java.lang"}, 1),
                new Question("Which GUI toolkit is used in this application?",
                        new String[]{"JavaFX", "Swing", "AWT", "HTML"}, 1)
        };
    }

    // 1. Login Panel
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("System Login", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        userField = new JTextField(15);
        passField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());

            if (user.equals(currentUsername) && pass.equals(currentPassword)) {
                cardLayout.show(mainPanel, "Profile");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // 2. Profile Panel
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Update Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        nameField = new JTextField(displayName, 15);
        newPassField = new JPasswordField(currentPassword, 15);
        JButton startExamButton = new JButton("Save & Start Exam");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Display Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("New Password:"), gbc);
        gbc.gridx = 1;
        panel.add(newPassField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(startExamButton, gbc);

        startExamButton.addActionListener(e -> {
            displayName = nameField.getText().trim();
            currentPassword = new String(newPassField.getPassword());
            startExam();
        });

        return panel;
    }

    // 3. Exam Panel
    private JPanel createExamPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        timerLabel = new JLabel("Time Left: 30:00", SwingConstants.LEFT);
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        timerLabel.setForeground(Color.RED);

        JLabel welcomeLabel = new JLabel("Exam in Progress", SwingConstants.RIGHT);
        topPanel.add(timerLabel, BorderLayout.WEST);
        topPanel.add(welcomeLabel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        questionLabel = new JLabel("Question goes here");
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        centerPanel.add(questionLabel);

        optionButtons = new JRadioButton[4];
        optionsGroup = new ButtonGroup();

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionsGroup.add(optionButtons[i]);
            centerPanel.add(optionButtons[i]);

            final int optIndex = i;
            optionButtons[i].addActionListener(e -> selectedAnswers.put(currentQuestionIndex, optIndex));
        }
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        prevButton = new JButton("Previous");
        nextButton = new JButton("Next");
        submitButton = new JButton("Submit Exam");

        bottomPanel.add(prevButton);
        bottomPanel.add(nextButton);
        bottomPanel.add(submitButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        prevButton.addActionListener(e -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                loadQuestion();
            }
        });

        nextButton.addActionListener(e -> {
            if (currentQuestionIndex < questions.length - 1) {
                currentQuestionIndex++;
                loadQuestion();
            }
        });

        submitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to submit the exam?",
                    "Confirm Submission", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                finishExam();
            }
        });

        return panel;
    }

    // 4. Result Panel
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        scoreLabel = new JLabel("Score: ", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        timeTakenLabel = new JLabel("Time Taken: ", SwingConstants.CENTER);

        topPanel.add(scoreLabel);
        topPanel.add(timeTakenLabel);
        panel.add(topPanel, BorderLayout.NORTH);

        breakdownArea = new JTextArea();
        breakdownArea.setEditable(false);
        panel.add(new JScrollPane(breakdownArea), BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            resetExam();
            cardLayout.show(mainPanel, "Login");
        });
        panel.add(logoutButton, BorderLayout.SOUTH);

        return panel;
    }

    private void startExam() {
        currentQuestionIndex = 0;
        selectedAnswers.clear();
        timeTakenInSeconds = 0;
        totalTimeInSeconds = 1800;

        loadQuestion();
        cardLayout.show(mainPanel, "Exam");

        examTimer = new Timer(1000, e -> {
            totalTimeInSeconds--;
            timeTakenInSeconds++;

            int mins = totalTimeInSeconds / 60;
            int secs = totalTimeInSeconds % 60;
            timerLabel.setText(String.format("Time Left: %02d:%02d", mins, secs));

            if (totalTimeInSeconds <= 0) {
                examTimer.stop();
                JOptionPane.showMessageDialog(this, "Time is up! Your exam has been automatically submitted.", "Notice", JOptionPane.WARNING_MESSAGE);
                finishExam();
            }
        });
        examTimer.start();
    }

    private void loadQuestion() {
        Question q = questions[currentQuestionIndex];
        questionLabel.setText((currentQuestionIndex + 1) + ". " + q.questionText);

        optionsGroup.clearSelection();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(q.options[i]);
            if (selectedAnswers.containsKey(currentQuestionIndex) && selectedAnswers.get(currentQuestionIndex) == i) {
                optionButtons[i].setSelected(true);
            }
        }

        prevButton.setEnabled(currentQuestionIndex > 0);
        nextButton.setEnabled(currentQuestionIndex < questions.length - 1);
    }

    private void finishExam() {
        if (examTimer != null && examTimer.isRunning()) {
            examTimer.stop();
        }

        int score = 0;
        StringBuilder breakdown = new StringBuilder();

        for (int i = 0; i < questions.length; i++) {
            Question q = questions[i];
            Integer userChoice = selectedAnswers.get(i);

            breakdown.append("Q").append(i + 1).append(": ").append(q.questionText).append("\n");
            if (userChoice != null) {
                breakdown.append("   Your Answer: ").append(q.options[userChoice]);
                if (userChoice == q.correctAnswerIndex) {
                    score++;
                    breakdown.append(" (Correct)\n");
                } else {
                    breakdown.append(" (Incorrect) | Correct Answer: ").append(q.options[q.correctAnswerIndex]).append("\n");
                }
            } else {
                breakdown.append("   Your Answer: Not Answered | Correct Answer: ").append(q.options[q.correctAnswerIndex]).append("\n");
            }
            breakdown.append("\n");
        }

        scoreLabel.setText("Score: " + score + " out of " + questions.length + " (" + displayName + ")");
        timeTakenLabel.setText(String.format("Time Taken: %02d mins %02d secs", timeTakenInSeconds / 60, timeTakenInSeconds % 60));
        breakdownArea.setText(breakdown.toString());

        cardLayout.show(mainPanel, "Result");
    }

    private void resetExam() {
        userField.setText("");
        passField.setText("");
        selectedAnswers.clear();
    }

    private void confirmExit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to quit?", "Exit Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private static class Question {
        String questionText;
        String[] options;
        int correctAnswerIndex;

        public Question(String questionText, String[] options, int correctAnswerIndex) {
            this.questionText = questionText;
            this.options = options;
            this.correctAnswerIndex = correctAnswerIndex;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new OnlineExamSystem().setVisible(true);
        });
    }
}
