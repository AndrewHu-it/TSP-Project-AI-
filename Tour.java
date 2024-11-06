import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Tour {
    private TSP.DataSet dataset;
    public City[] capitals;
    private int count;
    private ArrayList<City> cities_data;

    public Tour(TSP.DataSet dataset) {
        this.dataset = dataset;
        initialize_cities_data(dataset);
        initializeCapitals();
        count = 0;
    }

    public Tour(TSP.DataSet dataset, City initial_city) {
        this(dataset);
        this.capitals[0] = initial_city;
        count = 1;
    }

    public Tour(TSP.DataSet dataset, City[] initial_capitals) {
        this(dataset);
        for (int i = 0; i < initial_capitals.length && i < this.capitals.length; i++) {
            this.capitals[i] = initial_capitals[i];
        }
        count = initial_capitals.length;
    }

    private void initialize_cities_data(TSP.DataSet dataset) {
        this.cities_data = new ArrayList<>();
        switch (dataset) {
            case USA:
                for (US.State s : US.State.values()) {
                    cities_data.add(s.capital());
                }
                break;
            case CANADA:
                for (Canada.Province p : Canada.Province.values()) {
                    cities_data.add(p.capital());
                }
                break;
            case EUROPE:
                for (Europe.Country c : Europe.Country.values()) {
                    cities_data.add(c.capital());
                }
                break;
            case AMERICAS:
                for (Americas.Country c : Americas.Country.values()) {
                    cities_data.add(c.capital());
                }
                break;
            case CARIBBEAN:
                for (Caribbean.Country c : Caribbean.Country.values()) {
                    cities_data.add(c.capital());
                }
                break;
            case CENTRALAMERICA:
                for (CentralAmerica.Country c : CentralAmerica.Country.values()) {
                    cities_data.add(c.capital());
                }
                break;
            case NORTHAMERICA:
                for (NorthAmerica.Country c : NorthAmerica.Country.values()) {
                    cities_data.add(c.capital());
                }
                break;
            case SOUTHAMERICA:
                for (SouthAmerica.Country c : SouthAmerica.Country.values()) {
                    cities_data.add(c.capital());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown dataset");
        }
    }

    private void initializeCapitals() {
        capitals = new City[cities_data.size()];
    }

    public City[] getCities_data() {
        return this.cities_data.toArray(new City[0]);
    }

    public void prepend(City city) {
        if (count >= capitals.length)
            throw new IllegalStateException("Array is full, cannot prepend.");
        System.arraycopy(capitals, 0, capitals, 1, count);
        capitals[0] = city;
        count++;
    }

    public void append(City city) {
        if (count >= capitals.length)
            throw new IllegalStateException("Array is full, cannot append.");
        capitals[count++] = city;
    }

    public boolean contains(City city) {
        for (int i = 0; i < count; i++) {
            if (capitals[i].equals(city)) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return count;
    }

    public double length() {
        double totalDistance = 0;
        if (count > 1) {
            for (int i = 0; i < count - 1; i++) {
                totalDistance += capitals[i].distance(capitals[i + 1]);
            }
            totalDistance += capitals[count - 1].distance(capitals[0]);
        }
        return totalDistance;
    }

    public City closest() {
        if (count == 0 || cities_data.isEmpty()) {
            return null;
        }

        City lastCity = capitals[count - 1];
        City closestCity = null;
        double minDistance = Double.MAX_VALUE;

        for (City city : cities_data) {
            if (!contains(city)) {
                double distance = lastCity.distance(city);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestCity = city;
                }
            }
        }

        return closestCity;
    }

    public City farthest() {
        if (count == 0 || cities_data.isEmpty()) {
            return null;
        }

        City lastCity = capitals[count - 1];
        City furthestCity = null;
        double maxDistance = 0;

        for (City city : cities_data) {
            if (!contains(city)) {
                double distance = lastCity.distance(city);
                if (distance > maxDistance) {
                    maxDistance = distance;
                    furthestCity = city;
                }
            }
        }

        return furthestCity;
    }

    public void insert(City newCity) {
        if (newCity == null || count >= capitals.length) {
            throw new IllegalStateException("Cannot insert null city or full tour");
        }

        int bestPosition = -1;
        double minIncrease = Double.MAX_VALUE;

        for (int i = 0; i < count; i++) {
            City current = capitals[i];
            City next = capitals[(i + 1) % count];

            double currentToNew = current.distance(newCity);
            double newToNext = newCity.distance(next);
            double currentToNext = current.distance(next);

            double increase = currentToNew + newToNext - currentToNext;

            if (increase < minIncrease) {
                minIncrease = increase;
                bestPosition = i + 1;
            }
        }

        if (bestPosition != -1) {
            for (int j = count; j > bestPosition; j--) {
                capitals[j] = capitals[j - 1];
            }
            capitals[bestPosition] = newCity;
            count++;
        }
    }

    public void nearestNeighbor() {
        if (cities_data == null || cities_data.isEmpty()) {
            initialize_cities_data(dataset);
        }

        ArrayList<City> notInTour = new ArrayList<>(cities_data);

        for (int i = 0; i < count; i++) {
            notInTour.remove(capitals[i]);
        }

        City current = capitals[count - 1];

        while (!notInTour.isEmpty()) {
            double minDistance = Double.MAX_VALUE;
            City nearestCity = null;
            int nearestIndex = -1;

            for (int i = 0; i < notInTour.size(); i++) {
                City city = notInTour.get(i);
                double distance = current.distance(city);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestCity = city;
                    nearestIndex = i;
                }
            }

            notInTour.remove(nearestIndex);
            append(nearestCity);

            current = nearestCity;
        }
    }

    public void twoOpt() {
        boolean improvement = true;
        while (improvement) {
            improvement = false;
            double bestDistance = this.length();
            for (int i = 0; i < count - 1; i++) {
                for (int k = i + 1; k < count; k++) {
                    Tour newTour = this.twoOptSwap(i, k);
                    double newDistance = newTour.length();
                    if (newDistance < bestDistance) {
                        this.capitals = newTour.capitals.clone();
                        this.count = newTour.count;
                        bestDistance = newDistance;
                        improvement = true;
                    }
                }
            }
        }
    }

    private Tour twoOptSwap(int i, int k) {
        Tour newTour = new Tour(this.dataset);
        for (int c = 0; c <= i; c++) {
            newTour.append(this.capitals[c]);
        }
        for (int c = k; c > i; c--) {
            newTour.append(this.capitals[c]);
        }
        for (int c = k + 1; c < this.count; c++) {
            newTour.append(this.capitals[c]);
        }
        return newTour;
    }


    public void simulatedAnnealing(double initialTemperature, double coolingRate, int iterationLimit) {
        Tour currentSolution = new Tour(this.dataset);
        currentSolution.capitals = this.capitals.clone();
        currentSolution.count = this.count;

        Tour bestSolution = new Tour(this.dataset);
        bestSolution.capitals = this.capitals.clone();
        bestSolution.count = this.count;

        double temperature = initialTemperature;

        int iteration = 0;
        Random random = new Random();
        while (temperature > 1 && iteration < iterationLimit) {
            Tour newSolution = new Tour(this.dataset);
            newSolution.capitals = currentSolution.capitals.clone();
            newSolution.count = currentSolution.count;

            int tourSize = newSolution.count;
            int cityIndex1 = random.nextInt(tourSize);
            int cityIndex2 = random.nextInt(tourSize);
            while (cityIndex1 == cityIndex2) {
                cityIndex2 = random.nextInt(tourSize);
            }

            City tempCity = newSolution.capitals[cityIndex1];
            newSolution.capitals[cityIndex1] = newSolution.capitals[cityIndex2];
            newSolution.capitals[cityIndex2] = tempCity;

            double currentDistance = currentSolution.length();
            double neighborDistance = newSolution.length();
            double deltaDistance = neighborDistance - currentDistance;

            double acceptanceProbability = Math.exp(-deltaDistance / (temperature * 0.5));
            if (deltaDistance < 0 || acceptanceProbability > random.nextDouble()) {
                currentSolution = newSolution;
            }

            if (currentSolution.length() < bestSolution.length()) {
                bestSolution.capitals = currentSolution.capitals.clone();
                bestSolution.count = currentSolution.count;
            }

            temperature *= 1 - coolingRate;
            iteration++;
        }

        this.capitals = bestSolution.capitals.clone();
        this.count = bestSolution.count;
    }


    public void print() {
        for (int i = 0; i < count; i++) {
            System.out.print(capitals[i].region().code());
            if (i < count - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }
}
