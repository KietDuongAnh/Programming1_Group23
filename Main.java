import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws ParseException, IOException {
        run("C:\\Users\\Admin\\Desktop\\covid-data.csv");
    }

    // Convert Date type to String type with the format mm/dd/yyyy
    public static String dateToString(Date d) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        return df.format(d);
    }

    // Covert String type to Date type with the format mm//dd/yyyy or mm-dd-yy
    public static Date stringToDate(String n) throws ParseException {
        Date d;
        if (n.length() >= 9 || (n.length() == 8 && n.charAt(1) == '/')) {
            d = new SimpleDateFormat("MM/dd/yyyy").parse(n);
        } else {
            d = new SimpleDateFormat("MM-dd-yy").parse(n);
        }
        return d;
    }

    // Process the data file
    public static Assignment.ungroupedData processDataFile(String path) {
        String code = "AFG", continent, location;
        long newCases = 0L, newDeaths = 0L, newVaccinated = 0L, cases = 0L, deaths = 0L, vaccinated = 0L, prevVaccinated = 0L, population = 0L;
        Date rawDate;
        Assignment.ungroupedData u = new Assignment.ungroupedData();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.charAt(line.length() - 1) == ',') line += '0';
                String[] values = line.split(",");
                continent = values[1];
                location = values[2];
                rawDate = stringToDate(values[3]);
                if (values[4].equals("")) newCases = 0L;
                else newCases = Long.parseLong(values[4]);
                if (values[5].equals("")) newDeaths = 0L;
                else newDeaths = Long.parseLong(values[5]);
                if (values[6].equals("")) vaccinated = prevVaccinated;
                else {
                    vaccinated = Long.parseLong(values[6]);
                }
                if (code.equals(values[0])) {
                    cases += newCases;
                    deaths += newDeaths;
                    newVaccinated = vaccinated - prevVaccinated;
                } else {
                    cases = newCases;
                    deaths = newDeaths;
                    newVaccinated = vaccinated;
                    code = values[0];
                }
                prevVaccinated = vaccinated;
                if (values[7].equals("")) population = 0L;
                else population = Long.parseLong(values[7]);
                Assignment.rawDataPiece r = new Assignment.rawDataPiece(code, continent, location, newCases, newDeaths, newVaccinated,
                        cases, deaths, vaccinated, population, rawDate);
                u.addData(r);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return u;
    }

    // Calculate time range with a date pair
    public static Assignment.showDate calTimeRangeForDatePair(Date start, Date end) {
        long total = end.getTime() - start.getTime();
        int diff = (int) TimeUnit.DAYS.convert(total, TimeUnit.MILLISECONDS) + 1;
        return new Assignment.showDate(start, end, diff);
    }

    // Calculate time range with the start date with the number of days or weeks from it
    public static Assignment.showDate calTimeRangeFromDate(int numDays, int numWeeks, Date parDate) {
        Calendar c = Calendar.getInstance();
        int totalDays = numDays + 7 * numWeeks;
        Date startDate = parDate;
        c.setTime(startDate);
        c.add(Calendar.DATE, totalDays);
        Date endDate = c.getTime();
        return new Assignment.showDate(startDate, endDate, totalDays + 1);
    }

    // Calculate time range with the end date and the number of days or weeks up to it
    public static Assignment.showDate calTimeRangeToDate(int numDays, int numWeeks, Date parDate) {
        Calendar c = Calendar.getInstance();
        int totalDays = numDays + 7 * numWeeks;
        Date endDate = parDate;
        c.setTime(endDate);
        c.add(Calendar.DATE, -totalDays);
        Date startDate = c.getTime();
        return new Assignment.showDate(startDate, endDate, totalDays + 1);
    }

    // Tabular display
    public static void tabularDisplay(Assignment.groupedData g, int resultType, int metric) {
        System.out.println("Range\t\t\t\t\tValue");
        Assignment.ungroupedData u;
        String start = "", end = "";
        ArrayList<Long> result;

        for (int i = 0; i < g.len; i++) {
            u = g.getGroupedData(i);
            start = dateToString(u.getStartDate());
            end = dateToString(u.getEndDate());

            if (resultType == 1) result = g.calculateNewTotalAllGroups(metric);
            else result = g.calculateUpToAllGroups(metric);

            if (start.equals(end)) System.out.println(start + "\t\t\t" + result.get(i));
            else System.out.println(start + "-" + end + "\t\t" + result.get(i));
        }
    }

    // Chart display
    public static void chartDisplay(Assignment.groupedData g, int resultType, int metric) {
        int rows = 23, cols = 80, div;
        int numGroups = g.len;
        ArrayList<Long> result;

        if (resultType == 1) result = g.calculateNewTotalAllGroups(metric);
        else result = g.calculateUpToAllGroups(metric);
        Long myMax = Long.MIN_VALUE, myMin = Long.MAX_VALUE;
        for (Long i : result) {
            if (myMax < i) myMax = i;
            if (myMin > i) myMin = i;
        }

        int scaleX = (int) (cols / numGroups);
        if (scaleX < 1) {
            System.out.println("Can not print because out of index");
        }
        int scaleY = (int) ((myMax - myMin) / rows);

        ArrayList<Assignment.Point> points = new ArrayList<Assignment.Point>();

        for (int i = 1; i <= numGroups; i++) {
            div = (int) ((result.get(i - 1) - myMin) / scaleY);
            points.add(new Assignment.Point(i * scaleX, div));
        }

        boolean flag = false;
        for (int i = 1; i <= rows; i++) {
            System.out.print("|");
            for (int j = 0; j <= cols; j++) {
                flag = false;
                for (Assignment.Point p : points) {
                    if (j == p.x - 1 && i == rows - (p.y - 1)) {
                        System.out.print("*");
                        flag = true;
                    }
                }
                if (!flag) System.out.print(" ");
            }
            System.out.print("\n");
        }
        System.out.print("|");

        int scale = 1;
        for (int i = 1; i <= cols; i++) {
            flag = false;
            if (i == scale * scaleX) {
                if (scale < points.size() && points.get(scale - 1).y == 0) {
                    flag = true;
                    System.out.print("*");
                }
                scale++;
            }
            if (!flag) System.out.print("_");
        }
        System.out.print("\n");

    }

    // Run the whole program
    public static void run(String path) throws ParseException, IOException {
        Assignment.ungroupedData u = processDataFile(path);
        String code;
        int continueOption = 0;
        Date d1;
        Date d2;
        Assignment.showDate s = null;
        int num = 0, numDays, numWeeks;
        Scanner sc;
        Assignment.groupedData g = null;

        while (true) {
            sc = new Scanner(System.in);
            System.out.println("Input a code: ");
            code = sc.nextLine();
            System.out.println("\nTime range options: ");
            System.out.println("\n1. Start date and end date");
            System.out.println("\n2. A number of days or weeks from a particular date");
            System.out.println("\n3. A number of days or weeks to a particular date");
            int timeRangeChoice = 0;
            while (timeRangeChoice != 1 && timeRangeChoice != 2 && timeRangeChoice != 3) {
                System.out.println("Input a type of time range");
                timeRangeChoice = sc.nextInt();
                switch (timeRangeChoice) {
                    case 1:
                        Scanner sc1 = new Scanner(System.in);
                        System.out.println("Format of date is MM/DD/YYYY");
                        System.out.println("Input start date: ");
                        String s1 = sc1.nextLine();
                        System.out.println("Input end date: ");
                        String s2 = sc1.nextLine();
                        d1 = stringToDate(s1);
                        d2 = stringToDate(s2);
                        s = calTimeRangeForDatePair(d1, d2);
                        break;
                    case 2:
                        Scanner sc2 = new Scanner(System.in);
                        System.out.println("Format of date is MM/DD/YYYY");
                        System.out.println("Input a date: ");
                        s1 = sc2.nextLine();
                        d1 = stringToDate(s1);
                        System.out.println("Input number of days: ");
                        numDays = sc2.nextInt();
                        System.out.println("Input number of weeks: ");
                        numWeeks = sc2.nextInt();
                        s = calTimeRangeFromDate(numDays, numWeeks, d1);
                        break;
                    case 3:
                        Scanner sc3 = new Scanner(System.in);
                        System.out.println("Format of date is MM/DD/YYYY");
                        System.out.println("Input a date: ");
                        s1 = sc3.nextLine();
                        d1 = stringToDate(s1);
                        System.out.println("Input number of days: ");
                        numDays = sc3.nextInt();
                        System.out.println("Input number of weeks: ");
                        numWeeks = sc3.nextInt();
                        s = calTimeRangeToDate(numDays, numWeeks, d1);
                        break;
                    default:
                        System.out.println("Invalid time range!");
                        break;
                }
            }
            int groupingChoice = 0;
            System.out.println("Choose a type of grouping: ");
            System.out.println("\n1. No grouping");
            System.out.println("\n2. Number of groups");
            System.out.println("\n3. Number of days");
            while (groupingChoice != 1 && groupingChoice != 2 && groupingChoice != 3) {
                System.out.println("Input a type of grouping");
                groupingChoice = sc.nextInt();
                switch (groupingChoice) {
                    case 1:
                        g = new Assignment.groupedData(u, "no_group", code, num, s.start, s.end, s.timeRange);
                        break;
                    case 2:
                        System.out.println("Input number of groups: ");
                        num = sc.nextInt();
                        g = new Assignment.groupedData(u, "group_by_number_of_groups", code, num, s.start, s.end, s.timeRange);
                        break;
                    case 3:
                        System.out.println("Input number of days: ");
                        num = sc.nextInt();
                        g = new Assignment.groupedData(u, "group_by_number_of_days", code, num, s.start, s.end, s.timeRange);
                        break;
                    default:
                        System.out.println("Invalid grouping options!");
                        break;
                }
            }

            int metric = 0;
            System.out.println("\nChoose a metric");
            System.out.println("\n1. Positive cases");
            System.out.println("\n2. Deaths");
            System.out.println("\n3. People vaccinated");
            while (true) {
                metric = sc.nextInt();
                if (metric == 1 || metric == 2 || metric == 3) break;
                System.out.println("Invalid input, please try again!");
            }

            int resultType = 0;
            System.out.println("Choose a result type: ");
            System.out.println("\n1. Calculating total new results");
            System.out.println("\n2. Calculating total up to results");

            while (true) {
                resultType = sc.nextInt();
                if (resultType == 1 || resultType == 2) break;
                System.out.println("Invalid input, please try again!");
            }

            int displayType = 0;
            System.out.println("Choose a display type: ");
            System.out.println("\n1. Tabular display");
            System.out.println("\n2. Chart display");
            while (true) {
                displayType = sc.nextInt();
                if (displayType == 1 || displayType == 2) break;
                System.out.println("Invalid input, please try again!");
            }

            System.out.println("Here are your result!");
            if (displayType == 1) tabularDisplay(g, resultType, metric);
            else chartDisplay(g, resultType, metric);

            System.out.println("Do you want to continue?");
            System.out.println("\n1. Yes");
            System.out.println("\n2. No");
            while (true) {
                continueOption = sc.nextInt();
                if (continueOption == 1) break;
                if (continueOption == 2) {
                    sc.close();
                    return;
                }
                System.out.println("Invalid input, please try again!");
            }
        }
    }
}

