package buddyapp.com;

/**
 * Created by titech on 6/9/17.
 */

public class ContactModel {
    private String name;
    private String number;

    public ContactModel(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return this.name;
    }

    public String getNumber() {
        return this.number;
    }

}