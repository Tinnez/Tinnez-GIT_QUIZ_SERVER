package academy.mindswap.server.commands;

import academy.mindswap.server.Game;
import academy.mindswap.server.messages.GameMessages;

public class HelpHandler implements CommandHandler {

    @Override
    public void execute(Game game, Game.PlayerConnectionHandler playerConnectionHandler) {
        playerConnectionHandler.send(GameMessages.COMMANDS_LIST);
    }
}
