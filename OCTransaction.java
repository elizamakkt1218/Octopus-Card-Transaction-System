/*Mak Kwan Ting
COMP2026- Assignment 3: OCTransaction System
 */

import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import Util.*;

public class OCTransaction {
    private final Date date;
    private final String transactionID;
    private final double amount;
    private final String type;
    private Status status;

    //============================================================
    // Constructors
    public OCTransaction(String type, String dateTimeStr, String transactionID, String amountStr) throws OCTransactionFormatException, ParseException{
        this(type, parseDateTimeStr(dateTimeStr), transactionID, parseAmountStr(amountStr));
    }

    public OCTransaction(String type, Date date, String transactionID, double amount) throws OCTransactionFormatException {
        this.type = type;
        this.date = date;
        this.transactionID = transactionID;
        this.amount = amount;

        // chk type
        if (!typeIsValid(this.type)) {
            throw new OCTransactionFormatException("Invalid transaction type: " + this.type);
        }
    }

    //============================================================
    // parseTransaction: type dateTime transactionID amount...
    public static OCTransaction parseTransaction(String record) throws OCTransactionFormatException, ParseException {
        String [] tokens = Tokenizer.getTokens(record);

        // chk for blank line (no tokens)
        if (tokens.length == 0) {
            return null;
        }

        // chk transaction type
        String transactionType = tokens[0];
        if (transactionType.equalsIgnoreCase(MTR.TypeHdrStr)) {
            return MTR.parseTransaction(record);
        } else if (transactionType.equalsIgnoreCase(BusFare.TypeHdrStr)) {
            return BusFare.parseTransaction(record);
        } else if (transactionType.equalsIgnoreCase(Retail.TypeHdrStr)) {
            return Retail.parseTransaction(record);
        } else if (transactionType.equalsIgnoreCase(TopUp.TypeHdrStr)) {
            return TopUp.parseTransaction(record);
        } else {
            throw new OCTransactionFormatException("parseTransaction: Invalid transaction type: " + tokens[0]);
        }
    }


    //============================================================
    // Helper Methods
    public String getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public String getDateStr() {
        return new SimpleDateFormat("MMM. d, yyyy (E)").format(date);
    }

    public String getTimeStr() {
        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    public String getTransactionID() {
        return transactionID;
    }

    public double getAmount() {
        return amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getRecordHdr() {
        return type + " " + DateTimeUtil.dateTime2Str(date) + " " + transactionID + " " + amount;
    }


    //============================================================
    // Helper Method -- parseDateTimeStr
    private static Date parseDateTimeStr(String dateStr) throws OCTransactionFormatException, ParseException {
        try{
            return DateTimeUtil.str2DateTime(dateStr);
        }catch (NumberFormatException e){
            throw new OCTransactionFormatException("parseDateTimeStr: Corrupted DateTime: " + dateStr);
        }
    }


    //============================================================
    // Helper Method -- parseAmountStr
    private static double parseAmountStr(String amountStr) throws OCTransactionFormatException {
        try {
            return Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            throw new OCTransactionFormatException("parseAmountStr: Corrupted amount: " + amountStr);
        }
    }


    //============================================================
    // Helper Method -- matchDate
    protected boolean matchDate(String matchDateStr) throws ParseException {
        String dateFormat = "yyyy-MM-dd";

        // validate dateStr
        if (matchDateStr.length() != 10) {
            throw new ParseException("Invalid date format", 0);
        }
        new SimpleDateFormat(dateFormat).parse(matchDateStr);
        return new SimpleDateFormat(dateFormat).format(date).equals(matchDateStr);
    }


    //============================================================
    // toString
    @Override
    public String toString() {
        String str = "";
        str += "    TransactionID: " + transactionID + "\n";
        str += "    Date/Time: " + getDateStr() + " at " + getTimeStr() + "\n";
        str += "    Amount: " + amount + "\n";
        str += "    Status: " + status + "\n";
        return str;
    }


    //============================================================
    // OCTransactionSearchException
    public static class OCTransactionSearchException extends Exception {
        public OCTransactionSearchException(String ocTransactionSearchExMsg) {
            super(ocTransactionSearchExMsg);
        }
    }


    //============================================================
    // OCTransactionFormatException
    public static class OCTransactionFormatException extends Exception {
        public OCTransactionFormatException(String ocTransactionFormatExMsg) {
            super(ocTransactionFormatExMsg);
        }
    }

    enum Status{
        COMPLETED,
        MTR_OUTSTANDING,
        MTR_COMPLETED
    }

    public static boolean typeIsValid(String OCTransactionType){
        switch(OCTransactionType.toLowerCase()){
            case "mtr":
            case "busfare":
            case "topup":
            case "retail":
                return true;
            default:
                return false;
        }
    }

    public String toRecord(){
        return "";
    }

    public boolean match(String[] criteria) throws ParseException, OCTransactionSearchException{
        return true;
    }
}
