// 241RDB050 Anastasija Voropajeva 2
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);
    static String filename = "db.csv";
    static LinkedList<Data> database = new LinkedList<Data>();
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static class Data {
        private int id;
        private String city;
        private LocalDate date;
        private int days;
        private double price;
        private String vehicle;

        public Data(int id, String city, LocalDate date, int days, double price, String vehicle) {
            this.id = id;
            this.city = city;
            this.date = date;
            this.days = days;
            this.price = price;
            this.vehicle = vehicle;
        }

        public int getId() { return id; }
        public String getCity() { return city; }
        public LocalDate getDate() { return date; }
        public int getDays() { return days; }
        public double getPrice() { return price; }
        public String getVehicle() { return vehicle; }
    }
    public static void main(String[] args) {
        loop: while (true) {
            String userInput = sc.nextLine();
            String[] splitInput = userInput.split(" ", 2);
            String cmd = splitInput[0];
            
            read();

            switch (cmd) {
                case "exit":
                    break loop;
                case "print":
                    print();
                    break;
                case "add":
                try {
                    String[] arguments = handleArguments(userInput);
                    if (arguments.length != 6) {
                        System.out.println("wrong field count");
                        break; 
                    }
                    boolean hasEmpty = false;
                    for (int i = 0; i < arguments.length; i++){
                        if (arguments[i].isEmpty()){
                            System.out.println("wrong field count");
                            hasEmpty=true;
                            break;
                        } 
                    }
                    if (hasEmpty==true){
                        break;
                    }
                    int id = validateID(arguments[0]);
                    if (id == -1){
                        break;
                    }

                    String city = validateCity(arguments[1]);

                    String date = validateDate(arguments[2]);
                    if (date == "!"){
                        break;
                    }

                    int days = validateDays(arguments[3]);
                    if (days == -2){
                        break;
                    }

                    double price = validatePrice(arguments[4]);
                    if (price == -2){
                        break;
                    }

                    String vehicle = validateVehicle(arguments[5]);
                    if (vehicle == "!"){
                        break;
                    }
                    
                    add(id, city, date, days, price, vehicle);
                } catch (Exception ex){
                    System.out.println("oops, seems like something went wrong *_*");
                    System.out.println(ex.getMessage());
                }
                    break;
                case "del":
                    try {
                        if (splitInput.length != 2) {
                            System.out.println("wrong id");
                            break;
                        }
                        int target = Integer.parseInt(splitInput[1]);
                        del(target);
                    } catch (NumberFormatException ex){
                        System.out.println("wrong id");
                    }
                    break;
                case "edit":
                    String[] arguments = handleArguments(userInput);

                    if (arguments.length != 6) {
                        System.out.println("wrong field count");
                        for (int i=0; i < arguments.length; i++){
                            System.out.println(arguments[i]);
                        }
                        break; 
                    }

                    // id gets here special treatment
                    int id =0;
                    boolean found = false;
                    try {
                        id = Integer.parseInt(arguments[0]);
                        for (Data record : database){
                            if (record.id==id){
                                found=true;
                            }
                        }
                    } catch (Exception ex){
                        System.out.println("wrong id");
                        break;
                    }
                    if (found==false){
                        System.out.println("wrong id");
                        break;
                    }
                    String city = validateCity(arguments[1]);
                    String date = validateDate(arguments[2]);
                    if (date == "!"){
                        break;
                    }
                    int days = validateDays(arguments[3]);
                    if (days == -2){
                        break;
                    }
                    double price = validatePrice(arguments[4]);
                    if (price == -2){
                        break;
                    }
                    String vehicle = validateVehicle(arguments[5]);
                    if (date == "!"){
                        break;
                    }
         
                    edit(id, city, date, days, price, vehicle);

                    break;
                case "find":
                    try {
                        if (splitInput.length != 2) {
                            System.out.println("wrong price");
                            break;
                        }
                        price = Double.parseDouble(splitInput[1]);
                        find(price);
                    } catch (NumberFormatException ex) {
                        System.out.println("wrong price");
                    }
                    break;
                case "avg":
                    avg();
                    break;
                case "sort":
                    sort();
                    break;
                default:
                    System.out.println("wrong command");
                    break;
            }
        }
        sc.close();
    }

    //File processing!
    public static void read(){
        String line;
        String[] splitLine = null;
        Data data;
        database.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            while ((line = br.readLine()) != null) {
                splitLine = line.split(";");
                int id = Integer.parseInt(splitLine[0]);
                String city = splitLine[1];
                LocalDate date = LocalDate.parse(splitLine[2], formatter);
                int days = Integer.parseInt(splitLine[3]);
                double price = Double.parseDouble(splitLine[4]);
                String vehicle = splitLine[5];
                data = new Data(id, city, date, days, price, vehicle);
                database.add(data);
            }
        } catch (IOException ex){
            System.out.println(ex.getMessage());
            return;
       } 
    }
    public static void write(){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))){
            for (Data record : database) {
                String formattedDate = record.getDate().format(formatter);
                bw.write(String.format("%d;%s;%s;%d;%.2f;%s\n", record.getId(), record.getCity(), formattedDate, record.getDays(), record.getPrice(), record.getVehicle()));
            }
        } catch (IOException ex){
            System.out.println(ex.getMessage());
            return; 
        }
    }
    //Handle User input!
    public static String[] handleArguments(String userInput) {
        String[] splitInput = userInput.split(" ", 2);
        if (splitInput.length < 2) {
            return new String[0];
        }
        String[] arguments = splitInput[1].split(";", -1);
        return arguments;
    }
    
    //Main functionality!
    public static void print(){
       System.out.println("------------------------------------------------------------");
       System.out.printf("%-4s%-21s%-11s%6s%10s%-8s\n", "ID", "City", "Date", "Days", "Price", " Vehicle");
       System.out.println("------------------------------------------------------------");
        for (Data record : database) {       
            String formattedDate = record.getDate().format(formatter);
            System.out.printf("%-4s%-21s%-11s%6s%10.2f %-8s\n", record.getId(), record.getCity(), formattedDate, record.getDays(), record.getPrice(), record.vehicle);
        }
        System.out.println("------------------------------------------------------------");
    }

    public static void sort() {
        database.sort(Comparator.comparing(Data::getDate));
        write();
        System.out.println("sorted");
    }

    public static void find(double price) {
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-4s%-21s%-11s%6s%10s%-8s\n", "ID", "City", "Date", "Days", "Price", " Vehicle");
        System.out.println("------------------------------------------------------------");
        for (Data record : database) {
            if (record.getPrice() <= price) {
                String formattedDate = record.getDate().format(formatter);
                System.out.printf("%-4s%-21s%-11s%6s%10.2f %-8s\n", record.getId(), record.getCity(), formattedDate, record.getDays(), record.getPrice(), record.vehicle);   
            }
        }
        System.out.println("------------------------------------------------------------");
    }

    public static void avg(){
        double sum = 0;
        for (Data record: database){
            sum += record.getPrice();
        }
        double average = sum / database.size();
        System.out.printf("average=%.2f\n", average);
    }

    public static void del(int target){
        int cnt = 0;
        boolean exists = false;
        for (Data record: database) {
            if (record.getId()==target){
                exists = true;
                break;
            }
            cnt++;
        }
        if (exists == false) {
            System.out.println("wrong id");
            return;
        } else {
            database.remove(cnt);
            write();
            System.out.println("deleted");
        }
    }

    public static void add(int id, String city, String date, int days, double price, String vehicle) {
        Data newData;
        LocalDate formatteddate = LocalDate.parse(date, formatter);
        newData = new Data(id, city, formatteddate, days, price, vehicle);
        database.add(newData);
        write();
        System.out.println("added");
    }

    public static void edit(int id, String city, String date, int days, double price, String vehicle) {
        for (Data record: database) {
            if (record.getId() == id){               
                
                if (!city.isEmpty()) record.city = city;
                if (days != -1) record.days = days;
                if (price != -1) record.price = price;
                if (!vehicle.isEmpty()) record.vehicle = vehicle;
                if (date != null && !date.isEmpty()){
                    LocalDate formattedDate = LocalDate.parse(date, formatter);
                    record.date = formattedDate;
                }
            }
        }
        write();
        System.out.println("changed");
    }    
    
    // User Input validation!
    public static int validateID(String idstr){
        int id=0;
        try {
            id = Integer.parseInt(idstr);
        } catch (Exception ex){
            System.out.println("wrong id");
            return -1;
        }
        boolean free = true;
        for (Data records: database){
            if (records.getId()==id){
                free = false;
            }
        }
        if (free == false || id/100.0 <= 1.0){
            System.out.println("wrong id");
            return -1;
        }
        return id;
    }
    public static String validateCity(String city){
        if (city == null ||city.isEmpty()){
            return "";
        }
        city = city.toLowerCase();
        char[] formattedCity = city.toCharArray();
        formattedCity[0] = Character.toUpperCase(formattedCity[0]);   
        city = new String(formattedCity);
        return city;
    }
    public static String validateDate(String date){
        if (date == null||date.isEmpty()){
            return "";
        }
        if (!date.matches("^(0[1-9]|[12][0-9]|3[01])[\\/](0[1-9]|1[012])[\\/]\\d{4}$")) {// no leap year check 
            System.out.println("wrong date");// thats terrible, i could've format missing zero in date myself instead of worsening someones day
            return "!";
        }
        return date;
    }
    public static int validateDays(String daystr){
        if (daystr == null||daystr.isEmpty()){
            return -1;
        }
        if (!daystr.matches("\\d+")) {
            System.out.println("wrong day count");
            return -2;
        }
        int days = Integer.parseInt(daystr);
        return days;

    }
    public static double validatePrice(String pricestr){
            if (pricestr == null||pricestr.isEmpty()){
                return -1;
            }
            if (!pricestr.matches("^\\d{1,}(\\.\\d{1,})?$")){
                System.out.println("wrong price");
                return -2;
            }
            double price = Double.parseDouble(pricestr);
            return price;
    }
    public static String validateVehicle(String vehicle){
        if (vehicle == null||vehicle.isEmpty()){
            return "";
        }
        if (!vehicle.equalsIgnoreCase("PLANE")&&!vehicle.equalsIgnoreCase("BUS") && !vehicle.equalsIgnoreCase("BOAT") && !vehicle.equalsIgnoreCase("TRAIN")) {
            System.out.println("wrong vehicle");
            return "!";
        }
        vehicle = vehicle.toUpperCase();
        return vehicle;
    }
}