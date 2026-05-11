package app.entities;

public class User {
    private int userId;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private int phoneNumber;
    private int postalCode;
    private double balance;

    public User(int userId, String email, String password, String firstName, String lastName,
                String address, int phoneNumber, int postalCode, double balance) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.postalCode = postalCode;
        this.balance = balance;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public double getBalance() {
        return balance;
    }
}




