package beleg.rouletteservice.handler.game.wheel;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
// super einfache implementierung eines roulette 'rads'
// durch inteface könnte man aber auch ein zweites bauen, welches auch logik für eine 00 enthält um
// beispielsweise das europäische roulette mit dem amerikanischen auszutauschen
@Component
public class RouletteWheelImpl implements IRouletteWheel {

    private final SecureRandom random = new SecureRandom();

    @Override
    public int spin() {
        return random.nextInt(37); // 0 bis 36 inklusive
    }
}