class showDate {
    Date start, end;
    int timeRange;

    public showDate(Date start, Date end, int timeRange) {
        this.start = start;
        this.end = end;
        this.timeRange = timeRange;
    }
}

class rawDataPiece {
    String code, continent, location;
    long newCases, newDeaths, newVaccinated, cases, deaths, vaccinated, population;
    Date rawDate;

    public rawDataPiece(String code, String continent, String location, long newCases, long newDeaths,
                        long newVaccinated, long cases, long deaths, long vaccinated, long population,
                        Date rawDate) {
        this.code = code;
        this.continent = continent;
        this.location = location;
        this.newCases = newCases;
        this.newDeaths = newDeaths;
        this.newVaccinated = newVaccinated;
        this.cases = cases;
        this.deaths = deaths;
        this.vaccinated = vaccinated;
        this.population = population;
        this.rawDate = rawDate;
    }

    public rawDataPiece() {
        this.code = "";
        this.continent = "";
        this.location = "";
        this.newCases = 0L;
        this.newDeaths = 0L;
        this.newVaccinated = 0L;
        this.cases = 0L;
        this.deaths = 0L;
        this.vaccinated = 0L;
        this.population = 0L;
        this.rawDate = null;
    }

    // Display
    public void displayPiece() {
        System.out.println(code + "\t" + rawDate + "\t" + newCases + "\t" +
                newDeaths + "\t" + newVaccinated + "\t" + cases
                + "\t" + deaths + "\t" + vaccinated);
    }
}

