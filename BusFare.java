import Util.Tokenizer;

import java.text.ParseException;

public class BusFare extends OCTransaction{
    private String route;
    private String station;
    private String terminal;
    public static final String TypeHdrStr = "BusFare";

    public BusFare(String dateTimeStr, String transactionID, String amountStr, String route,
               String station, String terminal) throws OCTransactionFormatException,  ParseException {
        super(TypeHdrStr, dateTimeStr, transactionID, amountStr);
        this.route = route;
        this.station = station;
        this.terminal = terminal;
        setStatus(Status.COMPLETED);
    }

    public String getRoute() {
        return route;
    }

    public String getStation() {
        return station;
    }

    public String getTerminal() {
        return terminal;
    }

    public String toRecord(){
        return getType() + " " + getDate() + " " + getTransactionID() + " " + getAmount() + " " + getRoute()
                + " " + getStation() + " to " + getTerminal();
    }

    public boolean match(String[] criteria) throws ParseException, OCTransactionSearchException{
        if (criteria[0].equalsIgnoreCase("route") && criteria[1].equals(getRoute())){
            return true;
        }else if (criteria[0].equalsIgnoreCase("station") && criteria[1].equals(getStation())){
            return true;
        }else if (criteria[0].equalsIgnoreCase("terminal") && criteria[1].equals(getTerminal())){
            return true;
        }else if (criteria[0].equalsIgnoreCase("date") && matchDate(criteria[1])){
            return true;
        }else if (criteria.length < 2){
            throw new OCTransactionSearchException("search: Error: Invalid number of arguments.");
        }
        return false;
    }

    public static BusFare parseTransaction(String record)throws OCTransactionFormatException, ParseException{
        String [] tokens = Tokenizer.getTokens(record);

        if (tokens.length < 7){
            throw new OCTransactionFormatException("Invalid number of arguments.");
        }

        String dateTimeStr = tokens[1];
        String transactionID = tokens[2];
        String amountStr = tokens[3];
        String route = tokens[4];
        String station = "";
        String terminal = "";
        int temp = 0;
        for (int i = 5; i < tokens.length; i++){
            if (!(tokens[i].equals("to"))){
                station += tokens[i];
                station += " ";
            }else{
                temp = i;
                break;
            }
        }
        for (int j = temp + 1; j < tokens.length; j++){
            terminal += tokens[j];
        }
        return new BusFare(dateTimeStr, transactionID, amountStr, route ,station, terminal);
    }

    public String toString(){
        String output = "";
        output += "  [Bus Fare]" +
                "\n    Route: " + getRoute() +
                "\n    Terminal: " + getTerminal() +
                "\n    Station: " + getStation() +
                "\n" + super.toString();
        return output;
    }
}
