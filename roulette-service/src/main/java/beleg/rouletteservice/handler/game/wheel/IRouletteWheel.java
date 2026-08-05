package beleg.rouletteservice.handler.game.wheel;


// interface für die spin logik des roulette rads, in mockito tests kann man dann das verhalten vorgeben
public interface IRouletteWheel {

    int spin();
}