class ungroupedData {
    int len;
    ArrayList<Assignment.rawDataPiece> allData;

    public ungroupedData() {
        this.len = 0;
        this.allData = new ArrayList<Assignment.rawDataPiece>();
    }

    // Get the start date
    public Date getStartDate() {
        if (len > 0) return this.allData.get(0).rawDate;
        return null;
    }

    // Get the end date
    public Date getEndDate() {
        if (len > 0) return this.allData.get(len - 1).rawDate;
        return null;
    }

    // Add data
    public void addData(Assignment.rawDataPiece piece) {
        this.allData.add(piece);
        this.len += 1;
    }

    // Get data
    public Assignment.rawDataPiece getDataPiece(int i) {
        if (i < this.len) {
            return allData.get(i);
        }
        return null;
    }

    // Display data
    public void displayData() {
        //System.out.println("code\t\t\tdate\tnewcases\tnewdeaths\tnewvaccinated\tcases\tdeaths\tvaccinated");
        for (Assignment.rawDataPiece r : allData) r.displayPiece();
        System.out.println("Finish printing a group!\n");
    }

    // Calculate total new Covid-19 cases of a group
    public long totalNewCasesResult() {
        long total = 0L;
        for (Assignment.rawDataPiece d : allData) total += d.newCases;
        return total;
    }

