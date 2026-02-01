package ui;

import java.util.Scanner;
import java.util.UUID;

public class InputValidator {

     public int readInt(Scanner scanner) {
          while (true) {
               try {
                    return Integer.parseInt(scanner.nextLine());
               } catch (NumberFormatException e) {
                    System.out.print("Введите корректное число: ");
               }
          }
     }

     public int readIndex(Scanner scanner, int max) {
          while (true) {
               try {
                    int value = Integer.parseInt(scanner.nextLine());
                    if (value >= 1 && value <= max) {
                         return value - 1;
                    }
                    System.out.printf("Введите число от 1 до %d: ", max);
               } catch (NumberFormatException e) {
                    System.out.print("Введите корректное число: ");
               }
          }
     }


     public UUID readUUID(Scanner scanner) {
          while (true) {
               try {
                    return UUID.fromString(scanner.nextLine().trim());
               } catch (IllegalArgumentException e) {
                    System.out.print("Введите корректный ид: ");
               }
          }
     }

     public String readEmail(Scanner scanner) {
          String emailRegex = "^(?i)[\\p{L}0-9._%+-]+@[\\p{L}0-9.-]+\\.[\\p{L}]{2,}$";

          while (true) {
               System.out.print("Введите email: ");
               String input = scanner.nextLine().trim();

               if (input.matches(emailRegex)) {
                    return input;
               } else {
                    System.out.print("Введите корректный email (пример: max2БА@яндекс.com): ");
               }
          }
     }
}

