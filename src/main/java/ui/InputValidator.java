package ui;

import java.util.Scanner;

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

