package academy.mindswap.server.messages;

import academy.mindswap.server.ConsoleColors;

public class GameMessages {

    public static final String GAME_STARTED = "                               ╔╦╗╦ ╦╔═╗  ╔═╗ ╦ ╦╦╔═╗  ╔═╗╔═╗╔╦╗╔═╗  ┬\n" + ConsoleColors.BLUE_BOLD_BRIGHT +
                                              "                                ║ ╠═╣║╣   ║═╬╗║ ║║╔═╝  ║ ╦╠═╣║║║║╣   │\n" + ConsoleColors.RED_BOLD_BRIGHT +
                                              "                                ╩ ╩ ╩╚═╝  ╚═╝╚╚═╝╩╚═╝  ╚═╝╩ ╩╩ ╩╚═╝  o\n" + ConsoleColors.RESET;

    public static final String PLAYER_WON = ConsoleColors.GREEN + "Player %s won!\n" + ConsoleColors.RESET;
    public static final String PLAYER_LOST = ConsoleColors.RED +  "Player %s lost.\n" + ConsoleColors.RESET;
    public static final String CORRECT_ANSWER = ConsoleColors.GREEN_BOLD_BRIGHT + "Congratulations %s, you guessed right!\n" + ConsoleColors.RESET;
    public static final String WRONG_ANSWER = ConsoleColors.RED_BOLD_BRIGHT + "You are dumb %s, you guessed wrong. The right answer was %s).\n" + ConsoleColors.RESET;
    public static final String NO_MORE_QUESTIONS = "There are no more new questions left. Please insert new questions.\n";
    public static final String REPEATED_QUESTIONS = "Pick another question, this one was already asked.\n";
    public static final String INVALID_QUESTION_ID = "Invalid or inexistent ID number\n";
    public static final String NO_MORE_HINTS = "You have no hints remaining\n";
    public static final String NO_MORE_5050 = "You have no 50/50 remaining\n";
    public static final String INVALID_INPUT = "Invalid input. Choose one of the valid inputs: (a, b, c, d, f for 50/50, h for hint, s to swap question)\n";
    public static final String HELP_ALREADY_USED = "You have already used this help\n";
    public static final String LOCK_ANSWER = "Do you want to lock your answer? Repeat character to confirm or choose different answer\n";
    public static final String SELECT_ANSWER = "Select your answer\n";
    public static final String ROUND_NUMBER = ConsoleColors.BLUE + "    Round number : %s    \n" + ConsoleColors.RESET;


    public static final String CLIENT_ACCEPTED = "Client%d accepted: %s\n";
    public static final String PORT_ERROR = "Unable to start game on port %s\n";
    public static final String CLOSED_CONNECTION = "Client%d closed the connection.\n";
//    public static final String CLIENT_MESSAGE = "Client%d said: %s\n";
    public static final String CLIENT_SEND_MESSAGE = "Send message to client: %s\n";



    public static final String PLAYER_ENTERED_GAME = " entered the game.";
    public static final String NO_SUCH_COMMAND = "⚠️ Invalid command!";
    public static final String COMMANDS_LIST = """
            List of available commands:
            /list -> gets you the list of connected players
            /whisper <username> <message> -> lets you whisper a message to a single connected players
            /quit -> exits the server""";
    public static final String CLIENT_DISCONNECTED = " left the game.";
    public static final String WHISPER_INSTRUCTIONS = "Invalid whisper use. Correct use: '/whisper <username> <message>";
    public static final String NO_SUCH_CLIENT = "The client you want to whisper to doesn't exists.";
    public static final String WHISPER = "(whisper)";
    public static final String CLIENT_ERROR = "Something went wrong with this client's connection. Error: ";

}
