package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private Transfer transfer;
    private TransferType transferType;
    private TransferStatus transferStatus;

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

    private AccountService accountService = new AccountService();
    private TransferService transferService = new TransferService();
	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        int currentUserId = currentUser.getUser().getId();
        Account account = accountService.getAccountByUserId(currentUserId);
        System.out.println("Your current balance is: $" + account.getBalance());

	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
        Transfer[] transfers = transferService.listAllTransfers();
        if (transfers != null) {
            consoleService.printTransfers(transfers);
        } else {
            consoleService.printErrorMessage();
        }
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
        Transfer[] pendingTransfers = transferService.listPendingTransfers();

        if (pendingTransfers != null && pendingTransfers.length > 0) {
            consoleService.printPendingTransfers(pendingTransfers);

            int response = consoleService.promptForInt("Enter the Transfer ID to approve/reject (0 to cancel): ");
            if (response != 0) {
                handlePendingTransferResponse(response);
            }
        } else {
            System.out.println("No pending transfer requests.");
        }
	}

    private UserService userService = new UserService();
    public User[] handleListUsers() {
        User[] users = userService.listUsers();
        if (users != null) {
            consoleService.printUsers(users, currentUser);
        } else {
            consoleService.printErrorMessage();
        }
        return users;
    }
	private void sendBucks() {
        // show a list of users
        User[] users = handleListUsers();

        // picking a user_id and storing the User
        int response = consoleService.promptForInt("Please Type in a User_Id to SEND (0 to cancel): ");
        if (response == 0) {
            return;
        } else if (response == currentUser.getUser().getId()){
            System.out.println("Can not send money to self");
            return;
        } else if (userService.getUserById(response) == null) {
            System.out.println("Invalid User-Id");
            return;
        }

        BigDecimal amountToSend = consoleService.promptForBigDecimal("How much would you like to SEND: $ ");
         // Check if the entered number is non-negative
        if (amountToSend.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Please enter a positive number.");
            return;
        }
        // initializing a transferDto to SEND
        // does not include logic to see if user has enough
        TransferDto transferDto = new TransferDto();
        transferDto.setTransferType("Send");
        transferDto.setUserIdFrom(currentUser.getUser().getId());
        transferDto.setUserIdTo(response);
        transferDto.setAmount(amountToSend);
        System.out.println(". . . . processing . . . .");
        boolean isTransferSuccess = transferService.processTransfer(transferDto);
        if (isTransferSuccess) {
            System.out.println("Transfer Approved :)");
        } else {
            System.out.println("Transfer Unsuccessful :(");
        }
	}

	private void requestBucks() {
        User[] users = handleListUsers();

        // Prompt user to select a user to request money from
        int response = consoleService.promptForInt("Please enter the User Id to request money from (0 to cancel): ");
        if (response == 0) {
            return;
        } else if (response == currentUser.getUser().getId()) {
            System.out.println("Cannot request money from yourself.");
            return;
        } else if (userService.getUserById(response) == null) {
            System.out.println("Invalid User Id");
            return;
        }

        // Prompt user for the amount to request
        BigDecimal amountToRequest = consoleService.promptForBigDecimal("How much would you like to request: $ ");

        TransferDto transferDto = new TransferDto();
        transferDto.setTransferType("Request");
        transferDto.setUserIdFrom(currentUser.getUser().getId());
        transferDto.setUserIdTo(response);
        transferDto.setAmount(amountToRequest);

        boolean isRequestSuccess = transferService.processTransfer(transferDto);
        if (isRequestSuccess) {
            System.out.println("Transfer request sent successfully!");
        } else {
            System.out.println("Transfer request failed.");
        }
    }

    private void handlePendingTransferResponse(int transferId) {
        // Prompt the user for approval or rejection
        int response = consoleService.promptForInt("Enter 1 to approve, 2 to reject: ");
        if (response == 1) {
            // Approve the transfer
            if (transferService.approveTransfer(transferId)) {
                System.out.println("Transfer approved successfully!");
            } else {
                System.out.println("Error approving the transfer.");
            }
        } else if (response == 2) {
            // Reject the transfer
            if (transferService.rejectTransfer(transferId)) {
                System.out.println("Transfer rejected successfully!");
            } else {
                System.out.println("Error rejecting the transfer.");
            }
        } else {
            System.out.println("Invalid selection.");
        }

    }

}
