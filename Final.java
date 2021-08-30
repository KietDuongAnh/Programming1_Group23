//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.Scanner;
//import java.util.concurrent.TimeUnit;
//
//public class Final {
//    public static void main(String[] args) throws ParseException, IOException {
//        run("C:\\Users\\Admin\\Desktop\\covid-data.csv");
//        //ungroupedData u = processDataFile("./data.csv");
//        //Date d1 = StringtoDate("3/20/2020");
//        //Date d2 = StringtoDate("4/30/2020");
//        //showDate s = calTimeRangeForDatePair(d1, d2);
//
//        //showDate s = calTimeRangeFromDate(2, 0, d1);
//        //showDate s = calTimeRangeToDate(0, 1, d2);
//        //int timeRange = s.timeRange;
//        //groupedData g = new groupedData(u, "group_by_number_of_groups", "DEU", 3, d1, d2, timeRange);
//        //groupedData g = new groupedData(u, "group_by_number_of_days", "DEU", 6, d1, d2, timeRange);
//        //groupedData g = new groupedData(u, "no_group", "DEU", 5, d1, d2, timeRange);
//        //g.displayGroupedData();
//        // ArrayList<Long> result = g.calculateNewTotalAllGroups(3);
//        // System.out.println(result);
//        //tabularDisplay(g, 2, 2);
//        //chartDisplay(g, 2, 2);
//    }
//    public static void tabularDisplay(groupedData g, int resultType, int metric) {
//        System.out.println("Range\t\t\t\t\tValue");
//        ungroupedData u;
//        String start = "", end = "";
//        ArrayList<Long> result;
//        for (int i = 0; i < g.len; i++)
//        {
//            u = g.getGroupedData(i);
//            start = DatetoString(u.getStartDate());
//            end = DatetoString(u.getEndDate());
//            if (resultType == 1) result = g.calculateNewTotalAllGroups(metric);
//            else result = g.calculateUpToAllGroups(metric);
//            if (start.equals(end)) System.out.println(start + "\t\t\t" + result.get(i));
//            else System.out.println(start + "-" + end + "\t\t" + result.get(i));
//        }
//    }
//    public static void chartDisplay(groupedData g, int resultType, int metric) {
//        int rows = 23, cols = 80, div;
//        int numGroups = g.len;
//        ArrayList<Long> result;
//        if (resultType == 1) result = g.calculateNewTotalAllGroups(metric);
//        else result = g.calculateUpToAllGroups(metric);
//        Long myMax = Long.MIN_VALUE, myMin = Long.MAX_VALUE;
//
//        for (Long i : result)
//        {
//            if (myMax < i) myMax = i;
//            if (myMin > i) myMin = i;
//        }
//
//        int scaleX = (int)(cols / numGroups);
//        if(scaleX < 1){
//            System.out.println("Can not print because out of index");
//        }
//        int scaleY = (int)((myMax - myMin) / rows);
//
//        ArrayList <Point> points = new ArrayList <Point> ();
//
//        for (int i = 1; i <= numGroups; i++)
//        {
//            div = (int)((result.get(i - 1) - myMin) / scaleY);
//            points.add(new Point(i * scaleX, div));
//        }
//
//        boolean flag = false;
//        for (int i = 1; i <= rows; i++)
//        {
//            System.out.print("|");
//            for (int j = 0; j <= cols; j++)
//            {
//                flag = false;
//                for (Point p : points)
//                {
//                    if (j == p.x - 1 && i == rows - (p.y - 1))
//                    {
//                        System.out.print("*");
//                        flag = true;
//                    }
//                }
//                if (!flag) System.out.print(" ");
//            }
//            System.out.print("\n");
//        }
//        System.out.print("|");
//
//        int scale = 1;
//        for (int i = 1; i <= cols; i++)
//        {
//            flag = false;
//            if (i == scale * scaleX)
//            {
//                if (scale < points.size() && points.get(scale - 1).y == 0)
//                {
//                    flag = true;
//                    System.out.print("*");
//                }
//                scale++;
//            }
//            if (!flag) System.out.print("_");
//        }
//        System.out.print("\n");
//
//    }
//    public static String DatetoString(Date d) {
//        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
//        return df.format(d);
//    }
//    public static ungroupedData processDataFile(String line) {
//        String code = "AFG", continent, location;
//        long newCases = 0L, newDeaths = 0L, newVaccinated = 0L, cases = 0L, deaths = 0L, vaccinated = 0L, prevVaccinated=0L, population=0L;
//        Date rawDate;
//        ungroupedData u = new ungroupedData();
//        try {
//            BufferedReader br = new BufferedReader(new FileReader(line));
//            line = br.readLine();
//            while ((line = br.readLine()) != null) {
//                if (line.charAt(line.length() - 1) == ',') line += '0';
//                String[] values = line.split(",");
//                //System.out.println(Arrays.toString(values));
//                continent = values[1];
//                location = values[2];
//                rawDate = StringtoDate(values[3]);
//                if (values[4].equals("")) newCases = 0L;
//                else newCases = Long.parseLong(values[4]);
//                if (values[5].equals("")) newDeaths = 0L;
//                else newDeaths = Long.parseLong(values[5]);
//                if (values[6].equals("")) vaccinated = prevVaccinated;
//                else {
//                    vaccinated = Long.parseLong(values[6]);
//                }
//                if (code.equals(values[0]))
//                {
//                    cases += newCases;
//                    deaths += newDeaths;
//                    newVaccinated = vaccinated - prevVaccinated;
//                }
//                else
//                {
//                    cases = newCases;
//                    deaths = newDeaths;
//                    newVaccinated = vaccinated;
//                    code = values[0];
//                }
//                prevVaccinated = vaccinated;
//                if (values[7].equals("")) population = 0L;
//                else population = Long.parseLong(values[7]);
//                rawDataPiece r = new rawDataPiece(code, continent, location, newCases, newDeaths, newVaccinated,
//                                                    cases, deaths, vaccinated, population, rawDate);
//                u.addData(r);
//            }
//            br.close();
//        } catch (FileNotFoundException e){
//            e.printStackTrace();
//        } catch (IOException e){
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return u;
//    }
//    public static void run(String path) throws ParseException, IOException {
//        ungroupedData u = processDataFile(path);
//        String code;
//        int continueOption = 0;
//        Date d1;
//        Date d2;
//        showDate s = null;
//        int num = 0, numDays, numWeeks;
//        Scanner sc;
//        groupedData g = null;
//
//        while (true)
//        {
//            sc = new Scanner(System.in);
//            System.out.println("Input a code: ");
//            code = sc.nextLine();
//            System.out.println("\nTime range options: ");
//            System.out.println("\n1. Start date and end date");
//            System.out.println("\n2. A number of days or weeks from a particular date");
//            System.out.println("\n3. A number of days or weeks to a particular date");
//            int timeRangeChoice = 0;
//            while (timeRangeChoice != 1 && timeRangeChoice != 2 && timeRangeChoice != 3) {
//                System.out.println("Input a type of time range");
//                timeRangeChoice = sc.nextInt();
//                switch (timeRangeChoice) {
//                    case 1:
//                        Scanner sc1 = new Scanner(System.in);
//                        System.out.println("Format of date is MM/DD/YYYY");
//                        System.out.println("Input start date: ");
//                        String s1 = sc1.nextLine();
//                        System.out.println("Input end date: ");
//                        String s2 = sc1.nextLine();
//                        d1 = StringtoDate(s1);
//                        d2 = StringtoDate(s2);
//                        s = calTimeRangeForDatePair(d1, d2);
//                        break;
//                    case 2:
//                        Scanner sc2 = new Scanner(System.in);
//                        System.out.println("Format of date is MM/DD/YYYY");
//                        System.out.println("Input a date: ");
//                        s1 = sc2.nextLine();
//                        d1 = StringtoDate(s1);
//                        System.out.println("Input number of days: ");
//                        numDays = sc2.nextInt();
//                        System.out.println("Input number of weeks: ");
//                        numWeeks = sc2.nextInt();
//                        s = calTimeRangeFromDate(numDays, numWeeks, d1);
//                        break;
//                    case 3:
//                        Scanner sc3 = new Scanner(System.in);
//                        System.out.println("Format of date is MM/DD/YYYY");
//                        System.out.println("Input a date: ");
//                        s1 = sc3.nextLine();
//                        d1 = StringtoDate(s1);
//                        System.out.println("Input number of days: ");
//                        numDays = sc3.nextInt();
//                        System.out.println("Input number of weeks: ");
//                        numWeeks = sc3.nextInt();
//                        s = calTimeRangeToDate(numDays, numWeeks, d1);
//                        break;
//                    default:
//                        System.out.println("Invalid time range!");
//                        break;
//                }
//            }
//            int groupingChoice = 0;
//            System.out.println("Choose a type of grouping: ");
//            System.out.println("\n1. No grouping");
//            System.out.println("\n2. Number of groups");
//            System.out.println("\n3. Number of days");
//            while (groupingChoice != 1 && groupingChoice != 2 && groupingChoice != 3) {
//                System.out.println("Input a type of grouping");
//                groupingChoice = sc.nextInt();
//                switch (groupingChoice) {
//                    case 1:
//                        g = new groupedData(u, "no_group", code, num, s.start, s.end, s.timeRange);
//                        break;
//                    case 2:
//                        System.out.println("Input number of groups: ");
//                        num = sc.nextInt();
//                        g = new groupedData(u, "group_by_number_of_groups", code, num, s.start, s.end, s.timeRange);
//                        break;
//                    case 3:
//                        System.out.println("Input number of days: ");
//                        num = sc.nextInt();
//                        g = new groupedData(u, "group_by_number_of_days", code, num, s.start, s.end, s.timeRange);
//                        break;
//                    default:
//                        System.out.println("Invalid grouping options!");
//                        break;
//                }
//            }
//
//            int metric = 0;
//            System.out.println("\nChoose a metric");
//            System.out.println("\n1. Positive cases");
//            System.out.println("\n2. Deaths");
//            System.out.println("\n3. People vaccinated");
//            while (true)
//            {
//                metric = sc.nextInt();
//                if (metric == 1 || metric == 2 || metric == 3) break;
//                System.out.println("Invalid input, please try again!");
//            }
//
//            int resultType = 0;
//            System.out.println("Choose a result type: ");
//            System.out.println("\n1. Calculating total results");
//            System.out.println("\n2. Calculating up to results");
//
//            while (true)
//            {
//                resultType = sc.nextInt();
//                if (resultType == 1 || resultType == 2) break;
//                System.out.println("Invalid input, please try again!");
//            }
//
//            int displayType = 0;
//            System.out.println("Choose a display type: ");
//            System.out.println("\n1. Tabular display");
//            System.out.println("\n2. Chart display");
//            while (true)
//            {
//                displayType = sc.nextInt();
//                if (displayType == 1 || displayType == 2) break;
//                System.out.println("Invalid input, please try again!");
//            }
//
//            System.out.println("Here are your result!");
//            if (displayType == 1) tabularDisplay(g, resultType, metric);
//            else chartDisplay(g, resultType, metric);
//
//            System.out.println("Do you want to continue?");
//            System.out.println("\n1. Yes");
//            System.out.println("\n2. No");
//            while (true)
//            {
//                continueOption = sc.nextInt();
//                if (continueOption == 1) break;
//                if (continueOption == 2)
//                {
//                    sc.close();
//                    return;
//                }
//                System.out.println("Invalid input, please try again!");
//            }
//        }
//    }
//    public static Date StringtoDate(String n) throws ParseException {
//        Date d;
//        if (n.length() >= 9 || (n.length() == 8 && n.charAt(1) == '/')){
//            d = new SimpleDateFormat("MM/dd/yyyy").parse(n);
//        }
//        else
//        {
//           d = new SimpleDateFormat("MM-dd-yy").parse(n);
//        } return d;
//    }
//    public static showDate calTimeRangeForDatePair(Date start, Date end){
//        long total = end.getTime() - start.getTime();
//        int diff = (int)TimeUnit.DAYS.convert(total, TimeUnit.MILLISECONDS) + 1;
//        return new showDate(start, end, diff);
//    }
//    public static showDate calTimeRangeFromDate(int numDays, int numWeeks, Date parDate){
//        Calendar c = Calendar.getInstance();
////        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
//        int totalDays = numDays + 7 * numWeeks;
//        Date startDate = parDate;
//        c.setTime(startDate);
//        c.add(Calendar.DATE, totalDays);
//        Date endDate = c.getTime();
//        return new showDate(startDate, endDate, totalDays + 1);
//    }
//    public static showDate calTimeRangeToDate(int numDays, int numWeeks, Date parDate){
//        Calendar c = Calendar.getInstance();
//        int totalDays = numDays + 7 * numWeeks;
//        Date endDate = parDate;
//        c.setTime(endDate);
//        c.add(Calendar.DATE, -totalDays);
//        Date startDate = c.getTime();
//        return new showDate(startDate, endDate, totalDays + 1);
//    }
//}
//class showDate{
//    Date start, end;
//    int timeRange;
//    public showDate(Date start, Date end, int timeRange){
//        this.start = start;
//        this.end = end;
//        this.timeRange = timeRange;
//    }
//}
//class rawDataPiece{
//    String code, continent, location;
//    long newCases, newDeaths, newVaccinated, cases, deaths, vaccinated, population;
//    Date rawDate;
//    public rawDataPiece(String code, String continent, String location, long newCases, long newDeaths,
//                        long newVaccinated, long cases, long deaths, long vaccinated, long population,
//                        Date rawDate)
//    {
//        this.code = code;
//        this.continent = continent;
//        this.location = location;
//        this.newCases = newCases;
//        this.newDeaths = newDeaths;
//        this.newVaccinated = newVaccinated;
//        this.cases = cases;
//        this.deaths = deaths;
//        this.vaccinated = vaccinated;
//        this.population = population;
//        this.rawDate = rawDate;
//    }
//    public rawDataPiece()
//    {
//        this.code = "";
//        this.continent = "";
//        this.location = "";
//        this.newCases = 0L;
//        this.newDeaths = 0L;
//        this.newVaccinated = 0L;
//        this.cases = 0L;
//        this.deaths = 0L;
//        this.vaccinated = 0L;
//        this.population = 0L;
//        this.rawDate = null;
//    }
//    public void displayPiece()
//    {
//        System.out.println(code + "\t" + rawDate + "\t" + newCases + "\t" +
//                            newDeaths + "\t" + newVaccinated + "\t" + cases
//                            + "\t" + deaths + "\t" + vaccinated);
//        //System.out.println(code + "\t" + rawDate + "\t" + newVaccinated+ "\t" + vaccinated);
//    }
//}
//class ungroupedData{
//    int len;
//    ArrayList<rawDataPiece> allData;
//    public ungroupedData(){
//        this.len = 0;
//        this.allData = new ArrayList<rawDataPiece>();
//    }
//    public Date getStartDate() {
//        if (len > 0) return this.allData.get(0).rawDate;
//        return null;
//    }
//    public Date getEndDate() {
//        if (len > 0) return this.allData.get(len - 1).rawDate;
//        return null;
//    }
//    public void addData(rawDataPiece piece){
//        this.allData.add(piece);
//        this.len += 1;
//    }
//    public rawDataPiece getDataPiece(int i){
//        if (i < this.len){
//            return allData.get(i);
//        }
//        return null;
//    }
//    public void displayData(){
//        //System.out.println("code\t\t\tdate\tnewcases\tnewdeaths\tnewvaccinated\tcases\tdeaths\tvaccinated");
//        for (rawDataPiece r : allData) r.displayPiece();
//        System.out.println("Finish printing a group!\n");
//    }
//    public long totalNewCasesResult(){
//        long total = 0L;
//        for (rawDataPiece d : allData) total += d.newCases;
//        return total;
//    }
//    public long totalNewDeathsResult(){
//        long total = 0L;
//        for (rawDataPiece d : allData) total += d.newDeaths;
//        return total;
//    }
//    public long totalNewVaccinatedResult(){
//        long total = 0L;
//        for (rawDataPiece d : allData) total += d.newVaccinated;
//        return total;
//    }
//    public long uptoCasesResult(){
//        return allData.get(len - 1).cases;
//    }
//    public long uptoDeathsResult(){
//        return allData.get(len - 1).deaths;
//    }
//    public long uptoVaccinatedResult(){
//        return allData.get(len - 1).vaccinated;
//    }
//}
//class groupedData{
//    int len;
//    ArrayList<ungroupedData> myGroupData;
//    ungroupedData myData;
//    public groupedData(ungroupedData myData, String type, String code, int num, Date start, Date end, int timeRange) throws IOException{
//        myGroupData = new ArrayList<ungroupedData>();
//        this.myData = myData;
//        this.len = 0;
//        if (type.equals("no_group")) this.noGroup(code, start, timeRange);
//        else if (type.equals("group_by_number_of_groups")) this.groupByNumberOfGroups(code, num, start, end, timeRange);
//        else if (type.equals("group_by_number_of_days")) {
//            boolean flag = this.groupByNumberOfDays(code, num, start, end, timeRange);
//            if (!flag) throw new IOException("Number of days is invalid!\n");
//        }
//    }
//    public ungroupedData getGroupedData(int index) {
//        return this.myGroupData.get(index);
//    }
//    public void displayGroupedData(){
//        for( ungroupedData u : myGroupData) u.displayData();
//        System.out.println("Finish printing all groups!\n");
//    }
//    public static int fitData(String code, Date start, ungroupedData u){
//        int i = 0;
//        while (i < u.len){
//            if (code.equals(u.getDataPiece(i).code) && start.compareTo(u.getDataPiece(i).rawDate) == 0)
//            {
//                break;
//            }
//            i++;
//        }
//        return i;
//    }
//    public void noGroup(String code, Date start, int timeRange){
//        ungroupedData myUngroupedData;
//        int i = fitData(code, start, this.myData);
//        int j = 0;
//        while (j < timeRange){
//            myUngroupedData = new ungroupedData();
//            myUngroupedData.addData(myData.getDataPiece(i));
//            this.myGroupData.add(myUngroupedData);
//            this.len++;
//            j++;
//            i++;
//        }
//    }
//    public void groupByNumberOfGroups(String code, int numGroups, Date start, Date end, int timeRange){
//        int mod = timeRange % numGroups;
//        int div = (int)(timeRange / numGroups);
//        int counter = 0;
//        ungroupedData myUngroupedData = new ungroupedData();
//        int i = fitData(code, start, this.myData);
//        int j = 0;
//        while (j < timeRange - mod){
//            myUngroupedData.addData(myData.getDataPiece(i));
//            i++;
//            j++;
//            counter++;
//            if (counter == div){
//                counter = 0;
//                this.myGroupData.add(myUngroupedData);
//                this.len++;
//                myUngroupedData = new ungroupedData();
//            }
//        }
//        while (j < timeRange){
//            ungroupedData tmp = this.myGroupData.get(this.len - 1);
//            tmp.addData(myData.getDataPiece(i));
//            i++;
//            j++;
//        }
//    }
//    public boolean groupByNumberOfDays(String code, int numDays, Date start, Date end, int timeRange) throws IOException{
//        if(timeRange % numDays != 0) {
//            return false;
//        }
//        if (timeRange % numDays == 0){
//            int counter = 0;
//            int j = 0;
//            ungroupedData myUngroupedData = new ungroupedData();
//            int i = fitData(code, start, this.myData);
//            while (j <= timeRange){
//                if (counter == numDays) {
//                    counter = 0;
//                    this.myGroupData.add(myUngroupedData);
//                    this.len++;
//                    myUngroupedData = new ungroupedData();
//                }
//                myUngroupedData.addData(myData.getDataPiece(i));
//                i++;
//                j++;
//                counter++;
//            }
//        } return true;
//    }
//    public ArrayList<Long> calculateNewTotalAllGroups(int metric)
//    {
//        ArrayList<Long> result = new ArrayList<Long>();
//        switch(metric)
//        {
//            case 1:
//                for (ungroupedData group : this.myGroupData) result.add(group.totalNewCasesResult());
//                break;
//            case 2:
//                for (ungroupedData group : this.myGroupData) result.add(group.totalNewDeathsResult());
//                break;
//            case 3:
//                for (ungroupedData group : this.myGroupData) result.add(group.totalNewVaccinatedResult());
//                break;
//        }
//        return result;
//    }
//    public ArrayList<Long> calculateUpToAllGroups(int metric)
//    {
//        ArrayList<Long> result = new ArrayList<Long>();
//        switch(metric)
//        {
//            case 1:
//                for (ungroupedData group : this.myGroupData) result.add(group.uptoCasesResult());
//                break;
//            case 2:
//                for (ungroupedData group : this.myGroupData) result.add(group.uptoDeathsResult());
//                break;
//            case 3:
//                for (ungroupedData group : this.myGroupData) result.add(group.uptoVaccinatedResult());
//                break;
//        }
//        return result;
//    }
//}
//class Point
//{
//    int x, y;
//    Point(int x, int y) {
//        this.x = x;
//        this.y = y;
//    }
//}
