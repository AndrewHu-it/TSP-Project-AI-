import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TSP {

    public enum DataSet {
        USA, CANADA, EUROPE, AMERICAS, CARIBBEAN, CENTRALAMERICA, NORTHAMERICA, SOUTHAMERICA,
    }

    public enum TourGenerationMethod {
        NN, NEAR, FAR, RANDOM
    }

    public enum ImprovementMethod {
        TWO_OPT, SIMULATED_ANNEALING
    }

    public static void main(String[] args) {

        int seed = 1974;
        int count = 1;
        int size = 3;

        TourGenerationMethod tour_generation_method = TourGenerationMethod.NN;
        DataSet data_set = DataSet.USA;
        boolean improve_tour = false;
        boolean length = false;
        ImprovementMethod improvementMethod = ImprovementMethod.TWO_OPT; // Default improvement method

        double initialTemperature = 10000;
        double coolingRate = 0.003;
        int iterationLimit = 200000;

        ArrayList<City> initial_cities = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {

            switch (args[i]) {
                case "-nn":
                    tour_generation_method = TourGenerationMethod.NN;
                    break;
                case "-near":
                    tour_generation_method = TourGenerationMethod.NEAR;
                    break;
                case "-far":
                    tour_generation_method = TourGenerationMethod.FAR;
                    break;
                case "-length":
                    length = true;
                    break;
                case "-random":
                    tour_generation_method = TourGenerationMethod.RANDOM;
                    break;
                case "-size":
                    size = Integer.parseInt(args[++i]);
                    break;
                case "-count":
                    count = Integer.parseInt(args[++i]);
                    break;
                case "-seed":
                    seed = Integer.parseInt(args[++i]);
                    break;
                case "-improve":
                    improve_tour = true;
                    break;
                case "-sa":
                    improvementMethod = ImprovementMethod.SIMULATED_ANNEALING;
                    break;
                case "-temp":
                    initialTemperature = Double.parseDouble(args[++i]);
                    break;
                case "-cooling":
                    coolingRate = Double.parseDouble(args[++i]);
                    break;
                case "-iterations":
                    iterationLimit = Integer.parseInt(args[++i]);
                    break;
                case "-usa":
                    data_set = DataSet.USA;
                    break;
                case "-canada":
                    data_set = DataSet.CANADA;
                    break;
                case "-europe":
                    data_set = DataSet.EUROPE;
                    break;
                case "-americas":
                    data_set = DataSet.AMERICAS;
                    break;
                case "-caribbean":
                    data_set = DataSet.CARIBBEAN;
                    break;
                case "-central_america":
                    data_set = DataSet.CENTRALAMERICA;
                    break;
                case "-north_america":
                    data_set = DataSet.NORTHAMERICA;
                    break;
                case "-south_america":
                    data_set = DataSet.SOUTHAMERICA;
                    break;
                default:
                    try {
                        switch (data_set) {
                            case USA:
                                US.State state = US.find(args[i]);
                                if (state == null) {
                                    System.err.println("Invalid state: " + args[i]);
                                } else {
                                    initial_cities.add(state.capital());
                                }
                                break;
                            case CANADA:
                                Canada.Province province = Canada.find(args[i]);
                                if (province == null) {
                                    System.err.println("Invalid province: " + args[i]);
                                } else {
                                    initial_cities.add(province.capital());
                                }
                                break;
                            case EUROPE:
                                Europe.Country countryEU = Europe.find(args[i]);
                                if (countryEU == null) {
                                    System.err.println("Invalid country: " + args[i]);
                                } else {
                                    initial_cities.add(countryEU.capital());
                                }
                                break;
                            case AMERICAS:
                                Americas.Country countryAM = Americas.find(args[i]);
                                if (countryAM == null) {
                                    System.err.println("Invalid country: " + args[i]);
                                } else {
                                    initial_cities.add(countryAM.capital());
                                }
                                break;
                            case CARIBBEAN:
                                Caribbean.Country countryCAR = Caribbean.find(args[i]);
                                if (countryCAR == null) {
                                    System.err.println("Invalid country: " + args[i]);
                                } else {
                                    initial_cities.add(countryCAR.capital());
                                }
                                break;
                            case CENTRALAMERICA:
                                CentralAmerica.Country countryCA = CentralAmerica.find(args[i]);
                                if (countryCA == null) {
                                    System.err.println("Invalid country: " + args[i]);
                                } else {
                                    initial_cities.add(countryCA.capital());
                                }
                                break;
                            case NORTHAMERICA:
                                NorthAmerica.Country countryNA = NorthAmerica.find(args[i]);
                                if (countryNA == null) {
                                    System.err.println("Invalid country: " + args[i]);
                                } else {
                                    initial_cities.add(countryNA.capital());
                                }
                                break;
                            case SOUTHAMERICA:
                                SouthAmerica.Country countrySA = SouthAmerica.find(args[i]);
                                if (countrySA == null) {
                                    System.err.println("Invalid country: " + args[i]);
                                } else {
                                    initial_cities.add(countrySA.capital());
                                }
                                break;
                            default:
                                System.err.println("Invalid dataset: " + data_set);
                                break;
                        }
                    } catch (Exception e) {
                        System.err.println("No such argument: " + args[i]);
                    }
                    break;
            }
        }

        Tour[] tours = generateTours(count, tour_generation_method, data_set, initial_cities, size, seed);

        if (!improve_tour && length){
            System.out.println("First tour Length: " + tours[0].length());
        }

        if (improve_tour) {
            if (tours.length > 0) {
                Tour tour_to_improve = tours[0];
                if (length) {
                    System.out.println("Initial Tour Length: " + tour_to_improve.length());
                    tour_to_improve.print();
                }
                if (improvementMethod == ImprovementMethod.TWO_OPT) {
                    tour_to_improve.twoOpt();
                } else if (improvementMethod == ImprovementMethod.SIMULATED_ANNEALING) {
                    tour_to_improve.simulatedAnnealing(initialTemperature, coolingRate, iterationLimit);
                    tour_to_improve.twoOpt();
                }
                if (length) {
                    System.out.println("Improved Tour Length: " + tour_to_improve.length());
                }
                tour_to_improve.print();
            } else {
                System.err.println("No tours available to improve.");
            }
        } else {
            for (Tour tour : tours) {
                tour.print();
            }
        }
    }


    public static City[] getAllCities(DataSet dataset) {
        ArrayList<City> cities = new ArrayList<City>();

        switch (dataset) {
            case USA:
                for (US.State state : US.State.values()) {
                    cities.add(state.capital());
                }
                break;
            case CANADA:
                for (Canada.Province province : Canada.Province.values()) {
                    cities.add(province.capital());
                }
                break;
            case EUROPE:
                for (Europe.Country country : Europe.Country.values()) {
                    cities.add(country.capital());
                }
                break;
            case AMERICAS:
                for (Americas.Country country : Americas.Country.values()) {
                    cities.add(country.capital());
                }
                break;
            case CARIBBEAN:
                for (Caribbean.Country country : Caribbean.Country.values()) {
                    cities.add(country.capital());
                }
                break;
            case CENTRALAMERICA:
                for (CentralAmerica.Country country : CentralAmerica.Country.values()) {
                    cities.add(country.capital());
                }
                break;
            case NORTHAMERICA:
                for (NorthAmerica.Country country : NorthAmerica.Country.values()) {
                    cities.add(country.capital());
                }
                break;
            case SOUTHAMERICA:
                for (SouthAmerica.Country country : SouthAmerica.Country.values()) {
                    cities.add(country.capital());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown dataset");
        }

        return cities.toArray(new City[cities.size()]);
    }


    public static Tour[] generateTours(int count, TourGenerationMethod method, DataSet dataset,
                                       ArrayList<City> initialCities, int size, int seed) {
        City[] allCities = getAllCities(dataset);
        Random random = new Random(seed);
        Tour[] tours;

        if (method == TourGenerationMethod.NN) {
            if (initialCities.isEmpty()) {
                tours = new Tour[count];
                for (int i = 0; i < count; i++) {
                    City startCity = allCities[random.nextInt(allCities.length)];
                    Tour tour = new Tour(dataset, startCity);
                    tour.nearestNeighbor();
                    tours[i] = tour;
                }
            } else {

                tours = new Tour[initialCities.size()];
                for (int i = 0; i < initialCities.size(); i++) {
                    City startCity = initialCities.get(i);
                    Tour tour = new Tour(dataset, startCity);
                    tour.nearestNeighbor();
                    tours[i] = tour;
                }
            }
        } else {
            if (initialCities.isEmpty()) {
                tours = new Tour[count];
                for (int i = 0; i < count; i++) {
                    ArrayList<City> availableCities = new ArrayList<>(Arrays.asList(allCities));
                    Tour tour;
                    ArrayList<City> startingCities = new ArrayList<>();

                    for (int s = 0; s < size; s++) {
                        City city = availableCities.remove(random.nextInt(availableCities.size()));
                        startingCities.add(city);
                    }

                    City[] startingCitiesArray = startingCities.toArray(new City[0]);
                    tour = new Tour(dataset, startingCitiesArray);

                    while (tour.size() < allCities.length) {
                        City nextCity;
                        if (method == TourGenerationMethod.NEAR) {
                            nextCity = tour.closest();
                        } else if (method == TourGenerationMethod.FAR) {
                            nextCity = tour.farthest();
                        } else if (method == TourGenerationMethod.RANDOM) {
                            nextCity = availableCities.get(random.nextInt(availableCities.size()));
                        } else {
                            throw new IllegalArgumentException("Unknown method: " + method);
                        }
                        tour.insert(nextCity);
                        availableCities.remove(nextCity);
                    }
                    tours[i] = tour;
                }
            } else {
                tours = new Tour[1];
                Tour tour = new Tour(dataset, initialCities.toArray(new City[0]));
                ArrayList<City> availableCities = new ArrayList<>(Arrays.asList(allCities));
                availableCities.removeAll(initialCities);

                while (tour.size() < allCities.length) {
                    City nextCity;
                    if (method == TourGenerationMethod.NEAR) {
                        nextCity = tour.closest();
                    } else if (method == TourGenerationMethod.FAR) {
                        nextCity = tour.farthest();
                    } else if (method == TourGenerationMethod.RANDOM) {
                        nextCity = availableCities.get(random.nextInt(availableCities.size()));
                    } else {
                        throw new IllegalArgumentException("Unknown method: " + method);
                    }
                    tour.insert(nextCity);
                    availableCities.remove(nextCity);
                }
                tours[0] = tour;
            }
        }
        return tours;
    }
}
