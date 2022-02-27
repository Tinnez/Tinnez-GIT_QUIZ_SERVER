package academy.mindswap.server.commands;

import academy.mindswap.server.Game;
import academy.mindswap.server.messages.GameMessages;

public class QuitHandler implements CommandHandler {

    @Override
    public void execute(Game game, Game.PlayerConnectionHandler playerConnectionHandler) {
        game.removePlayer(playerConnectionHandler);
        game.broadcast_(playerConnectionHandler.getName(), playerConnectionHandler.getName() + GameMessages.CLIENT_DISCONNECTED);
        game.transmitWithoutUserName(playerConnectionHandler.getName(), "You left the game.");
        playerConnectionHandler.close();
    }
}
