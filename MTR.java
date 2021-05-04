import Util.Tokenizer;

import java.text.ParseException;

public class MTR extends OCTransaction{
    private MTRType mtrType;
    private String station;
    public static final String TypeHdrStr = "MTR";
    private MTR matchingTransaction;

    public MTR(String dateTimeStr, String transactionID, String amountStr, String mtrType,
               String station) throws OCTransactionFormatException , ParseException {
        super(TypeHdrStr, dateTimeStr, transactionID, amountStr);
        this.station = station;
        if (mtrType.equalsIgnoreCase("checkIn")){
            this.mtrType = MTRType.checkIn;
        }else if (mtrType.equalsIgnoreCase("checkOut")){
            this.mtrType = MTRType.checkOut;
        }
        setStatus(Status.MTR_OUTSTANDING);
    }

    enum MTRType{
        checkIn,
        checkOut
    }

    public MTR getMatching(MTR checkInTransaction){
        this.matchingTransaction = checkInTransaction;
        return matchingTransaction;
    }

    public MTRType getMtrType() {
        return mtrType;
    }

    public String getStation() {
        return station;
    }

    public String toRecord(){
        return getType() + " " + getDate() + " " + getTransactionID() + " " + getAmount() + " " + getMtrType()
                + " " + getStation();
    }
    public boolean match(String[] criteria)throws ParseException, OCTransactionSearchException{
        if (criteria[0].equalsIgnoreCase("station") && criteria[1].equalsIgnoreCase(getStation())){
            return true;
        }else if (criteria[0].equalsIgnoreCase("mtrType") && criteria[1].equalsIgnoreCase(getMtrType().toString())){
            return true;
        }else if (criteria[0].equalsIgnoreCase("status") && criteria[1].equals(getStatus().toString())){
            return true;
        }else if (criteria[0].equalsIgnoreCase("date") && matchDate(criteria[1])){
            return true;
        }else if (criteria.length < 2){
            throw new OCTransactionSearchException("search: Error: Invalid number of arguments.");
        }
        return false;
    }

    public static MTR parseTransaction(String record)throws OCTransactionFormatException, ParseException{
        String [] tokens = Tokenizer.getTokens(record);

        if (tokens.length < 6){
            throw new OCTransactionFormatException("Invalid number of arguments.");
        }

        String dateTimeStr = tokens[1];
        String transactionID = tokens[2];
        String amountStr = tokens[3];
        String mtrType = tokens[4];
        String station = "";
        for (int i = 5; i < tokens.length; i++){
            station += tokens[i];
            station += " ";
        }
        return new MTR(dateTimeStr, transactionID, amountStr, mtrType,station);
    }

    public String toString(){
        String output = "";
        output += "  [MTR Transaction]";
        if (this.getMtrType().equals(MTRType.checkIn)){
            output += "\n    MTR Type: CheckIn";
        }else{
            output += "\n    MTR Type: CheckOut";
        }
        if (matchingTransaction != null) {
            output += "\n    Matching TransactionID: " + matchingTransaction.getTransactionID() +
                    "\n      Date/Time: " + matchingTransaction.getDateStr() + " at " + matchingTransaction.getTimeStr() +
                    "\n       Station: " + matchingTransaction.getStation();
        }else{
            output += "\n    Matching TransactionID: OUTSTANDING!";
        }
        output += "\n     Station: " + this.getStation() + "\n" + super.toString();
        return output;
    }
}
