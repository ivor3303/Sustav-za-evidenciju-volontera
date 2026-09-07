package hr.tvz.projekt.log;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "actionLog")
@XmlAccessorType(XmlAccessType.FIELD)
public class ActionLog {

    @XmlElement(name = "entry")
    private List<LogEntry> entries = new ArrayList<>();

    public List<LogEntry> getEntries() {
        return entries;
    }

    public void addEntry(LogEntry entry) {
        entries.add(entry);
    }
}

