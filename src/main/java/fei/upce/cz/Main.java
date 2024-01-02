package fei.upce.cz;



import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner user_input = new Scanner(System.in);
        System.out.println("Zadejte lokaci kde chcete zjistit počasí:");
        String pocasko = user_input.nextLine();
        WeatherApp pocasi = new WeatherApp(pocasko);
        pocasi.APICallCords();
        pocasi.APICallWeather();
    }
}