    // Calculate total new deaths of a group
    public long totalNewDeathsResult() {
        long total = 0L;
        for (Assignment.rawDataPiece d : allData) total += d.newDeaths;
        return total;
    }

    // Calculate total new vaccinated people of a group
    public long totalNewVaccinatedResult() {
        long total = 0L;
        for (Assignment.rawDataPiece d : allData) total += d.newVaccinated;
        return total;
    }

    // Calculate total Covid-19 cases up to the last day of a group
    public long uptoCasesResult() {
        return allData.get(len - 1).cases;
    }

    // Calculate total deaths up to the last day of a group
    public long uptoDeathsResult() {
        return allData.get(len - 1).deaths;
    }

    // Calculate total vaccinated people up to the last day of a group
    public long uptoVaccinatedResult() {
        return allData.get(len - 1).vaccinated;
    }
}

class groupedData {
    int len;
    ArrayList<Assignment.ungroupedData> myGroupData;
    Assignment.ungroupedData myData;

    public groupedData(Assignment.ungroupedData myData, String type, String code, int num, Date start, Date end, int timeRange) throws IOException {
        myGroupData = new ArrayList<Assignment.ungroupedData>();
        this.myData = myData;
        this.len = 0;
        if (type.equals("no_group")) this.noGroup(code, start, timeRange);
        else if (type.equals("group_by_number_of_groups")) this.groupByNumberOfGroups(code, num, start, end, timeRange);
        else if (type.equals("group_by_number_of_days")) {
            boolean flag = this.groupByNumberOfDays(code, num, start, end, timeRange);
            if (!flag) throw new IOException("Number of days is invalid!\n");
        }
    }

    // Get grouped data
    public Assignment.ungroupedData getGroupedData(int index) {
        return this.myGroupData.get(index);
    }

