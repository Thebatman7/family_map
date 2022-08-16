package Model;

import java.util.concurrent.ThreadLocalRandom;

public class Names {
    private String[] data;

    public Names() {}
    public Names(String[] data) {
        this.data = data;
    }

    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }

    public String getRandomName(){
        int randomIndex = getRandomNumber(0, data.length - 1);
        return data[randomIndex];
    }


    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
        /*
        or alternatively we could use this
        public static int getRandomValue(int min, int max) {
            //get and return the random integer within Min and Max
            return ThreadLocalRandom
                    .current()
                    .nextInt(min, max + 1);
        }
        */
    }
}
