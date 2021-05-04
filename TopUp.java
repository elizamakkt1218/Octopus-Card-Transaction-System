import Util.Tokenizer;

import java.text.ParseException;

public class TopUp extends OCTransaction{
    private String topUpType;
    private String agent;
    public static final String TypeHdrStr = "TopUp";

    public TopUp(String dateTimeStr, String transactionID, String amountStr, String topUpType,
                 String agent) throws OCTransactionFormatException , ParseException {
        super(TypeHdrStr, dateTimeStr, transactionID, amountStr);
        this.topUpType = topUpType;
        this.agent = agent;
        setStatus(Status.COMPLETED);
    }

    public String getTopUpType() {
        return topUpType;
    }

    public String getAgent() {
        return agent;
    }

    public String toRecord(){
        return getType() + " " + getDate() + " " + getTransactionID() + " " + getAmount() + " " + getTopUpType()
                + ", " + getAgent();
    }

    public boolean match(String[] criteria)throws ParseException, OCTransactionSearchException{
        if (criteria[0].equalsIgnoreCase(getTopUpType())){
            return true;
        } else if (criteria[0].equalsIgnoreCase(getTopUpType()) && criteria[1].equalsIgnoreCase(getAgent())){
            return true;
        }else if (criteria[0].equalsIgnoreCase("date") && matchDate(criteria[1])) {
            return true;
        }
        return false;
    }

    public static TopUp parseTransaction(String record)throws OCTransactionFormatException, ParseException{
        String [] tokens = Tokenizer.getTokens(record);

        if (tokens.length < 6){
            throw new OCTransactionFormatException("Invalid number of arguments.");
        }

        String dateTimeStr = tokens[1];
        String transactionID = tokens[2];
        String amountStr = tokens[3];
        String topUpType = tokens[4];
        String agent = "";
        for (int i = 5; i < tokens.length; i++){
            agent += tokens[i];
            agent += " ";
        }
        return new TopUp(dateTimeStr, transactionID, amountStr, topUpType, agent);
    }

    public String toString(){
        String output = "";
        output += "  [Top Up]" +
                "\n    Agent: " + getAgent() +
                "\n    Type: " + getTopUpType() +
                "\n"+ super.toString();
        return output;
    }
}
