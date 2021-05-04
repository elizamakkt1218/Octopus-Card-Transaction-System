import Util.Tokenizer;

import java.text.ParseException;

public class Retail extends OCTransaction{
    private String retailer;
    private String description;
    public static final String TypeHdrStr = "Retail";

    public Retail(String dateTimeStr, String transactionID, String amountStr, String retailer,
                   String description) throws OCTransactionFormatException, ParseException {
        super(TypeHdrStr, dateTimeStr, transactionID, amountStr);
        this.retailer = retailer;
        this.description = description;
        setStatus(Status.COMPLETED);
    }

    public String getRetailer() {
        return retailer;
    }

    public String getDescription() {
        return description;
    }

    public String toRecord(){
        return getType() + " " + getDate() + " " + getTransactionID() + " " + getAmount() + " " + getRetailer()
                + ", " + getDescription();
    }

    public boolean match(String[] criteria)throws ParseException, OCTransactionSearchException{
        if (criteria[0].equalsIgnoreCase("retailer") && criteria[1].equals(getRetailer())){
            return true;
        }else if (criteria[0].equalsIgnoreCase("description") && criteria[1].equals(getDescription())){
            return true;
        } else if (criteria[0].equalsIgnoreCase("date") && matchDate(criteria[1])){
            return true;
        }else if (criteria.length < 2){
            throw new OCTransactionSearchException("search: Error: Invalid number of arguments.");
        }
        return false;
    }

    public static Retail parseTransaction(String record)throws OCTransactionFormatException, ParseException{
        String [] tokens = Tokenizer.getTokens(record);

        if (tokens.length < 6){
            throw new OCTransactionFormatException("Invalid number of arguments.");
        }

        String dateTimeStr = tokens[1];
        String transactionID = tokens[2];
        String amountStr = tokens[3];
        String retailer = "";
        String description = "";
        int temp = 0;
        for (int i = 4; i < tokens.length; i++) {
            for (int index = 0; index < tokens[i].length(); index++) {
                char c = tokens[i].charAt(index);
                if (c != ',') {
                    retailer += c;
                } else {
                    temp = i;
                    break;
                }
            }
            if (temp == i){
                break;
            }
            retailer += " ";
        }

        for (int j = temp + 1; j < tokens.length; j++){
            for (int index = 0; index < tokens[j].length(); index++) {
                char c = tokens[j].charAt(index);
                description += c;
            }
            description += " ";
        }
        return new Retail(dateTimeStr, transactionID, amountStr, retailer, description);
    }

    public String toString(){
        String output = "";
        output += "  [Retail]" +
                "\n    Retailer: " + getRetailer() +
                "\n    Description: " + getDescription() +
                "\n" + super.toString();
        return output;
    }
}
