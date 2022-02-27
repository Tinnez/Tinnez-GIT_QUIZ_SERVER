package academy.mindswap.server.commands;

import academy.mindswap.server.Game;


public class ShoutHandler implements CommandHandler {
    @Override
    public void execute(Game game, Game.PlayerConnectionHandler playerConnectionHandler) {
        String message = playerConnectionHandler.getMessage();
        String messageToSend = message.substring(6);
        game.broadcast_(playerConnectionHandler.getName(), messageToSend.toUpperCase());
    }
}
