package com.techelevator.tenmo.services;


import com.techelevator.tenmo.App;
import com.techelevator.tenmo.model.*;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

//    public BigDecimal promptForBigDecimal(String prompt) {
//        System.out.print(prompt);
//        while (true) {
//            try {
//                return new BigDecimal(scanner.nextLine());
//            } catch (NumberFormatException e) {
//                System.out.println("Please enter a decimal number.");
//            }
//        }
//    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }
//    public BigDecimal promptForBigDecimal(String prompt) {
//        System.out.print(prompt);
//        while (true) {
//            try {
//                BigDecimal userInput = new BigDecimal(scanner.nextLine());
//
//                // Check if the entered number is non-negative
//                if (userInput.compareTo(BigDecimal.ZERO) >= 0) {
//                    return userInput;
//                } else {
//                    System.out.println("Please enter a non-negative number.");
//                }
//            } catch (NumberFormatException e) {
//                System.out.println("Please enter a valid number.");
//            }
//        }
//    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }
    public void printUsers(User[] users, AuthenticatedUser authenticatedUser) {
        System.out.println("------------------");
        System.out.println("Id  : Username");
        System.out.println("------------------");
        App app = new App();
        for (User user : users) {
            if (user.getId() != authenticatedUser.getUser().getId())
            System.out.println(user.getId() + ": " + user.getUsername());
        }
        System.out.println();
    }
    public void printTransfers(Transfer[] transfers) {
        System.out.println("------------------");
        System.out.println("Transfer History");
        System.out.println("------------------");
        String transactionType;
        TransferType transferType;
        for (Transfer transfer : transfers) {
            if (transfer.getTransferTypeId() == 1) {
                transactionType = "Request";
            } else {
                transactionType = "Send";
            }
            System.out.println("From: " + transfer.getAccountFrom() +
                    " To: " + transfer.getAccountTo() + " || Transaction Type: " + transactionType + " || Amount: " + transfer.getAmount());
        }
        System.out.println();
    }
    public void printPendingTransfers(Transfer[] transfers, AuthenticatedUser authenticatedUser) {
        AccountService account = new AccountService();
        int userId = authenticatedUser.getUser().getId();
        Account account1 = account.getAccountByUserId(userId);
        int accountId = account1.getId();
        System.out.println("----------------------------------");
        System.out.println("Requested Payment From Others");
        System.out.println("----------------------------------");
        for (Transfer transfer : transfers) {
            if (transfer.getAccountTo() == accountId) {
                System.out.println(transfer.getAccountFrom() + " has requested $"
                                    + transfer.getAmount() + " from you");
            }
        }
        System.out.println("----------------------------------");
        System.out.println("Requested Payment From You");
        System.out.println("----------------------------------");
        for (Transfer transfer : transfers) {
            if (transfer.getAccountTo() != account.getAccountByUserId(userId).getId()) {
                System.out.println("You have requested $"
                        + transfer.getAmount() + " to " + transfer.getAccountTo());
            }
        }
        System.out.println();
    }

}
