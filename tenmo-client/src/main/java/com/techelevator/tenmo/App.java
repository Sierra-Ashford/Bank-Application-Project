package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService();
    private final UserService userService = new UserService();
    private final TransferService transferService = new TransferService();

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
        userService.setAuthToken(currentUser.getToken());
        userService.setCurrentUser(currentUser.getUser());
        accountService.setAuthToken(currentUser.getToken());
        accountService.setCurrentUser(currentUser.getUser());
        transferService.setAuthToken(currentUser.getToken());
        transferService.setCurrentUser(currentUser.getUser());
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
//                DemoInterface di = new JjDemo();
//                DemoInterface di;
//
//                System.out.println(di.helloWorld());
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        int accountId = userService.getAccountId(currentUser.getUser().getId());
        Account account = accountService.getCurrentBalance(accountId);
        System.out.println("Your current balance is: $" + account.getBalance());
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        Transfer[] transfers = transferService.getTransfers();
        if (transfers.length <= 0) {
            System.out.println("No transfer history at this time.");
            return;
        }
        System.out.println();
        System.out.println("-----------------------------------");
        System.out.println("Transfers");
        System.out.println("ID:      To/From:         Amount:");
        System.out.println("-----------------------------------");
        consoleService.printTransfers(transfers, currentUser);
        System.out.println("-----------------------------------");
        System.out.println();

       int choice = consoleService.promptForInt("Please enter the Transfer ID to see details (0 to cancel): ");

        if (choice == 0) {
            return;
        }
        Transfer transfer = transferService.getTransfersById(choice);
        if (transfer != null) {
            System.out.println();
            System.out.println("-------------------------");
            System.out.println("Transfer Details");
            System.out.println("-------------------------");
            consoleService.printTransferDetails(transfer);
            System.out.println("-------------------------");
        } else {
            System.out.println("Invalid Transfer Id!");
        }

	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
        Transfer[] transfers = transferService.getTransfersByStatus("Pending");
        if (transfers.length <= 0) {
            System.out.println("No pending transfers at this time.");
            return;
        }
        System.out.println();
        System.out.println("-----------------------------------");
        System.out.println("Pending Transfers");
        System.out.println("ID:      To:           Amount:");
        System.out.println("-----------------------------------");
        consoleService.printTransfers(transfers, currentUser);
        System.out.println("-----------------------------------");
        System.out.println();

        int transferId = consoleService.promptForInt("Please enter the Transfer ID to approve/reject (0 to cancel): ");
        if (transferId == 0) {
            return;
        }
        Transfer transfer = transferService.getTransfersById(transferId);
        if (transfer == null) {
            System.out.println("Invalid Transfer Id!");
            return;
        }

        System.out.println();
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or reject");
        System.out.println("-----------------------------------");
        int option = consoleService.promptForInt("Please choose an option: ");
        if (option == 1) {
            TransferStatusDto dto = new TransferStatusDto();
            dto.setTransferStatus("Approved");
            boolean successful = transferService.updateTransferStatus(transferId, dto);
            if (successful) {
                System.out.println("Transfer has been approved!");
            } else {
                System.out.println("Unable to update transfer.");
            }
        } else if (option == 2) {
            TransferStatusDto dto = new TransferStatusDto();
            dto.setTransferStatus("Rejected");
            boolean successful = transferService.updateTransferStatus(transferId, dto);
            if (successful) {
                System.out.println("Transfer has been rejected!");
            } else {
                System.out.println("Unable to update transfer.");
            }
        } else if (option == 0) {
            return;
        } else {
            System.out.println("Invalid selection!");
        }
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
        System.out.println();
        System.out.println("-------------------------");
        System.out.println("Users");
        System.out.println("ID        Name");
        System.out.println("-------------------------");
		User[] users = userService.getUsers();


        consoleService.printUsers(users, currentUser);

        int choice = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
        if (choice == 0) {
            return;
        }
        if (choice == currentUser.getUser().getId()) {
            System.out.println("Invalid selection. Cannot transfer to yourself.");
            return;
        }
        if (userService.getAccountId(choice) == null) {
            System.out.println("Invalid User Id!");
            return;
        }

        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        if (amount.compareTo(new BigDecimal("0")) == -1 || amount.compareTo(new BigDecimal("0")) == 0 ) {
            System.out.println("Invalid amount. Transfer amount must be more than $0.00.");
            return;
        }

        TransferDto transferDto = new TransferDto();
        transferDto.setTransferType("Send");
        transferDto.setUserIdFrom(currentUser.getUser().getId());
        transferDto.setUserIdTo(choice);
        transferDto.setAmount(amount);

        boolean success = transferService.processTransfer(transferDto);
        if (success) {
            System.out.println("Transfer Approved!");
        } else {
            consoleService.printErrorMessage();
            System.out.println("Unable to complete transfer.");
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
        System.out.println();
        System.out.println("-------------------------");
        System.out.println("Users");
        System.out.println("ID        Name");
        System.out.println("-------------------------");
        User[] users = userService.getUsers();
        consoleService.printUsers(users, currentUser);

        int userId = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): ");
        if (userId == 0) {
            return;
        }
        if (userId == currentUser.getUser().getId()) {
            System.out.println("Invalid selection. Cannot request from yourself.");
            return;
        }
        if (userService.getAccountId(userId) == null) {
            System.out.println("Invalid User Id!");
            return;
        }

        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        if (amount.compareTo(new BigDecimal("0")) == -1 || amount.compareTo(new BigDecimal("0")) == 0 ) {
            System.out.println("Invalid amount. Transfer request must be more than $0.00.");
            return;
        }

        TransferDto transferDto = new TransferDto();
        transferDto.setTransferType("Request");
        transferDto.setUserIdFrom(userId);
        transferDto.setUserIdTo(currentUser.getUser().getId());
        transferDto.setAmount(amount);

        boolean success = transferService.processTransfer(transferDto);
        if (success) {
            System.out.println("Transfer request sent!");
        } else {
            consoleService.printErrorMessage();
            System.out.println("Unable to complete request.");
        }
	}

}
