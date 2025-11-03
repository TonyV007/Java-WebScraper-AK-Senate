package org.example;

public class Senator {

    // These are all the fields the company asked for
    String name;
    String title;
    String position; // This is the field we're fixing
    String party;
    String address;
    String phone;
    String email; // We'll keep this field, even though it will be null
    String url;

    // We can update the toString() method to see everything
    @Override
    public String toString() {
        return "Senator{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", position='" + position + '\'' +
                ", party='" + party + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}