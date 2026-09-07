package hr.tvz.projekt.log;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.time.LocalDateTime;

@XmlRootElement(name = "logEntry")
@XmlAccessorType(XmlAccessType.FIELD)
public class LogEntry {

    @XmlElement
    private String timestamp;

    @XmlElement
    private String action;

    // Prazni konstruktor treba za JAXB
    public LogEntry() {
    }

    public LogEntry(String action) {
        this.timestamp = LocalDateTime.now().toString();
        this.action = action;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAction() {
        return action;
    }
}
