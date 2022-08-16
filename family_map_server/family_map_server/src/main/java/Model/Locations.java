package Model;

public class Locations {
    private Location[] data;

    public Locations() {}
    public Locations(Location[] data) {
        this.data = data;
    }

    public Location[] getData() {
        return data;
    }

    public void setData(Location[] data) {
        this.data = data;
    }

    public Location getRandomLocation(){
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
