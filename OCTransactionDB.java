import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


public class OCTransactionDB {
    private List<OCTransaction> transactionList;
    //============================================================
    // constructor
    public OCTransactionDB() {
        transactionList = new ArrayList<OCTransaction>();
    }

    //============================================================
    // loadDB
    public void loadDB(String fName) throws OCTransactionDBException, ParseException {
        int cnt = 0;
        int lineNo = 0;

        try {
            System.out.println("Loading transaction db from " + fName + "...");
            Scanner in = new Scanner(new File(fName));

            while (in.hasNext()) {
                String line = in.nextLine();
                lineNo++;
                try {
                    if (addTransaction(line) != null) {
                        cnt++;
                    }
                } catch (OCTransactionDBException | OCTransaction.OCTransactionFormatException e) {
                    System.out.println("OCTransactionDB.loadDB: error loading record from line " + lineNo + " of " + fName + " -- " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            throw new OCTransactionDBException("loadDB failed: File not found (" + fName + ")!");
        }
        System.out.println(cnt + " Octopus card transactions loaded.");
    }


    //============================================================
    // saveDB
    public void saveDB(String fName) throws Exception{
        int cnt = 0;

        PrintWriter out = new PrintWriter(fName);
        for (OCTransaction ocTransaction : transactionList) {
            out.println(ocTransaction.toRecord());
            cnt++;
        }
        out.close();
        System.out.println(cnt + " Octopus card transactions saved to " + fName + ".");
    }


    //============================================================
    // list
    public void list(String type) {
        int cnt = 0;

        for (OCTransaction ocTransaction: transactionList){
            if (ocTransaction.getType().equalsIgnoreCase(type)){
                System.out.println(ocTransaction.toString());
            }else if (type.equals("")){
                System.out.println(ocTransaction.toString());
            }
            cnt++;
        }
        System.out.println(cnt + " record(s) found.");
    }

    public void list() {
        list("");
    }


    //============================================================
    // addTransaction
    public void addTransaction(OCTransaction newTransaction) throws OCTransactionDBException {
        if (searchIdx(newTransaction.getType(), newTransaction.getDate(), newTransaction.getTransactionID()) != -1) {
            throw new OCTransactionDBException("Duplicated record is found!");
        }
        List<OCTransaction> newTransactionList = new ArrayList<OCTransaction>();
        for (OCTransaction transaction: transactionList){
            newTransactionList.add(transaction);
        }
        newTransactionList.add(newTransaction);
        for (int i = 0; i < newTransactionList.size(); i++){
            for (int j = 0; j < newTransactionList.size(); j++) {
                OCTransaction transaction1 = newTransactionList.get(i);
                OCTransaction transaction2 = newTransactionList.get(j);
                if (transaction1.getDate().compareTo(transaction2.getDate()) < 0){
                    OCTransaction temp = transaction1;
                    newTransactionList.set(j, temp);
                    newTransactionList.set(i, transaction2);
                }
            }
        }
        for (int j = newTransactionList.size() - 1; j >= 1; j--){
            OCTransaction transaction1 = newTransactionList.get(j);
            OCTransaction transaction2 = newTransactionList.get(j-1);
            if (transaction1.getDate().compareTo(transaction2.getDate()) < 0){
                OCTransaction temp = transaction1;
                newTransactionList.set(j, temp);
                newTransactionList.set(j-1, transaction2);
            }
        }
        transactionList = newTransactionList;
    }

    public OCTransaction addTransaction(String record) throws OCTransactionDBException, OCTransaction.OCTransactionFormatException
            , ParseException {
        OCTransaction transaction = OCTransaction.parseTransaction(record);

        // skip blank lines
        if (transaction == null) {
            return null;
        }

        addTransaction(transaction);

        if (transaction.getType().equalsIgnoreCase("MTR") &&
                ((MTR) transaction).getMtrType() == MTR.MTRType.checkOut) {
            mtrCheckOut((MTR) transaction);
        }
        return transaction;
    }

    //============================================================
    // mtrCheckOut
    private void mtrCheckOut(MTR chkOutTransaction) throws OCTransactionDBException {
            for (int i = transactionList.size() - 1; i >= 0; i--) {
                if (transactionList.get(i).getType().equalsIgnoreCase("MTR")) {
                    MTR transaction = (MTR) transactionList.get(i);
                    if (transaction.getMtrType() == MTR.MTRType.checkIn &&
                            transaction.getStatus() == OCTransaction.Status.MTR_OUTSTANDING) {
                        transaction.setStatus(OCTransaction.Status.MTR_COMPLETED);
                        transaction.getMatching(chkOutTransaction);
                        chkOutTransaction.setStatus(OCTransaction.Status.MTR_COMPLETED);
                        chkOutTransaction.getMatching(transaction);
                        return;
                    }
                }
            }
            throw new OCTransactionDBException("mtrCheckOut: No outstanding MTR CheckIn transaction found!");
    }

    //============================================================
    // search
    public OCTransaction [] search(String type, String[] criteria) throws OCTransaction.OCTransactionSearchException, ParseException {
        OCTransaction [] searchResult = new OCTransaction[0];
        int cnt = 1;
        if (!(OCTransaction.typeIsValid(type))){
            throw new OCTransaction.OCTransactionSearchException("Invalid search type: " + type);
        }
        // search through the transactions now
        for (OCTransaction transaction : transactionList) {
            if (transaction.getType().equalsIgnoreCase(type) && transaction.match(criteria)) {
                OCTransaction[] newSearchArray = new OCTransaction[searchResult.length + 1];
                for (int i = 0; i < searchResult.length; i++){
                    newSearchArray[i] = searchResult[i];
                }
                newSearchArray[newSearchArray.length - 1] = transaction;
                searchResult = newSearchArray;
            }
        }
        return searchResult;
    }


    //============================================================
    // searchIdx
    private int searchIdx(String type, Date date, String transactionID) {
        for (int i = 0; i < transactionList.size(); i++) {
            if (transactionList.get(i).getType().equalsIgnoreCase(type) && transactionList.get(i).getDate().equals(date)
                    && transactionList.get(i).getTransactionID().equalsIgnoreCase(transactionID)) {
                return i;
            }
        }
        return -1;
    }


    //============================================================
    // OCTransactionDBException
    public static class OCTransactionDBException extends Exception {
        public OCTransactionDBException(String ocTransactionDBExMsg) {
            super(ocTransactionDBExMsg);
        }
    }
}
