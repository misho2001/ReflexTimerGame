package com.example.reflex;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.image.Image;

public class ReflexGameApp extends Application {

    private ConfigurableApplicationContext spring;

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private final String api = "http://localhost:8080/api";

    private Stage stage;
    private String playerName = "";

    private long targetMs;
    private long startedAt;

    private boolean waitingForSecondPress = false;

    private Label gameStatus;
    private Label targetLabel;
    private Button gameButton;
    private VBox root;

    @Override
    public void init() {
        spring = new SpringApplicationBuilder(
                ReflexGameApplication.class
        ).run();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        stage.setTitle("Reflex | Timer Challenge");
        stage.setMinWidth(780);
        stage.setMinHeight(560);


        try {
            Image appIcon = new Image(getClass().getResourceAsStream("/logo.png"));
            stage.getIcons().add(appIcon);
        } catch (Exception e) {

            System.err.println("Could not load application icon: " + e.getMessage());
        }

        showWelcome();
        stage.show();
    }

    private void base(String title, String subtitle) {
        root = new VBox(18);
        root.setPadding(new Insets(28, 34, 30, 34));
        root.setAlignment(Pos.TOP_CENTER);

        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #0b1224, #172554);"
        );

        Label brand = new Label("◈  REFLEX");
        brand.setStyle(
                "-fx-text-fill:#67e8f9;" +
                        "-fx-font-size:15px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-letter-spacing:3px;"
        );

        Label heading = new Label(title);
        heading.setStyle(
                "-fx-text-fill:white;" +
                        "-fx-font-size:30px;" +
                        "-fx-font-weight:800;"
        );

        Label sub = new Label(subtitle);
        sub.setWrapText(true);
        sub.setStyle(
                "-fx-text-fill:#a5b4fc;" +
                        "-fx-font-size:13px;"
        );