    // Display grouped data
    public void displayGroupedData() {
        for (Assignment.ungroupedData u : myGroupData) u.displayData();
        System.out.println("Finish printing all groups!\n");
    }

    // Fit data by finding the index of the start date in ArrayList of rawDataPiece objects
    public static int fitData(String code, Date startDate, Assignment.ungroupedData u) {
        int i = 0;
        while (i < u.len) {
            if (code.equals(u.getDataPiece(i).code) && startDate.compareTo(u.getDataPiece(i).rawDate) == 0) {
                break;
            }
            i++;
        }
        return i;
    }

    // No grouping
    public void noGroup(String code, Date start, int timeRange) {
        Assignment.ungroupedData myUngroupedData;
        int i = fitData(code, start, this.myData);
        int j = 0;

        while (j < timeRange) {
            myUngroupedData = new Assignment.ungroupedData();
            myUngroupedData.addData(myData.getDataPiece(i));
            this.myGroupData.add(myUngroupedData);
            this.len++;
            j++;
            i++;
        }
    }

    // Grouping by number of groups
    public void groupByNumberOfGroups(String code, int numGroups, Date start, Date end, int timeRange) {
        int mod = timeRange % numGroups;
        int div = (int) (timeRange / numGroups);
        int counter = 0;
        Assignment.ungroupedData myUngroupedData = new Assignment.ungroupedData();
        int i = fitData(code, start, this.myData);
        int j = 0;

        // Add the equal number of days to each group
        while (j < timeRange - mod) {
            myUngroupedData.addData(myData.getDataPiece(i));
            i++;
            j++;
            counter++;
            if (counter == div) {
                counter = 0;
                this.myGroupData.add(myUngroupedData);
                this.len++;
                myUngroupedData = new Assignment.ungroupedData();
            }
        }

        // Add remaining days to the last group
        while (j < timeRange) {
            Assignment.ungroupedData tmp = this.myGroupData.get(this.len - 1);
            tmp.addData(myData.getDataPiece(i));
            i++;
            j++;
        }
    }

    // Grouping by number of days
    public boolean groupByNumberOfDays(String code, int numDays, Date start, Date end, int timeRange) throws IOException {
        if (timeRange % numDays != 0) {
            return false;
        }
        if (timeRange % numDays == 0) {
            int counter = 0;
            int j = 0;
            Assignment.ungroupedData myUngroupedData = new Assignment.ungroupedData();
            int i = fitData(code, start, this.myData);

            while (j <= timeRange) {
                if (counter == numDays) {
                    counter = 0;
                    this.myGroupData.add(myUngroupedData);
                    this.len++;
                    myUngroupedData = new Assignment.ungroupedData();
                }
                myUngroupedData.addData(myData.getDataPiece(i));
                i++;
                j++;
                counter++;
            }
        }
        return true;
    }

    // Calculate total new result for all groups
    public ArrayList<Long> calculateNewTotalAllGroups(int metric) {
        ArrayList<Long> result = new ArrayList<Long>();
        switch (metric) {
            case 1:
                for (Assignment.ungroupedData group : this.myGroupData) result.add(group.totalNewCasesResult());
                break;
            case 2:
                for (Assignment.ungroupedData group : this.myGroupData) result.add(group.totalNewDeathsResult());
                break;
            case 3:
                for (Assignment.ungroupedData group : this.myGroupData) result.add(group.totalNewVaccinatedResult());
                break;
        }
        return result;
    }

    // Calculate total result up to the last day for all groups
    public ArrayList<Long> calculateUpToAllGroups(int metric) {
        ArrayList<Long> result = new ArrayList<Long>();
        switch (metric) {
            case 1:
                for (Assignment.ungroupedData group : this.myGroupData) result.add(group.uptoCasesResult());
                break;
            case 2:
                for (Assignment.ungroupedData group : this.myGroupData) result.add(group.uptoDeathsResult());
                break;
            case 3:
                for (Assignment.ungroupedData group : this.myGroupData) result.add(group.uptoVaccinatedResult());
                break;
        }
        return result;
    }
}

class Point {
    int x, y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
