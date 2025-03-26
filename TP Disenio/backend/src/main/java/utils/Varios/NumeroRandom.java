package utils.Varios;

import lombok.Getter;

@Getter
public class NumeroRandom {
        public int getRandomNumberBetween(int min, int max) {
            if (min > max) {
                throw new IllegalArgumentException("El valor mínimo no puede ser mayor que el valor máximo.");
            }
            return (int) (Math.random() * ((max - min) + 1)) + min;
        }
}


