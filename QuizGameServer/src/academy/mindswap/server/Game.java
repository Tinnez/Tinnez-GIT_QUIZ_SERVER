package academy.mindswap.server;

import academy.mindswap.server.commands.Command;
import academy.mindswap.server.messages.GameMessages;
import academy.mindswap.player.Player;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Game {

    private final List<PlayerConnectionHandler> players;
    private int totalNumberOfQuestions;
    private final int numberOfPlayers;

    public Game(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
//        this.player = null;
        this.players = new CopyOnWriteArrayList<>();
        // players = Collections.synchronizedList(new ArrayList<>());
        //   players = new ArrayList<>();
    }

    private void formatQuestions(Question question, PlayerConnectionHandler playerConnectionHandler) {

        int space = question.getAnswerA().length() > question.getAnswerC().length() ?
                question.getAnswerA().length() - question.getAnswerC().length() :
                question.getAnswerC().length() - question.getAnswerA().length();

        String spaces = " ".repeat(space);

        System.out.println(question.getQuestion());

        System.out.println();

        if (question.getAnswerA().length() > question.getAnswerC().length()) {
            System.out.println(question.getAnswerA() + "              " +
                    "                  " + question.getAnswerB());

            System.out.println(question.getAnswerC() + spaces + "              " +
                    "                  " + question.getAnswerD());
        } else {
            System.out.println(question.getAnswerA() + spaces + "              " +
                    "                  " + question.getAnswerB());

            System.out.println(question.getAnswerC() + "              " +
                    "                  " + question.getAnswerD());
        }

        System.out.println();
    }

    public int getTotalNumberOfQuestions() {
        try {
            this.totalNumberOfQuestions = Files.readAllLines(Paths.get("./src/q&a.txt")).stream()
                    .filter(q -> q.endsWith("?"))
                    .toList()
                    .size();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return totalNumberOfQuestions;
    }

    public Question pickRandomQuestion(PlayerConnectionHandler playerConnectionHandler) throws IOException {

        // toDo: Deviamos ter feito uma classe para ler o ficheiro todo e ir buscar daí em vez de o ler sempre que fazemos questão nova

        if (playerConnectionHandler.questions.size() == getTotalNumberOfQuestions()) {
            System.out.println(GameMessages.NO_MORE_QUESTIONS);
            return null;
        }

        BufferedReader reader = new BufferedReader(new FileReader("./src/q&a.txt"));

        int lines = 0;

        while (reader.readLine() != null) {
            lines++;
        }

        List<String> file = Files.readAllLines(Paths.get("./src/q&a.txt"));
        int randomNumber = (int) ((Math.random() * (lines - 0)) + 0);
        String randomQuestion = file.get(randomNumber);

        if (randomQuestion.endsWith("?") && !playerConnectionHandler.repeatedQuestion(randomQuestion)) {

            Question question = new Question();

            question.setQuestion(randomQuestion);
            question.setQuestionID(file.get(randomNumber - 2));
            question.setDifficulty(file.get(randomNumber - 1));
            question.setAnswerA(file.get(randomNumber + 1));
            question.setAnswerB(file.get(randomNumber + 2));
            question.setAnswerC(file.get(randomNumber + 3));
            question.setAnswerD(file.get(randomNumber + 4));
            question.setCorrectAnswer(file.get(randomNumber + 5));
            question.setHint(file.get(randomNumber + 6));
            question.setQuestionLine(randomNumber + 1);

            playerConnectionHandler.questions.add(question);

            reader.close();

            return question;
        }

        return pickRandomQuestion(playerConnectionHandler);
    }

    private void show_Hint(PlayerConnectionHandler playerConnectionHandler) {

        if (playerConnectionHandler.getHintsRemaining() == 0) {
            transmitWithoutUserName(playerConnectionHandler.getName(), GameMessages.NO_MORE_HINTS);
            return;
        }

        Question question = playerConnectionHandler.questions.get(playerConnectionHandler.questions.size() - 1);

        if (question.isShowHint()) {
            transmitWithoutUserName(playerConnectionHandler.getName(), GameMessages.HELP_ALREADY_USED);
            return;
        }

        transmitWithoutUserName(playerConnectionHandler.getName(), question.getHint());
        transmitWithoutUserName(playerConnectionHandler.getName(), "");
        playerConnectionHandler.setHintsRemaining(playerConnectionHandler.getHintsRemaining() - 1);
        question.setShowHint(true);
    }

    private void show_5050(PlayerConnectionHandler playerConnectionHandler) throws IOException, InterruptedException {

        // toDo Question should have a List<Answers> answers (answers.get(0)-> a) etc...) for easier implementation of 5050

        if (playerConnectionHandler.get_5050Remaining() == 0) {
            transmitWithoutUserName(playerConnectionHandler.getName(), GameMessages.NO_MORE_5050);
            return;
        }

        Question question = playerConnectionHandler.questions.get(playerConnectionHandler.questions.size() - 1);

        if (question.isShow5050()) {
            transmitWithoutUserName(playerConnectionHandler.getName(), GameMessages.HELP_ALREADY_USED);
            return;
        }

        String answer = question.getCorrectAnswer() + ")";
        int randomNumber = (int) ((Math.random() * (3 - 0)) + 0);

        if (question.getAnswerA().startsWith(answer)) {
            switch (randomNumber) {
                case 0:
                    question.setAnswerB(question.getAnswerB().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerC(question.getAnswerC().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 1:
                    question.setAnswerB(question.getAnswerB().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerD(question.getAnswerD().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 2:
                    question.setAnswerC(question.getAnswerC().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerD(question.getAnswerD().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
            }
        } else if (question.getAnswerB().startsWith(answer)) {
            switch (randomNumber) {
                case 0:
                    question.setAnswerA(question.getAnswerA().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerC(question.getAnswerC().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 1:
                    question.setAnswerA(question.getAnswerA().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerD(question.getAnswerD().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 2:
                    question.setAnswerC(question.getAnswerC().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerD(question.getAnswerD().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
            }
        } else if (question.getAnswerC().startsWith(answer)) {
            switch (randomNumber) {
                case 0:
                    question.setAnswerA(question.getAnswerA().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerB(question.getAnswerB().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 1:
                    question.setAnswerA(question.getAnswerA().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerD(question.getAnswerD().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 2:
                    question.setAnswerB(question.getAnswerB().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerD(question.getAnswerD().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
            }
        } else if (question.getAnswerD().startsWith(answer)) {
            switch (randomNumber) {
                case 0:
                    question.setAnswerA(question.getAnswerA().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerB(question.getAnswerB().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 1:
                    question.setAnswerA(question.getAnswerA().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerC(question.getAnswerC().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
                case 2:
                    question.setAnswerB(question.getAnswerB().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    question.setAnswerC(question.getAnswerC().replaceAll("[a-zA-Z0-9?)=(/%&#$]", ""));
                    break;
            }
        }

        question.setShow5050(true);
        playerConnectionHandler.set_5050Remaining(playerConnectionHandler.get_5050Remaining() - 1);
        transmitQuestion(playerConnectionHandler, question);

    }

    private void loading() throws InterruptedException {

        System.out.println();

        new Thread(() -> {
            int i = 0;
            while (i++ < 100) {
                System.out.print("Loading Questions: [");
                int j = 0;
                while (j++ < i) {
                    System.out.print(ConsoleColors.GREEN + "€" + ConsoleColors.RESET);
                }
                while (j++ < 100) {
                    System.out.print(" ");
                }
                System.out.print("] : " + i + "%");
                try {
                    Thread.sleep(50l);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.print("\r");
            }
        }).start();

        Thread.sleep(8000);
    }

    private void gameOver() {

        System.out.println("Game is over");
        System.out.println("The winner is " + players.get(0).name);
        System.out.println("=================================================================");
        broadcast("Game is over");
        broadcast(" ");
        broadcast("Congrats " + players.get(0).getName() + " you won !!");
        broadcast("=================================================================");

    }

    private Question transmitQuestion(PlayerConnectionHandler playerHandler) throws InterruptedException, IOException {

        Question question = pickRandomQuestion(playerHandler);
        transmit(playerHandler.getName(), question.getQuestion());
        Thread.sleep(500);
        transmitWithoutUserName(playerHandler.getName(), "");
        transmitWithoutUserName(playerHandler.getName(), question.getAnswerA());
        transmitWithoutUserName(playerHandler.getName(), question.getAnswerB());
        transmitWithoutUserName(playerHandler.getName(), question.getAnswerC());
        transmitWithoutUserName(playerHandler.getName(), question.getAnswerD());
        broadcast("");
        transmitWithoutUserName(playerHandler.getName(), GameMessages.SELECT_ANSWER);

        return question;
    }

    private void transmitQuestion(PlayerConnectionHandler playerHandler, Question question) throws InterruptedException, IOException {

        transmit(playerHandler.getName(), question.getQuestion());
        Thread.sleep(500);
        transmitWithoutUserName(playerHandler.getName(), "");
        transmitWithoutUserName(playerHandler.getName(), question.getAnswerA());
        transmitWithoutUserName(playerHandler.getName(), question.getAnswerB());
        transmitWithoutUserName(playerHandler.getName(), question.getAnswerC());
        transmitWithoutUserName(playerHandler.getName(), question.getAnswerD());
        broadcast("");
        transmitWithoutUserName(playerHandler.getName(), GameMessages.SELECT_ANSWER);
    }

    private void round(PlayerConnectionHandler playerHandler) throws IOException, InterruptedException {

        playerHandler.send(playerHandler.name + " is your turn to answer...");
        playerHandler.send("");
        System.out.println(playerHandler.name + " is playing...");

        for (PlayerConnectionHandler ph : players) {
            if (ph != playerHandler) {
                ph.send(playerHandler.name + " is playing...");
                ph.send("");
            }
        }

        Question question = transmitQuestion(playerHandler);


        boolean playerHasFinished = false;

        while (!playerHasFinished) {

            String choice = playerHandler.in.readLine();

            switch (choice) {
                case "a", "b", "c", "d":

                    if (choice.equalsIgnoreCase(question.getCorrectAnswer())) {
                        transmitWithoutUserName(playerHandler.getName(), GameMessages.CORRECT_ANSWER);
                    } else {
                        transmitWithoutUserName(playerHandler.getName(), ConsoleColors.RED_BOLD_BRIGHT + "You guessed wrong. The correct answer was " + question.getCorrectAnswer() + ")" + ConsoleColors.RESET);
                        playerHandler.setLivesRemaining(playerHandler.getLivesRemaining() - 1);
                        transmitWithoutUserName(playerHandler.getName(), "You have " + playerHandler.getLivesRemaining() + " lives remaining");
                    }
                    playerHasFinished = true;
                    break;

                case "f":
                    show_5050(playerHandler);
                    break;

                case "h":
                    show_Hint(playerHandler);
                    break;

                case "s":
                    Question newQuestion = transmitQuestion(playerHandler);
                    question = newQuestion;
                    break;

                default:
                    transmitWithoutUserName(playerHandler.getName(), GameMessages.INVALID_INPUT);

            }

        }

        if (playerHandler.getLivesRemaining() == 0) {
            transmitWithoutUserName(playerHandler.getName(), "You have lost!");
            broadcast_(playerHandler.getName(), "has left the game");
            removePlayer(playerHandler);
        }

    }

    public class Question {

        private String questionID;
        private String difficulty;
        private String question;
        private String answerA;
        private String answerB;
        private String answerC;
        private String answerD;
        private String correctAnswer;
        private String hint;
        private int questionLine;
        private boolean showHint;
        private boolean show5050;


        public Question() {

        }

        public boolean isShowHint() {
            return showHint;
        }

        public void setShowHint(boolean showHint) {
            this.showHint = showHint;
        }

        public boolean isShow5050() {
            return show5050;
        }

        public void setShow5050(boolean show5050) {
            this.show5050 = show5050;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswerA() {
            return answerA;
        }

        public void setAnswerA(String answerA) {
            this.answerA = answerA;
        }

        public String getAnswerB() {
            return answerB;
        }

        public void setAnswerB(String answerB) {
            this.answerB = answerB;
        }

        public String getAnswerC() {
            return answerC;
        }

        public void setAnswerC(String answerC) {
            this.answerC = answerC;
        }

        public String getAnswerD() {
            return answerD;
        }

        public void setAnswerD(String answerD) {
            this.answerD = answerD;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public String getHint() {
            return hint;
        }

        public void setHint(String hint) {
            this.hint = hint;
        }

        public int getQuestionLine() {
            return questionLine;
        }

        public void setQuestionLine(int questionLine) {
            this.questionLine = questionLine;
        }

        public String getQuestionID() {
            return questionID;
        }

        public void setQuestionID(String questionID) {
            this.questionID = questionID;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }


    }

//    public void myAnswers() throws IOException {
//
//        BufferedReader reader = new BufferedReader(new FileReader("./src/answers.txt"));
//
//        String[] divide = Files.readAllLines(Paths.get("./src/answers.txt")).toString().split("____");
//
//        String[][] answers = new String[divide.length][6];
//
//        for (int i = 0; i < divide.length; i++) {
//            for (int j = 0; j < 6; j++) {
//                answers[i][j] = reader.readLine();
//            }
//        }
//
//        reader.close();
//        System.out.println(answers[3][4]);
//
//    }


//    public void roundMaker(PlayerConnectionHandler player)throws IOException {
//
//        while (player.getLivesRemaining() > 0 && totalNumberOfQuestions >= player.getQuestionsAsked()) {
//
//            Scanner scanner = new Scanner(System.in);
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            boolean roundIsOver = false;
//
//            try {
//                broadcast(pickRandomQuestion(player).getQuestion());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                showRandomQuestion(player);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            System.out.printf(GameMessages.SELECT_ANSWER);
//
//            while(!roundIsOver){
//                String choice = scanner.nextLine();
//                switch (choice) {
//                    case "a", "b", "c", "d":
//                        System.out.printf(GameMessages.LOCK_ANSWER);
//                        checkCorrectAnswer();
//                        roundIsOver = true;
//                        break;
//                    case "f":
//                        try {
//                            show5050();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        break;
//                    case "h":
//                        showHint();
//                        break;
//                    default:
//                        System.out.printf(GameMessages.INVALID_INPUT);
//                }
//            }
//        }
//    }

    private ServerSocket serverSocket;
    private ExecutorService service;

    public void start(int port) throws IOException, InterruptedException {

        this.serverSocket = new ServerSocket(port);
        System.out.println("Game started. Waiting for players to connect...");
        service = Executors.newCachedThreadPool();

        while (players.size() < numberOfPlayers) {
            Socket clientSocket = serverSocket.accept();
            PlayerConnectionHandler ph = new PlayerConnectionHandler(clientSocket);
            players.add(ph);
            service.submit(ph);
            if (players.size() < numberOfPlayers) {
                broadcast("");
                transmit(ph.getName(), "Wait for other players to join");
            }
        }

        System.out.println("Ready to play!");

        Thread.sleep(500);
//        new Thread(this).start();
        run();
    }

    public void broadcast_(String name, String message) {
        players.stream()
                .filter(handler -> !handler.getName().equals(name))
                .forEach(handler -> handler.send(name + ": " + message));
    }

    public void transmit(String name, String message) {
        players.stream()
                .filter(handler -> handler.getName().equals(name))
                .forEach(handler -> handler.send(name + ": " + message));
    }

    public void transmitWithoutUserName(String name, String message) {
        players.stream()
                .filter(handler -> handler.getName().equals(name))
                .forEach(handler -> handler.send(message));
    }

    public void broadcast(String message) {
        players.stream()
                .forEach(handler -> handler.send(message));
    }

    public String listPlayers() {
        StringBuffer buffer = new StringBuffer();
        players.forEach(player -> buffer.append(player.getName()).append("\n"));
        return buffer.toString();
    }

    public void removePlayer(PlayerConnectionHandler playerConnectionHandler) {
        players.remove(playerConnectionHandler);

    }

    public Optional<PlayerConnectionHandler> getPlayerByName(String name) {
        return players.stream()
                .filter(playerConnectionHandler -> playerConnectionHandler.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public class PlayerConnectionHandler implements Runnable {

        private boolean roundOver;
        private int hintsRemaining;
        private int _5050Remaining;
        private int livesRemaining;
        private boolean isPlaying;
        private int moneyAmount;
        private List<Question> questions = new LinkedList<>();


        private String name;
        private Socket playerSocket;
        private BufferedWriter out;
        private BufferedReader in;
        private String message;


        public PlayerConnectionHandler(Socket clientSocket) throws IOException {

            this.playerSocket = clientSocket;
            this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            this.name = generateName();
            this.hintsRemaining = 3;
            this._5050Remaining = 3;
            this.livesRemaining = 3;


        }

        public List<Question> getQuestions() {
            return questions;
        }

        public int getQuestionsAsked() {
            return questions.size();
        }

        public boolean repeatedQuestion(String checkQuestion) {
            for (Question question : questions) {
                if (question.getQuestion().equals(checkQuestion)) {
                    return true;
                }
            }
            return false;
        }

        public boolean isRoundOver() {
            return roundOver;
        }

        public void setRoundOver(boolean roundIsOver) {
            this.roundOver = roundIsOver;
        }

        public String getName() {
            return name;
        }

        public int getMoneyAmount() {
            return moneyAmount;
        }

        public void setMoneyAmount(int moneyAmount) {
            if (getMoneyAmount() < 0) {
                this.moneyAmount = 0;
                return;
            }
            this.moneyAmount = moneyAmount;
        }

        public int getHintsRemaining() {
            return hintsRemaining;
        }

        public int get_5050Remaining() {
            return _5050Remaining;
        }

        public int getLivesRemaining() {
            return livesRemaining;
        }

        public void setHintsRemaining(int hintsRemaining) {
            this.hintsRemaining = hintsRemaining;
        }

        public void set_5050Remaining(int _5050Remaining) {
            this._5050Remaining = _5050Remaining;
        }

        public void setLivesRemaining(int livesRemaining) {
            this.livesRemaining = livesRemaining;
        }

        public boolean isPlaying() {
            return isPlaying;
        }

        public void setPlaying(boolean playing) {
            isPlaying = playing;
        }

        @Override
        public void run() {

            try {
                // BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                Scanner in = new Scanner(playerSocket.getInputStream());
                while (in.hasNext()) {
                    message = in.nextLine();
                    if (isCommand(message)) {
                        dealWithCommand(message);
                        continue;
                    }
                    if (message.equals("")) {
                        continue;
                    }

                    transmitWithoutUserName(this.getName(), " ");
                    transmitWithoutUserName(this.getName(), GameMessages.LOCK_ANSWER);
                }
            } catch (IOException e) {
                System.err.println(GameMessages.CLIENT_ERROR + e.getMessage());
            }
        }

        private boolean isCommand(String message) {
            return message.startsWith("/");
        }

        private void dealWithCommand(String message) throws IOException {
            String description = message.split(" ")[0];
            Command command = Command.getCommandFromDescription(description);

            if (command == null) {
                out.write(GameMessages.NO_SUCH_COMMAND);
                out.newLine();
                out.flush();
                return;
            }

            command.getHandler().execute(Game.this, this);
        }

        public void send(String message) {
            try {
                out.write(message);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                removePlayer(this);
                e.printStackTrace();
            }
        }

        public void close() {
            try {
                playerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getMessage() {
            return message;
        }

        public String generateName() throws IOException {
            send("");
            send("Please enter your name");
            String name = in.readLine();
            return name;
        }

        public String waitForUserInput() throws IOException {
            String choice = in.readLine();
            return choice;
        }
    }

    public void run() {

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        broadcast("");
        broadcast("");
        broadcast(GameMessages.GAME_STARTED);

        try {
            loading();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean isGameOver = false;

        while (!isGameOver) {
            for (int i = 0; i < players.size(); i++) {
                try {
                    round(players.get(i));
                    if (players.size() == 1) {
                        isGameOver = true;
                        gameOver();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }

}

