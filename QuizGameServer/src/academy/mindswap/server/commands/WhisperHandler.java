package academy.mindswap.server.commands;

import academy.mindswap.server.Game;
import academy.mindswap.server.messages.GameMessages;

import java.util.Optional;

public class WhisperHandler implements CommandHandler {

    @Override
    public void execute(Game game, Game.PlayerConnectionHandler playerConnectionHandler) {
        String message = playerConnectionHandler.getMessage();

        if (message.split(" ").length < 3) {
            playerConnectionHandler.send(GameMessages.WHISPER_INSTRUCTIONS);
            return;
        }

        Optional<Game.PlayerConnectionHandler> receiverClient = game.getPlayerByName(message.split(" ")[1]);

        if (receiverClient.isEmpty()) {
            playerConnectionHandler.send(GameMessages.NO_SUCH_CLIENT);
            return;
        }

        String messageToSend = message.substring(message.indexOf(" ") + 1).substring(message.indexOf(" ") + 1);
        receiverClient.get().send(playerConnectionHandler.getName() + GameMessages.WHISPER + ": " + messageToSend);
    }
}
