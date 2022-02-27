package academy.mindswap.server;

import java.io.IOException;

public class GameLauncher {

    public static void main(String[] args) {
        Game game = new Game(3);

        try {
           game.start(8082);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