        root.getChildren().addAll(brand, heading, sub);
    }

    private Button button(String text) {
        Button b = new Button(text);

        b.setMaxWidth(Double.MAX_VALUE);

        b.setStyle(
                "-fx-background-color:#06b6d4;" +
                        "-fx-text-fill:#071326;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:12;" +
                        "-fx-padding:13 20;"
        );

        b.setOnMouseEntered(e -> b.setStyle(
                "-fx-background-color:#67e8f9;" +
                        "-fx-text-fill:#071326;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:12;" +
                        "-fx-padding:13 20;"
        ));

        b.setOnMouseExited(e -> b.setStyle(
                "-fx-background-color:#06b6d4;" +
                        "-fx-text-fill:#071326;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-background-radius:12;" +
                        "-fx-padding:13 20;"
        ));

        return b;
    }

    private Label info(String text) {
        Label label = new Label(text);

        label.setWrapText(true);
        label.setStyle(
                "-fx-text-fill:#e2e8f0;" +
                        "-fx-font-size:14px;"
        );

        return label;
    }

    private VBox panel() {
        VBox panel = new VBox(14);

        panel.setPadding(new Insets(22));
        panel.setMaxWidth(560);

        panel.setStyle(
                "-fx-background-color:rgba(30,41,59,0.94);" +
                        "-fx-background-radius:18;" +
                        "-fx-border-color:#334155;" +
                        "-fx-border-radius:18;"
        );

        return panel;
    }

    private void nav() {
        HBox nav = new HBox(10);
        nav.setAlignment(Pos.CENTER);

        Button home = button("Player");
        Button game = button("Game");
        Button scores = button("TOP 10");

        home.setOnAction(e -> showWelcome());

        game.setOnAction(e -> {
            if (!playerName.isBlank()) {
                showGame();
            } else {
                showWelcome();
            }
        });

        scores.setOnAction(e -> showLeaderboard());

        nav.getChildren().addAll(home, game, scores);
        root.getChildren().add(nav);
    }

    private void showWelcome() {
        base(
                "Test Your Reflex",
                "Check the target time and try to guess it as accurately as possible."
        );

        VBox p = panel();

        p.getChildren().addAll(
                info("Player Name"),
                info("Enter your name and view your last two results.")
        );

        TextField name = new TextField(playerName);
        name.setPromptText("e.g. John");

        name.setStyle(
                "-fx-background-color:#f8fafc;" +
                        "-fx-background-radius:10;" +
                        "-fx-padding:12;" +
                        "-fx-font-size:14px;"
        );

        Button continueBtn = button("Continue →");
        Label history = info("Your last two results will appear here.");

        continueBtn.setOnAction(e -> {
            String n = name.getText().trim();

            if (n.length() < 2 || n.length() > 30) {
                history.setText(
                        "Name must be between 2 and 30 characters."
                );
                return;
            }

            playerName = n;
            history.setText("Loading...");

            try {
                String data = get(
                        "/players/" + enc(playerName) + "/recent"
                );

                history.setText(formatRecent(data));

            } catch (Exception ex) {
                history.setText(
                        "Could not connect to the API. " +
                                "Make sure Spring Boot is running."
                );
            }

            Button play = button("Start Game");
            play.setOnAction(ev -> showGame());

            if (!p.getChildren().contains(play)) {
                p.getChildren().add(play);
            }
        });

        p.getChildren().addAll(name, continueBtn, history);

        root.getChildren().add(p);
        nav();

        stage.setScene(new Scene(root, 900, 650));
    }

    private String formatRecent(String json) {
        if (json.equals("[]")) {
            return "No previous results yet. Play your first round!";
        }

        return "Last two results:\n" + json;
    }

    private void showGame() {
        // Generate a new target time every time the game screen is opened
        generateTargetTime();

        base(
                "Timer Challenge",
                "Memorize the target time. Start the timer and press it a second time " +
                        "when you think that time has elapsed."
        );

        VBox p = panel();

        targetLabel = new Label();
        targetLabel.setWrapText(true);
        targetLabel.setAlignment(Pos.CENTER);
        targetLabel.setMaxWidth(Double.MAX_VALUE);

        targetLabel.setStyle(
                "-fx-text-fill:#67e8f9;" +
                        "-fx-font-size:27px;" +
                        "-fx-font-weight:bold;"
        );

        updateTargetLabel();

        gameStatus = info(
                "Target time is visible. Press the button to start the timer."
        );

        gameButton = button("Start");
        gameButton.setOnAction(e -> handleGameButton());

        Button newRoundButton = button("New Target Time");
        newRoundButton.setOnAction(e -> {
            generateTargetTime();
            updateTargetLabel();

            waitingForSecondPress = false;

            gameStatus.setText(
                    "New target is ready. Press Start."
            );

            gameButton.setText("Start");
        });

        p.getChildren().addAll(
                targetLabel,
                gameStatus,
                gameButton,
                newRoundButton,
                info(
                        "To win, your time must differ from the target by at most " +
                                "50 milliseconds."
                )
        );

        root.getChildren().add(p);
        nav();

        stage.setScene(new Scene(root, 900, 650));
    }

    private void generateTargetTime() {
        // Random target between 2 and 12 seconds
        targetMs = ThreadLocalRandom.current()
                .nextLong(2000, 12001);

        waitingForSecondPress = false;
    }

    private void updateTargetLabel() {
        targetLabel.setText(
                String.format(
                        Locale.US,
                        "Target Time: %.3f seconds",
                        targetMs / 1000.0
                )
        );
    }

    private void handleGameButton() {
        if (!waitingForSecondPress) {
            // First press: Start the timer
            startedAt = System.nanoTime();
            waitingForSecondPress = true;

            gameStatus.setText(
                    "Timer started! Wait and press a second time."
            );

            gameButton.setText("Catch!");

        } else {
            // Second press: Calculate the result
            long elapsedMs =
                    (System.nanoTime() - startedAt) / 1_000_000;

            long delta = Math.abs(elapsedMs - targetMs);

            waitingForSecondPress = false;

            boolean win = delta <= 50;

            gameButton.setDisable(true);
            gameStatus.setText("Saving result...");

            try {
                String body =
                        "{\"playerName\":\"" + escape(playerName) +
                                "\",\"targetMs\":" + targetMs +
                                ",\"elapsedMs\":" + elapsedMs +
                                ",\"differenceMs\":" + delta +
                                ",\"won\":" + win + "}";

                post("/scores", body);

                gameStatus.setText(
                        String.format(
                                Locale.US,
                                "%s%nTarget Time: %.3f s%n" +
                                        "Your Time: %.3f s%n" +
                                        "Difference: %.3f s",
                                win
                                        ? "You Won! (±0.050 seconds)"
                                        : "Missed this time — try again.",
                                targetMs / 1000.0,
                                elapsedMs / 1000.0,
                                delta / 1000.0
                        )
                );

                gameButton.setText("Another Round");

                // New round with a new target time
                gameButton.setOnAction(e -> {
                    generateTargetTime();
                    updateTargetLabel();

                    gameStatus.setText(
                            "New target is ready. Start a new round."
                    );

                    gameButton.setText("Start");
                    gameButton.setOnAction(ev -> handleGameButton());
                });

            } catch (Exception ex) {
                gameStatus.setText(
                        "Could not save the result. Check Spring Boot " +
                                "and the database."
                );

                gameButton.setText("Try Again");
                gameButton.setOnAction(e -> {
                    generateTargetTime();
                    updateTargetLabel();

                    gameStatus.setText(
                            "New target is ready. Start a new round."
                    );

                    gameButton.setText("Start");
                    gameButton.setOnAction(ev -> handleGameButton());
                });

            } finally {
                gameButton.setDisable(false);
            }
        }
    }

    private void showLeaderboard() {
        base(
                "TOP 10 Players",
                "The lowest time difference wins in the ranking."
        );

        VBox p = panel();
        Label results = info("Loading...");

        Button refresh = button("Refresh");

        refresh.setOnAction(e -> {
            try {
                results.setText(
                        formatLeaderboard(get("/scores/top10"))
                );

            } catch (Exception ex) {
                results.setText(
                        "Could not load results. Check if the API is running."
                );
            }
        });

        p.getChildren().addAll(refresh, results);

        root.getChildren().add(p);
        nav();

        stage.setScene(new Scene(root, 900, 650));

        refresh.fire();
    }


    private String formatLeaderboard(String json) {
        if (json.equals("[]")) {
            return "No results yet. Be the first!";
        }

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();

            com.fasterxml.jackson.databind.JsonNode array =
                    mapper.readTree(json);

            StringBuilder result = new StringBuilder();
            result.append("TOP 10 Players\n\n");

            int rank = 1;

            for (com.fasterxml.jackson.databind.JsonNode score : array) {
                String name = score.has("playerName")
                        ? score.get("playerName").asText()
                        : "Unknown";

                String value = score.has("differenceMs")
                        ? String.format(
                        Locale.US,
                        "%.3f seconds",
                        score.get("differenceMs").asDouble() / 1000.0
                )
                        : "Unknown result";
                result.append(rank++)
                        .append(". ")
                        .append(name)
                        .append(" — ")
                        .append(value)
                        .append("\n");
            }

            return result.toString();

        } catch (Exception e) {
            return "Failed to read results.";
        }
    }


    private String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private String get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + path))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> response = http.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() >= 400) {
            throw new RuntimeException(response.body());
        }

        return response.body();
    }

    private String post(String path, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api + path))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(5))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = http.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() >= 400) {
            throw new RuntimeException(response.body());
        }

        return response.body();
    }

    @Override
    public void stop() {
        if (spring != null) {
            spring.close();
        }
    }
}