package com.bank.model.statements;

import com.bank.storage.Storable;
import com.bank.storage.UnMarshalingException;
import java.time.LocalDate;


public class Statement implements Storable {
    protected String type;
    protected LocalDate date;
    protected double amount;
    protected double balanceAfter;
    protected String description;
    protected String transactor;
    protected String senderIban;
    protected String receiverIban;


    public Statement(String type, LocalDate dateTime, double amount, String description, String transactor, String senderIban , String receiverIban, double balanceAfter) {
        this.type = type;
        this.date = dateTime;
        this.amount = amount;
        this.description = description;
        this.transactor = transactor;
        this.senderIban = senderIban;
        this.receiverIban = receiverIban;
        this.balanceAfter = balanceAfter;
    }

    public Statement(){
    }



    @Override
    public String marshal() {
        StringBuilder sb = new StringBuilder("type:").append(this.type).append(",");
        sb.append("dateTime:").append(this.date).append(",");
        sb.append("amount:").append(this.amount).append(",");
        sb.append("balanceAfter:").append(this.balanceAfter).append(",");
        sb.append("description:").append(this.description).append(",");
        sb.append("transactor:").append(this.transactor).append(",");
        sb.append("senderIban:").append(this.senderIban).append(",");
        sb.append("receiverIban:").append(this.receiverIban);
        return sb.toString();
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        try {
            String[] fields = data.split(",");
            for (String field : fields) {
                String[] parts = field.split(":");
                if (parts.length != 2) continue;
                String key = parts[0].trim();
                String value = parts[1].trim();

                switch (key) {
                    case "type":
                        this.type = value;
                        break;
                    case "dateTime":
                        this.date = LocalDate.parse(value);
                        break;
                    case "amount":
                        this.amount = Double.parseDouble(value);
                        break;
                    case "balanceAfter":
                        this.balanceAfter = Double.parseDouble(value);
                        break;
                    case "description":
                        this.description = value;
                        break;
                    case "transactor":
                        this.transactor = value;
                        break;
                    case "senderIban":
                        this.senderIban = value;
                        break;
                    case "receiverIban":
                        this.receiverIban = value;
                        break;
                }
            }
        } catch (Exception e) {
            throw new UnMarshalingException("Failed to parse statement: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Type= " + type + ", Date= " + date + ", Amount= " + amount + ", Balance After= " + balanceAfter +
                ", Description= " + description  + ", Transactor= " + transactor + ", SenderIban= '" + senderIban  +
                ", ReceiverIban= " + receiverIban;
    }
}
