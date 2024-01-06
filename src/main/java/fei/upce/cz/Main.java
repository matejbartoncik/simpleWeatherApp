package fei.upce.cz;


import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner user_input = new Scanner(System.in);

        do {
            System.out.print("Zadejte lokaci kde chcete zjistit počasí: ");
            String userLocation = user_input.nextLine();
            WeatherApp pocasi = new WeatherApp(userLocation);
            System.out.print("Chcete zjistit počasí v jiné lokaci? (ano/ne)");
            String odpoved = user_input.nextLine();
            if (odpoved.equals("ne")) {
                System.out.println("Ukončuji program...");
                System.out.println("Program ukončen");
                break;
            }
            if (!odpoved.equals("ano")) {
                System.out.println("Neplatná odpoveď");
            }
        } while (true);
    }


}

