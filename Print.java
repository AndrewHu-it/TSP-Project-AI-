import java.util.ArrayList;
import java.util.Scanner;

public class Print {

    public static final double MILES_PER_KILOMETER = 0.621371;

    public static ArrayList<City> readTour(Scanner scanner, Region[] regions) {
        ArrayList<City> tour = new ArrayList<>();
        while (scanner.hasNext()) {
            String item = scanner.next();
            Region region = Region.find(item, regions);
            if (region == null) {
                String kind = Region.kind(regions);
                throw new IllegalArgumentException(String.format("Invalid %s: %s", kind ,item));
            } else {
                tour.add(region.capital());
            }
        }
        return tour;
    }

    public static String name(City city, boolean full) {
        if (full) {
            return city.name() + ", " + city.region().toString();
        } else {
            return city.region().toString();
        }
    }


    public static void help() {
        System.out.println();
        System.out.println("java Print <options> <list of cities on the tour>");
        System.out.println();
        System.out.println("    Prints the cities/states/provinces/countries in a tour");
        System.out.println("    one per line with the code, name and distance (from");
        System.out.println("    the previous city) and the total length of the tour");
        System.out.println();
        System.out.println("    If a tour is not provided on the command line the");
        System.out.println("    tour is read from standard input which allows the");
        System.out.println("    tour produced by your code to be printed using a pipe:");
        System.out.println();
        System.out.println("        java TSP -canada | java Print -canada -miles");
        System.out.println();
        System.out.println("    Valid command line options are:");
        System.out.println();
        System.out.println("    -usa               Use the USA data set");
        System.out.println("    -us                   abbreviation for -usa");
        System.out.println("    -canada            Use the canada data set");
        System.out.println("    -can                  abbreviation for -can");
        System.out.println("    -central_america   Use the central america data set");
        System.out.println("    -central              abbreviation for -central_america");
        System.out.println("    -ca                   abbreviation for -central_americal");
        System.out.println("    -south_america     Use the south america data set");
        System.out.println("    -south                abbreviation for -south_america");
        System.out.println("    -sa                   abbreviation for -south_america");
        System.out.println("    -north_america     Use the north america data set");
        System.out.println("    -north                abbrevation for -north_america");
        System.out.println("    -na                   abbreviation for -north_america");
        System.out.println("    -caribbean         Use the caribbean data set");
        System.out.println("    -car                  abbreviation for -caribbean");
        System.out.println("    -europe            Use the europe data set");
        System.out.println("    -eu                   abbreviation for -europe");
        System.out.println("    -verbose           Print the full name of each city/state");
        System.out.println("    -length            Print only the length of the tour");
        System.out.println("    -miles             Print distances in miles");
        System.out.println("    -kilometers        Print distances in kilometers");
        System.out.println("    -km                   abbreviation for -kilometers");
        System.out.println();
    }


    public static void main(String[] args) {
        ArrayList<City> tour = new ArrayList<>();
        Region[] regions = US.State.values();
        boolean verbose = false;
        boolean quiet = false;
        boolean miles = false;
        int count = 0;

        for (String arg : args) {
            switch (arg) {
                case "-us":
                case "-usa":
                    regions = US.State.values();
                    break;

                case "-can":
                case "-canada":
                    regions = Canada.Province.values();
                    break;

                case "-ca":
                case "-central":
                case "-central_america":
                    regions = CentralAmerica.Country.values();
                    break;

                case "-sa":
                case "-south":
                case "-south_america":
                    regions = SouthAmerica.Country.values();
                    break;

                case "-na":
                case "-north":
                case "-north_america":
                    regions = NorthAmerica.Country.values();
                    break;

                case "-car":
                case "-carribean":
                    regions = Caribbean.Country.values();
                    break;

                case "-eu":
                case "-europe":
                    regions = Europe.Country.values();
                    break;

                case "-verbose":
                    verbose = true;
                    break;

                case "+verbose":
                    verbose = false;
                    break;

                case "-quiet":
                case "-length":
                    quiet = true;
                    break;

                case "+quiet":
                case "+length":
                    quiet = false;
                    break;

                case "-miles":
                    miles = true;
                    break;

                case "-km":
                case "-kilometers":
                    miles = false;
                    break;

                case "-help":
                    help();
                    return;

                default:
                    if (arg.startsWith("-") || arg.startsWith("+")) {
                        System.err.println("Invalid option: " + arg);

                    } else {
                        Region region = Region.find(arg, regions);
                        if (region == null) {
                            String kind = Region.kind(regions);
                            System.err.println("Invalid " + kind + ": " + arg);
                        } else {
                            City city = region.capital();
                            if (tour.contains(city)) {
                                System.err.println("Duplicate region: " + arg);
                            } else {
                                tour.add(city);
                            }
                        }
                        count++;
                    }
                    break;
            }
        }

        if (count == 0) {
            tour = readTour(new Scanner(System.in), regions);
        }

        if (tour.size() < regions.length) {
            System.err.println("Incomplete tour");
        }

        double length = 0.0;
        String unit = miles ? "mi" : "km";
        if (tour.size() > 0) {
            City previous = tour.get(tour.size()-1);
            for (City current : tour) {
                String code = current.region().code();
                String name = name(current, verbose);
                double distance = previous.distance(current);
                if (miles) distance *= MILES_PER_KILOMETER;
                if (!quiet) {
                    System.out.printf("%s: %s (%,.1f %s)\n", code, name, distance, unit);
                }
                length += distance;
                previous = current;
            }
        }
        System.out.printf("%,.1f %s\n", length, unit);
    }
}
